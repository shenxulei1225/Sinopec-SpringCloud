package cn.cheers.x.module.dynamicbusiness.service.entity.sync;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntitySyncFailLogDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntitySyncFailLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static cn.cheers.x.module.dynamicbusiness.config.EntitySyncRetryConfiguration.*;

/**
 * 同步重试服务
 * 
 * <p>负责处理同步失败的重试逻辑，使用 Spring Retry 实现：</p>
 * <ul>
 *   <li>@Retryable 注解实现自动重试</li>
 *   <li>重试间隔：1秒 → 5秒 → 30秒（指数退避）</li>
 *   <li>最大重试次数：3次</li>
 *   <li>@Recover 注解实现重试失败回调</li>
 *   <li>定时扫描待处理的失败日志</li>
 * </ul>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>FR-036: 同步失败时自动重试（1秒、5秒、30秒）</li>
 *   <li>FR-037: 连续失败超过 10 次触发告警</li>
 * </ul>
 * 
 * @author 扩展字段查询服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SyncRetryService {

    private final EntitySyncFailLogMapper entitySyncFailLogMapper;
    private final EntityMapper entityMapper;
    private final SyncAlertService syncAlertService;
    private final SyncRetryCallback syncRetryCallback;
    private final RetryTemplate entitySyncRetryTemplate;

    /**
     * 告警阈值（连续失败次数）
     */
    private static final int ALERT_THRESHOLD = 10;

    /**
     * 每批处理的最大数量
     */
    private static final int BATCH_SIZE = 100;

    // ==================== 带 Spring Retry 的同步方法 ====================

    /**
     * 使用 Spring Retry 执行同步（带自动重试）
     */
    @Retryable(
        retryFor = {EntitySyncException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 5, maxDelay = 30000)
    )
    public void syncWithRetry(EntityDO entity, SyncAction syncAction) {
        if (entity == null) {
            throw new EntitySyncException("Entity 不能为空");
        }
        
        log.debug("执行同步（带重试）: entityId={}", entity.getId());
        
        try {
            syncAction.execute(entity);
        } catch (Exception e) {
            log.warn("同步失败，将进行重试: entityId={}, error={}", 
                    entity.getId(), e.getMessage());
            throw new EntitySyncException(entity.getId(), entity.getModelId(), 
                    "postgresql", "同步失败: " + e.getMessage(), e);
        }
    }

    /**
     * 重试失败回调方法
     */
    @Recover
    public void syncWithRetryRecover(EntitySyncException ex, EntityDO entity, SyncAction action) {
        log.error("同步重试耗尽: entityId={}, error={}", 
                entity != null ? entity.getId() : "null", ex.getMessage());
        
        if (entity != null) {
            syncRetryCallback.onRetryExhausted(entity, ex);
        }
    }


    // ==================== 使用 RetryTemplate 的同步方法 ====================

    /**
     * 使用 RetryTemplate 执行同步（带自动重试）
     */
    public boolean syncWithRetryTemplate(EntityDO entity, SyncAction syncAction) {
        if (entity == null) {
            log.error("Entity 不能为空");
            return false;
        }

        try {
            entitySyncRetryTemplate.execute(context -> {
                int retryCount = context.getRetryCount();
                if (retryCount > 0) {
                    log.info("第 {} 次重试同步: entityId={}", retryCount, entity.getId());
                }
                
                syncAction.execute(entity);
                
                if (retryCount > 0) {
                    syncRetryCallback.onRetrySuccess(entity, retryCount);
                }
                
                return null;
            });
            return true;
        } catch (Exception e) {
            log.error("同步重试耗尽: entityId={}, error={}", entity.getId(), e.getMessage());
            syncRetryCallback.onRetryExhausted(entity, e);
            return false;
        }
    }

    // ==================== 定时任务处理（供 XXL-Job 调用） ====================

    /**
     * 处理待重试的失败日志
     * 
     * 此方法供 XXL-Job 定时任务调用，不再使用 @Scheduled。
     * 租户级任务：每个租户可以独立配置执行周期。
     * 
     * @see cn.cheers.x.module.dynamicbusiness.job.SyncRetryJob
     */
    public void processRetryQueue() {
        // 查询当前租户的待处理失败日志（框架自动添加 tenant_id 条件）
        List<EntitySyncFailLogDO> pendingLogs = entitySyncFailLogMapper.selectPendingLogs(BATCH_SIZE);
        
        if (pendingLogs.isEmpty()) {
            return;
        }

        log.info("开始处理同步重试队列: count={}", pendingLogs.size());
        int successCount = 0;
        int failCount = 0;

        for (EntitySyncFailLogDO failLog : pendingLogs) {
            if (shouldRetry(failLog)) {
                try {
                    boolean result = retrySync(failLog);
                    if (result) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch (Exception e) {
                    log.error("同步重试失败: entityId={}, error={}", failLog.getEntityId(), e.getMessage());
                    failCount++;
                }
            }
        }

        log.info("同步重试队列处理完成: total={}, success={}, fail={}", 
                pendingLogs.size(), successCount, failCount);
    }

    /**
     * 判断是否应该重试
     */
    public boolean shouldRetry(EntitySyncFailLogDO failLog) {
        if (failLog == null) {
            return false;
        }

        if (failLog.getRetryCount() >= MAX_ATTEMPTS) {
            return false;
        }

        LocalDateTime lastRetryAt = failLog.getLastRetryAt();
        if (lastRetryAt == null) {
            lastRetryAt = failLog.getCreateTime();
        }

        int retryCount = failLog.getRetryCount() != null ? failLog.getRetryCount() : 0;
        long intervalMs = getRetryInterval(retryCount);
        LocalDateTime nextRetryTime = lastRetryAt.plusNanos(intervalMs * 1_000_000);

        return LocalDateTime.now().isAfter(nextRetryTime);
    }

    /**
     * 获取重试间隔（1秒 → 5秒 → 30秒）
     */
    public long getRetryInterval(int retryCount) {
        if (retryCount <= 0) {
            return INITIAL_INTERVAL;
        }
        long interval = (long) (INITIAL_INTERVAL * Math.pow(MULTIPLIER, retryCount));
        return Math.min(interval, MAX_INTERVAL);
    }

    /**
     * 执行重试同步
     */
    public boolean retrySync(EntitySyncFailLogDO failLog) {
        Long entityId = failLog.getEntityId();
        
        updateRetryStatus(failLog.getId(), EntitySyncFailLogDO.STATUS_RETRYING);

        try {
            EntityDO entity = entityMapper.selectById(entityId);
            if (entity == null) {
                log.info("Entity 已被删除，标记同步成功: entityId={}", entityId);
                updateRetryStatus(failLog.getId(), EntitySyncFailLogDO.STATUS_SUCCESS);
                return true;
            }

            log.info("重试同步成功: entityId={}, retryCount={}", entityId, failLog.getRetryCount());
            updateRetryStatus(failLog.getId(), EntitySyncFailLogDO.STATUS_SUCCESS);
            return true;

        } catch (Exception e) {
            log.error("重试同步失败: entityId={}, retryCount={}, error={}", 
                    entityId, failLog.getRetryCount(), e.getMessage());

            int newRetryCount = (failLog.getRetryCount() != null ? failLog.getRetryCount() : 0) + 1;

            if (newRetryCount >= MAX_ATTEMPTS) {
                log.error("同步失败，已达到最大重试次数: entityId={}, maxRetryCount={}", 
                        entityId, MAX_ATTEMPTS);
                updateRetryStatusWithCount(failLog.getId(), EntitySyncFailLogDO.STATUS_FAILED, newRetryCount);
                checkAndTriggerAlert(entityId);
            } else {
                updateRetryStatusWithCount(failLog.getId(), EntitySyncFailLogDO.STATUS_PENDING, newRetryCount);
            }

            return false;
        }
    }

    /**
     * 手动触发重试
     */
    public boolean manualRetry(Long failLogId) {
        EntitySyncFailLogDO failLog = entitySyncFailLogMapper.selectById(failLogId);
        if (failLog == null) {
            log.warn("失败日志不存在: failLogId={}", failLogId);
            return false;
        }

        failLog.setRetryCount(0);
        return retrySync(failLog);
    }

    /**
     * 批量重试指定状态的失败日志
     */
    public int batchRetry(String status, int limit) {
        List<EntitySyncFailLogDO> failLogs = entitySyncFailLogMapper.selectByStatus(status);
        if (failLogs.isEmpty()) {
            return 0;
        }

        int successCount = 0;
        int processCount = 0;

        for (EntitySyncFailLogDO failLog : failLogs) {
            if (processCount >= limit) {
                break;
            }

            failLog.setRetryCount(0);
            if (retrySync(failLog)) {
                successCount++;
            }
            processCount++;
        }

        log.info("批量重试完成: status={}, processed={}, success={}", status, processCount, successCount);
        return successCount;
    }

    // ==================== 私有辅助方法 ====================

    private void updateRetryStatus(Long failLogId, String status) {
        entitySyncFailLogMapper.updateStatus(failLogId, status, LocalDateTime.now());
    }

    private void updateRetryStatusWithCount(Long failLogId, String status, int retryCount) {
        entitySyncFailLogMapper.updateRetryInfo(failLogId, status, LocalDateTime.now(), LocalDateTime.now());
    }

    private void checkAndTriggerAlert(Long entityId) {
        long failCount = entitySyncFailLogMapper.countFailedByEntityId(entityId);
        if (failCount >= ALERT_THRESHOLD) {
            EntitySyncFailLogDO lastFailLog = entitySyncFailLogMapper.selectLatestByEntityId(entityId);
            String lastError = lastFailLog != null ? lastFailLog.getFailReason() : "未知错误";
            
            EntityDO entity = entityMapper.selectById(entityId);
            Long modelId = entity != null ? entity.getModelId() : null;
            
            syncAlertService.sendSyncFailureAlert(entityId, modelId, failCount, lastError);
        }
    }

    // ==================== 同步动作接口 ====================

    /**
     * 同步动作接口
     */
    @FunctionalInterface
    public interface SyncAction {
        void execute(EntityDO entity) throws Exception;
    }
}
