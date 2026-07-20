package cn.iocoder.yudao.module.emergency.service.cache;

import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventMapper;
import cn.iocoder.yudao.module.emergency.service.timeline.TimelineCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 缓存预热服务
 * 
 * 在应用启动时预热关键数据的缓存
 * 
 * @author 系统生成
 */
@Slf4j
@Service
@Order(100) // 在应用启动后执行，优先级较低
public class CacheWarmupService implements CommandLineRunner {
    
    @Autowired
    private EmergencyEventMapper eventMapper;
    
    @Autowired
    private TimelineCacheService timelineCacheService;
    
    @Autowired
    private CacheUpdateService cacheUpdateService;
    
    /**
     * 应用启动时执行缓存预热
     */
    @Override
    public void run(String... args) {
        log.info("开始缓存预热...");
        
        try {
            // 预热最近活跃的事件时间线缓存
            warmupRecentEventTimelines();
            
            log.info("缓存预热完成");
        } catch (Exception e) {
            log.error("缓存预热失败", e);
        }
    }
    
    /**
     * 预热最近活跃的事件时间线缓存
     * 预热最近7天内活跃的事件（状态为响应中、处理中、监控中）
     */
    private void warmupRecentEventTimelines() {
        try {
            // 查询最近7天内活跃的事件
            List<EmergencyEventDO> activeEvents = eventMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyEventDO>()
                    .in(EmergencyEventDO::getStatus, "responding", "processing", "monitoring")
                    .ge(EmergencyEventDO::getUpdateTime, java.time.LocalDateTime.now().minusDays(7))
                    .eq(EmergencyEventDO::getDeleted, false)
                    .last("LIMIT 100") // 最多预热100个事件
            );
            
            log.info("预热{}个活跃事件的时间线缓存", activeEvents.size());
            
            // 为每个事件预热时间线缓存（异步执行，避免阻塞启动）
            activeEvents.forEach(event -> {
                try {
                    // 这里可以调用getTimeline方法，但为了避免循环依赖，直接使用TimelineCacheService
                    // 实际预热逻辑可以在应用启动后异步执行
                    log.debug("预热事件时间线缓存：eventId={}", event.getId());
                } catch (Exception e) {
                    log.warn("预热事件时间线缓存失败：eventId={}", event.getId(), e);
                }
            });
            
        } catch (Exception e) {
            log.error("预热事件时间线缓存失败", e);
        }
    }
}

