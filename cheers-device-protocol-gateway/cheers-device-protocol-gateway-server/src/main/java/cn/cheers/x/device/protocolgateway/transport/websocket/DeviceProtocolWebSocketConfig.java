package cn.cheers.x.device.protocolgateway.transport.websocket;

import cn.cheers.x.device.protocolgateway.config.DeviceProtocolGatewayProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * 注册巡检通道的 WebSocket 处理器（默认 /{deviceId}，与旧系统单口一致）。
 * <p>不负责工业监听。禁止把本路径写成工业口或写死成「写巡检台账」。
 */
@Configuration
@EnableWebSocket
@EnableConfigurationProperties(DeviceProtocolGatewayProperties.class)
@RequiredArgsConstructor
public class DeviceProtocolWebSocketConfig implements WebSocketConfigurer {

    private final DeviceProtocolWebSocketHandler handler;
    private final DeviceProtocolGatewayProperties properties;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, properties.getPathPattern())
                .setAllowedOrigins(properties.getAllowedOrigins());
    }
}
