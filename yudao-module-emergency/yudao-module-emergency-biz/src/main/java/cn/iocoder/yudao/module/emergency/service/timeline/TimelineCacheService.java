package cn.iocoder.yudao.module.emergency.service.timeline;

import cn.iocoder.yudao.module.emergency.controller.admin.response.vo.TimelineItemRespVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 时间线缓存服务
 * 
 * 用于缓存事件时间线数据，减少数据库查询，提升查询性能
 * 
 * @author 系统生成
 */
@Slf4j
@Service
public class TimelineCacheService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String TIMELINE_CACHE_KEY = "timeline:event:%d";
    private static final long TIMELINE_CACHE_EXPIRE = 300; // 5分钟
    
    /**
     * 获取时间线缓存
     * 
     * @param eventId 事件ID
     * @return 时间线数据，如果缓存未命中则返回null
     */
    public List<TimelineItemRespVO> getTimelineFromCache(Long eventId) {
        String key = String.format(TIMELINE_CACHE_KEY, eventId);
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                log.debug("时间线缓存命中：eventId={}", eventId);
                // 类型安全检查，避免未检查的类型转换警告
                if (cached instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<TimelineItemRespVO> result = (List<TimelineItemRespVO>) cached;
                    return result;
                } else {
                    log.warn("时间线缓存类型不匹配：eventId={}, cachedType={}", eventId, cached.getClass().getName());
                }
            }
        } catch (Exception e) {
            log.warn("获取时间线缓存失败：eventId={}", eventId, e);
        }
        return null;
    }
    
    /**
     * 设置时间线缓存
     * 
     * @param eventId 事件ID
     * @param timeline 时间线数据
     */
    public void setTimelineCache(Long eventId, List<TimelineItemRespVO> timeline) {
        String key = String.format(TIMELINE_CACHE_KEY, eventId);
        try {
            redisTemplate.opsForValue().set(key, timeline, TIMELINE_CACHE_EXPIRE, TimeUnit.SECONDS);
            log.debug("时间线缓存设置：eventId={}, size={}", eventId, timeline.size());
        } catch (Exception e) {
            log.warn("设置时间线缓存失败：eventId={}", eventId, e);
        }
    }
    
    /**
     * 清除时间线缓存
     * 
     * @param eventId 事件ID
     */
    public void evictTimelineCache(Long eventId) {
        String key = String.format(TIMELINE_CACHE_KEY, eventId);
        try {
            redisTemplate.delete(key);
            log.debug("时间线缓存清除：eventId={}", eventId);
        } catch (Exception e) {
            log.warn("清除时间线缓存失败：eventId={}", eventId, e);
        }
    }
}







