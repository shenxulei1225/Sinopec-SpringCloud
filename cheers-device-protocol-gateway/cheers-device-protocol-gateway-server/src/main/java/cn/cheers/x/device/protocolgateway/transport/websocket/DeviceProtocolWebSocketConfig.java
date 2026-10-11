package cn.cheers.x.device.protocolgateway.transport.websocket;

import cn.cheers.x.device.protocolgateway.config.DeviceProtocolGatewayProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

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

    /**
     * 容器收包上限与连接上的上限用同一个数。只改连接、不改容器时，整包仍会在进页面之前被丢掉。
     */
    @Bean
    public ServletServerContainerFactoryBean deviceSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(DeviceProtocolWebSocketHandler.TEXT_MESSAGE_LIMIT);
        container.setMaxBinaryMessageBufferSize(DeviceProtocolWebSocketHandler.TEXT_MESSAGE_LIMIT);
        return container;
    }
}
