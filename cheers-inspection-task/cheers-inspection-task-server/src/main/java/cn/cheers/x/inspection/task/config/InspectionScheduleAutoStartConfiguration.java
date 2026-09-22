package cn.cheers.x.inspection.task.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 启用巡检排期到点自动开跑：轮询兜底 + 一次性 TaskScheduler。
 */
@Configuration
@EnableScheduling
public class InspectionScheduleAutoStartConfiguration {

    @Bean(name = "inspectionReservationTaskScheduler")
    public TaskScheduler inspectionReservationTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(4);
        scheduler.setThreadNamePrefix("patrol-reservation-auto-start-");
        scheduler.initialize();
        return scheduler;
    }
}
