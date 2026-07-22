package cn.iocoder.yudao.module.emergency.framework.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Redisson配置
 * 
 * 用于分布式锁实现
 * 
 * @author 系统生成
 */
@Configuration
@Profile("!test")  // 在测试环境中不生效
@ConditionalOnMissingBean(name = "redissonClient")  // 如果已经有redissonClient Bean，则不创建
public class RedissonConfig {
    
    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;
    
    @Value("${spring.data.redis.port:6379}")
    private int redisPort;
    
    @Value("${spring.data.redis.database:0}")
    private int redisDatabase;
    
    @Value("${spring.data.redis.password:}")
    private String redisPassword;
    
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        String address = String.format("redis://%s:%d", redisHost, redisPort);
        var singleServerConfig = config.useSingleServer()
              .setAddress(address)
              .setDatabase(redisDatabase)
              .setConnectionPoolSize(10)
              .setConnectionMinimumIdleSize(5);
        // 如果配置了密码，则设置密码
        if (redisPassword != null && !redisPassword.isEmpty()) {
            singleServerConfig.setPassword(redisPassword);
        }
        return Redisson.create(config);
    }
}



