package cn.iocoder.yudao.module.emergency.service.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 应急管理系统指标收集服务
 * 
 * @author 芋道源码
 */
@Service
@Slf4j
@ConditionalOnBean(MeterRegistry.class)
public class EmergencyMetricsService {

    @Resource
    private MeterRegistry meterRegistry;

    // ========== 事件相关指标 ==========
    private Counter eventCreatedCounter;
    private Counter eventStatusChangeCounter;
    private Timer eventResponseTimeTimer;
    private AtomicInteger activeEventCount = new AtomicInteger(0);

    // ========== 响应相关指标 ==========
    private Counter responseStartedCounter;
    private Timer responseDurationTimer;
    private AtomicInteger activeResponseCount = new AtomicInteger(0);

    // ========== 任务相关指标 ==========
    private Counter taskCreatedCounter;
    private Counter taskCompletedCounter;
    private Timer taskCompletionTimeTimer;
    private Counter taskTimeoutCounter;
    private AtomicInteger activeTaskCount = new AtomicInteger(0);

    // ========== 指令相关指标 ==========
    private Counter commandIssuedCounter;
    private Timer commandExecutionTimeTimer;

    // ========== API性能指标 ==========
    private Counter apiRequestCounter;
    private Counter apiErrorCounter;
    private Timer apiRequestDurationTimer;

    @PostConstruct
    public void init() {
        // 初始化事件指标
        eventCreatedCounter = Counter.builder("emergency.event.created.total")
                .description("事件创建总数")
                .register(meterRegistry);
        
        eventStatusChangeCounter = Counter.builder("emergency.event.status.changes.total")
                .description("事件状态变更总数")
                .register(meterRegistry);
        
        eventResponseTimeTimer = Timer.builder("emergency.event.response.time")
                .description("事件响应时间（从创建到响应）")
                .register(meterRegistry);
        
        Gauge.builder("emergency.event.active.count", activeEventCount, AtomicInteger::get)
                .description("当前活跃事件数")
                .register(meterRegistry);

        // 初始化响应指标
        responseStartedCounter = Counter.builder("emergency.response.started.total")
                .description("响应启动总数")
                .register(meterRegistry);
        
        responseDurationTimer = Timer.builder("emergency.response.duration")
                .description("响应持续时间")
                .register(meterRegistry);
        
        Gauge.builder("emergency.response.active.count", activeResponseCount, AtomicInteger::get)
                .description("当前活跃响应数")
                .register(meterRegistry);

        // 初始化任务指标
        taskCreatedCounter = Counter.builder("emergency.task.created.total")
                .description("任务创建总数")
                .register(meterRegistry);
        
        taskCompletedCounter = Counter.builder("emergency.task.completed.total")
                .description("任务完成总数")
                .register(meterRegistry);
        
        taskCompletionTimeTimer = Timer.builder("emergency.task.completion.time")
                .description("任务完成时间")
                .register(meterRegistry);
        
        taskTimeoutCounter = Counter.builder("emergency.task.timeout.total")
                .description("任务超时总数")
                .register(meterRegistry);
        
        Gauge.builder("emergency.task.active.count", activeTaskCount, AtomicInteger::get)
                .description("当前活跃任务数")
                .register(meterRegistry);

        // 初始化指令指标
        commandIssuedCounter = Counter.builder("emergency.command.issued.total")
                .description("指令发布总数")
                .register(meterRegistry);
        
        commandExecutionTimeTimer = Timer.builder("emergency.command.execution.time")
                .description("指令执行时间")
                .register(meterRegistry);

        // 初始化API性能指标
        apiRequestCounter = Counter.builder("emergency.api.request.total")
                .description("API请求总数")
                .register(meterRegistry);
        
        apiErrorCounter = Counter.builder("emergency.api.error.total")
                .description("API错误总数")
                .register(meterRegistry);
        
        apiRequestDurationTimer = Timer.builder("emergency.api.request.duration")
                .description("API请求耗时")
                .register(meterRegistry);

        log.info("应急管理系统指标服务初始化完成");
    }

    // ========== 事件指标方法 ==========
    
    /**
     * 记录事件创建
     */
    public void recordEventCreated() {
        eventCreatedCounter.increment();
        activeEventCount.incrementAndGet();
    }

    /**
     * 记录事件状态变更
     */
    public void recordEventStatusChange() {
        eventStatusChangeCounter.increment();
    }

    /**
     * 记录事件响应时间
     */
    public void recordEventResponseTime(long milliseconds) {
        eventResponseTimeTimer.record(milliseconds, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    /**
     * 减少活跃事件数
     */
    public void decrementActiveEventCount() {
        activeEventCount.decrementAndGet();
    }

    // ========== 响应指标方法 ==========
    
    /**
     * 记录响应启动
     */
    public void recordResponseStarted() {
        responseStartedCounter.increment();
        activeResponseCount.incrementAndGet();
    }

    /**
     * 记录响应持续时间
     */
    public void recordResponseDuration(long milliseconds) {
        responseDurationTimer.record(milliseconds, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    /**
     * 减少活跃响应数
     */
    public void decrementActiveResponseCount() {
        activeResponseCount.decrementAndGet();
    }

    // ========== 任务指标方法 ==========
    
    /**
     * 记录任务创建
     */
    public void recordTaskCreated() {
        taskCreatedCounter.increment();
        activeTaskCount.incrementAndGet();
    }

    /**
     * 记录任务完成
     */
    public void recordTaskCompleted() {
        taskCompletedCounter.increment();
        activeTaskCount.decrementAndGet();
    }

    /**
     * 记录任务完成时间
     */
    public void recordTaskCompletionTime(long milliseconds) {
        taskCompletionTimeTimer.record(milliseconds, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    /**
     * 记录任务超时
     */
    public void recordTaskTimeout() {
        taskTimeoutCounter.increment();
    }

    // ========== 指令指标方法 ==========
    
    /**
     * 记录指令发布
     */
    public void recordCommandIssued() {
        commandIssuedCounter.increment();
    }

    /**
     * 记录指令执行时间
     */
    public void recordCommandExecutionTime(long milliseconds) {
        commandExecutionTimeTimer.record(milliseconds, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    // ========== API性能指标方法 ==========
    
    /**
     * 记录API请求
     */
    public void recordApiRequest(String path, String method) {
        Counter.builder("emergency.api.request.total")
                .description("API请求总数")
                .tag("path", path)
                .tag("method", method)
                .register(meterRegistry)
                .increment();
    }

    /**
     * 记录API错误
     */
    public void recordApiError(String path, String method, String status) {
        Counter.builder("emergency.api.error.total")
                .description("API错误总数")
                .tag("path", path)
                .tag("method", method)
                .tag("status", status)
                .register(meterRegistry)
                .increment();
    }

    /**
     * 记录API请求耗时
     */
    public void recordApiRequestDuration(String path, String method, long milliseconds) {
        Timer.builder("emergency.api.request.duration")
                .description("API请求耗时")
                .tag("path", path)
                .tag("method", method)
                .register(meterRegistry)
                .record(milliseconds, java.util.concurrent.TimeUnit.MILLISECONDS);
    }
}

