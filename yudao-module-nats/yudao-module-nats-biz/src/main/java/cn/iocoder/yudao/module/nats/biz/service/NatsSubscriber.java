package cn.iocoder.yudao.module.nats.biz.service;

import cn.iocoder.yudao.module.nats.api.packet.RuntimePacket;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * NATS 订阅者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NatsSubscriber {

    private final Connection connection;
    private final ObjectMapper objectMapper;
    private Dispatcher dispatcher;

    @PostConstruct
    public void init() {
        if (connection == null) {
            log.warn("[NATS] 连接未建立，跳过订阅");
            return;
        }

        dispatcher = connection.createDispatcher(msg -> {
            String subject = msg.getSubject();
            String body = new String(msg.getData(), StandardCharsets.UTF_8);
            log.info("[NATS] 收到消息: subject={}, body={}", subject, body);
            try {
                RuntimePacket<Object> packet = objectMapper.readValue(body, RuntimePacket.class);
                log.info("[NATS] 解析成功: subject={}, type={}, timestamp={}",
                        packet.getSubject(), packet.getType(), packet.getTimestamp());
            } catch (JsonProcessingException e) {
                log.debug("[NATS] 非 RuntimePacket 消息: {}", body);
            }
        });

        log.info("[NATS] Dispatcher 已创建");
    }

    public void subscribe(String subject) {
        if (dispatcher == null) {
            log.warn("[NATS] Dispatcher 未初始化");
            return;
        }
        dispatcher.subscribe(subject);
        log.info("[NATS] 已订阅: subject={}", subject);
    }

    public void subscribeWildcard(String subjectPattern) {
        if (dispatcher == null) {
            log.warn("[NATS] Dispatcher 未初始化");
            return;
        }
        dispatcher.subscribe(subjectPattern);
        log.info("[NATS] 已订阅通配符: subjectPattern={}", subjectPattern);
    }

    public void subscribeAndProcess(String subject, Consumer<RuntimePacket<?>> handler) {
        if (connection == null) {
            log.warn("[NATS] 连接未建立，跳过订阅: subject={}", subject);
            return;
        }
        Dispatcher subjectDispatcher = connection.createDispatcher(msg -> {
            String body = new String(msg.getData(), StandardCharsets.UTF_8);
            try {
                RuntimePacket<?> packet = objectMapper.readValue(body, new TypeReference<RuntimePacket<?>>() {});
                handler.accept(packet);
            } catch (JsonProcessingException e) {
                log.error("[NATS] 消息处理失败: subject={}, error={}", subject, e.getMessage());
            }
        });
        subjectDispatcher.subscribe(subject);
        log.info("[NATS] 已订阅并注册处理器: subject={}", subject);
    }
}
