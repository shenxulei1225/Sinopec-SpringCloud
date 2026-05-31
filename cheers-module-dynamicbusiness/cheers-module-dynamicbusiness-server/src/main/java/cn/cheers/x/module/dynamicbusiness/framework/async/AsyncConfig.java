package cn.cheers.x.module.dynamicbusiness.framework.async;

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
 * 用于支持 @Async 注解的异步方法执行
 * 主要用于计算字段预计算等异步场景
 *
 * @author yudao
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

    /**
     * 计算字段预计算执行器
     * 用于处理计算字段的异步预计算任务
     */
    @Bean(name = "precomputeExecutor")
    public Executor precomputeExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(2);
        // 最大线程数
        executor.setMaxPoolSize(5);
        // 队列容量
        executor.setQueueCapacity(200);
        // 线程名前缀
        executor.setThreadNamePrefix("precompute-");
        // 拒绝策略：调用者运行策略（如果线程池已满，由调用线程执行任务）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        log.info("[AsyncConfig][预计算执行器初始化完成] corePoolSize=2, maxPoolSize=5, queueCapacity=200");
        return executor;
    }

    /**
     * 通用异步任务执行器
     * 用于处理其他异步任务
     */
    @Bean(name = "systemAsyncExecutor")
    public Executor systemAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(5);
        // 最大线程数
        executor.setMaxPoolSize(10);
        // 队列容量
        executor.setQueueCapacity(100);
        // 线程名前缀
        executor.setThreadNamePrefix("system-async-");
        // 拒绝策略：调用者运行策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        log.info("[AsyncConfig][系统异步执行器初始化完成] corePoolSize=5, maxPoolSize=10, queueCapacity=100");
        return executor;
    }

    /**
     * Entity 同步执行器
     * 用于处理 Entity 数据同步到查询索引的异步任务
     * 
     * 配置说明：
     * - 核心线程数 3：保证基本的同步吞吐量
     * - 最大线程数 8：应对突发的同步请求
     * - 队列容量 500：缓冲大量的同步任务
     * - 拒绝策略：CallerRunsPolicy，确保同步任务不会丢失
     */
    @Bean(name = "entitySyncExecutor")
    public Executor entitySyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(3);
        // 最大线程数
        executor.setMaxPoolSize(8);
        // 队列容量
        executor.setQueueCapacity(500);
        // 线程名前缀
        executor.setThreadNamePrefix("entity-sync-");
        // 拒绝策略：调用者运行策略（确保同步任务不会丢失）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间
        executor.setAwaitTerminationSeconds(120);
        executor.initialize();
        log.info("[AsyncConfig][Entity同步执行器初始化完成] corePoolSize=3, maxPoolSize=8, queueCapacity=500");
        return executor;
    }
}
