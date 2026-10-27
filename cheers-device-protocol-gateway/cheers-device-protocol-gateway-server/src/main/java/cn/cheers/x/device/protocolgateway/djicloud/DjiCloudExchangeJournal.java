package cn.cheers.x.device.protocolgateway.djicloud;

import cn.cheers.x.device.protocolgateway.protocol.exchange.RunningTaskExchangeRecorder;
import cn.cheers.x.inspection.task.api.dto.TaskExchangeTranscriptReqDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 大疆上云发出的报文离开收包线程后，记一条已下发事件。
 * <p>飞行编号或事务编号能对上这次执行时带上执行记录编号。对不上就不记。
 * <p>不负责：改步骤完成、改整次执行状态、从报文路径记飞机位置。
 * <p>禁止：在收包线程里调用巡检；把报文原文写进过程。
 */
@Slf4j
@Component
public class DjiCloudExchangeJournal {

    private static final Set<String> GATEWAY_TAILS = Set.of(
            "status", "status_reply", "requests", "requests_reply", "events", "events_reply",
            "services", "services_reply", "property/set", "property/set_reply", "drc/up", "drc/down");

    private final ObjectMapper objectMapper;
    private final DjiCloudOnlineRegistry onlineRegistry;
    private final RunningTaskExchangeRecorder transcripts;
    private final BlockingQueue<Item> queue = new ArrayBlockingQueue<>(512);
    private final ConcurrentMap<String, Long> tidToRecord = new ConcurrentHashMap<>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread worker;

    public DjiCloudExchangeJournal(
            ObjectMapper objectMapper,
            DjiCloudOnlineRegistry onlineRegistry,
            RunningTaskExchangeRecorder transcripts) {
        this.objectMapper = objectMapper;
        this.onlineRegistry = onlineRegistry;
        this.transcripts = transcripts;
    }

    @PostConstruct
    void start() {
        running.set(true);
        worker = new Thread(this::drain, "dji-cloud-exchange");
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

    /** 收包线程调用。只入队。 */
    public void acceptInbound(String topic, byte[] payload) {
        if (topic == null || payload == null || payload.length == 0) {
            return;
        }
        accept(topic, new String(payload, StandardCharsets.UTF_8), TaskExchangeTranscriptReqDTO.INBOUND);
    }

    /** 发出之后调用。只入队。 */
    public void acceptOutbound(String topic, String json) {
        if (topic == null || json == null || json.isBlank()) {
            return;
        }
        accept(topic, json, TaskExchangeTranscriptReqDTO.OUTBOUND);
    }

    private void accept(String topic, String json, String direction) {
        JsonNode root = read(json);
        remember(root);
        if (!TaskExchangeTranscriptReqDTO.OUTBOUND.equals(direction)) {
            return;
        }
        String deviceId = deviceId(topic);
        if (deviceId == null) {
            return;
        }
        Long recordId = DjiCloudExchangePolicy.flightRecordId(root);
        if (recordId == null) {
            recordId = linked(root);
        }
        if (!queue.offer(new Item(deviceId, direction, json, recordId, null))) {
            log.error("[dji-cloud] 已下发事件队列已满，未记下 deviceId={}", deviceId);
        }
    }

    private void drain() {
        while (running.get()) {
            try {
                Item item = queue.poll(1, TimeUnit.SECONDS);
                if (item == null) {
                    continue;
                }
                if (item.payloadJson() != null) {
                    transcripts.recordNow(item.deviceId(), item.direction(), item.payloadJson(), item.recordId());
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                return;
            } catch (RuntimeException ex) {
                log.warn("[dji-cloud] 记入任务失败", ex);
            }
        }
    }

    private void remember(JsonNode root) {
        String tid = DjiCloudExchangePolicy.tid(root);
        Long recordId = DjiCloudExchangePolicy.flightRecordId(root);
        if (tid != null && recordId != null) {
            tidToRecord.put(tid, recordId);
        }
    }

    private Long linked(JsonNode root) {
        String tid = DjiCloudExchangePolicy.tid(root);
        return tid == null ? null : tidToRecord.get(tid);
    }

    private String deviceId(String topic) {
        String serial = DjiCloudExchangePolicy.serialOf(topic);
        if (serial == null) {
            return null;
        }
        String tail = topic.substring(topic.lastIndexOf('/') + 1);
        if ("osd".equals(tail) || "state".equals(tail)) {
            String gateway = onlineRegistry.singleGatewayOf(serial);
            return gateway != null ? gateway : serial;
        }
        if (GATEWAY_TAILS.contains(tail) || topic.contains("/property/") || topic.contains("/drc/")) {
            return serial;
        }
        return serial;
    }

    private JsonNode read(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception ex) {
            return objectMapper.createObjectNode();
        }
    }

    private record Item(
            String deviceId,
            String direction,
            String payloadJson,
            Long recordId,
            DjiCloudExchangePolicy.Pose pose) {
    }
}
