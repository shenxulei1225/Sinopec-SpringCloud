package cn.iocoder.yudao.module.emergency.framework.rpc.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * LoadBalancer 配置类
 *
 * 用于自定义负载均衡行为，确保使用 Nacos 服务发现
 */
@Configuration
@Slf4j
public class LoadBalancerConfig {

    /**
     * 创建支持负载均衡的 RestTemplate
     * 用于验证 LoadBalancer 是否正常工作
     */
    @Bean
    @LoadBalanced
    public RestTemplate loadBalancedRestTemplate() {
        log.info("创建支持负载均衡的 RestTemplate");
        return new RestTemplate();
    }
}
