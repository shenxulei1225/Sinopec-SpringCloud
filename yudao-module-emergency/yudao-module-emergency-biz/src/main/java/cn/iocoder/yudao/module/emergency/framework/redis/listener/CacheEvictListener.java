package cn.iocoder.yudao.module.emergency.framework.redis.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis缓存失效监听器
 * 
 * 用于监听缓存失效通知，在分布式环境中同步缓存失效
 * 
 * @author 系统生成
 */
@Slf4j
@Component
public class CacheEvictListener implements MessageListener {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = new String(message.getChannel());
            String key = new String(message.getBody());
            
            if ("cache:evict".equals(channel)) {
                // 删除本地缓存（如果使用多级缓存）
                // 注意：这里主要是为了在分布式环境中同步缓存失效
                // 如果使用本地缓存（如Caffeine），需要在这里清除本地缓存
                redisTemplate.delete(key);
                
                log.info("缓存失效通知：channel={}, key={}", channel, key);
            } else {
                log.debug("收到其他Redis消息：channel={}, key={}", channel, key);
            }
        } catch (Exception e) {
            log.error("处理缓存失效通知失败", e);
        }
    }
}








