package cn.iocoder.yudao.module.nats.biz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * NATS 配置属性
 */
@Data
@ConfigurationProperties(prefix = "cheers.nats")
public class NatsProperties {

    /**
     * 是否启用 NATS
     */
    private Boolean enabled = true;

    /**
     * NATS Server 地址
     */
    private String server = "nats://127.0.0.1:4222";
}
