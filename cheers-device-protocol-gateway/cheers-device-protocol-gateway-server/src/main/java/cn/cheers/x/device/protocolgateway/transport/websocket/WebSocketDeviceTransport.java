package cn.cheers.x.device.protocolgateway.transport.websocket;

import cn.cheers.x.device.protocolgateway.transport.DeviceSessionRegistry;
import cn.cheers.x.device.protocolgateway.transport.DeviceTransport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * WebSocket 通讯实现：只负责写出文本，不解析 opcode。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketDeviceTransport implements DeviceTransport {

    private final DeviceSessionRegistry sessionRegistry;

    @Override
    public boolean sendText(String deviceId, String text) {
        WebSocketSession session = sessionRegistry.find(deviceId).orElse(null);
        if (session == null || !session.isOpen()) {
            log.warn("[device-protocol] 发送失败，设备不在线 deviceId={}", deviceId);
            return false;
        }
        try {
            session.sendMessage(new TextMessage(text));
            return true;
        } catch (IOException e) {
            log.warn("[device-protocol] 发送异常 deviceId={}", deviceId, e);
            return false;
        }
    }

    @Override
    public boolean isOnline(String deviceId) {
        return sessionRegistry.find(deviceId).map(WebSocketSession::isOpen).orElse(false);
    }
}
