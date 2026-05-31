package cn.cheers.x.module.dynamicbusiness.service.computed.precompute;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.computed.ComputedFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.computed.PrecomputedValueDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.computed.ComputedFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.computed.PrecomputedValueMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.service.computed.cache.ComputedFieldCacheService;
import cn.cheers.x.module.dynamicbusiness.service.computed.executor.ComputedFieldExecutor;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityDoVoHelper;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import cn.cheers.x.module.dynamicbusiness.service.field.CustomFieldValidationService;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 预计算服务实现
 * 
 * 提供计算字段的异步预计算功能，包括：
 * - 基于 @Async 的异步预计算
 * - 数据变化触发重算
 * - 预计算任务管理和统计
 * - 预计算结果持久化存储
 * - 失败重试机制
 * - 定时任务处理待计算队列
 * 
 * @author yudao
 */
@Service
@Slf4j
public class PrecomputeServiceImpl implements PrecomputeService {

    /** 默认预计算缓存 TTL（分钟） */
    private static final int DEFAULT_PRECOMPUTE_TTL = 60;

    /** 预计算 TTL 倍数（相对于普通缓存） */
    private static final int PRECOMPUTE_TTL_MULTIPLIER = 10;

    /** 最大重试次数 */
    private static final int MAX_RETRY_COUNT = 3;

    /** 批量处理大小 */
    private static final int BATCH_SIZE = 100;

    /** 重试间隔（分钟） */
    private static final int RETRY_INTERVAL_MINUTES = 5;

    @Resource
    private ComputedFieldMapper computedFieldMapper;

    @Resource
    private ComputedFieldExecutor computedFieldExecutor;

    @Resource
    private ComputedFieldCacheService cacheService;

    @Resource
    private EntityCoreService entityCoreService;

    @Resource
    private CustomFieldValidationService customFieldValidationService;

    @Resource
    private PrecomputedValueMapper precomputedValueMapper;

    @Resource
    private ModelMapper modelMapper;

    /** 统计：总任务数 */
    private final AtomicLong totalTasks = new AtomicLong(0);

    /** 统计：完成任务数 */
    private final AtomicLong completedTasks = new AtomicLong(0);

    /** 统计：失败任务数 */
    private final AtomicLong failedTasks = new AtomicLong(0);

    /** 统计：总执行时间（毫秒） */
    private final AtomicLong totalExecutionTimeMs = new AtomicLong(0);

    /** 待处理任务队列（用于去重） */
    private final Set<String> pendingTasks = ConcurrentHashMap.newKeySet();

    // ========== 异步预计算 ==========

    @Override
    @Async("precomputeExecutor")
    public void triggerPrecomputeAsync(Long entityId) {
        if (entityId == null) {
            return;
        }

        String taskKey = "entity:" + entityId;
        if (!pendingTasks.add(taskKey)) {
            log.debug("[triggerPrecomputeAsync][任务已在队列中，跳过，entityId={}]", entityId);
            return;
        }

        totalTasks.incrementAndGet();
        long startTime = System.currentTimeMillis();

        try {
            // 1. 获取实体对应的 Model ID
            Long modelId = getModelIdByEntityId(entityId);
            if (modelId == null) {
                log.warn("[triggerPrecomputeAsync][无法获取实体的 Model ID，entityId={}]", entityId);
                failedTasks.incrementAndGet();
                return;
            }

            // 2. 获取该 Model 的所有预计算字段
            List<ComputedFieldDO> precomputedFields = computedFieldMapper.selectByModelId(modelId)
                    .stream()
                    .filter(ComputedFieldDO::isPrecomputed)
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(precomputedFields)) {
                log.debug("[triggerPrecomputeAsync][Model 没有预计算字段，modelId={}]", modelId);
                return;
            }

            // 3. 执行预计算
            executePrecompute(modelId, entityId, precomputedFields);

            completedTasks.incrementAndGet();
            log.info("[triggerPrecomputeAsync][预计算完成，entityId={}, 字段数={}]", 
                    entityId, precomputedFields.size());

        } catch (Exception e) {
            failedTasks.incrementAndGet();
            log.error("[triggerPrecomputeAsync][预计算失败，entityId={}]", entityId, e);
        } finally {
            pendingTasks.remove(taskKey);
            totalExecutionTimeMs.addAndGet(System.currentTimeMillis() - startTime);
        }
    }

    @Override
    @Async("precomputeExecutor")
    public void triggerFieldPrecomputeAsync(Long modelId, String fieldCode, Long entityId) {
        if (modelId == null || fieldCode == null || entityId == null) {
            return;
        }

        String taskKey = "field:" + modelId + ":" + fieldCode + ":" + entityId;
        if (!pendingTasks.add(taskKey)) {
            log.debug("[triggerFieldPrecomputeAsync][任务已在队列中，跳过，taskKey={}]", taskKey);
            return;
        }

        totalTasks.incrementAndGet();
        long startTime = System.currentTimeMillis();

        try {
            // 1. 获取计算字段配置
            ComputedFieldDO field = computedFieldMapper.selectByModelIdAndFieldCode(modelId, fieldCode);
            if (field == null || !field.isPrecomputed()) {
                log.warn("[triggerFieldPrecomputeAsync][字段不存在或不是预计算字段，modelId={}, fieldCode={}]", 
                        modelId, fieldCode);
                failedTasks.incrementAndGet();
                return;
            }

            // 2. 执行预计算
            executeSinglePrecompute(modelId, entityId, field);

            completedTasks.incrementAndGet();
            log.debug("[triggerFieldPrecomputeAsync][字段预计算完成，modelId={}, fieldCode={}, entityId={}]", 
                    modelId, fieldCode, entityId);

        } catch (Exception e) {
            failedTasks.incrementAndGet();
            log.error("[triggerFieldPrecomputeAsync][字段预计算失败，modelId={}, fieldCode={}, entityId={}]", 
                    modelId, fieldCode, entityId, e);
        } finally {
            pendingTasks.remove(taskKey);
            totalExecutionTimeMs.addAndGet(System.currentTimeMillis() - startTime);
        }
    }

    @Override
    @Async("precomputeExecutor")
    public void triggerBatchPrecomputeAsync(Long modelId, List<Long> entityIds) {
        if (modelId == null || CollectionUtils.isEmpty(entityIds)) {
            return;
        }

        log.info("[triggerBatchPrecomputeAsync][开始批量预计算，modelId={}, 实体数={}]", 
                modelId, entityIds.size());

        // 获取该 Model 的所有预计算字段
        List<ComputedFieldDO> precomputedFields = computedFieldMapper.selectByModelId(modelId)
                .stream()
                .filter(ComputedFieldDO::isPrecomputed)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(precomputedFields)) {
            log.debug("[triggerBatchPrecomputeAsync][Model 没有预计算字段，modelId={}]", modelId);
            return;
        }

        int successCount = 0;
        int failCount = 0;

        for (Long entityId : entityIds) {
            try {
                executePrecompute(modelId, entityId, precomputedFields);
                successCount++;
            } catch (Exception e) {
                failCount++;
                log.warn("[triggerBatchPrecomputeAsync][预计算失败，entityId={}]", entityId, e);
            }
        }

        log.info("[triggerBatchPrecomputeAsync][批量预计算完成，modelId={}, 成功={}, 失败={}]", 
                modelId, successCount, failCount);
    }

    // ========== 数据变化触发 ==========

    @Override
    public void onEntityCreated(Long modelId, Long entityId) {
        if (modelId == null || entityId == null) {
            return;
        }

        log.debug("[onEntityCreated][实体创建，触发预计算，modelId={}, entityId={}]", modelId, entityId);
        
        // 异步触发预计算
        triggerPrecomputeAsync(entityId);
    }

    @Override
    public void onEntityUpdated(Long modelId, Long entityId, List<String> changedFields) {
        if (modelId == null || entityId == null) {
            return;
        }

        log.debug("[onEntityUpdated][实体更新，触发预计算，modelId={}, entityId={}, changedFields={}]", 
                modelId, entityId, changedFields);

        // 1. 清除该实体的预计算缓存
        cacheService.refreshEntityCache(entityId);

        // 2. 获取依赖变化字段的预计算字段
        List<ComputedFieldDO> affectedFields = getAffectedPrecomputedFields(modelId, changedFields);
        
        if (!CollectionUtils.isEmpty(affectedFields)) {
            // 3. 异步重新计算受影响的字段
            for (ComputedFieldDO field : affectedFields) {
                triggerFieldPrecomputeAsync(modelId, field.getFieldCode(), entityId);
            }
        }
    }

    @Override
    public void onEntityDeleted(Long modelId, Long entityId) {
        if (modelId == null || entityId == null) {
            return;
        }

        log.debug("[onEntityDeleted][实体删除，清除预计算缓存，modelId={}, entityId={}]", modelId, entityId);

        // 1. 清除该实体的所有预计算缓存
        cacheService.refreshEntityCache(entityId);

        // 2. 删除该实体的所有预计算值记录
        try {
            int deleted = precomputedValueMapper.deleteByEntityId(entityId);
            if (deleted > 0) {
                log.debug("[onEntityDeleted][删除预计算值记录，entityId={}, count={}]", entityId, deleted);
            }
        } catch (Exception e) {
            log.warn("[onEntityDeleted][删除预计算值记录失败，entityId={}]", entityId, e);
        }
    }

    @Override
    @Async("precomputeExecutor")
    public void onRelatedDataChanged(String targetBusinessType, String targetModelCode, Long targetEntityId) {
        if (targetBusinessType == null || targetModelCode == null || targetEntityId == null) {
            return;
        }

        log.debug("[onRelatedDataChanged][关联数据变化，触发预计算，targetBusinessType={}, targetModelCode={}, targetEntityId={}]", 
                targetBusinessType, targetModelCode, targetEntityId);

        // 1. 查找所有引用该目标的聚合统计字段
        List<ComputedFieldDO> aggregateFields = computedFieldMapper.selectPrecomputedFields()
                .stream()
                .filter(f -> ComputedFieldDO.COMPUTE_TYPE_AGGREGATE.equals(f.getComputeType()))
                .filter(f -> targetBusinessType.equals(f.getTargetBusinessType()))
                .filter(f -> targetModelCode.equals(f.getTargetModelCode()))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(aggregateFields)) {
            return;
        }

        // 2. 对于每个受影响的字段，找到需要重算的实体
        for (ComputedFieldDO field : aggregateFields) {
            try {
                // 获取该 Model 的所有实体（简化实现，实际应根据关联条件筛选）
                List<Long> affectedEntityIds = getAffectedEntityIds(field, targetEntityId);
                
                for (Long entityId : affectedEntityIds) {
                    triggerFieldPrecomputeAsync(field.getModelId(), field.getFieldCode(), entityId);
                }
            } catch (Exception e) {
                log.warn("[onRelatedDataChanged][处理聚合字段失败，fieldCode={}]", field.getFieldCode(), e);
            }
        }
    }

    // ========== 预计算任务管理 ==========

    @Override
    public int getPendingTaskCount() {
        return pendingTasks.size();
    }

    @Override
    public int clearPendingTasks() {
        int count = pendingTasks.size();
        pendingTasks.clear();
        log.info("[clearPendingTasks][清空待处理任务，数量={}]", count);
        return count;
    }

    @Override
    public int recomputeAll(Long modelId) {
        log.info("[recomputeAll][开始重新计算所有预计算字段，modelId={}]", modelId);

        List<ComputedFieldDO> fields;
        if (modelId != null) {
            fields = computedFieldMapper.selectByModelId(modelId)
                    .stream()
                    .filter(ComputedFieldDO::isPrecomputed)
                    .collect(Collectors.toList());
        } else {
            fields = computedFieldMapper.selectPrecomputedFields();
        }

        if (CollectionUtils.isEmpty(fields)) {
            log.info("[recomputeAll][没有预计算字段需要重算]");
            return 0;
        }

        int totalCount = 0;

        // 按 Model 分组处理
        Map<Long, List<ComputedFieldDO>> fieldsByModel = fields.stream()
                .collect(Collectors.groupingBy(ComputedFieldDO::getModelId));

        for (Map.Entry<Long, List<ComputedFieldDO>> entry : fieldsByModel.entrySet()) {
            Long currentModelId = entry.getKey();
            List<ComputedFieldDO> modelFields = entry.getValue();

            // 获取该 Model 的所有实体 ID（简化实现）
            // 先获取 Model 的 businessTypeCode
            ModelDO model = modelMapper.selectById(currentModelId);
            if (model == null) {
                log.warn("[recomputeAll][Model 不存在，跳过，modelId={}]", currentModelId);
                continue;
            }
            List<Long> entityIds = getEntityIdsByModelId(currentModelId, model.getBusinessTypeCode());
            
            if (!CollectionUtils.isEmpty(entityIds)) {
                for (Long entityId : entityIds) {
                    executePrecompute(currentModelId, entityId, modelFields);
                    totalCount++;
                }
            }
        }

        log.info("[recomputeAll][重新计算完成，总计 {} 个实体]", totalCount);
        return totalCount;
    }

    // ========== 预计算统计 ==========

    @Override
    public PrecomputeStatistics getStatistics() {
        long total = totalTasks.get();
        long completed = completedTasks.get();
        long failed = failedTasks.get();
        long pending = pendingTasks.size();
        double avgTime = completed > 0 ? (double) totalExecutionTimeMs.get() / completed : 0.0;

        // 从数据库获取持久化的统计信息
        try {
            long dbPending = precomputedValueMapper.countPending();
            long dbFailed = precomputedValueMapper.countFailed();
            // 合并内存和数据库的统计
            pending = Math.max(pending, dbPending);
            failed = Math.max(failed, dbFailed);
        } catch (Exception e) {
            log.debug("[getStatistics][获取数据库统计失败]", e);
        }

        return new PrecomputeStatistics(total, completed, failed, pending, avgTime);
    }

    // ========== 私有方法 ==========

    /**
     * 执行预计算
     */
    private void executePrecompute(Long modelId, Long entityId, List<ComputedFieldDO> fields) {
        for (ComputedFieldDO field : fields) {
            executeSinglePrecompute(modelId, entityId, field);
        }
    }

    /**
     * 执行单个字段的预计算
     */
    private void executeSinglePrecompute(Long modelId, Long entityId, ComputedFieldDO field) {
        try {
            // 1. 执行计算
            Object value = computedFieldExecutor.execute(entityId, field, Collections.emptyMap());

            // 2. 存储预计算结果到缓存（使用较长的 TTL）
            if (value != null) {
                int ttl = calculatePrecomputeTtl(field);
                cacheService.setCachedValue(modelId, field.getFieldCode(), entityId, value, ttl);
                log.debug("[executeSinglePrecompute][预计算完成，entityId={}, fieldCode={}, value={}, ttl={}分钟]", 
                        entityId, field.getFieldCode(), value, ttl);
            }

            // 3. 持久化存储预计算结果
            persistPrecomputedValue(modelId, entityId, field, value, null);

        } catch (Exception e) {
            log.warn("[executeSinglePrecompute][预计算失败，entityId={}, fieldCode={}]", 
                    entityId, field.getFieldCode(), e);
            // 记录失败状态
            persistPrecomputedValue(modelId, entityId, field, null, e.getMessage());
        }
    }

    /**
     * 持久化预计算结果
     * 
     * @param modelId Model ID
     * @param entityId 实体 ID
     * @param field 计算字段
     * @param value 计算结果（成功时）
     * @param errorMessage 错误信息（失败时）
     */
    @Transactional(rollbackFor = Exception.class)
    protected void persistPrecomputedValue(Long modelId, Long entityId, ComputedFieldDO field, 
                                           Object value, String errorMessage) {
        try {
            // 查询是否已存在
            PrecomputedValueDO existing = precomputedValueMapper.selectByEntityAndField(
                    modelId, entityId, field.getFieldCode());

            LocalDateTime now = LocalDateTime.now();
            long durationMs = 0; // 简化实现，实际应记录计算耗时

            if (existing != null) {
                // 更新现有记录
                existing.setComputedValue(value != null ? JSON.toJSONString(value) : null);
                existing.setValueType(determineValueType(field));
                existing.setComputeStatus(errorMessage == null ? 
                        PrecomputedValueDO.STATUS_COMPLETED : PrecomputedValueDO.STATUS_FAILED);
                existing.setLastComputeTime(now);
                existing.setComputeDurationMs(durationMs);
                existing.setErrorMessage(errorMessage);
                if (errorMessage != null) {
                    existing.setRetryCount(existing.getRetryCount() != null ? 
                            existing.getRetryCount() + 1 : 1);
                    // 设置下次重试时间
                    existing.setNextComputeTime(now.plusMinutes(RETRY_INTERVAL_MINUTES));
                } else {
                    existing.setRetryCount(0);
                    existing.setNextComputeTime(null);
                }
                precomputedValueMapper.updateById(existing);
            } else {
                // 创建新记录
                PrecomputedValueDO newValue = PrecomputedValueDO.builder()
                        .modelId(modelId)
                        .entityId(entityId)
                        .fieldCode(field.getFieldCode())
                        .computedValue(value != null ? JSON.toJSONString(value) : null)
                        .valueType(determineValueType(field))
                        .computeStatus(errorMessage == null ? 
                                PrecomputedValueDO.STATUS_COMPLETED : PrecomputedValueDO.STATUS_FAILED)
                        .lastComputeTime(now)
                        .computeDurationMs(durationMs)
                        .errorMessage(errorMessage)
                        .retryCount(errorMessage != null ? 1 : 0)
                        .nextComputeTime(errorMessage != null ? 
                                now.plusMinutes(RETRY_INTERVAL_MINUTES) : null)
                        .version(1)
                        .build();
                precomputedValueMapper.insert(newValue);
            }
        } catch (Exception e) {
            log.warn("[persistPrecomputedValue][持久化预计算结果失败，entityId={}, fieldCode={}]", 
                    entityId, field.getFieldCode(), e);
        }
    }

    /**
     * 确定值类型
     */
    private String determineValueType(ComputedFieldDO field) {
        if (field.getResultType() != null) {
            return field.getResultType();
        }
        return PrecomputedValueDO.VALUE_TYPE_NUMBER;
    }

    /**
     * 计算预计算 TTL
     */
    private int calculatePrecomputeTtl(ComputedFieldDO field) {
        if (field.getCacheTtlMinutes() != null && field.getCacheTtlMinutes() > 0) {
            return field.getCacheTtlMinutes() * PRECOMPUTE_TTL_MULTIPLIER;
        }
        return DEFAULT_PRECOMPUTE_TTL;
    }

    /**
     * 获取实体对应的 Model ID
     * 
     * 注意：此方法需要 businessTypeCode，但调用方可能不知道。
     * 这里采用降级策略：尝试从所有可能的业务类型中查找。
     * 更好的做法是调用方传入 businessTypeCode。
     */
    private Long getModelIdByEntityId(Long entityId) {
        // 注意：此方法已废弃，因为 getEntity 需要 businessTypeCode
        // 建议调用方直接传入 businessTypeCode 和 entityId
        log.warn("[getModelIdByEntityId][此方法已废弃，请使用带 businessTypeCode 的方法，entityId={}]", entityId);
        return null;
    }

    /**
     * 获取受影响的预计算字段
     */
    private List<ComputedFieldDO> getAffectedPrecomputedFields(Long modelId, List<String> changedFields) {
        if (CollectionUtils.isEmpty(changedFields)) {
            // 如果没有指定变化字段，返回所有预计算字段
            return computedFieldMapper.selectByModelId(modelId)
                    .stream()
                    .filter(ComputedFieldDO::isPrecomputed)
                    .collect(Collectors.toList());
        }

        // 获取所有预计算字段
        List<ComputedFieldDO> precomputedFields = computedFieldMapper.selectByModelId(modelId)
                .stream()
                .filter(ComputedFieldDO::isPrecomputed)
                .collect(Collectors.toList());

        // 过滤出依赖变化字段的预计算字段
        return precomputedFields.stream()
                .filter(field -> isFieldAffected(field, changedFields))
                .collect(Collectors.toList());
    }

    /**
     * 判断字段是否受影响
     */
    private boolean isFieldAffected(ComputedFieldDO field, List<String> changedFields) {
        // 公式计算字段：检查公式引用的字段
        if (ComputedFieldDO.COMPUTE_TYPE_FORMULA.equals(field.getComputeType())) {
            List<String> formulaFields = field.getFormulaFields();
            if (!CollectionUtils.isEmpty(formulaFields)) {
                return formulaFields.stream().anyMatch(changedFields::contains);
            }
        }
        
        // 聚合统计字段：检查目标字段
        if (ComputedFieldDO.COMPUTE_TYPE_AGGREGATE.equals(field.getComputeType())) {
            String targetFieldCode = field.getTargetFieldCode();
            if (targetFieldCode != null) {
                return changedFields.contains(targetFieldCode);
            }
        }

        // 默认认为受影响
        return true;
    }

    /**
     * 获取受影响的实体 ID 列表
     * 
     * 根据聚合字段的关联条件，查找需要重新计算的实体。
     * 例如：当设备状态变化时，需要找到该设备所属部门的实体，
     * 以便重新计算"部门下的故障设备数"。
     * 
     * @param field 计算字段配置
     * @param targetEntityId 变化的目标实体 ID
     * @return 受影响的实体 ID 列表
     */
    private List<Long> getAffectedEntityIds(ComputedFieldDO field, Long targetEntityId) {
        try {
            // 获取关联条件
            Map<String, Object> relationCondition = field.getRelationCondition();
            if (CollectionUtils.isEmpty(relationCondition)) {
                log.debug("[getAffectedEntityIds][无关联条件，返回空列表，fieldCode={}]", field.getFieldCode());
                return Collections.emptyList();
            }

            // 解析关联条件
            // 关联条件格式：{"relation_field": "department_id", "current_field": "id"}
            // 表示：目标实体的 department_id 字段 = 当前实体的 id 字段
            String relationField = (String) relationCondition.get("relation_field");
            String currentField = (String) relationCondition.get("current_field");

            if (relationField == null || currentField == null) {
                log.debug("[getAffectedEntityIds][关联条件不完整，返回空列表，fieldCode={}]", field.getFieldCode());
                return Collections.emptyList();
            }

            // 获取目标实体的关联字段值
            // 需要先获取目标实体的 businessTypeCode
            // 这里简化处理：假设目标实体和当前字段属于同一个业务类型
            ModelDO fieldModel = modelMapper.selectById(field.getModelId());
            if (fieldModel == null) {
                log.warn("[getAffectedEntityIds][字段的 Model 不存在，fieldCode={}, modelId={}]", 
                        field.getFieldCode(), field.getModelId());
                return Collections.emptyList();
            }
            // 尝试从目标实体的 Model 获取 businessTypeCode（简化处理）
            // 实际应该通过其他方式确定目标实体的 businessTypeCode
            EntityDO targetEntityDO = entityCoreService.get(targetEntityId, fieldModel.getBusinessTypeCode());
            var targetEntity = targetEntityDO != null ? EntityDoVoHelper.toRespVO(targetEntityDO, customFieldValidationService) : null;
            if (targetEntity == null) {
                // 如果获取失败，可能是 businessTypeCode 不匹配，尝试其他方式
                log.debug("[getAffectedEntityIds][目标实体不存在或 businessTypeCode 不匹配，targetEntityId={}, businessTypeCode={}]", 
                        targetEntityId, fieldModel.getBusinessTypeCode());
                return Collections.emptyList();
            }

            // 从目标实体的 customFields 中获取关联字段值
            Object relationValue = getFieldValueFromEntity(targetEntity, relationField);
            if (relationValue == null) {
                return Collections.emptyList();
            }

            // 查找关联字段值匹配的实体
            // 这里简化实现：返回 Model 下所有实体，实际应根据 currentField 进行精确匹配
            // fieldModel 已在上面声明，直接使用
            List<Long> entityIds = entityCoreService.getEntityIdsByModelId(field.getModelId(), fieldModel.getBusinessTypeCode());
            
            // 过滤出关联字段值匹配的实体
            List<Long> affectedIds = new ArrayList<>();
            for (Long entityId : entityIds) {
                EntityDO entityDO = entityCoreService.get(entityId, fieldModel.getBusinessTypeCode());
                var entity = entityDO != null ? EntityDoVoHelper.toRespVO(entityDO, customFieldValidationService) : null;
                if (entity != null) {
                    Object currentValue = getFieldValueFromEntity(entity, currentField);
                    if (currentValue != null && currentValue.equals(relationValue)) {
                        affectedIds.add(entityId);
                    }
                }
            }

            log.debug("[getAffectedEntityIds][找到受影响的实体，fieldCode={}, count={}]", 
                    field.getFieldCode(), affectedIds.size());
            return affectedIds;

        } catch (Exception e) {
            log.warn("[getAffectedEntityIds][获取受影响实体失败，fieldCode={}]", field.getFieldCode(), e);
            return Collections.emptyList();
        }
    }

    /**
     * 从实体中获取字段值
     * 
     * @param entity 实体
     * @param fieldCode 字段编码
     * @return 字段值
     */
    private Object getFieldValueFromEntity(cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO entity, 
                                           String fieldCode) {
        if (entity == null || fieldCode == null) {
            return null;
        }

        // 检查是否是内置字段
        if ("id".equals(fieldCode)) {
            return entity.getId();
        }
        if ("name".equals(fieldCode)) {
            return entity.getName();
        }
        if ("model_id".equals(fieldCode) || "modelId".equals(fieldCode)) {
            return entity.getModelId();
        }

        // 从 customFields JSON 中获取
        String customFieldsJson = entity.getCustomFields();
        if (customFieldsJson != null && !customFieldsJson.isEmpty()) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> customFields = (Map<String, Object>) JSON.parseObject(customFieldsJson, Map.class);
                if (customFields != null) {
                    return customFields.get(fieldCode);
                }
            } catch (Exception e) {
                log.debug("[getFieldValueFromEntity][解析 customFields 失败，entityId={}]", entity.getId());
            }
        }

        return null;
    }

    // ========== 预计算任务处理（供 XXL-Job 调用） ==========

    /**
     * 处理待计算的预计算任务
     * 
     * 此方法供 XXL-Job 定时任务调用，不再使用 @Scheduled。
     * 租户级任务：每个租户可以独立配置执行周期。
     * 
     * @see cn.cheers.x.module.dynamicbusiness.job.PrecomputeJob
     */
    public void processPendingPrecomputes() {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // 1. 查询当前租户的待计算任务（框架自动添加 tenant_id 条件）
            List<PrecomputedValueDO> pendingTasks = precomputedValueMapper.selectPendingRecompute(now, BATCH_SIZE);
            if (!CollectionUtils.isEmpty(pendingTasks)) {
                log.info("[processPendingPrecomputes][开始处理待计算任务，数量={}]", pendingTasks.size());
                for (PrecomputedValueDO task : pendingTasks) {
                    processPrecomputeTask(task);
                }
            }

            // 2. 查询当前租户的失败可重试任务
            List<PrecomputedValueDO> failedTasks = precomputedValueMapper.selectFailedRetryable(MAX_RETRY_COUNT, BATCH_SIZE);
            if (!CollectionUtils.isEmpty(failedTasks)) {
                log.info("[processPendingPrecomputes][开始处理失败重试任务，数量={}]", failedTasks.size());
                for (PrecomputedValueDO task : failedTasks) {
                    processPrecomputeTask(task);
                }
            }

        } catch (Exception e) {
            log.error("[processPendingPrecomputes][处理待计算任务失败]", e);
        }
    }

    /**
     * 处理单个预计算任务
     */
    private void processPrecomputeTask(PrecomputedValueDO task) {
        try {
            // 使用乐观锁更新状态为计算中
            int updated = precomputedValueMapper.updateStatusToComputing(task.getId(), task.getVersion());
            if (updated == 0) {
                // 已被其他线程处理
                return;
            }

            // 获取计算字段配置
            ComputedFieldDO field = computedFieldMapper.selectByModelIdAndFieldCode(
                    task.getModelId(), task.getFieldCode());
            if (field == null) {
                log.warn("[processPrecomputeTask][计算字段不存在，taskId={}]", task.getId());
                return;
            }

            // 执行预计算
            executeSinglePrecompute(task.getModelId(), task.getEntityId(), field);

        } catch (Exception e) {
            log.warn("[processPrecomputeTask][处理预计算任务失败，taskId={}]", task.getId(), e);
        }
    }

    // ========== 预计算值查询 ==========

    /**
     * 获取预计算值
     * 
     * @param modelId Model ID
     * @param entityId 实体 ID
     * @param fieldCode 字段编码
     * @return 预计算值，不存在返回 null
     */
    public Object getPrecomputedValue(Long modelId, Long entityId, String fieldCode) {
        PrecomputedValueDO value = precomputedValueMapper.selectByEntityAndField(modelId, entityId, fieldCode);
        if (value == null || !value.isCompleted()) {
            return null;
        }
        
        try {
            return JSON.parse(value.getComputedValue());
        } catch (Exception e) {
            return value.getComputedValue();
        }
    }

    /**
     * 标记实体的所有预计算值为待重算
     * 
     * @param entityId 实体 ID
     */
    public void markEntityForRecompute(Long entityId) {
        precomputedValueMapper.updateStatusToPendingByEntityId(entityId);
    }

    /**
     * 获取 Model 的所有实体 ID
     * 
     * @param modelId Model ID
     * @param businessTypeCode 业务类型编码
     */
    private List<Long> getEntityIdsByModelId(Long modelId, String businessTypeCode) {
        try {
            // 简化实现：通过 EntityService 获取
            // 实际应分页处理大量数据
            return entityCoreService.getEntityIdsByModelId(modelId, businessTypeCode);
        } catch (Exception e) {
            log.warn("[getEntityIdsByModelId][获取实体 ID 列表失败，modelId={}, businessTypeCode={}]", 
                    modelId, businessTypeCode, e);
            return Collections.emptyList();
        }
    }
}
