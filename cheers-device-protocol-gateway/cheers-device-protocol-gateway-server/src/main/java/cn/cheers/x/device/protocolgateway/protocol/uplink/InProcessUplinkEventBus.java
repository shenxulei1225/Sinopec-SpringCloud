package cn.cheers.x.device.protocolgateway.protocol.uplink;

import cn.cheers.x.device.protocolgateway.api.dto.DeviceUplinkEventDTO;
import cn.cheers.x.device.protocolgateway.config.DeviceProtocolGatewayProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 进程内上报出队：把事件带离 WebSocket 收包线程。
 * <p>负责：入队、工作线程投递给消费者。
 * <p>不负责：巡检回写、持久化落库。
 * <p>禁止：把本类当成正式持久化总线。正式主路径仍是消息中间件；本实现是过渡，待替换。
 */
@Slf4j
@Component
public class InProcessUplinkEventBus implements UplinkEventPublisher {

    private final List<UplinkEventConsumer> consumers;
    private final BlockingQueue<DeviceUplinkEventDTO> queue;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread worker;

    public InProcessUplinkEventBus(
            DeviceProtocolGatewayProperties properties,
            List<UplinkEventConsumer> consumers) {
        this.consumers = consumers;
        this.queue = new ArrayBlockingQueue<>(Math.max(16, properties.getUplinkQueueCapacity()));
    }

    @PostConstruct
    void start() {
        running.set(true);
        worker = new Thread(this::drain, "device-uplink-bus");
        worker.setDaemon(true);
        worker.start();
    }

    @PreDestroy
    void stop() {
        running.set(false);
        if (worker != null) {
            worker.interrupt();
        }
    }

    @Override
    public void publish(DeviceUplinkEventDTO event) {
        boolean offered = queue.offer(event);
        if (!offered) {
            log.error("[device-protocol] 上报队列已满，投递失败 channel={} deviceId={} messageKind={} msgId={}",
                    event.channelCode(), event.deviceId(), event.messageKind(), event.msgId());
        }
    }

    private void drain() {
        while (running.get()) {
            try {
                DeviceUplinkEventDTO event = queue.poll(1, TimeUnit.SECONDS);
                if (event == null) {
                    continue;
                }
                for (UplinkEventConsumer consumer : consumers) {
                    try {
                        consumer.consume(event);
                    } catch (Exception ex) {
                        log.warn("[device-protocol] 上报消费者失败 consumer={} channel={} deviceId={} messageKind={}: {}",
                                consumer.getClass().getSimpleName(),
                                event.channelCode(), event.deviceId(), event.messageKind(), ex.getMessage());
                    }
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
