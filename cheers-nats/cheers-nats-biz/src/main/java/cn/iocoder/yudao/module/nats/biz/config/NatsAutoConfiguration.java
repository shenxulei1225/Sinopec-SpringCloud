package cn.iocoder.yudao.module.nats.biz.config;

import io.nats.client.Connection;
import io.nats.client.Nats;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * NATS 自动配置
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(NatsProperties.class)
public class NatsAutoConfiguration {

    @Autowired
    private NatsProperties properties;

    private Connection connection;

    @PostConstruct
    public void init() throws Exception {
        if (properties.getEnabled() != null && properties.getEnabled()) {
            connection = Nats.connect(properties.getServer());
            log.info("[NATS] 连接成功: {}", properties.getServer());
        } else {
            log.warn("[NATS] 已禁用");
        }
    }

    @PreDestroy
    public void destroy() throws Exception {
        if (connection != null) {
            connection.close();
            log.info("[NATS] 连接已关闭");
        }
    }

    @Bean
    public Connection natsConnection() {
        return connection;
    }
}
