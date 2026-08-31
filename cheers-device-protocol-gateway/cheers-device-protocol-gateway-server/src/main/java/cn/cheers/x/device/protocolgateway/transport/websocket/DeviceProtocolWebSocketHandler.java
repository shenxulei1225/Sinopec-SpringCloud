package cn.cheers.x.device.protocolgateway.transport.websocket;

import cn.cheers.x.device.protocolgateway.protocol.uplink.UplinkDispatcher;
import cn.cheers.x.device.protocolgateway.transport.DeviceSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * WebSocket 处理器：路径末段为 deviceId；上行交给 UplinkDispatcher。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceProtocolWebSocketHandler extends TextWebSocketHandler {

    private final DeviceSessionRegistry sessionRegistry;
    private final UplinkDispatcher uplinkDispatcher;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String deviceId = extractDeviceId(session);
        sessionRegistry.bind(deviceId, session);
        log.info("[device-protocol] 地面站已连接 deviceId={}", deviceId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String deviceId = extractDeviceId(session);
        sessionRegistry.unbind(deviceId);
        log.info("[device-protocol] 地面站已断开 deviceId={} status={}", deviceId, status);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String deviceId = extractDeviceId(session);
        uplinkDispatcher.dispatch(deviceId, message.getPayload());
    }

    static String extractDeviceId(WebSocketSession session) {
        String path = session.getUri() != null ? session.getUri().getPath() : "";
        int slash = path.lastIndexOf('/');
        if (slash < 0 || slash == path.length() - 1) {
            throw new IllegalStateException("WebSocket 路径末段缺少 deviceId: " + path);
        }
        return path.substring(slash + 1);
    }
}
