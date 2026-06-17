package cn.cheers.x.module.dynamicbusiness.service.entity.sync;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityFieldIndexDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntitySyncFailLogDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityFieldIndexMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntitySyncFailLogMapper;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.service.field.SmartSearchableService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
 *   <li>只同步标记为 is_searchable=true 的字段</li>
 *   <li>根据字段类型存储到对应的值列（value_string、value_number、value_date 等）</li>
 *   <li>同步采用"删除后插入"策略，确保数据一致性</li>
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

    private final EntityMapper entityMapper;
    private final EntityFieldIndexMapper entityFieldIndexMapper;
    private final EntitySyncFailLogMapper entitySyncFailLogMapper;
    private final FieldMapper fieldMapper;
    private final ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    private final SyncAlertService syncAlertService;
    private final SmartSearchableService smartSearchableService;

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

    // ==================== 日期格式化器 ====================

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter ISO_DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

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
            // 1. 获取 Model 的可查询字段列表
            List<FieldDO> searchableFields = getSearchableFields(modelId);
            if (searchableFields.isEmpty()) {
                log.debug("Model 没有可查询字段，跳过同步: modelId={}", modelId);
                // 删除可能存在的旧索引数据
                entityFieldIndexMapper.deleteByEntityId(entityId);
                return;
            }

            // 2. 解析 Entity 的 customFields
            Map<String, Object> customFieldsMap = entity.getCustomFields() != null
                    ? entity.getCustomFields() : Map.of();

            // 3. 删除旧的索引数据
            entityFieldIndexMapper.deleteByEntityId(entityId);

            // 4. 构建并插入新的索引数据
            List<EntityFieldIndexDO> indexRecords = buildIndexRecords(entity, searchableFields, customFieldsMap);
            if (!indexRecords.isEmpty()) {
                for (EntityFieldIndexDO record : indexRecords) {
                    entityFieldIndexMapper.insert(record);
                }
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
    public boolean resyncByEntityId(Long entityId, String businessTypeCode) {
        if (entityId == null) {
            log.warn("[resyncByEntityId][Entity ID 不能为空]");
            return false;
        }
        if (businessTypeCode == null || businessTypeCode.isEmpty()) {
            log.warn("[resyncByEntityId][businessTypeCode 不能为空][entityId={}]", entityId);
            return false;
        }

        // 注意：此方法目前仍使用 entityMapper，仅支持通用表
        // 如果需要支持动态表，应该使用 IndexRebuildService.resyncByEntityId(entityId, businessTypeCode)
        EntityDO entity = entityMapper.selectById(entityId);
        if (entity == null) {
            log.warn("[resyncByEntityId][Entity 不存在于通用表][entityId={}, businessTypeCode={}]", 
                    entityId, businessTypeCode);
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
        EntityDO entity = entityMapper.selectById(entityId);

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
    public List<EntitySyncFailLogDO> getPendingFailLogsByBusinessTypeCode(String businessTypeCode, int limit) {
        return entitySyncFailLogMapper.selectPendingLogsByBusinessTypeCode(businessTypeCode, limit);
    }

    @Override
    public long countPendingFailLogsByBusinessTypeCode(String businessTypeCode) {
        return entitySyncFailLogMapper.countByStatusAndBusinessTypeCode(
                EntitySyncFailLogDO.STATUS_PENDING, businessTypeCode);
    }

    @Override
    public long countConsecutiveFailures(Long entityId) {
        return entitySyncFailLogMapper.countFailedByEntityId(entityId);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 获取 Model 的可查询字段列表
     */
    private List<FieldDO> getSearchableFields(Long modelId) {
        // 获取 Model 关联的字段 ID 列表
        List<Long> fieldIds = modelFieldAssignmentMapper.selectFieldIdsByModelId(modelId);
        if (fieldIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 查询字段详情，过滤出可查询的字段
        // 注意：isSearchable 已移至 ModelFieldAssignmentDO，这里需要从模型字段分配中判断
        List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByModelId(modelId);
        Map<Long, ModelFieldAssignmentDO> assignmentMap = assignments.stream()
                .collect(Collectors.toMap(ModelFieldAssignmentDO::getFieldId, a -> a));
        
        return fieldIds.stream()
                .map(fieldMapper::selectById)
                .filter(field -> {
                    if (field == null) {
                        return false;
                    }
                    // 优先使用模型字段分配中的配置，如果为 null 则使用智能默认值
                    ModelFieldAssignmentDO assignment = assignmentMap.get(field.getId());
                    Boolean isSearchable;
                    if (assignment != null && assignment.getIsSearchable() != null) {
                        isSearchable = assignment.getIsSearchable();
                    } else {
                        // 使用智能默认值服务获取字段类型的默认可查询属性
                        isSearchable = smartSearchableService.getDefaultSearchable(field.getType());
                    }
                    return Boolean.TRUE.equals(isSearchable);
                })
                .collect(Collectors.toList());
    }

    /**
     * 构建索引记录列表
     * 
     * <p>注意：customFields 使用字段 ID 作为 key，而不是字段 code。
     * 因此需要使用 field.getId().toString() 来查找值。</p>
     */
    private List<EntityFieldIndexDO> buildIndexRecords(EntityDO entity, 
                                                        List<FieldDO> searchableFields,
                                                        Map<String, Object> customFieldsMap) {
        List<EntityFieldIndexDO> records = new ArrayList<>();

        for (FieldDO field : searchableFields) {
            String fieldCode = field.getCode();
            // customFields 现在使用字段 code 作为 key
            Object value = customFieldsMap.get(fieldCode);

            // 跳过空值
            if (value == null) {
                log.debug("字段值为空，跳过: fieldId={}, fieldCode={}", field.getId(), fieldCode);
                continue;
            }

            EntityFieldIndexDO record = EntityFieldIndexDO.builder()
                    .entityId(entity.getId())
                    .modelId(entity.getModelId())
                    .fieldCode(fieldCode)
                    .build();
            // createTime/updateTime 由 MyBatis Plus 自动填充

            // 根据字段类型设置对应的值列
            setValueByFieldType(record, field.getType(), value);

            records.add(record);
            log.debug("构建索引记录: entityId={}, fieldId={}, fieldCode={}, value={}", 
                    entity.getId(), field.getId(), fieldCode, value);
        }

        return records;
    }

    /**
     * 根据字段类型设置索引记录的值
     */
    private void setValueByFieldType(EntityFieldIndexDO record, String fieldType, Object value) {
        if (value == null) {
            return;
        }

        String valueStr = value.toString();

        switch (fieldType.toUpperCase()) {
            case "NUMBER":
            case "INTEGER":
            case "DECIMAL":
                try {
                    record.setValueNumber(new BigDecimal(valueStr));
                } catch (NumberFormatException e) {
                    log.warn("数值转换失败，存储为字符串: fieldCode={}, value={}", 
                            record.getFieldCode(), valueStr);
                    record.setValueString(valueStr);
                }
                break;

            case "DATE":
                try {
                    LocalDate date = parseDate(valueStr);
                    record.setValueDate(date);
                } catch (DateTimeParseException e) {
                    log.warn("日期转换失败，存储为字符串: fieldCode={}, value={}", 
                            record.getFieldCode(), valueStr);
                    record.setValueString(valueStr);
                }
                break;

            case "DATETIME":
                try {
                    LocalDateTime dateTime = parseDateTime(valueStr);
                    record.setValueDatetime(dateTime);
                } catch (DateTimeParseException e) {
                    log.warn("日期时间转换失败，存储为字符串: fieldCode={}, value={}", 
                            record.getFieldCode(), valueStr);
                    record.setValueString(valueStr);
                }
                break;

            case "BOOLEAN":
                try {
                    Boolean boolValue = Boolean.parseBoolean(valueStr);
                    record.setValueBoolean(boolValue);
                } catch (Exception e) {
                    record.setValueString(valueStr);
                }
                break;

            case "TEXT":
            case "STRING":
            case "ENUM":
            case "SELECT":
            case "ENTITY_REF":
            default:
                // 字符串类型，截断过长的值
                if (valueStr.length() > 500) {
                    valueStr = valueStr.substring(0, 500);
                }
                record.setValueString(valueStr);
                break;
        }
    }

    /**
     * 解析日期字符串
     */
    private LocalDate parseDate(String dateStr) {
        // 尝试多种格式
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            // 尝试 ISO 格式
            return LocalDate.parse(dateStr);
        }
    }

    /**
     * 解析日期时间字符串
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        // 尝试多种格式
        try {
            return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(dateTimeStr, ISO_DATETIME_FORMATTER);
            } catch (DateTimeParseException e2) {
                // 尝试只有日期的情况
                LocalDate date = parseDate(dateTimeStr);
                return date.atStartOfDay();
            }
        }
    }

    /**
     * 记录同步失败日志
     * 
     * <p>记录失败日志时会保存 businessTypeCode，以便补同步时能够路由到正确的存储策略。</p>
     */
    private void recordSyncFailure(EntityDO entity, String failReason) {
        try {
            EntitySyncFailLogDO failLog = EntitySyncFailLogDO.builder()
                    .entityId(entity.getId())
                    .businessTypeCode(entity.getBusinessTypeCode())
                    .modelId(entity.getModelId())
                    .engineType(ENGINE_TYPE)
                    .failReason(failReason)
                    .retryCount(0)
                    .status(EntitySyncFailLogDO.STATUS_PENDING)
                    .build();

            entitySyncFailLogMapper.insert(failLog);
            log.debug("记录同步失败日志: entityId={}, businessTypeCode={}, failReason={}", 
                    entity.getId(), entity.getBusinessTypeCode(), failReason);

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
            
            // 获取 modelId
            EntityDO entity = entityMapper.selectById(entityId);
            Long modelId = entity != null ? entity.getModelId() : null;
            
            // 调用告警服务
            syncAlertService.sendSyncFailureAlert(entityId, modelId, failCount, lastError);
        }
    }
}
