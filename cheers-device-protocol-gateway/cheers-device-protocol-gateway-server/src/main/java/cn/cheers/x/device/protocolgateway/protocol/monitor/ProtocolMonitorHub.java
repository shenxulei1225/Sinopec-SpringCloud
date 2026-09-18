package cn.cheers.x.device.protocolgateway.protocol.monitor;

import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorDeviceDTO;
import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorEventDTO;
import cn.cheers.x.device.protocolgateway.config.DeviceProtocolGatewayProperties;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import cn.cheers.x.device.protocolgateway.transport.DeviceSessionRegistry;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 实时监控内存窗口：每台设备的收发都复制进来，工具页按选中设备只看其中一台。
 * <p>负责：按设备环形缓冲、按设备 SSE、设备清单。
 * <p>不负责：回执、同步等待、上报总线、字段说明翻译；不负责停其他设备的收发。
 * <p>禁止：把所有设备的包摊成一路广播；在收包线程里对浏览器 HTTP。
 */
@Slf4j
@Component
public class ProtocolMonitorHub {

    public static final String DIRECTION_INBOUND = "inbound";
    public static final String DIRECTION_OUTBOUND = "outbound";
    public static final String DIRECTION_SESSION = "session";

    private final DeviceProtocolGatewayProperties properties;
    private final EnvelopeJsonCodec envelopeJsonCodec;
    private final DeviceSessionRegistry sessionRegistry;
    private final Map<String, List<ProtocolMonitorEventDTO>> windows = new ConcurrentHashMap<>();
    private final Map<String, CopyOnWriteArrayList<SseEmitter>> subscribers = new ConcurrentHashMap<>();
    private final ExecutorService fanout = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "protocol-monitor-fanout");
        thread.setDaemon(true);
        return thread;
    });

    public ProtocolMonitorHub(
            DeviceProtocolGatewayProperties properties,
            EnvelopeJsonCodec envelopeJsonCodec,
            DeviceSessionRegistry sessionRegistry) {
        this.properties = properties;
        this.envelopeJsonCodec = envelopeJsonCodec;
        this.sessionRegistry = sessionRegistry;
    }

    /**
     * 复制设备发来的一包。解析失败也留下原文，识别写成「无法解析」。
     */
    public void copyInbound(String deviceId, String payloadJson) {
        Integer opcode = null;
        String msgId = "";
        try {
            opcode = envelopeJsonCodec.readOpcode(payloadJson);
            msgId = envelopeJsonCodec.readMsgId(payloadJson);
        } catch (IllegalArgumentException ignored) {
            // 原文仍要给人看，不猜操作码
        }
        accept(new ProtocolMonitorEventDTO(
                newEventId(),
                deviceId,
                DIRECTION_INBOUND,
                opcode,
                msgId,
                ProtocolMonitorIdentity.forInbound(opcode),
                ProtocolMonitorIdentity.exchangeModeName(opcode),
                payloadJson,
                System.currentTimeMillis()
        ));
    }

    /**
     * 复制平台写出的一包。写出失败的调用方不应进来。
     */
    public void copyOutbound(String deviceId, String payloadJson) {
        Integer opcode = null;
        String msgId = "";
        try {
            opcode = envelopeJsonCodec.readOpcode(payloadJson);
            msgId = envelopeJsonCodec.readMsgId(payloadJson);
        } catch (IllegalArgumentException ignored) {
            // 原文仍要给人看
        }
        accept(new ProtocolMonitorEventDTO(
                newEventId(),
                deviceId,
                DIRECTION_OUTBOUND,
                opcode,
                msgId,
                ProtocolMonitorIdentity.forOutbound(opcode),
                ProtocolMonitorIdentity.exchangeModeName(opcode),
                payloadJson,
                System.currentTimeMillis()
        ));
    }

    /**
     * 设备连上或断开。不是报文，列表里单独认成会话。
     */
    public void copySession(String deviceId, String identity) {
        accept(new ProtocolMonitorEventDTO(
                newEventId(),
                deviceId,
                DIRECTION_SESSION,
                null,
                "",
                identity,
                "",
                "",
                System.currentTimeMillis()
        ));
    }

    /**
     * 必须先有设备标识。先登记订阅，再把窗口里已有的发给这一路。
     */
    public SseEmitter subscribe(String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            throw new IllegalArgumentException("必须先选一台设备才开始推");
        }
        String key = deviceId.trim();
        SseEmitter emitter = new SseEmitter(0L);
        subscribers.computeIfAbsent(key, ignored -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> removeSubscriber(key, emitter));
        emitter.onTimeout(() -> removeSubscriber(key, emitter));
        emitter.onError(error -> removeSubscriber(key, emitter));
        for (ProtocolMonitorEventDTO event : snapshot(key)) {
            sendQuietly(emitter, event);
        }
        return emitter;
    }

    public List<ProtocolMonitorEventDTO> snapshot(String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            return List.of();
        }
        List<ProtocolMonitorEventDTO> window = windows.get(deviceId.trim());
        if (window == null) {
            return List.of();
        }
        synchronized (window) {
            return List.copyOf(window);
        }
    }

    public List<ProtocolMonitorDeviceDTO> listDevices() {
        Set<String> ids = new LinkedHashSet<>(sessionRegistry.listOnlineDeviceIds());
        ids.addAll(windows.keySet());
        List<ProtocolMonitorDeviceDTO> rows = new ArrayList<>();
        for (String deviceId : ids) {
            List<ProtocolMonitorEventDTO> recent = snapshot(deviceId);
            Long lastAt = recent.isEmpty() ? null : recent.get(recent.size() - 1).occurredAtEpochMs();
            rows.add(new ProtocolMonitorDeviceDTO(deviceId, sessionRegistry.isOnline(deviceId), lastAt));
        }
        rows.sort(Comparator.comparing(ProtocolMonitorDeviceDTO::deviceId));
        return List.copyOf(rows);
    }

    void accept(ProtocolMonitorEventDTO event) {
        List<ProtocolMonitorEventDTO> window = windows.computeIfAbsent(
                event.deviceId(), ignored -> new ArrayList<>());
        synchronized (window) {
            window.add(event);
            int keep = Math.max(1, properties.getMonitorWindowSize());
            if (window.size() > keep) {
                window.subList(0, window.size() - keep).clear();
            }
        }
        fanout.execute(() -> fanout(event));
    }

    @PreDestroy
    void shutdown() {
        fanout.shutdownNow();
        subscribers.values().forEach(list -> list.forEach(SseEmitter::complete));
        subscribers.clear();
    }

    private void fanout(ProtocolMonitorEventDTO event) {
        CopyOnWriteArrayList<SseEmitter> emitters = subscribers.get(event.deviceId());
        if (emitters == null || emitters.isEmpty()) {
            return;
        }
        for (SseEmitter emitter : emitters) {
            sendQuietly(emitter, event);
        }
    }

    private void sendQuietly(SseEmitter emitter, ProtocolMonitorEventDTO event) {
        try {
            emitter.send(SseEmitter.event().name("monitor").data(event));
        } catch (IOException ex) {
            emitter.complete();
        }
    }

    private void removeSubscriber(String deviceId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> emitters = subscribers.get(deviceId);
        if (emitters == null) {
            return;
        }
        emitters.remove(emitter);
        if (emitters.isEmpty()) {
            subscribers.remove(deviceId, emitters);
        }
    }

    private static String newEventId() {
        return UUID.randomUUID().toString();
    }
}
