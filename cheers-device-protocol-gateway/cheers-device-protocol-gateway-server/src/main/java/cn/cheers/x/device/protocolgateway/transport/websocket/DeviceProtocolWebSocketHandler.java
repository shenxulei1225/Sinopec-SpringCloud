package cn.cheers.x.device.protocolgateway.transport.websocket;

import cn.cheers.x.device.protocolgateway.api.channel.AccessChannelCodes;
import cn.cheers.x.device.protocolgateway.protocol.monitor.ProtocolMonitorHub;
import cn.cheers.x.device.protocolgateway.protocol.uplink.UplinkIngressService;
import cn.cheers.x.device.protocolgateway.transport.DeviceSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * 巡检接入通道：单口 WebSocket，路径末段为设备编号（与旧系统一致）；正文交给通信层收包顺序。
 * <p>不负责填包、不读巡检库、不接工业报文。禁止在本类写业务回写或 HTTP；禁止把本口当成工业通道。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceProtocolWebSocketHandler extends TextWebSocketHandler {

    private final DeviceSessionRegistry sessionRegistry;
    private final UplinkIngressService uplinkIngressService;
    private final ProtocolMonitorHub protocolMonitorHub;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String deviceId = extractDeviceId(session);
        sessionRegistry.bind(deviceId, session);
        protocolMonitorHub.copySession(deviceId, "设备已连接");
        log.info("[device-protocol] 无人机系统已连接 deviceId={}", deviceId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String deviceId = extractDeviceId(session);
        sessionRegistry.unbind(deviceId);
        protocolMonitorHub.copySession(deviceId, "设备已断开");
        log.info("[device-protocol] 无人机系统已断开 deviceId={} status={}", deviceId, status);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String deviceId = extractDeviceId(session);
        uplinkIngressService.handle(AccessChannelCodes.INSPECTION, deviceId, message.getPayload());
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
