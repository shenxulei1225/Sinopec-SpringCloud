package cn.cheers.x.device.protocolgateway.transport.websocket;

import cn.cheers.x.device.protocolgateway.protocol.monitor.ProtocolMonitorHub;
import cn.cheers.x.device.protocolgateway.transport.DeviceSessionRegistry;
import cn.cheers.x.device.protocolgateway.transport.DeviceTransport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * WebSocket 通讯实现：只负责写出文本，不解析 opcode。
 * <p>同一台设备的写出排成一队，由后台线程写到连接上。调用方把文本交进队列就返回，不等这一笔写完，也不等设备答卷。
 * 心跳和页面下发不能同时写同一条连接，否则连接会被关掉。
 * <p>会话当时不在线则立刻失败。已经交进队列后写出失败只记日志，不在这里关掉连接。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketDeviceTransport implements DeviceTransport {

    private final DeviceSessionRegistry sessionRegistry;
    private final ProtocolMonitorHub protocolMonitorHub;
    /** 按设备排队写出。不要锁 WebSocket 会话本身，否则会和收包线程互相等待。 */
    private final ConcurrentMap<String, Object> writeLocks = new ConcurrentHashMap<>();
    /** 每台设备一条写出线程，保证顺序，也不让页面请求线程堵在连接上。 */
    private final ConcurrentMap<String, ExecutorService> writers = new ConcurrentHashMap<>();

    @Override
    public boolean sendText(String deviceId, String text) {
        WebSocketSession session = sessionRegistry.find(deviceId).orElse(null);
        if (session == null || !session.isOpen()) {
            log.warn("[device-protocol] 发送失败，设备不在线 deviceId={}", deviceId);
            return false;
        }
        writers.computeIfAbsent(deviceId, this::newWriter).execute(() -> writeOne(deviceId, text));
        return true;
    }

    private ExecutorService newWriter(String deviceId) {
        return Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "device-ws-write-" + deviceId);
            thread.setDaemon(true);
            return thread;
        });
    }

    private void writeOne(String deviceId, String text) {
        WebSocketSession session = sessionRegistry.find(deviceId).orElse(null);
        if (session == null || !session.isOpen()) {
            log.warn("[device-protocol] 发送失败，设备不在线 deviceId={}", deviceId);
            return;
        }
        Object lock = writeLocks.computeIfAbsent(deviceId, ignored -> new Object());
        try {
            synchronized (lock) {
                if (!session.isOpen()) {
                    log.warn("[device-protocol] 发送失败，设备不在线 deviceId={}", deviceId);
                    return;
                }
                session.sendMessage(new TextMessage(text));
            }
            protocolMonitorHub.copyOutbound(deviceId, text);
        } catch (IOException | IllegalStateException e) {
            log.warn("[device-protocol] 发送异常 deviceId={}", deviceId, e);
        }
    }

    @Override
    public boolean isOnline(String deviceId) {
        return sessionRegistry.find(deviceId).map(WebSocketSession::isOpen).orElse(false);
    }
}
