package cn.iocoder.yudao.module.emergency.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务配置
 * 
 * 用于支持@Async注解的异步方法执行
 * 主要用于指令删除级联处理等异步场景
 *
 * @author 芋道源码
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

    /**
     * 异步任务执行器
     * 用于处理指令删除级联等异步任务
     */
    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(5);
        // 最大线程数
        executor.setMaxPoolSize(10);
        // 队列容量
        executor.setQueueCapacity(100);
        // 线程名前缀
        executor.setThreadNamePrefix("emergency-async-");
        // 拒绝策略：调用者运行策略（如果线程池已满，由调用线程执行任务）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        log.info("[AsyncConfig][异步任务执行器初始化完成] corePoolSize=5, maxPoolSize=10, queueCapacity=100");
        return executor;
    }
}






