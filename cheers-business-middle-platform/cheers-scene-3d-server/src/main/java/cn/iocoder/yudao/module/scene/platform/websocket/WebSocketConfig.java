package cn.iocoder.yudao.module.scene.platform.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket 配置类
 */
@Configuration
public class WebSocketConfig {

    /**
     * 注入 ServerEndpointExporter
     * <p>
     * 该 Bean 会自动注册使用 @ServerEndpoint 注解声明的 WebSocket endpoint。
     * 如果部署到外部 Tomcat，需改为配置 Tomcat 的 WebSocket 支持。
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
