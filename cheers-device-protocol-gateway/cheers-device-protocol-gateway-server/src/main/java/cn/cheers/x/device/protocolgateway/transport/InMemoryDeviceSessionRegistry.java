package cn.cheers.x.device.protocolgateway.transport;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存会话表：单机绑定 deviceId → WebSocketSession。
 */
@Component
public class InMemoryDeviceSessionRegistry implements DeviceSessionRegistry {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void bind(String deviceId, WebSocketSession session) {
        sessions.put(deviceId, session);
    }

    @Override
    public void unbind(String deviceId) {
        sessions.remove(deviceId);
    }

    @Override
    public Optional<WebSocketSession> find(String deviceId) {
        return Optional.ofNullable(sessions.get(deviceId));
    }
}
