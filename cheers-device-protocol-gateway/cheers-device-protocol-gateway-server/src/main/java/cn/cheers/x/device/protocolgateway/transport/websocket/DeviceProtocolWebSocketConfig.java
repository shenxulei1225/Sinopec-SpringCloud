package cn.cheers.x.device.protocolgateway.transport.websocket;

import cn.cheers.x.device.protocolgateway.config.DeviceProtocolGatewayProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * 注册可配置路径的 WebSocket 处理器（默认 /{deviceId}）。
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
