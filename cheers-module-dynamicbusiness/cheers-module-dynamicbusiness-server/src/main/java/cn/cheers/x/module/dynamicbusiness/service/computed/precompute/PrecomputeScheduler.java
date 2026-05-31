package cn.cheers.x.module.dynamicbusiness.service.computed.precompute;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.computed.ComputedFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.computed.ComputedFieldMapper;
import cn.cheers.x.module.dynamicbusiness.service.computed.cache.ComputedFieldCacheService;
import cn.cheers.x.module.dynamicbusiness.service.computed.executor.ComputedFieldExecutor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 预计算调度器
 * 
 * 负责管理 PRECOMPUTED 策略的计算字段：
 * - 定时预计算任务
 * - 数据变化触发重算
 * - 批量预计算
 * - 预计算队列管理
 * 
 * @author yudao
 */
@Service
@Slf4j
public class PrecomputeScheduler {

    /** 预计算任务队列 */
    private final BlockingQueue<PrecomputeTask> taskQueue = new LinkedBlockingQueue<>(10000);
    
    /** 正在处理的任务集合（用于去重） */
    private final Set<String> processingTasks = ConcurrentHashMap.newKeySet();
    
    /** 调度器是否运行中 */
    private final AtomicBoolean running = new AtomicBoolean(true);
    
    /** 预计算统计 */
    private final PrecomputeStatistics statistics = new PrecomputeStatistics();
    
    /** 默认预计算 TTL（分钟） */
    private static final int DEFAULT_PRECOMPUTE_TTL_MINUTES = 60;
    
    /** 批量处理大小 */
    private static final int BATCH_SIZE = 100;

    @Resource
    private ComputedFieldMapper computedFieldMapper;

    @Resource
    private ComputedFieldExecutor computedFieldExecutor;

    @Resource
    private ComputedFieldCacheService cacheService;

    /**
     * 提交预计算任务
     * 
     * @param modelId Model ID
     * @param entityId 实体 ID
     * @param priority 优先级（数字越小优先级越高）
     * @return 是否成功提交
     */
    public boolean submitTask(Long modelId, Long entityId, int priority) {
        String taskKey = buildTaskKey(modelId, entityId);
        
        // 检查是否已在处理中
        if (processingTasks.contains(taskKey)) {
            log.debug("[submitTask][任务已在处理中，跳过，modelId={}, entityId={}]", modelId, entityId);
            return false;
        }
        
        PrecomputeTask task = new PrecomputeTask(modelId, entityId, priority, System.currentTimeMillis());
        boolean offered = taskQueue.offer(task);
        
        if (offered) {
            statistics.incrementSubmitted();
            log.debug("[submitTask][预计算任务已提交，modelId={}, entityId={}, 队列大小={}]", 
                    modelId, entityId, taskQueue.size());
        } else {
            statistics.incrementRejected();
            log.warn("[submitTask][预计算任务队列已满，任务被拒绝，modelId={}, entityId={}]", 
                    modelId, entityId);
        }
        
        return offered;
    }
    
    /**
     * 提交高优先级预计算任务（数据变化触发）
     * 
     * @param modelId Model ID
     * @param entityId 实体 ID
     * @return 是否成功提交
     */
    public boolean submitHighPriorityTask(Long modelId, Long entityId) {
        return submitTask(modelId, entityId, 0);
    }
    
    /**
     * 提交普通优先级预计算任务
     * 
     * @param modelId Model ID
     * @param entityId 实体 ID
     * @return 是否成功提交
     */
    public boolean submitNormalTask(Long modelId, Long entityId) {
        return submitTask(modelId, entityId, 10);
    }
    
    /**
     * 批量提交预计算任务
     * 
     * @param modelId Model ID
     * @param entityIds 实体 ID 列表
     * @param priority 优先级
     * @return 成功提交的数量
     */
    public int submitBatchTasks(Long modelId, List<Long> entityIds, int priority) {
        if (CollectionUtils.isEmpty(entityIds)) {
            return 0;
        }
        
        int submitted = 0;
        for (Long entityId : entityIds) {
            if (submitTask(modelId, entityId, priority)) {
                submitted++;
            }
        }
        
        log.info("[submitBatchTasks][批量提交预计算任务，modelId={}, 请求数={}, 成功数={}]", 
                modelId, entityIds.size(), submitted);
        return submitted;
    }

    /**
     * 处理预计算任务队列
     * 
     * 定时任务，每 5 秒执行一次
     */
    @Scheduled(fixedDelay = 5000)
    public void processTaskQueue() {
        if (!running.get()) {
            return;
        }
        
        List<PrecomputeTask> batch = new ArrayList<>();
        taskQueue.drainTo(batch, BATCH_SIZE);
        
        if (batch.isEmpty()) {
            return;
        }
        
        // 按优先级排序
        batch.sort(Comparator.comparingInt(PrecomputeTask::priority));
        
        log.debug("[processTaskQueue][开始处理预计算任务，数量={}]", batch.size());
        
        for (PrecomputeTask task : batch) {
            processTask(task);
        }
    }
    
    /**
     * 处理单个预计算任务
     */
    private void processTask(PrecomputeTask task) {
        String taskKey = buildTaskKey(task.modelId(), task.entityId());
        
        // 标记为处理中
        if (!processingTasks.add(taskKey)) {
            log.debug("[processTask][任务已在处理中，跳过，taskKey={}]", taskKey);
            return;
        }
        
        try {
            // 获取该 Model 的所有预计算字段
            List<ComputedFieldDO> precomputedFields = computedFieldMapper.selectByModelId(task.modelId())
                    .stream()
                    .filter(ComputedFieldDO::isPrecomputed)
                    .collect(Collectors.toList());
            
            if (CollectionUtils.isEmpty(precomputedFields)) {
                return;
            }
            
            // 执行预计算
            int successCount = 0;
            for (ComputedFieldDO field : precomputedFields) {
                try {
                    Object value = computedFieldExecutor.execute(task.entityId(), field, Collections.emptyMap());
                    if (value != null) {
                        int ttl = field.getCacheTtlMinutes() != null ? 
                                field.getCacheTtlMinutes() * 10 : DEFAULT_PRECOMPUTE_TTL_MINUTES;
                        cacheService.setPrecomputedValue(task.modelId(), field.getFieldCode(), 
                                task.entityId(), value, ttl);
                        successCount++;
                    }
                } catch (Exception e) {
                    log.error("[processTask][预计算失败，fieldCode={}, entityId={}]", 
                            field.getFieldCode(), task.entityId(), e);
                    statistics.incrementFailed();
                }
            }
            
            statistics.incrementCompleted();
            log.debug("[processTask][预计算任务完成，modelId={}, entityId={}, 字段数={}, 成功数={}]", 
                    task.modelId(), task.entityId(), precomputedFields.size(), successCount);
            
        } finally {
            processingTasks.remove(taskKey);
        }
    }
    
    /**
     * 立即执行预计算（同步）
     * 
     * @param modelId Model ID
     * @param entityId 实体 ID
     * @return 预计算结果
     */
    public Map<String, Object> executeImmediately(Long modelId, Long entityId) {
        List<ComputedFieldDO> precomputedFields = computedFieldMapper.selectByModelId(modelId)
                .stream()
                .filter(ComputedFieldDO::isPrecomputed)
                .collect(Collectors.toList());
        
        if (CollectionUtils.isEmpty(precomputedFields)) {
            return Collections.emptyMap();
        }
        
        Map<String, Object> results = new HashMap<>();
        
        for (ComputedFieldDO field : precomputedFields) {
            try {
                Object value = computedFieldExecutor.execute(entityId, field, Collections.emptyMap());
                if (value != null) {
                    int ttl = field.getCacheTtlMinutes() != null ? 
                            field.getCacheTtlMinutes() * 10 : DEFAULT_PRECOMPUTE_TTL_MINUTES;
                    cacheService.setPrecomputedValue(modelId, field.getFieldCode(), entityId, value, ttl);
                    results.put(field.getFieldCode(), value);
                }
            } catch (Exception e) {
                log.error("[executeImmediately][预计算失败，fieldCode={}, entityId={}]", 
                        field.getFieldCode(), entityId, e);
            }
        }
        
        return results;
    }
    
    /**
     * 异步执行预计算
     * 
     * @param modelId Model ID
     * @param entityId 实体 ID
     * @return CompletableFuture
     */
    @Async
    public CompletableFuture<Map<String, Object>> executeAsync(Long modelId, Long entityId) {
        return CompletableFuture.completedFuture(executeImmediately(modelId, entityId));
    }
    
    /**
     * 数据变化触发重算
     * 
     * 当实体数据发生变化时调用此方法
     * 
     * @param modelId Model ID
     * @param entityId 实体 ID
     */
    public void onDataChanged(Long modelId, Long entityId) {
        // 1. 使现有缓存失效
        cacheService.invalidateCache(modelId, entityId, false);
        
        // 2. 提交高优先级预计算任务
        submitHighPriorityTask(modelId, entityId);
        
        log.info("[onDataChanged][数据变化触发预计算，modelId={}, entityId={}]", modelId, entityId);
    }
    
    /**
     * 批量数据变化触发重算
     * 
     * @param modelId Model ID
     * @param entityIds 实体 ID 列表
     */
    public void onBatchDataChanged(Long modelId, List<Long> entityIds) {
        if (CollectionUtils.isEmpty(entityIds)) {
            return;
        }
        
        // 1. 批量使缓存失效
        for (Long entityId : entityIds) {
            cacheService.invalidateCache(modelId, entityId, false);
        }
        
        // 2. 批量提交高优先级预计算任务
        submitBatchTasks(modelId, entityIds, 0);
        
        log.info("[onBatchDataChanged][批量数据变化触发预计算，modelId={}, 实体数={}]", 
                modelId, entityIds.size());
    }
    
    /**
     * 获取预计算统计信息
     * 
     * @return 统计信息
     */
    public PrecomputeStatisticsVO getStatistics() {
        return new PrecomputeStatisticsVO(
                statistics.getSubmitted(),
                statistics.getCompleted(),
                statistics.getFailed(),
                statistics.getRejected(),
                taskQueue.size(),
                processingTasks.size()
        );
    }
    
    /**
     * 清空任务队列
     * 
     * @return 清除的任务数量
     */
    public int clearTaskQueue() {
        int size = taskQueue.size();
        taskQueue.clear();
        log.info("[clearTaskQueue][清空预计算任务队列，清除数量={}]", size);
        return size;
    }
    
    /**
     * 暂停调度器
     */
    public void pause() {
        running.set(false);
        log.info("[pause][预计算调度器已暂停]");
    }
    
    /**
     * 恢复调度器
     */
    public void resume() {
        running.set(true);
        log.info("[resume][预计算调度器已恢复]");
    }
    
    /**
     * 检查调度器是否运行中
     */
    public boolean isRunning() {
        return running.get();
    }
    
    private String buildTaskKey(Long modelId, Long entityId) {
        return modelId + ":" + entityId;
    }
    
    /**
     * 预计算任务
     */
    public record PrecomputeTask(
            Long modelId,
            Long entityId,
            int priority,
            long submitTime
    ) {}
    
    /**
     * 预计算统计
     */
    private static class PrecomputeStatistics {
        private final AtomicInteger submitted = new AtomicInteger(0);
        private final AtomicInteger completed = new AtomicInteger(0);
        private final AtomicInteger failed = new AtomicInteger(0);
        private final AtomicInteger rejected = new AtomicInteger(0);
        
        void incrementSubmitted() { submitted.incrementAndGet(); }
        void incrementCompleted() { completed.incrementAndGet(); }
        void incrementFailed() { failed.incrementAndGet(); }
        void incrementRejected() { rejected.incrementAndGet(); }
        
        int getSubmitted() { return submitted.get(); }
        int getCompleted() { return completed.get(); }
        int getFailed() { return failed.get(); }
        int getRejected() { return rejected.get(); }
    }
    
    /**
     * 预计算统计 VO
     */
    public record PrecomputeStatisticsVO(
            int submitted,
            int completed,
            int failed,
            int rejected,
            int queueSize,
            int processingCount
    ) {}
}
