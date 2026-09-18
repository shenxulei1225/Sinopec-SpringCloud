package cn.cheers.x.module.dynamicbusiness.service.entity.sync;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityFieldIndexDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntitySyncFailLogDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityFieldIndexMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntitySyncFailLogMapper;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.service.entity.index.EntityFieldIndexRecordBuilder;
import cn.cheers.x.module.dynamicbusiness.service.entity.index.ExtensionFieldIndexEligibility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Entity 数据同步服务实现
 * 
 * <p>负责将 Entity 数据同步到查询索引（entity_field_index 表）。</p>
 * 
 * <h3>同步策略</h3>
 * <ul>
 *   <li>同步可搜索 / 可筛选 / 可排序任一为真的扩展字段</li>
 *   <li>根据字段类型存储到对应的值列（value_string、value_number、value_date 等）</li>
 *   <li>同一实体：先删后按批插入；型号级重建走字段索引服务的批量写入</li>
 *   <li>使用 Spring Retry 实现自动重试（1秒、5秒、30秒）</li>
 * </ul>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>FR-034: Entity 保存时自动同步到查询索引</li>
 *   <li>FR-035: 异步同步，不阻塞主业务流程</li>
 *   <li>FR-036: 同步失败时自动重试（1秒、5秒、30秒）</li>
 *   <li>FR-037: 连续失败超过 10 次触发告警</li>
 * </ul>
 * 
 * @author 扩展字段查询服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EntitySyncServiceImpl implements EntitySyncService {

    private final EntityRepository entityRepository;
    private final EntityFieldIndexMapper entityFieldIndexMapper;
    private final EntitySyncFailLogMapper entitySyncFailLogMapper;
    private final FieldMapper fieldMapper;
    private final ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    private final SyncAlertService syncAlertService;

    /**
     * 引擎类型常量
     */
    private static final String ENGINE_TYPE = "postgresql";

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY_COUNT = 3;

    /**
     * 告警阈值（连续失败次数）
     */
    private static final int ALERT_THRESHOLD = 10;

    // ==================== 核心同步方法（带 Spring Retry） ====================

    @Override
    @Async("entitySyncExecutor")
    public void syncEntityAsync(EntityDO entity) {
        log.debug("开始异步同步 Entity: entityId={}, modelId={}", entity.getId(), entity.getModelId());
        try {
            // 使用带重试的同步方法
            syncEntityWithSpringRetry(entity);
            log.debug("异步同步 Entity 成功: entityId={}", entity.getId());
        } catch (Exception e) {
            // 重试耗尽后的异常会被 @Recover 方法处理
            log.error("异步同步 Entity 最终失败: entityId={}, error={}", entity.getId(), e.getMessage(), e);
        }
    }

    /**
     * 使用 Spring Retry 执行同步（带自动重试）
     * 
     * <p>重试策略：</p>
     * <ul>
     *   <li>最大重试次数：3次</li>
     *   <li>重试间隔：1秒 → 5秒 → 30秒（指数退避）</li>
     *   <li>仅对 EntitySyncException 进行重试</li>
     * </ul>
     * 
     * @param entity 要同步的 Entity
     * @throws EntitySyncException 同步失败时抛出
     */
    @Retryable(
        retryFor = {EntitySyncException.class},
        maxAttempts = 3,
        backoff = @Backoff(
            delay = 1000,      // 初始间隔 1 秒
            multiplier = 5,    // 乘数 5（1秒 → 5秒 → 25秒，接近设计的 30秒）
            maxDelay = 30000   // 最大间隔 30 秒
        )
    )
    public void syncEntityWithSpringRetry(EntityDO entity) {
        log.debug("执行同步（带 Spring Retry）: entityId={}", entity.getId());
        syncEntity(entity);
    }

    /**
     * Spring Retry 重试失败回调
     * 
     * <p>当 syncEntityWithSpringRetry 方法重试耗尽后调用此方法。</p>
     * 
     * @param exception 最后一次失败的异常
     * @param entity 同步失败的 Entity
     */
    @Recover
    public void syncEntityWithSpringRetryRecover(EntitySyncException exception, EntityDO entity) {
        log.error("Entity 同步重试耗尽: entityId={}, modelId={}, error={}", 
                entity != null ? entity.getId() : "null",
                entity != null ? entity.getModelId() : "null",
                exception.getMessage());
        
        if (entity != null) {
            // 记录失败日志
            recordSyncFailure(entity, exception.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncEntity(EntityDO entity) {
        if (entity == null || entity.getId() == null) {
            throw new EntitySyncException("Entity 不能为空");
        }

        Long entityId = entity.getId();
        Long modelId = entity.getModelId();

        log.debug("开始同步 Entity 到索引表: entityId={}, modelId={}", entityId, modelId);

        try {
            List<FieldDO> indexableFields = getIndexableFields(modelId);
            if (indexableFields.isEmpty()) {
                log.debug("Model 没有应进索引表的扩展字段，跳过同步: modelId={}", modelId);
                entityFieldIndexMapper.deleteByEntityId(entityId);
                return;
            }

            Map<String, Object> customFieldsMap = entity.getCustomFields() != null
                    ? entity.getCustomFields() : Map.of();

            entityFieldIndexMapper.deleteByEntityId(entityId);

            List<EntityFieldIndexDO> indexRecords = buildIndexRecords(entity, indexableFields, customFieldsMap);
            if (!indexRecords.isEmpty()) {
                entityFieldIndexMapper.insertBatch(indexRecords, EntityFieldIndexRecordBuilder.INDEX_INSERT_BATCH_SIZE);
                log.debug("同步 Entity 到索引表成功: entityId={}, fieldCount={}", entityId, indexRecords.size());
            }

        } catch (Exception e) {
            log.error("同步 Entity 到索引表失败: entityId={}, error={}", entityId, e.getMessage(), e);
            throw new EntitySyncException(entityId, modelId, ENGINE_TYPE, 
                    "同步失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean syncEntityWithRetry(EntityDO entity) {
        int retryCount = 0;
        long[] retryIntervals = {1000, 5000, 30000}; // 1秒、5秒、30秒

        while (retryCount <= MAX_RETRY_COUNT) {
            try {
                syncEntity(entity);
                return true;
            } catch (Exception e) {
                retryCount++;
                if (retryCount > MAX_RETRY_COUNT) {
                    log.error("同步 Entity 失败，已达到最大重试次数: entityId={}, retryCount={}", 
                            entity.getId(), retryCount);
                    recordSyncFailure(entity, e.getMessage());
                    return false;
                }

                long interval = retryIntervals[Math.min(retryCount - 1, retryIntervals.length - 1)];
                log.warn("同步 Entity 失败，{}ms 后重试: entityId={}, retryCount={}, error={}", 
                        interval, entity.getId(), retryCount, e.getMessage());

                try {
                    Thread.sleep(interval);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("重试等待被中断: entityId={}", entity.getId());
                    recordSyncFailure(entity, "重试等待被中断");
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFromIndex(Long entityId) {
        if (entityId == null) {
            return;
        }
        log.debug("从索引表删除 Entity: entityId={}", entityId);
        entityFieldIndexMapper.deleteByEntityId(entityId);
    }

    @Override
    public void batchSyncEntities(List<EntityDO> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }

        log.info("开始批量同步 Entity: count={}", entities.size());
        int successCount = 0;
        int failCount = 0;

        for (EntityDO entity : entities) {
            try {
                syncEntity(entity);
                successCount++;
            } catch (Exception e) {
                failCount++;
                log.error("批量同步 Entity 失败: entityId={}, error={}", entity.getId(), e.getMessage());
                recordSyncFailure(entity, e.getMessage());
            }
        }

        log.info("批量同步 Entity 完成: total={}, success={}, fail={}", 
                entities.size(), successCount, failCount);
    }

    // ==================== 补同步方法 ====================

    @Override
    public boolean resyncByEntityId(Long entityId, String entityTypeCode) {
        if (entityId == null) {
            log.warn("[resyncByEntityId][Entity ID 不能为空]");
            return false;
        }
        if (entityTypeCode == null || entityTypeCode.isEmpty()) {
            log.warn("[resyncByEntityId][entityTypeCode 不能为空][entityId={}]", entityId);
            return false;
        }

        EntityDO entity = entityRepository.findById(entityId, entityTypeCode);
        if (entity == null) {
            log.warn("[resyncByEntityId][Entity 不存在][entityId={}, entityTypeCode={}]",
                    entityId, entityTypeCode);
            return false;
        }

        return syncEntityWithRetry(entity);
    }

    @Override
    public int resyncFailedByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        List<EntitySyncFailLogDO> failLogs = entitySyncFailLogMapper.selectByTimeRange(
                startTime, endTime, EntitySyncFailLogDO.STATUS_FAILED);

        if (failLogs.isEmpty()) {
            log.info("时间范围内没有失败的同步记录: {} - {}", startTime, endTime);
            return 0;
        }

        log.info("开始补同步失败记录: count={}, timeRange={} - {}", failLogs.size(), startTime, endTime);
        int successCount = 0;

        for (EntitySyncFailLogDO failLog : failLogs) {
            if (processFailLog(failLog)) {
                successCount++;
            }
        }

        log.info("补同步完成: total={}, success={}", failLogs.size(), successCount);
        return successCount;
    }

    // ==================== 失败日志处理 ====================

    @Override
    public List<EntitySyncFailLogDO> getPendingFailLogs(int limit) {
        return entitySyncFailLogMapper.selectPendingLogs(limit);
    }

    @Override
    public boolean processFailLog(EntitySyncFailLogDO failLog) {
        if (failLog == null) {
            return false;
        }

        Long entityId = failLog.getEntityId();
        String entityTypeCode = failLog.getEntityTypeCode();
        EntityDO entity = (entityTypeCode == null || entityTypeCode.isBlank())
                ? null
                : entityRepository.findById(entityId, entityTypeCode);

        if (entity == null) {
            // Entity 已被删除，标记为成功
            updateFailLogStatus(failLog.getId(), EntitySyncFailLogDO.STATUS_SUCCESS);
            return true;
        }

        // 更新状态为重试中
        updateFailLogRetryInfo(failLog.getId(), EntitySyncFailLogDO.STATUS_RETRYING);

        try {
            syncEntity(entity);
            // 同步成功，更新状态
            updateFailLogStatus(failLog.getId(), EntitySyncFailLogDO.STATUS_SUCCESS);
            return true;
        } catch (Exception e) {
            log.error("处理失败日志失败: failLogId={}, entityId={}, error={}", 
                    failLog.getId(), entityId, e.getMessage());
            // 同步失败，更新状态
            updateFailLogStatus(failLog.getId(), EntitySyncFailLogDO.STATUS_FAILED);
            return false;
        }
    }

    @Override
    public long countPendingFailLogs() {
        return entitySyncFailLogMapper.countByStatus(EntitySyncFailLogDO.STATUS_PENDING);
    }

    @Override
    public List<EntitySyncFailLogDO> getPendingFailLogsByEntityTypeCode(String entityTypeCode, int limit) {
        return entitySyncFailLogMapper.selectPendingLogsByEntityTypeCode(entityTypeCode, limit);
    }

    @Override
    public long countPendingFailLogsByEntityTypeCode(String entityTypeCode) {
        return entitySyncFailLogMapper.countByStatusAndEntityTypeCode(
                EntitySyncFailLogDO.STATUS_PENDING, entityTypeCode);
    }

    @Override
    public long countConsecutiveFailures(Long entityId) {
        return entitySyncFailLogMapper.countFailedByEntityId(entityId);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 该型号应写入索引表的扩展字段：可搜索、可筛选、可排序任一为真。
     */
    private List<FieldDO> getIndexableFields(Long modelId) {
        List<Long> fieldIds = modelFieldAssignmentMapper.selectFieldIdsByModelId(modelId);
        if (fieldIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByModelId(modelId);
        Map<Long, ModelFieldAssignmentDO> assignmentMap = assignments.stream()
                .collect(Collectors.toMap(ModelFieldAssignmentDO::getFieldId, a -> a, (a, b) -> a));

        return fieldIds.stream()
                .map(fieldMapper::selectById)
                .filter(field -> {
                    if (field == null) {
                        return false;
                    }
                    ModelFieldAssignmentDO assignment = assignmentMap.get(field.getId());
                    Boolean searchable = assignment != null ? assignment.getIsSearchable() : null;
                    Boolean filterable = assignment != null ? assignment.getIsFilterable() : null;
                    Boolean sortable = assignment != null ? assignment.getIsSortable() : null;
                    return ExtensionFieldIndexEligibility.shouldWriteIndex(
                            searchable, filterable, sortable, field.getType());
                })
                .collect(Collectors.toList());
    }

    private List<EntityFieldIndexDO> buildIndexRecords(EntityDO entity,
                                                       List<FieldDO> indexableFields,
                                                       Map<String, Object> customFieldsMap) {
        List<EntityFieldIndexDO> records = new ArrayList<>();
        for (FieldDO field : indexableFields) {
            EntityFieldIndexDO record = EntityFieldIndexRecordBuilder.tryBuild(entity, field, customFieldsMap);
            if (record != null) {
                records.add(record);
            }
        }
        return records;
    }

    /**
     * 记录同步失败日志
     * 
     * <p>记录失败日志时会保存 entityTypeCode，以便补同步时能够路由到正确的存储策略。</p>
     */
    private void recordSyncFailure(EntityDO entity, String failReason) {
        try {
            EntitySyncFailLogDO failLog = EntitySyncFailLogDO.builder()
                    .entityId(entity.getId())
                    .entityTypeCode(entity.getEntityTypeCode())
                    .modelId(entity.getModelId())
                    .engineType(ENGINE_TYPE)
                    .failReason(failReason)
                    .retryCount(0)
                    .status(EntitySyncFailLogDO.STATUS_PENDING)
                    .build();

            entitySyncFailLogMapper.insert(failLog);
            log.debug("记录同步失败日志: entityId={}, entityTypeCode={}, failReason={}", 
                    entity.getId(), entity.getEntityTypeCode(), failReason);

            // 检查是否需要告警
            checkAndTriggerAlert(entity.getId());

        } catch (Exception e) {
            log.error("记录同步失败日志失败: entityId={}, error={}", entity.getId(), e.getMessage());
        }
    }

    /**
     * 更新失败日志状态
     */
    private void updateFailLogStatus(Long failLogId, String status) {
        entitySyncFailLogMapper.updateStatus(failLogId, status, LocalDateTime.now());
    }

    /**
     * 更新失败日志重试信息
     */
    private void updateFailLogRetryInfo(Long failLogId, String status) {
        entitySyncFailLogMapper.updateRetryInfo(failLogId, status, LocalDateTime.now(), LocalDateTime.now());
    }

    /**
     * 检查并触发告警
     */
    private void checkAndTriggerAlert(Long entityId) {
        long failCount = countConsecutiveFailures(entityId);
        if (failCount >= ALERT_THRESHOLD) {
            // 获取最后一次失败的错误信息
            EntitySyncFailLogDO lastFailLog = entitySyncFailLogMapper.selectLatestByEntityId(entityId);
            String lastError = lastFailLog != null ? lastFailLog.getFailReason() : "未知错误";
            
            // 获取 modelId（须带 entityTypeCode，经 Repository 访问专用表）
            Long modelId = null;
            if (lastFailLog != null
                    && lastFailLog.getEntityTypeCode() != null
                    && !lastFailLog.getEntityTypeCode().isBlank()) {
                EntityDO entity = entityRepository.findById(entityId, lastFailLog.getEntityTypeCode());
                modelId = entity != null ? entity.getModelId() : null;
            }
            
            // 调用告警服务
            syncAlertService.sendSyncFailureAlert(entityId, modelId, failCount, lastError);
        }
    }
}
