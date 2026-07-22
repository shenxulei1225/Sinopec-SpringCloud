package cn.iocoder.yudao.module.emergency.service.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 缓存更新服务
 * 
 * 实现Cache-Aside缓存模式，提供带缓存的读取和更新功能
 * 
 * @author 系统生成
 */
@Slf4j
@Service
public class CacheUpdateService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * Cache-Aside模式：读取数据
     * 
     * @param key 缓存键
     * @param type 数据类型
     * @param dataLoader 数据加载器（从数据库加载）
     * @param expireSeconds 缓存过期时间（秒）
     * @return 数据
     */
    public <T> T getWithCache(String key, Class<T> type, Supplier<T> dataLoader, long expireSeconds) {
        // 1. 先查缓存
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                log.debug("缓存命中：key={}", key);
                return type.cast(cached);
            }
        } catch (Exception e) {
            log.warn("获取缓存失败：key={}", key, e);
        }
        
        // 2. 缓存未命中，查数据库
        log.debug("缓存未命中，查询数据库：key={}", key);
        T data = dataLoader.get();
        
        // 3. 写入缓存
        if (data != null) {
            try {
                redisTemplate.opsForValue().set(key, data, expireSeconds, TimeUnit.SECONDS);
                log.debug("缓存设置：key={}, expireSeconds={}", key, expireSeconds);
            } catch (Exception e) {
                log.warn("设置缓存失败：key={}", key, e);
            }
        }
        
        return data;
    }
    
    /**
     * Cache-Aside模式：更新数据
     * 
     * @param key 缓存键
     * @param newData 新数据
     * @param dbUpdater 数据库更新器
     * @param expireSeconds 缓存过期时间（秒）
     */
    @Transactional(rollbackFor = Exception.class)
    public <T> void updateWithCache(String key, T newData, Runnable dbUpdater, long expireSeconds) {
        // 1. 先更新数据库
        log.debug("更新数据库：key={}", key);
        dbUpdater.run();
        
        // 2. 删除缓存（让下次查询时重新加载）
        try {
            redisTemplate.delete(key);
            log.debug("缓存删除：key={}", key);
            
            // 3. 发布缓存失效通知（分布式环境）
            redisTemplate.convertAndSend("cache:evict", key);
            log.debug("缓存失效通知发布：key={}", key);
        } catch (Exception e) {
            log.warn("删除缓存失败：key={}", key, e);
        }
    }
    
    /**
     * 清除缓存
     * 
     * @param key 缓存键
     */
    public void evictCache(String key) {
        try {
            redisTemplate.delete(key);
            redisTemplate.convertAndSend("cache:evict", key);
            log.debug("缓存清除：key={}", key);
        } catch (Exception e) {
            log.warn("清除缓存失败：key={}", key, e);
        }
    }
    
    /**
     * 缓存预热
     * 
     * @param keyPattern 缓存键模式（用于生成键）
     * @param dataLoader 数据加载器
     * @param keyGenerator 键生成器
     * @param expireSeconds 缓存过期时间（秒）
     */
    public void warmupCache(String keyPattern, Supplier<List<?>> dataLoader, Function<Object, String> keyGenerator, long expireSeconds) {
        try {
            List<?> dataList = dataLoader.get();
            for (Object data : dataList) {
                String key = keyGenerator.apply(data);
                redisTemplate.opsForValue().set(key, data, expireSeconds, TimeUnit.SECONDS);
            }
            log.debug("缓存预热完成：keyPattern={}, size={}", keyPattern, dataList.size());
        } catch (Exception e) {
            log.warn("缓存预热失败：keyPattern={}", keyPattern, e);
        }
    }
}








