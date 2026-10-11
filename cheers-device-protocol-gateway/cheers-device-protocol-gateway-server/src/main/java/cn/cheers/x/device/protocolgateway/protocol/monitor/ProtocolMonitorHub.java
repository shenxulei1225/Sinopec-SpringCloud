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
 * <p>负责：按设备环形缓冲、按设备 SSE、设备清单、清掉当前这一台的副本。窗口里只存原文。
 * <p>不负责：回执、同步等待、上报总线、自己读字段说明；不负责停其他设备的收发；不负责关设备连接。
 * <p>禁止：把所有设备的包摊成一路广播；在收包线程里对浏览器 HTTP；在窗口里改原文。
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
    private final ProtocolMonitorFieldPresenter fieldPresenter;
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
            DeviceSessionRegistry sessionRegistry,
            ProtocolMonitorFieldPresenter fieldPresenter) {
        this.properties = properties;
        this.envelopeJsonCodec = envelopeJsonCodec;
        this.sessionRegistry = sessionRegistry;
        this.fieldPresenter = fieldPresenter;
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
                System.currentTimeMillis(),
                List.of(),
                null
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
                System.currentTimeMillis(),
                List.of(),
                null
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
                System.currentTimeMillis(),
                List.of(),
                null
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
        List<ProtocolMonitorEventDTO> backlog = snapshotForView(key);
        fanout.execute(() -> {
            for (ProtocolMonitorEventDTO event : backlog) {
                sendQuietly(key, emitter, event);
            }
        });
        return emitter;
    }

    /**
     * 给页面的回放。窗口原文不动，这里才按字段说明填中文名。
     */
    public List<ProtocolMonitorEventDTO> snapshotForView(String deviceId) {
        return snapshot(deviceId).stream().map(fieldPresenter::present).toList();
    }

    /**
     * 清掉这一台已经复制的报文。设备连接保持，其它设备的窗口不动。
     * 没选设备则拒绝。窗口本来就是空的，也算清完。
     *
     * @return 清完这一刻的服务器时间。页面丢掉这个时间及以前的副本，避免推送里还没画出来的旧报文又补回来。
     */
    public long clear(String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            throw new IllegalArgumentException("必须先选一台设备才清理报文");
        }
        windows.remove(deviceId.trim());
        return System.currentTimeMillis();
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
            rows.add(new ProtocolMonitorDeviceDTO(
                    deviceId, sessionRegistry.isOnline(deviceId), lastAt, "", ""));
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
            sendQuietly(event.deviceId(), emitter, event);
        }
    }

    /**
     * 浏览器已经关掉时，写出会失败。丢掉这一路即可。
     * 不要把失败再写成页面错误，否则事件流会被普通 JSON 打断，左侧列表就收不到后续报文。
     */
    private void sendQuietly(String deviceId, SseEmitter emitter, ProtocolMonitorEventDTO event) {
        try {
            emitter.send(SseEmitter.event().name("monitor").data(fieldPresenter.present(event)));
        } catch (Exception ex) {
            removeSubscriber(deviceId, emitter);
            try {
                emitter.complete();
            } catch (Exception ignored) {
                // 连接已经断了
            }
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
