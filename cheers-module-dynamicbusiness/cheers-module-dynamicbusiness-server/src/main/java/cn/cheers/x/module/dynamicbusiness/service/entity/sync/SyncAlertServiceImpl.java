package cn.cheers.x.module.dynamicbusiness.service.entity.sync;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntitySyncFailLogDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntitySyncFailLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 同步告警服务实现
 * 
 * <p>当前实现为日志告警，后续可扩展为：</p>
 * <ul>
 *   <li>邮件通知</li>
 *   <li>短信通知</li>
 *   <li>钉钉/企业微信通知</li>
 *   <li>监控系统集成（如 Prometheus AlertManager）</li>
 * </ul>
 * 
 * <h3>告警去重机制</h3>
 * <p>在指定时间窗口内（默认 5 分钟），同一 Entity 只发送一次告警，避免告警风暴。</p>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>FR-037: 连续失败超过 10 次触发告警</li>
 *   <li>FR-043: 同步失败记录到 dynamic_entity_sync_fail_log 表</li>
 * </ul>
 * 
 * @author 扩展字段查询服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SyncAlertServiceImpl implements SyncAlertService {

    private final EntitySyncFailLogMapper entitySyncFailLogMapper;

    /**
     * 告警日志前缀
     */
    private static final String ALERT_PREFIX = "【Entity同步告警】";

    /**
     * 告警去重时间窗口（毫秒）- 5 分钟
     */
    private static final long ALERT_DEDUP_WINDOW_MS = 5 * 60 * 1000;

    /**
     * 告警去重缓存：entityId -> 最后告警时间
     */
    private final Map<Long, Long> alertDeduplicationCache = new ConcurrentHashMap<>();

    /**
     * 告警统计计数器
     */
    private final AtomicLong totalAlertCount = new AtomicLong(0);
    private final AtomicLong warningAlertCount = new AtomicLong(0);
    private final AtomicLong errorAlertCount = new AtomicLong(0);
    private final AtomicLong criticalAlertCount = new AtomicLong(0);

    @Override
    public void sendSyncFailureAlert(Long entityId, Long modelId, long failCount, String lastError) {
        // 检查是否应该发送告警（防重复）
        if (!shouldSendAlert(entityId)) {
            log.debug("告警被去重，跳过发送: entityId={}, failCount={}", entityId, failCount);
            return;
        }

        // 获取告警级别
        String alertLevel = getAlertLevel(failCount);

        // 构建告警消息
        String message = String.format(
                "%s [%s] Entity 同步连续失败 - entityId=%d, modelId=%d, failCount=%d, lastError=%s",
                ALERT_PREFIX, alertLevel, entityId, modelId, failCount, truncateError(lastError));
        
        // 根据告警级别使用不同的日志级别
        switch (alertLevel) {
            case ALERT_LEVEL_CRITICAL:
                log.error(message);
                criticalAlertCount.incrementAndGet();
                break;
            case ALERT_LEVEL_ERROR:
                log.error(message);
                errorAlertCount.incrementAndGet();
                break;
            case ALERT_LEVEL_WARNING:
            default:
                log.warn(message);
                warningAlertCount.incrementAndGet();
                break;
        }

        // 更新统计
        totalAlertCount.incrementAndGet();

        // 记录告警时间（用于去重）
        alertDeduplicationCache.put(entityId, System.currentTimeMillis());
        
        // TODO: 集成外部告警系统
        // 1. 邮件通知
        // mailService.sendAlert("Entity同步失败告警", message);
        
        // 2. 钉钉/企业微信通知
        // webhookService.sendAlert(message);
        
        // 3. 监控系统
        // metricsService.incrementCounter("entity_sync_failure_alert", 
        //     "entity_id", entityId.toString(), 
        //     "model_id", modelId.toString(),
        //     "level", alertLevel);
    }

    @Override
    public void sendBatchSyncFailureAlert(Long modelId, int failCount, int totalCount) {
        double failRate = totalCount > 0 ? (failCount * 100.0 / totalCount) : 0;
        
        // 根据失败率确定告警级别
        String alertLevel;
        if (failRate >= 50) {
            alertLevel = ALERT_LEVEL_CRITICAL;
            criticalAlertCount.incrementAndGet();
        } else if (failRate >= 20) {
            alertLevel = ALERT_LEVEL_ERROR;
            errorAlertCount.incrementAndGet();
        } else {
            alertLevel = ALERT_LEVEL_WARNING;
            warningAlertCount.incrementAndGet();
        }

        String message = String.format(
                "%s [%s] 批量同步失败 - modelId=%d, failCount=%d, totalCount=%d, failRate=%.2f%%",
                ALERT_PREFIX, alertLevel, modelId, failCount, totalCount, failRate);
        
        if (alertLevel.equals(ALERT_LEVEL_CRITICAL) || alertLevel.equals(ALERT_LEVEL_ERROR)) {
            log.error(message);
        } else {
            log.warn(message);
        }

        totalAlertCount.incrementAndGet();
        
        // TODO: 集成外部告警系统
    }

    @Override
    public void sendIndexRebuildFailureAlert(Long modelId, String errorMessage) {
        String message = String.format(
                "%s [%s] 索引重建失败 - modelId=%d, error=%s",
                ALERT_PREFIX, ALERT_LEVEL_ERROR, modelId, truncateError(errorMessage));
        
        log.error(message);
        errorAlertCount.incrementAndGet();
        totalAlertCount.incrementAndGet();
        
        // TODO: 集成外部告警系统
    }

    @Override
    public boolean shouldSendAlert(Long entityId) {
        if (entityId == null) {
            return false;
        }

        Long lastAlertTime = alertDeduplicationCache.get(entityId);
        if (lastAlertTime == null) {
            return true;
        }

        // 检查是否超过去重时间窗口
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAlertTime > ALERT_DEDUP_WINDOW_MS) {
            // 清理过期的缓存条目
            alertDeduplicationCache.remove(entityId);
            return true;
        }

        return false;
    }

    @Override
    public String getAlertLevel(long failCount) {
        if (failCount >= THRESHOLD_CRITICAL) {
            return ALERT_LEVEL_CRITICAL;
        } else if (failCount >= THRESHOLD_ERROR) {
            return ALERT_LEVEL_ERROR;
        } else {
            return ALERT_LEVEL_WARNING;
        }
    }

    @Override
    public AlertStatistics getAlertStatistics() {
        long pendingFailLogs = 0;
        try {
            pendingFailLogs = entitySyncFailLogMapper.countByStatus(EntitySyncFailLogDO.STATUS_PENDING);
        } catch (Exception e) {
            log.warn("获取待处理失败日志数量失败: {}", e.getMessage());
        }

        return new AlertStatistics(
                totalAlertCount.get(),
                warningAlertCount.get(),
                errorAlertCount.get(),
                criticalAlertCount.get(),
                pendingFailLogs
        );
    }

    /**
     * 截断过长的错误信息
     */
    private String truncateError(String error) {
        if (error == null) {
            return "未知错误";
        }
        if (error.length() > 500) {
            return error.substring(0, 500) + "...";
        }
        return error;
    }

    /**
     * 清理过期的去重缓存（可由定时任务调用）
     */
    public void cleanupDeduplicationCache() {
        long currentTime = System.currentTimeMillis();
        alertDeduplicationCache.entrySet().removeIf(
                entry -> currentTime - entry.getValue() > ALERT_DEDUP_WINDOW_MS
        );
        log.debug("清理告警去重缓存完成，当前缓存大小: {}", alertDeduplicationCache.size());
    }

    /**
     * 重置告警统计（用于测试）
     */
    public void resetStatistics() {
        totalAlertCount.set(0);
        warningAlertCount.set(0);
        errorAlertCount.set(0);
        criticalAlertCount.set(0);
        alertDeduplicationCache.clear();
    }
}
