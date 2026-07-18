package cn.cheers.x.yudao.module.scene.platform.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 帧调度器 - 定时触发帧聚合
 * 
 * 每帧（默认 16ms = 60fps）调用 FrameAggregator.onFrameTick()
 * 将一帧内的所有变化批量推送给前端
 */
@Slf4j
@Component
public class FrameScheduler {

    @Autowired
    private FrameAggregator frameAggregator;

    /**
     * 每帧触发 - 使用 @Scheduled 定时调用
     * 
     * fixedRate = 16 表示每 16ms 执行一次（60fps）
     */
    @Scheduled(fixedRate = 16)
    public void onFrameTick() {
        try {
            frameAggregator.onFrameTick();
        } catch (Exception e) {
            log.error("帧调度执行异常", e);
        }
    }
}
