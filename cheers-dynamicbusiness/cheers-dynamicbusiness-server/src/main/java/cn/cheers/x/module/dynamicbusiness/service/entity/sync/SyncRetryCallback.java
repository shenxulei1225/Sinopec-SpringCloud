package cn.cheers.x.module.dynamicbusiness.service.entity.sync;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntitySyncFailLogDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntitySyncFailLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static cn.cheers.x.module.dynamicbusiness.config.EntitySyncRetryConfiguration.MAX_ATTEMPTS;

/**
 * 同步重试失败回调处理器
 * 
 * <p>当同步重试达到最大次数后仍然失败时，执行以下操作：</p>
 * <ul>
 *   <li>记录失败日志到 entity_sync_fail_log 表</li>
 *   <li>检查是否需要触发告警（连续失败超过 10 次）</li>
 * </ul>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>FR-036: 同步失败时自动重试（1秒、5秒、30秒）</li>
 *   <li>FR-037: 连续失败超过 10 次触发告警</li>
 *   <li>FR-043: 同步失败记录到 dynamic_entity_sync_fail_log 表</li>
 * </ul>
 * 
 * @author 扩展字段查询服务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SyncRetryCallback {

    private final EntitySyncFailLogMapper entitySyncFailLogMapper;
    private final SyncAlertService syncAlertService;

    /**
     * 引擎类型常量
     */
    private static final String ENGINE_TYPE = "postgresql";

    /**
     * 告警阈值（连续失败次数）
     */
    private static final int ALERT_THRESHOLD = 10;

    /**
     * 重试失败后的回调处理
     * 
     * <p>当同步重试达到最大次数后仍然失败时调用此方法。</p>
     * 
     * @param entity 同步失败的 Entity
     * @param exception 最后一次失败的异常
     */
    public void onRetryExhausted(EntityDO entity, Throwable exception) {
        if (entity == null) {
            log.error("重试失败回调：Entity 为空");
            return;
        }

        Long entityId = entity.getId();
        Long modelId = entity.getModelId();
        String failReason = exception != null ? exception.getMessage() : "未知错误";

        log.error("Entity 同步重试耗尽，记录失败日志: entityId={}, modelId={}, error={}", 
                entityId, modelId, failReason);

        // 1. 记录失败日志
        recordSyncFailure(entity, failReason);

        // 2. 检查是否需要告警
        checkAndTriggerAlert(entityId, modelId, failReason);
    }

    /**
     * 重试成功后的回调处理
     * 
     * <p>当同步在重试后成功时调用此方法，用于清理之前的失败记录。</p>
     * 
     * @param entity 同步成功的 Entity
     * @param retryCount 重试次数
     */
    public void onRetrySuccess(EntityDO entity, int retryCount) {
        if (entity == null) {
            return;
        }

        Long entityId = entity.getId();
        log.info("Entity 同步重试成功: entityId={}, retryCount={}", entityId, retryCount);

        // 清理之前的失败记录（如果有）
        try {
            entitySyncFailLogMapper.deleteByEntityId(entityId);
        } catch (Exception e) {
            log.warn("清理失败记录时出错: entityId={}, error={}", entityId, e.getMessage());
        }
    }

    /**
     * 记录同步失败日志
     */
    private void recordSyncFailure(EntityDO entity, String failReason) {
        try {
            EntitySyncFailLogDO failLog = EntitySyncFailLogDO.builder()
                    .entityId(entity.getId())
                    .modelId(entity.getModelId())
                    .engineType(ENGINE_TYPE)
                    .failReason(truncateFailReason(failReason))
                    .retryCount(MAX_ATTEMPTS)
                    .status(EntitySyncFailLogDO.STATUS_FAILED)
                    .lastRetryAt(LocalDateTime.now())
                    .build();

            entitySyncFailLogMapper.insert(failLog);
            log.debug("记录同步失败日志成功: entityId={}", entity.getId());

        } catch (Exception e) {
            log.error("记录同步失败日志失败: entityId={}, error={}", entity.getId(), e.getMessage());
        }
    }

    /**
     * 检查并触发告警
     */
    private void checkAndTriggerAlert(Long entityId, Long modelId, String lastError) {
        try {
            long failCount = entitySyncFailLogMapper.countFailedByEntityId(entityId);
            if (failCount >= ALERT_THRESHOLD) {
                syncAlertService.sendSyncFailureAlert(entityId, modelId, failCount, lastError);
            }
        } catch (Exception e) {
            log.error("检查告警条件时出错: entityId={}, error={}", entityId, e.getMessage());
        }
    }

    /**
     * 截断过长的失败原因
     */
    private String truncateFailReason(String failReason) {
        if (failReason == null) {
            return "未知错误";
        }
        if (failReason.length() > 2000) {
            return failReason.substring(0, 2000) + "...";
        }
        return failReason;
    }
}
