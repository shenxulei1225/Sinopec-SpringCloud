package cn.cheers.x.device.protocolgateway.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * 网关配置与 HTTP 客户端（过渡上报消费者，不是正式总线）。
 */
@Configuration
@EnableConfigurationProperties(DeviceProtocolGatewayProperties.class)
public class DeviceProtocolGatewayConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
