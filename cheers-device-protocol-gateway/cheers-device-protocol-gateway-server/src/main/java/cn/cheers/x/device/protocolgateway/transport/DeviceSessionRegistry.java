package cn.cheers.x.device.protocolgateway.transport;

import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

/**
 * 通讯层会话注册表：deviceId ↔ WebSocket 会话。
 */
public interface DeviceSessionRegistry {

    void bind(String deviceId, WebSocketSession session);

    void unbind(String deviceId);

    Optional<WebSocketSession> find(String deviceId);
}
