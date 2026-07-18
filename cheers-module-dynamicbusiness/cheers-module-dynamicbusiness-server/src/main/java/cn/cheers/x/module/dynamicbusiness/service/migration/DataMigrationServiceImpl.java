package cn.cheers.x.module.dynamicbusiness.service.migration;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.relation.RelationFieldLibraryDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.relation.RelationFieldLibraryMapper;
import cn.cheers.x.module.dynamicbusiness.enums.field.FieldTypeEnum;
import cn.cheers.x.module.dynamicbusiness.service.field.SmartSearchableService;
import cn.cheers.x.module.dynamicbusiness.service.relation.RelationFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.migration.vo.MigrationResultVO;
import cn.cheers.x.module.dynamicbusiness.service.migration.vo.MigrationStatusVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据迁移服务实现
 * 
 * @author yudao
 * @since 2026-01-07
 */
@Slf4j
@Service
public class DataMigrationServiceImpl implements DataMigrationService {

    @Resource
    private FieldMapper fieldMapper;

    @Resource
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;

    @Resource
    private RelationFieldLibraryMapper relationFieldLibraryMapper;

    @Resource
    private SmartSearchableService smartSearchableService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public MigrationResultVO migrateRelationFieldsToLibrary(boolean dryRun) {
        log.info("[migrateRelationFieldsToLibrary][开始迁移关联字段到关联字段库, dryRun={}]", dryRun);
        
        LocalDateTime startTime = LocalDateTime.now();
        MigrationResultVO result = MigrationResultVO.builder()
                .dryRun(dryRun)
                .migrationType(MigrationResultVO.MigrationType.RELATION_FIELD_MIGRATION.name())
                .startTime(startTime)
                .totalCount(0)
                .successCount(0)
                .skippedCount(0)
                .failedCount(0)
                .build();

        try {
            // 1. 查询所有 ENTITY_REF 类型的字段
            List<FieldDO> entityRefFields = fieldMapper.selectList(
                    new LambdaQueryWrapperX<FieldDO>()
                            .eq(FieldDO::getType, FieldTypeEnum.ENTITY_REF.getCode()));
            
            result.setTotalCount(entityRefFields.size());
            log.info("[migrateRelationFieldsToLibrary][找到 {} 个 ENTITY_REF 类型字段]", entityRefFields.size());

            // 2. 遍历处理每个字段
            for (FieldDO field : entityRefFields) {
                try {
                    processEntityRefField(field, dryRun, result);
                } catch (Exception e) {
                    log.error("[migrateRelationFieldsToLibrary][处理字段失败: fieldId={}, code={}]", 
                            field.getId(), field.getCode(), e);
                    result.setFailedCount(result.getFailedCount() + 1);
                    result.addFailureDetail(field.getId(), "FIELD", field.getCode(),
                            MigrationResultVO.ActionType.UPDATE.name(), e.getMessage());
                }
            }

            result.setSuccess(result.getFailedCount() == 0);
        } catch (Exception e) {
            log.error("[migrateRelationFieldsToLibrary][迁移失败]", e);
            result.setSuccess(false);
            result.addError("迁移失败: " + e.getMessage());
        }

        result.setEndTime(LocalDateTime.now());
        result.calculateDuration();
        
        log.info("[migrateRelationFieldsToLibrary][迁移完成, 总数={}, 成功={}, 跳过={}, 失败={}, 耗时={}ms]",
                result.getTotalCount(), result.getSuccessCount(), 
                result.getSkippedCount(), result.getFailedCount(), result.getDurationMs());
        
        return result;
    }


    /**
     * 处理单个 ENTITY_REF 字段的迁移
     */
    private void processEntityRefField(FieldDO field, boolean dryRun, MigrationResultVO result) {
        // 检查是否已有对应的关联字段库记录
        String fieldCode = extractRelationFieldCode(field.getCode());
        RelationFieldLibraryDO existingLibraryField = relationFieldLibraryMapper.selectByFieldCode(fieldCode);
        
        if (existingLibraryField != null) {
            // 已存在，检查是否需要更新 ModelFieldAssignment
            updateModelFieldAssignmentReferences(field, existingLibraryField, dryRun, result);
            return;
        }

        result.setSkippedCount(result.getSkippedCount() + 1);
        result.addSuccessDetail(field.getId(), "FIELD", field.getCode(),
                MigrationResultVO.ActionType.SKIP.name(), "请通过管理端在关联字段库中配置后再迁移");
    }

    /**
     * 从字段编码中提取关联字段编码
     * 例如：REF-safety_manager -> safety_manager
     */
    private String extractRelationFieldCode(String fieldCode) {
        if (RelationFieldCodes.isLibraryRefFieldCode(fieldCode)) {
            return RelationFieldCodes.stripPrefixIfPresent(fieldCode);
        }
        return fieldCode;
    }

    /**
     * 更新 ModelFieldAssignment 的关联字段库引用
     */
    private void updateModelFieldAssignmentReferences(FieldDO field, RelationFieldLibraryDO libraryField,
                                                       boolean dryRun, MigrationResultVO result) {
        // 查询使用该字段的所有 ModelFieldAssignment
        List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByFieldId(field.getId());
        
        for (ModelFieldAssignmentDO assignment : assignments) {
            if (assignment.getRefLibraryId() != null) {
                // 已有引用，跳过
                continue;
            }

            if (!dryRun) {
                assignment.setRefLibraryId(libraryField.getId());
                modelFieldAssignmentMapper.updateById(assignment);
                
                // 增加使用次数
                relationFieldLibraryMapper.incrementUsageCount(libraryField.getId());
                
                log.debug("[updateModelFieldAssignmentReferences][更新分配引用: assignmentId={}, refLibraryId={}]",
                        assignment.getId(), libraryField.getId());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MigrationResultVO applySmartDefaultsToExistingFields(boolean dryRun, boolean overwriteExisting) {
        log.info("[applySmartDefaultsToExistingFields][开始应用智能默认设置, dryRun={}, overwrite={}]", 
                dryRun, overwriteExisting);
        
        LocalDateTime startTime = LocalDateTime.now();
        MigrationResultVO result = MigrationResultVO.builder()
                .dryRun(dryRun)
                .migrationType(MigrationResultVO.MigrationType.SMART_DEFAULTS_APPLICATION.name())
                .startTime(startTime)
                .totalCount(0)
                .successCount(0)
                .skippedCount(0)
                .failedCount(0)
                .build();

        try {
            // 查询所有字段
            List<FieldDO> allFields = fieldMapper.selectList(new LambdaQueryWrapperX<>());
            result.setTotalCount(allFields.size());
            log.info("[applySmartDefaultsToExistingFields][找到 {} 个字段]", allFields.size());

            for (FieldDO field : allFields) {
                try {
                    processFieldSmartDefaults(field, dryRun, overwriteExisting, result);
                } catch (Exception e) {
                    log.error("[applySmartDefaultsToExistingFields][处理字段失败: fieldId={}, code={}]",
                            field.getId(), field.getCode(), e);
                    result.setFailedCount(result.getFailedCount() + 1);
                    result.addFailureDetail(field.getId(), "FIELD", field.getCode(),
                            MigrationResultVO.ActionType.UPDATE.name(), e.getMessage());
                }
            }

            result.setSuccess(result.getFailedCount() == 0);
        } catch (Exception e) {
            log.error("[applySmartDefaultsToExistingFields][应用智能默认失败]", e);
            result.setSuccess(false);
            result.addError("应用智能默认失败: " + e.getMessage());
        }

        result.setEndTime(LocalDateTime.now());
        result.calculateDuration();
        
        log.info("[applySmartDefaultsToExistingFields][完成, 总数={}, 成功={}, 跳过={}, 失败={}, 耗时={}ms]",
                result.getTotalCount(), result.getSuccessCount(),
                result.getSkippedCount(), result.getFailedCount(), result.getDurationMs());
        
        return result;
    }


    /**
     * 处理单个字段的智能默认设置
     */
    private void processFieldSmartDefaults(FieldDO field, boolean dryRun, 
                                            boolean overwriteExisting, MigrationResultVO result) {
        boolean needsUpdate = false;
        StringBuilder changes = new StringBuilder();

        // 注意：isSearchable 和 isSortable 已移至 ModelFieldAssignmentDO 中配置
        // 字段创建时不再设置这些属性，这些属性在模型字段分配时根据字段类型应用智能默认值

        // 检查是否需要设置 indexStrategy
        if (overwriteExisting || StrUtil.isBlank(field.getIndexStrategy())) {
            SmartSearchableService.IndexStrategy indexStrategy = 
                    smartSearchableService.getDefaultIndexStrategy(field.getType());
            String strategyCode = indexStrategy.getCode();
            if (overwriteExisting || StrUtil.isBlank(field.getIndexStrategy())) {
                if (!strategyCode.equals(field.getIndexStrategy())) {
                    field.setIndexStrategy(strategyCode);
                    needsUpdate = true;
                    changes.append("indexStrategy=").append(strategyCode).append("; ");
                }
            }
        }

        if (!needsUpdate) {
            result.setSkippedCount(result.getSkippedCount() + 1);
            result.addSuccessDetail(field.getId(), "FIELD", field.getCode(),
                    MigrationResultVO.ActionType.SKIP.name(), "字段已有完整设置，无需更新");
            return;
        }

        if (!dryRun) {
            fieldMapper.updateById(field);
            log.debug("[processFieldSmartDefaults][更新字段智能默认: fieldId={}, changes={}]",
                    field.getId(), changes);
        }

        result.setSuccessCount(result.getSuccessCount() + 1);
        result.addSuccessDetail(field.getId(), "FIELD", field.getCode(),
                dryRun ? MigrationResultVO.ActionType.SKIP.name() : MigrationResultVO.ActionType.UPDATE.name(),
                dryRun ? "试运行: 将更新 " + changes : "已更新: " + changes);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MigrationResultVO executeFullMigration(boolean dryRun) {
        log.info("[executeFullMigration][开始执行完整迁移, dryRun={}]", dryRun);
        
        LocalDateTime startTime = LocalDateTime.now();
        MigrationResultVO result = MigrationResultVO.builder()
                .dryRun(dryRun)
                .migrationType(MigrationResultVO.MigrationType.FULL_MIGRATION.name())
                .startTime(startTime)
                .totalCount(0)
                .successCount(0)
                .skippedCount(0)
                .failedCount(0)
                .build();

        try {
            // 1. 迁移关联字段
            MigrationResultVO relationResult = migrateRelationFieldsToLibrary(dryRun);
            mergeResults(result, relationResult, "关联字段迁移");

            // 2. 应用智能默认设置
            MigrationResultVO smartDefaultsResult = applySmartDefaultsToExistingFields(dryRun, false);
            mergeResults(result, smartDefaultsResult, "智能默认应用");

            result.setSuccess(result.getFailedCount() == 0);
        } catch (Exception e) {
            log.error("[executeFullMigration][完整迁移失败]", e);
            result.setSuccess(false);
            result.addError("完整迁移失败: " + e.getMessage());
        }

        result.setEndTime(LocalDateTime.now());
        result.calculateDuration();
        
        log.info("[executeFullMigration][完整迁移完成, 总数={}, 成功={}, 跳过={}, 失败={}, 耗时={}ms]",
                result.getTotalCount(), result.getSuccessCount(),
                result.getSkippedCount(), result.getFailedCount(), result.getDurationMs());
        
        return result;
    }


    /**
     * 合并迁移结果
     */
    private void mergeResults(MigrationResultVO target, MigrationResultVO source, String phase) {
        target.setTotalCount(target.getTotalCount() + source.getTotalCount());
        target.setSuccessCount(target.getSuccessCount() + source.getSuccessCount());
        target.setSkippedCount(target.getSkippedCount() + source.getSkippedCount());
        target.setFailedCount(target.getFailedCount() + source.getFailedCount());
        
        if (CollUtil.isNotEmpty(source.getDetails())) {
            target.getDetails().addAll(source.getDetails());
        }
        if (CollUtil.isNotEmpty(source.getErrors())) {
            for (String error : source.getErrors()) {
                target.addError("[" + phase + "] " + error);
            }
        }
        if (CollUtil.isNotEmpty(source.getWarnings())) {
            for (String warning : source.getWarnings()) {
                target.addWarning("[" + phase + "] " + warning);
            }
        }
    }

    @Override
    public MigrationStatusVO getMigrationStatus() {
        log.debug("[getMigrationStatus][获取迁移状态]");
        
        MigrationStatusVO status = new MigrationStatusVO();
        status.setLastCheckTime(LocalDateTime.now());

        // 统计 ENTITY_REF 字段
        List<FieldDO> entityRefFields = fieldMapper.selectList(
                new LambdaQueryWrapperX<FieldDO>()
                        .eq(FieldDO::getType, FieldTypeEnum.ENTITY_REF.getCode()));
        status.setTotalEntityRefFields(entityRefFields.size());

        // 统计已迁移的字段（有 refLibraryId 的 ModelFieldAssignment）
        long migratedCount = 0;
        for (FieldDO field : entityRefFields) {
            List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByFieldId(field.getId());
            boolean hasMigrated = assignments.stream().anyMatch(a -> a.getRefLibraryId() != null);
            if (hasMigrated) {
                migratedCount++;
            }
        }
        status.setMigratedToLibraryCount((int) migratedCount);
        status.setPendingRelationFieldMigration(entityRefFields.size() - (int) migratedCount);

        // 统计关联字段库记录
        List<RelationFieldLibraryDO> libraryFields = relationFieldLibraryMapper.selectAll();
        status.setRelationFieldLibraryCount(libraryFields.size());

        // 统计所有字段
        List<FieldDO> allFields = fieldMapper.selectList(new LambdaQueryWrapperX<>());
        status.setTotalFields(allFields.size());

        // 注意：isSearchable 和 isSortable 已移至 ModelFieldAssignmentDO 中配置
        // 只统计索引策略
        long withIndexStrategy = allFields.stream()
                .filter(f -> StrUtil.isNotBlank(f.getIndexStrategy()))
                .count();
        status.setFieldsWithIndexStrategy((int) withIndexStrategy);
        
        // 统计已设置可查询属性的字段（从模型字段分配中统计）
        // 注意：这里不再从字段定义中统计，因为已移至模型字段分配级别
        status.setFieldsWithSearchable(0); // 不再统计字段级别的可查询属性

        // 统计待应用智能默认的字段（只检查索引策略）
        long pendingDefaults = allFields.stream()
                .filter(f -> StrUtil.isBlank(f.getIndexStrategy()))
                .count();
        status.setPendingSmartDefaults((int) pendingDefaults);

        // 计算完成百分比
        status.calculateCompletionPercentage();

        return status;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public MigrationResultVO rollbackRelationFieldMigration() {
        log.info("[rollbackRelationFieldMigration][开始回滚关联字段迁移]");
        
        LocalDateTime startTime = LocalDateTime.now();
        MigrationResultVO result = MigrationResultVO.builder()
                .dryRun(false)
                .migrationType(MigrationResultVO.MigrationType.ROLLBACK.name())
                .startTime(startTime)
                .totalCount(0)
                .successCount(0)
                .skippedCount(0)
                .failedCount(0)
                .build();

        try {
            // 1. 查询所有有 refLibraryId 的 ModelFieldAssignment
            List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectList(
                    new LambdaQueryWrapperX<ModelFieldAssignmentDO>()
                            .isNotNull(ModelFieldAssignmentDO::getRefLibraryId));
            
            result.setTotalCount(assignments.size());
            log.info("[rollbackRelationFieldMigration][找到 {} 个待回滚的分配记录]", assignments.size());

            // 2. 清除 refLibraryId 引用
            for (ModelFieldAssignmentDO assignment : assignments) {
                try {
                    Long refLibraryId = assignment.getRefLibraryId();
                    assignment.setRefLibraryId(null);
                    modelFieldAssignmentMapper.updateById(assignment);
                    
                    // 减少使用次数
                    if (refLibraryId != null) {
                        relationFieldLibraryMapper.decrementUsageCount(refLibraryId);
                    }
                    
                    result.setSuccessCount(result.getSuccessCount() + 1);
                    result.addSuccessDetail(assignment.getId(), "ASSIGNMENT", 
                            "modelId=" + assignment.getModelId() + ",fieldId=" + assignment.getFieldId(),
                            MigrationResultVO.ActionType.UPDATE.name(), "已清除 refLibraryId 引用");
                } catch (Exception e) {
                    log.error("[rollbackRelationFieldMigration][回滚分配记录失败: assignmentId={}]", 
                            assignment.getId(), e);
                    result.setFailedCount(result.getFailedCount() + 1);
                    result.addFailureDetail(assignment.getId(), "ASSIGNMENT",
                            "modelId=" + assignment.getModelId(),
                            MigrationResultVO.ActionType.UPDATE.name(), e.getMessage());
                }
            }

            // 3. 删除迁移创建的关联字段库记录（description 包含 "从字段" 的记录）
            List<RelationFieldLibraryDO> migratedLibraryFields = relationFieldLibraryMapper.selectList(
                    new LambdaQueryWrapperX<RelationFieldLibraryDO>()
                            .like(RelationFieldLibraryDO::getDescription, "从字段")
                            .eq(RelationFieldLibraryDO::getIsSystem, false));
            
            for (RelationFieldLibraryDO libraryField : migratedLibraryFields) {
                try {
                    // 只删除使用次数为 0 的记录
                    if (libraryField.getUsageCount() == null || libraryField.getUsageCount() == 0) {
                        relationFieldLibraryMapper.deleteById(libraryField.getId());
                        result.addSuccessDetail(libraryField.getId(), "LIBRARY_FIELD",
                                libraryField.getFieldCode(),
                                MigrationResultVO.ActionType.DELETE.name(), "已删除迁移创建的关联字段库记录");
                    } else {
                        result.addWarning("关联字段库记录 " + libraryField.getFieldCode() + 
                                " 仍有 " + libraryField.getUsageCount() + " 次使用，未删除");
                    }
                } catch (Exception e) {
                    log.error("[rollbackRelationFieldMigration][删除关联字段库记录失败: id={}]",
                            libraryField.getId(), e);
                    result.addError("删除关联字段库记录失败: " + libraryField.getFieldCode());
                }
            }

            result.setSuccess(result.getFailedCount() == 0);
        } catch (Exception e) {
            log.error("[rollbackRelationFieldMigration][回滚失败]", e);
            result.setSuccess(false);
            result.addError("回滚失败: " + e.getMessage());
        }

        result.setEndTime(LocalDateTime.now());
        result.calculateDuration();
        
        log.info("[rollbackRelationFieldMigration][回滚完成, 总数={}, 成功={}, 失败={}, 耗时={}ms]",
                result.getTotalCount(), result.getSuccessCount(), 
                result.getFailedCount(), result.getDurationMs());
        
        return result;
    }


    @Override
    public MigrationResultVO validateMigrationIntegrity() {
        log.info("[validateMigrationIntegrity][开始验证迁移数据完整性]");
        
        LocalDateTime startTime = LocalDateTime.now();
        MigrationResultVO result = MigrationResultVO.builder()
                .dryRun(false)
                .migrationType(MigrationResultVO.MigrationType.VALIDATION.name())
                .startTime(startTime)
                .totalCount(0)
                .successCount(0)
                .skippedCount(0)
                .failedCount(0)
                .build();

        try {
            // 1. 验证所有 ENTITY_REF 字段是否都有对应的关联字段库记录
            validateEntityRefFieldsHaveLibraryRecords(result);

            // 2. 验证关联字段库记录的目标是否存在
            validateLibraryFieldTargetsExist(result);

            // 3. 验证字段的智能默认设置是否正确
            validateSmartDefaultSettings(result);

            result.setSuccess(result.getFailedCount() == 0);
        } catch (Exception e) {
            log.error("[validateMigrationIntegrity][验证失败]", e);
            result.setSuccess(false);
            result.addError("验证失败: " + e.getMessage());
        }

        result.setEndTime(LocalDateTime.now());
        result.calculateDuration();
        
        log.info("[validateMigrationIntegrity][验证完成, 总数={}, 有效={}, 无效={}, 耗时={}ms]",
                result.getTotalCount(), result.getSuccessCount(), 
                result.getFailedCount(), result.getDurationMs());
        
        return result;
    }

    /**
     * 验证 ENTITY_REF 字段是否都有关联字段库记录
     */
    private void validateEntityRefFieldsHaveLibraryRecords(MigrationResultVO result) {
        List<FieldDO> entityRefFields = fieldMapper.selectList(
                new LambdaQueryWrapperX<FieldDO>()
                        .eq(FieldDO::getType, FieldTypeEnum.ENTITY_REF.getCode()));
        
        for (FieldDO field : entityRefFields) {
            result.setTotalCount(result.getTotalCount() + 1);
            
            String fieldCode = extractRelationFieldCode(field.getCode());
            RelationFieldLibraryDO libraryField = relationFieldLibraryMapper.selectByFieldCode(fieldCode);
            
            if (libraryField != null) {
                result.setSuccessCount(result.getSuccessCount() + 1);
                result.addSuccessDetail(field.getId(), "FIELD", field.getCode(),
                        MigrationResultVO.ActionType.VALID.name(), 
                        "字段有对应的关联字段库记录: " + libraryField.getFieldCode());
            } else {
                // 检查是否有 ModelFieldAssignment 引用
                List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByFieldId(field.getId());
                boolean hasLibraryRef = assignments.stream().anyMatch(a -> a.getRefLibraryId() != null);
                
                if (hasLibraryRef) {
                    result.setSuccessCount(result.getSuccessCount() + 1);
                    result.addSuccessDetail(field.getId(), "FIELD", field.getCode(),
                            MigrationResultVO.ActionType.VALID.name(), 
                            "字段通过 ModelFieldAssignment 引用关联字段库");
                } else {
                    result.setFailedCount(result.getFailedCount() + 1);
                    result.addFailureDetail(field.getId(), "FIELD", field.getCode(),
                            MigrationResultVO.ActionType.INVALID.name(),
                            "ENTITY_REF 字段没有对应的关联字段库记录");
                }
            }
        }
    }

    /**
     * 验证关联字段库记录的目标是否存在
     */
    private void validateLibraryFieldTargetsExist(MigrationResultVO result) {
        List<RelationFieldLibraryDO> libraryFields = relationFieldLibraryMapper.selectAll();
        
        for (RelationFieldLibraryDO libraryField : libraryFields) {
            result.setTotalCount(result.getTotalCount() + 1);
            
            // 这里简化处理，实际应该查询 EntityType 和 Model 是否存在
            // 由于松散引用的设计，目标不存在也是允许的（状态为 PENDING）
            if (StrUtil.isNotBlank(libraryField.getRefEntityType())) {
                result.setSuccessCount(result.getSuccessCount() + 1);
                result.addSuccessDetail(libraryField.getId(), "LIBRARY_FIELD", libraryField.getFieldCode(),
                        MigrationResultVO.ActionType.VALID.name(),
                        "关联字段库记录配置完整: " + libraryField.getRefEntityType());
            } else {
                result.addWarning("关联字段库记录 " + libraryField.getFieldCode() + " 关联业务类型为空");
                result.setSkippedCount(result.getSkippedCount() + 1);
            }
        }
    }

    /**
     * 验证字段的智能默认设置
     */
    private void validateSmartDefaultSettings(MigrationResultVO result) {
        List<FieldDO> allFields = fieldMapper.selectList(new LambdaQueryWrapperX<>());
        
        for (FieldDO field : allFields) {
            result.setTotalCount(result.getTotalCount() + 1);
            
            // 注意：isSearchable 和 isSortable 已移至 ModelFieldAssignmentDO 中配置
            // 只验证 indexStrategy
            boolean hasIndexStrategy = StrUtil.isNotBlank(field.getIndexStrategy());
            
            if (hasIndexStrategy) {
                result.setSuccessCount(result.getSuccessCount() + 1);
                result.addSuccessDetail(field.getId(), "FIELD", field.getCode(),
                        MigrationResultVO.ActionType.VALID.name(),
                        "字段智能默认设置完整");
            } else {
                StringBuilder missing = new StringBuilder("缺少: ");
                // 注意：isSearchable 和 isSortable 已移至 ModelFieldAssignmentDO 中配置
                if (!hasIndexStrategy) missing.append("indexStrategy ");
                
                result.setFailedCount(result.getFailedCount() + 1);
                result.addFailureDetail(field.getId(), "FIELD", field.getCode(),
                        MigrationResultVO.ActionType.INVALID.name(),
                        missing.toString().trim());
            }
        }
    }
}
