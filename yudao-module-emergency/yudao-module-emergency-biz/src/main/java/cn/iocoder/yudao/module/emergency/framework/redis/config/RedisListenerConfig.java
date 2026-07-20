package cn.iocoder.yudao.module.emergency.framework.redis.config;

import cn.iocoder.yudao.module.emergency.framework.redis.listener.CacheEvictListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * Redis消息监听器配置
 * 
 * 用于配置Redis Pub/Sub消息监听，实现缓存失效通知
 * 
 * @author 系统生成
 */
@Configuration
public class RedisListenerConfig {
    
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    
    @Autowired
    private CacheEvictListener cacheEvictListener;
    
    /**
     * 配置Redis消息监听容器
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        
        // 订阅缓存失效通知频道
        container.addMessageListener(
            new MessageListenerAdapter(cacheEvictListener, "onMessage"),
            new ChannelTopic("cache:evict")
        );
        
        return container;
    }
}








