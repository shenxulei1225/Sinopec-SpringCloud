package cn.cheers.x.module.dynamicbusiness.service.model;

import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.*;
import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeBaseFieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeRelationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.CustomRelationFieldCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldBatchAssignReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldAssignmentRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFilterFieldMetaRespVO;
import cn.cheers.x.module.dynamicbusiness.convert.field.FieldConvert;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.relation.RelationFieldLibraryDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.relation.RelationFieldLibraryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeResolver;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.StorageTypeEnum;
import cn.cheers.x.module.dynamicbusiness.enums.field.FieldTypeEnum;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeBaseFieldService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeRelationService;
import cn.cheers.x.module.dynamicbusiness.service.capability.BusinessCapabilityService;
import cn.cheers.x.module.dynamicbusiness.service.capability.form.ModelCrudFormFieldAssembler;
import cn.cheers.x.module.dynamicbusiness.service.field.SmartSearchableService;
import cn.cheers.x.module.dynamicbusiness.service.relation.RelationFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.relation.RelationFieldLibraryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * 模型字段分配 Service 实现类
 * 
 * 支持三种字段类型：
 * - 固定列字段（BASE）：来自业务类型配置,自动继承
 * - 扩展字段（CUSTOM）：用户通过 ModelFieldAssignment 添加
 * - 关联字段（RELATION）：引用其他业务实体（FR-BDA-030~034）
 * 
 * @author yudao
 */
@Service
@Validated
@Slf4j
public class ModelFieldAssignmentServiceImpl implements ModelFieldAssignmentService {

    @Resource
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private FieldMapper fieldMapper;
    @Resource
    private EntityRepository entityRepository;
    @Resource
    private EntityTypeScopeResolver entityTypeScopeResolver;
    @Resource
    private EntityTypeBaseFieldService entityTypeBaseFieldService;
    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private RelationFieldLibraryMapper relationFieldLibraryMapper;
    @Resource
    private RelationFieldLibraryService relationFieldLibraryService;
    @Resource
    private ModelRelationMapper modelRelationMapper;
    @Resource
    @Lazy // 避免循环依赖
    private ModelService modelService;
    @Resource
    private SmartSearchableService smartSearchableService;
    @Resource
    @Lazy // 避免循环依赖
    private EntityTypeRelationService entityTypeRelationService;
    @Resource
    private ModelFieldGroupService modelFieldGroupService;
    @Resource
    @Lazy
    private BusinessCapabilityService businessCapabilityService;

    private void notifyModelFieldDefinitionChanged(Long modelId) {
        if (modelId == null) {
            return;
        }
        businessCapabilityService.refreshModelCrudFormDefinition(modelId);
    }

    /** 写入/更新分配时同步 model_id + model_code、field_id + field_code（迁移以 code 为幂等键）。 */
    private void syncAssignmentIdentity(ModelFieldAssignmentDO assignment, ModelDO model, FieldDO field) {
        if (model != null) {
            assignment.setModelId(model.getId());
            assignment.setModelCode(model.getCode());
        }
        if (field != null) {
            assignment.setFieldId(field.getId());
            assignment.setFieldCode(field.getCode());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignFieldToModel(Long modelId, Long fieldId, Boolean required, Boolean isSearchable, Boolean isFilterable, Boolean isSortable, String defaultValue, String validationRules) {
        // 校验模型存在
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null) {
            throw new ServiceException(404, "模型不存在");
        }

        // 校验字段存在
        FieldDO field = fieldMapper.selectById(fieldId);
        if (field == null) {
            throw new ServiceException(404, "字段不存在：" + fieldId);
        }

        Long tenantId = getTenantId();

        // 检查是否已分配（正常记录）
        ModelFieldAssignmentDO exist = modelFieldAssignmentMapper.selectByModelIdAndFieldId(modelId, fieldId);
        if (exist != null) {
            // 已存在,更新业务规则
            exist.setRequired(required != null ? required : exist.getRequired());
            exist.setIsSearchable(isSearchable != null ? isSearchable : exist.getIsSearchable());
            exist.setIsFilterable(isFilterable != null ? isFilterable : exist.getIsFilterable());
            exist.setIsSortable(isSortable != null ? isSortable : exist.getIsSortable());
            exist.setDefaultValue(defaultValue != null ? defaultValue : exist.getDefaultValue());
            exist.setValidationRules(validationRules != null ? validationRules : exist.getValidationRules());
            syncAssignmentIdentity(exist, model, field);
            modelFieldAssignmentMapper.updateById(exist);
        } else {
            // 不存在正常记录,检查是否有已删除的记录（用于恢复）
            ModelFieldAssignmentDO deletedRecord = modelFieldAssignmentMapper.selectByModelIdAndFieldIdWithDeleted(
                    modelId, fieldId, tenantId);
            if (deletedRecord != null) {
                // 找到已删除的记录,使用原生 SQL 恢复它并更新业务规则
                // 注意：不能使用 updateById,因为 MyBatis Plus 的逻辑删除机制会阻止更新已删除的记录
                // 如果新值为 null,则使用原记录的值
                Boolean finalRequired = required != null ? required : deletedRecord.getRequired();
                Boolean finalIsSearchable = isSearchable != null ? isSearchable : deletedRecord.getIsSearchable();
                Boolean finalIsSortable = isSortable != null ? isSortable : deletedRecord.getIsSortable();
                String finalDefaultValue = defaultValue != null ? defaultValue : deletedRecord.getDefaultValue();
                String finalValidationRules = validationRules != null ? validationRules : deletedRecord.getValidationRules();
                Integer finalSort = deletedRecord.getSort(); // 保持原有的 sort 值
                modelFieldAssignmentMapper.restoreAndUpdateDeletedRecord(
                        deletedRecord.getId(),
                        finalRequired,
                        finalIsSearchable,
                        finalIsSortable,
                        finalDefaultValue,
                        finalValidationRules,
                        finalSort);
            } else {
                // 完全不存在,创建新分配
                ModelFieldAssignmentDO assignment = new ModelFieldAssignmentDO();
                syncAssignmentIdentity(assignment, model, field);
                assignment.setRequired(required != null ? required : false);
                assignment.setIsSearchable(isSearchable);
                assignment.setIsFilterable(isFilterable);
                assignment.setIsSortable(isSortable);
                assignment.setDefaultValue(defaultValue);
                assignment.setValidationRules(validationRules);
                assignment.setTenantId(tenantId);
                modelFieldAssignmentMapper.insert(assignment);
            }
        }
        notifyModelFieldDefinitionChanged(modelId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int assignFieldsToModel(ModelFieldBatchAssignReqVO reqVO) {
        // 校验模型存在
        ModelDO model = modelMapper.selectById(reqVO.getModelId());
        if (model == null) {
            throw new ServiceException(404, "模型不存在");
        }

        Long tenantId = getTenantId();

        // 批量分配字段
        int affectedCount = 0;
        for (ModelFieldBatchAssignReqVO.FieldAssignmentItem item : reqVO.getFieldAssignments()) {
            // 校验字段存在
            FieldDO field = fieldMapper.selectById(item.getFieldId());
            if (field == null) {
                throw new ServiceException(404, "字段不存在：" + item.getFieldId());
            }

            boolean isEntityRef = FieldTypeEnum.isEntityRef(field.getType());

            // 检查是否已分配（正常记录）
            ModelFieldAssignmentDO exist = modelFieldAssignmentMapper.selectByModelIdAndFieldId(
                    reqVO.getModelId(), item.getFieldId());
            if (exist != null) {
                // 已存在,更新业务规则
                exist.setRequired(item.getRequired());
                exist.setIsSearchable(item.getIsSearchable());
                exist.setIsFilterable(item.getIsFilterable());
                exist.setIsSortable(item.getIsSortable());
                exist.setDefaultValue(item.getDefaultValue());
                exist.setValidationRules(item.getValidationRules());
                if (item.getSort() != null) {
                    exist.setSort(item.getSort());
                }
                // 如果是关联字段,保存前端选择的目标业务类型作为兜底信息
                if (isEntityRef) {
                    String target = StringUtils.hasText(item.getTargetEntityType())
                            ? item.getTargetEntityType().trim()
                            : ModelCrudFormFieldAssembler.resolveTargetEntityTypeFromField(field);
                    if (target != null) {
                        exist.setTargetEntityType(target);
                    }
                }
                syncAssignmentIdentity(exist, model, field);
                modelFieldAssignmentMapper.updateById(exist);
                affectedCount++;
            } else {
                // 不存在正常记录,检查是否有已删除的记录（用于恢复）
                ModelFieldAssignmentDO deletedRecord = modelFieldAssignmentMapper.selectByModelIdAndFieldIdWithDeleted(
                        reqVO.getModelId(), item.getFieldId(), tenantId);
                if (deletedRecord != null) {
                    // 找到已删除的记录,使用原生 SQL 恢复它并更新业务规则
                    // 注意：不能使用 updateById,因为 MyBatis Plus 的逻辑删除机制会阻止更新已删除的记录
                    // 如果新值为 null,则使用原记录的值
                    Boolean finalRequired = item.getRequired() != null ? item.getRequired() : deletedRecord.getRequired();
                    Boolean finalIsSearchable = item.getIsSearchable() != null ? item.getIsSearchable() : deletedRecord.getIsSearchable();
                    Boolean finalIsSortable = item.getIsSortable() != null ? item.getIsSortable() : deletedRecord.getIsSortable();
                    String finalDefaultValue = item.getDefaultValue() != null ? item.getDefaultValue() : deletedRecord.getDefaultValue();
                    String finalValidationRules = item.getValidationRules() != null ? item.getValidationRules() : deletedRecord.getValidationRules();
                    Integer finalSort = item.getSort() != null ? item.getSort() : deletedRecord.getSort();
                    modelFieldAssignmentMapper.restoreAndUpdateDeletedRecord(
                            deletedRecord.getId(),
                            finalRequired,
                            finalIsSearchable,
                            finalIsSortable,
                            finalDefaultValue,
                            finalValidationRules,
                            finalSort);
                    affectedCount++;
                } else {
                    // 完全不存在,创建新分配
                    ModelFieldAssignmentDO assignment = new ModelFieldAssignmentDO();
                    syncAssignmentIdentity(assignment, model, field);
                    assignment.setRequired(item.getRequired() != null ? item.getRequired() : false);
                    assignment.setIsSearchable(item.getIsSearchable());
                    assignment.setIsFilterable(item.getIsFilterable());
                    assignment.setIsSortable(item.getIsSortable());
                    assignment.setDefaultValue(item.getDefaultValue());
                    assignment.setValidationRules(item.getValidationRules());
                    assignment.setSort(item.getSort());
                    // 如果是关联字段,保存前端选择的目标业务类型作为兜底信息
                    if (isEntityRef) {
                        String target = StringUtils.hasText(item.getTargetEntityType())
                                ? item.getTargetEntityType().trim()
                                : ModelCrudFormFieldAssembler.resolveTargetEntityTypeFromField(field);
                        assignment.setTargetEntityType(target);
                    }
                    assignment.setTenantId(tenantId);
                    modelFieldAssignmentMapper.insert(assignment);
                    affectedCount++;
                }
            }
        }
        if (affectedCount > 0) {
            notifyModelFieldDefinitionChanged(reqVO.getModelId());
        }
        return affectedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unassignFieldFromModel(Long modelId, Long fieldId) {
        // 校验模型存在
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null) {
            throw new ServiceException(404, "模型不存在");
        }

        // 后端不再校验业务类型固定列字段：
        // 固定列字段删除限制由前端控制（前端已经禁止固定列字段的删除操作）
        // 这里只校验字段在 dynamic_field 中是否存在,避免传入非法字段 ID
        FieldDO field = fieldMapper.selectById(fieldId);
        if (field == null) {
            throw new ServiceException(404, "字段不存在：" + fieldId);
        }

        // 检查是否有Entity使用该字段
        String storageType = entityTypeScopeResolver.resolveStorageEntityTypeCode(model.getEntityTypeCode());
        if (!StringUtils.hasText(storageType)) {
            storageType = model.getEntityTypeCode();
        }
        long entityCount = entityRepository.count(EntityRepository.EntityQuery.builder()
                .entityTypeCode(storageType)
                .modelId(modelId)
                .build());
        if (entityCount > 0) {
            // 提示用户受影响Entity的数量
            throw new ServiceException(400, "模型存在关联的业务实体（数量：" + entityCount + "）,解除字段分配前请先处理这些实体的字段数据");
        }

        // 删除字段分配
        ModelFieldAssignmentDO assignment = modelFieldAssignmentMapper.selectByModelIdAndFieldId(modelId, fieldId);
        if (assignment != null) {
            modelFieldAssignmentMapper.deleteById(assignment.getId());
            notifyModelFieldDefinitionChanged(modelId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchUnassignFieldsFromModel(Long modelId, List<Long> fieldIds) {
        // 校验模型存在
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null) {
            throw new ServiceException(404, "模型不存在");
        }

        // 后端不再校验业务类型固定列字段（固定列删除限制由前端控制）
        // 这里只做基础的字段存在性校验,避免传入非法字段 ID
        if (fieldIds == null || fieldIds.isEmpty()) {
            return 0;
        }
        List<FieldDO> fields = fieldMapper.selectList(
                new LambdaQueryWrapperX<FieldDO>()
                        .in(FieldDO::getId, fieldIds));
        Set<Long> existingFieldIds = fields.stream()
                .map(FieldDO::getId)
                .collect(Collectors.toSet());
        List<Long> invalidFieldIds = fieldIds.stream()
                .filter(id -> !existingFieldIds.contains(id))
                .collect(Collectors.toList());
        if (!invalidFieldIds.isEmpty()) {
            throw new ServiceException(404, "部分字段不存在：" + invalidFieldIds);
        }

        // 检查是否有Entity使用该模型
        String storageType = entityTypeScopeResolver.resolveStorageEntityTypeCode(model.getEntityTypeCode());
        if (!StringUtils.hasText(storageType)) {
            storageType = model.getEntityTypeCode();
        }
        long entityCount = entityRepository.count(EntityRepository.EntityQuery.builder()
                .entityTypeCode(storageType)
                .modelId(modelId)
                .build());
        if (entityCount > 0) {
            // 提示用户受影响Entity的数量
            throw new ServiceException(400, "模型存在关联的业务实体（数量：" + entityCount + "）,解除字段分配前请先处理这些实体的字段数据");
        }

        // 批量删除字段分配（使用 IN 查询提高效率）
        if (fieldIds.isEmpty()) {
            return 0;
        }
        int deleted = modelFieldAssignmentMapper.delete(
                new LambdaQueryWrapperX<ModelFieldAssignmentDO>()
                        .eq(ModelFieldAssignmentDO::getModelId, modelId)
                        .in(ModelFieldAssignmentDO::getFieldId, fieldIds));
        if (deleted > 0) {
            notifyModelFieldDefinitionChanged(modelId);
        }
        return deleted;
    }

    @Override
    public List<ModelFieldAssignmentRespVO> getModelFields(Long modelId) {
        // 校验模型存在
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null) {
            throw new ServiceException(404, "模型不存在");
        }

        List<ModelFieldAssignmentRespVO> result = new ArrayList<>();
        Set<String> coveredBaseLibraryCodes = new HashSet<>();

        // 1. 模型字段分配（含固定列 BASE 与扩展 CUSTOM / 关联 RELATION）
        List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByModelId(modelId);
        if (!assignments.isEmpty()) {
            // 查询字段详情
            List<Long> fieldIds = assignments.stream()
                    .map(ModelFieldAssignmentDO::getFieldId)
                    .collect(Collectors.toList());
            List<FieldDO> fields = fieldMapper.selectList(
                    new LambdaQueryWrapperX<FieldDO>()
                            .in(FieldDO::getId, fieldIds));


            // 组装扩展字段响应数据
            for (ModelFieldAssignmentDO assignment : assignments) {
                FieldDO field = fields.stream()
                        .filter(f -> f.getId().equals(assignment.getFieldId()))
                        .findFirst()
                        .orElse(null);
                if (field == null) {
                    continue;
                }

                ModelFieldAssignmentRespVO respVO = new ModelFieldAssignmentRespVO();
                respVO.setField(FieldConvert.INSTANCE.convert(field));
                respVO.setRequired(assignment.getRequired());
                // 优先使用模型字段分配中的配置,如果为 null 则使用智能默认值
                Boolean isSearchable = assignment.getIsSearchable();
                if (isSearchable == null) {
                    isSearchable = smartSearchableService.getDefaultSearchable(field.getType());
                }
                respVO.setIsSearchable(isSearchable);

                respVO.setIsFilterable(assignment.getIsFilterable());

                Boolean isSortable = assignment.getIsSortable();
                if (isSortable == null) {
                    isSortable = smartSearchableService.getDefaultSortable(field.getType());
                }
                respVO.setIsSortable(isSortable);
                respVO.setDefaultValue(assignment.getDefaultValue());
                respVO.setValidationRules(assignment.getValidationRules());
                respVO.setSort(assignment.getSort());
                boolean isBaseAssignment =
                        ModelFieldAssignmentRespVO.FIELD_SOURCE_BASE.equals(assignment.getFieldSource());
                if (isBaseAssignment) {
                    respVO.setFieldSource(ModelFieldAssignmentRespVO.FIELD_SOURCE_BASE);
                    // 固定列不可从模型移除，但模型级规则（必填/搜索/排序/筛选）可在此配置
                    respVO.setEditable(true);
                    respVO.setDeletable(false);
                    String registeredFieldCode = registeredFieldCodeForLibrary(model.getEntityTypeCode(), field);
                    respVO.setFieldCode(registeredFieldCode);
                    coveredBaseLibraryCodes.add(field.getCode());
                    applyBaseFieldDisplayAlias(respVO, model.getEntityTypeCode(), registeredFieldCode);
                } else {
                    respVO.setFieldSource(ModelFieldAssignmentRespVO.FIELD_SOURCE_CUSTOM);
                    respVO.setEditable(true);
                    respVO.setDeletable(true);
                }

                // 如果是关联字段（ENTITY_REF 或 ENTITY_REF_MULTI 类型）,填充关联信息
                if (!isBaseAssignment && FieldTypeEnum.isEntityRef(field.getType())) {
                    // 关联字段信息从权威来源获取：RelationFieldLibrary / ModelRelation
                    if (assignment.getRefLibraryId() != null) {
                        // 1. 从 RelationFieldLibrary 查询
                        RelationFieldLibraryDO relationField = relationFieldLibraryMapper.selectById(assignment.getRefLibraryId());
                        if (relationField != null) {
                            respVO.setRefLibraryId(assignment.getRefLibraryId());
                            respVO.setTargetEntityType(relationField.getRefEntityType());
                            respVO.setDisplayFieldCode(relationField.getDisplayFieldCode());
                            respVO.setFieldSource(ModelFieldAssignmentRespVO.FIELD_SOURCE_RELATION);
                            
                            // 获取目标业务类型名称（模型名称不再使用）
                            String[] targetNames = relationFieldLibraryService.getTargetNames(
                                    relationField.getRefEntityType(),
                                    null);
                            if (targetNames != null) {
                                respVO.setTargetEntityTypeName(targetNames[0]);
                            }
                        }
                    } else if (assignment.getTargetEntityType() != null) {
                        // 2. 兜底方式：字段分配记录中保存了目标业务类型
                        respVO.setFieldSource(ModelFieldAssignmentRespVO.FIELD_SOURCE_RELATION);
                        respVO.setTargetEntityType(assignment.getTargetEntityType());
                        EntityTypeDO targetEntityType = entityTypeMapper.selectByCode(assignment.getTargetEntityType());
                        if (targetEntityType != null) {
                            respVO.setTargetEntityTypeName(targetEntityType.getName());
                        }
                    } else {
                        log.warn("[getModelFields] 关联字段缺少来源信息, fieldId={}, modelId={}", field.getId(), modelId);
                    }
                }
                
                result.add(respVO);
            }
        }

        // 2. 兜底：尚未写入模型分配的固定列（专用表自动继承）
        if (model.getEntityTypeCode() != null) {
            EntityTypeDO entityType = entityTypeMapper.selectByCode(model.getEntityTypeCode());
            if (entityType != null) {
                StorageTypeEnum storageType = StorageTypeEnum.getByCode(entityType.getStorageType());
                if (storageType != null && storageType.isDedicated()) {
                    List<EntityTypeBaseFieldRespVO> baseFields =
                            entityTypeBaseFieldService.listByEntityTypeCode(model.getEntityTypeCode());
                    for (EntityTypeBaseFieldRespVO baseField : baseFields) {
                        String libraryCode = baseField.getLibraryFieldCode() != null
                                ? baseField.getLibraryFieldCode()
                                : baseField.getFieldCode();
                        if (libraryCode != null && coveredBaseLibraryCodes.contains(libraryCode)) {
                            continue;
                        }
                        result.add(convertBaseFieldToAssignmentRespVO(baseField, model.getEntityTypeCode()));
                    }
                }
            }
        }

        // 3. 按 sort 排序返回
        // 固定列字段使用 sortOrder,扩展字段使用 sort
        // 固定列字段排在前面（sort 值较小）,扩展字段排在后面
        result.sort((a, b) -> {
            Integer sortA = a.getSort();
            Integer sortB = b.getSort();
            if (sortA == null && sortB == null) {
                // 如果都为 null,按字段来源排序（BASE 在前,CUSTOM 在后）
                if (!Objects.equals(a.getFieldSource(), b.getFieldSource())) {
                    return ModelFieldAssignmentRespVO.FIELD_SOURCE_BASE.equals(a.getFieldSource()) ? -1 : 1;
                }
                // 同来源按字段 ID 或 fieldCode 排序
                if (a.getField() != null && b.getField() != null) {
                    Long idA = a.getField().getId();
                    Long idB = b.getField().getId();
                    if (idA != null && idB != null) {
                        return idA.compareTo(idB);
                    }
                }
                return 0;
            }
            if (sortA == null) return 1; // sort 为 null 的排在后面
            if (sortB == null) return -1;
            int sortCompare = sortA.compareTo(sortB);
            // 如果 sort 相同,按字段来源排序（BASE 在前）
            if (sortCompare == 0 && !Objects.equals(a.getFieldSource(), b.getFieldSource())) {
                return ModelFieldAssignmentRespVO.FIELD_SOURCE_BASE.equals(a.getFieldSource()) ? -1 : 1;
            }
            return sortCompare;
        });

        return result;
    }

    /**
     * 将固定列字段转换为 ModelFieldAssignmentRespVO
     * 
     * @param baseField 固定列字段
     * @param sourceEntityTypeCode 源业务类型编码（用于查询关联关系）
     * @return 模型字段分配响应 VO
     */
    private void applyBaseFieldDisplayAlias(
            ModelFieldAssignmentRespVO respVO, String entityTypeCode, String physicalFieldCode) {
        if (respVO == null || respVO.getField() == null || !StringUtils.hasText(entityTypeCode)
                || !StringUtils.hasText(physicalFieldCode)) {
            return;
        }
        EntityTypeBaseFieldDO baseField =
                entityTypeBaseFieldService.getBaseFieldByCode(entityTypeCode, physicalFieldCode);
        if (baseField != null && StringUtils.hasText(baseField.getFieldName())) {
            respVO.getField().setName(baseField.getFieldName());
        }
    }

    private String registeredFieldCodeForLibrary(String entityTypeCode, FieldDO libraryField) {
        if (libraryField == null || !StringUtils.hasText(entityTypeCode)) {
            return null;
        }
        for (EntityTypeBaseFieldDO baseField : entityTypeBaseFieldService.getBaseFieldsByEntityTypeCode(entityTypeCode)) {
            if (baseField.getLibraryFieldId() != null
                    && Objects.equals(baseField.getLibraryFieldId(), libraryField.getId())) {
                return baseField.getFieldCode();
            }
        }
        return libraryField.getCode();
    }

    private ModelFieldAssignmentRespVO convertBaseFieldToAssignmentRespVO(EntityTypeBaseFieldRespVO baseField, String sourceEntityTypeCode) {
        ModelFieldAssignmentRespVO respVO = new ModelFieldAssignmentRespVO();

        FieldRespVO fieldRespVO;
        FieldDO libraryField = baseField.getLibraryFieldId() != null
                ? fieldMapper.selectById(baseField.getLibraryFieldId())
                : (StringUtils.hasText(baseField.getFieldCode()) ? fieldMapper.selectByCode(baseField.getFieldCode()) : null);
        if (libraryField != null) {
            fieldRespVO = FieldConvert.INSTANCE.convert(libraryField);
            if (StringUtils.hasText(baseField.getFieldName())
                    && !Objects.equals(baseField.getFieldName(), libraryField.getName())) {
                fieldRespVO.setName(baseField.getFieldName());
            }
        } else {
            // 字段库尚未补齐时保留虚拟字段，避免接口不可用
            fieldRespVO = new FieldRespVO();
            fieldRespVO.setCode(baseField.getFieldCode());
            fieldRespVO.setName(baseField.getFieldName());
            fieldRespVO.setType(baseField.getDataType());
            fieldRespVO.setDescription(baseField.getDescription());
            fieldRespVO.setSource("BASE");
            fieldRespVO.setStatus(baseField.getStatus());
            fieldRespVO.setCreateTime(baseField.getCreateTime());
            log.warn("[convertBaseFieldToAssignmentRespVO] 固定列未找到字段库记录: entityType={}, fieldCode={}",
                    sourceEntityTypeCode, baseField.getFieldCode());
        }

        respVO.setField(fieldRespVO);
        respVO.setRequired(baseField.getRequired());
        // 固定列字段的 isSearchable 和 isSortable 使用字段 definition 中的默认值（通常固定列字段都是可查询和可排序的）
        // 由于固定列字段没有 ModelFieldAssignment,这里设置为 null,前端可以根据字段类型智能判断
        respVO.setIsSearchable(true); // 固定列字段默认可搜索
        respVO.setIsFilterable(true); // 固定列字段默认可筛选
        respVO.setIsSortable(true);   // 固定列字段默认可排序
        respVO.setDefaultValue(baseField.getDefaultValue());
        respVO.setSort(baseField.getSortOrder()); // 使用 sortOrder 作为 sort
        
        // 设置字段来源标识（FR-139, FR-141）
        respVO.setFieldSource(ModelFieldAssignmentRespVO.FIELD_SOURCE_BASE);
        respVO.setEditable(false);   // 固定列字段不可编辑（BR-STG-015）
        respVO.setDeletable(false);  // 固定列字段不可删除（BR-STG-015）
        
        // 设置固定列字段专用属性
        respVO.setFieldCode(baseField.getFieldCode());
        respVO.setDataType(baseField.getDataType());
        respVO.setTypeConfig(baseField.getTypeConfig());
        
        // 如果是关联字段（REF_Multi 类型）,从 fieldCode 解析关联信息
        // fieldCode 格式为 REL_{targetEntityTypeCode},例如 REL_REGION
        if ("REF_Multi".equals(baseField.getDataType()) && baseField.getFieldCode() != null 
                && baseField.getFieldCode().startsWith("REL_")) {
            try {
                // 从 fieldCode 解析出 targetEntityTypeCode（去掉 "REL_" 前缀）
                // 注意：fieldCode 中的 targetEntityTypeCode 是大写的（如 REL_REGION）,
                // 但实际的 targetEntityTypeCode 可能是 "Region",需要不区分大小写匹配
                String fieldCodeSuffix = baseField.getFieldCode().substring(4); // 去掉 "REL_" 前缀
                
                // 查询 EntityType 关联关系,获取关联信息
                if (sourceEntityTypeCode != null) {
                    // 查询所有从源业务类型出发的关联关系
                    List<EntityTypeRelationRespVO> relations = entityTypeRelationService.getRelationsBySourceCode(sourceEntityTypeCode);
                    
                    // 不区分大小写匹配 targetEntityTypeCode
                    EntityTypeRelationRespVO relation = relations.stream()
                            .filter(r -> r.getTargetEntityTypeCode() != null 
                                    && r.getTargetEntityTypeCode().equalsIgnoreCase(fieldCodeSuffix))
                            .findFirst()
                            .orElse(null);
                    
                    if (relation != null) {
                        respVO.setTargetEntityType(relation.getTargetEntityTypeCode());
                        respVO.setTargetEntityTypeName(relation.getTargetEntityTypeName());
                        respVO.setFieldSource(ModelFieldAssignmentRespVO.FIELD_SOURCE_RELATION);
                        
                        log.debug("[convertBaseFieldToAssignmentRespVO][为关联字段设置关联信息: fieldCode={}, targetEntityType={}]",
                                baseField.getFieldCode(), relation.getTargetEntityTypeCode());
                    } else {
                        log.debug("[convertBaseFieldToAssignmentRespVO][未找到匹配的关联关系: fieldCode={}, sourceEntityType={}]",
                                baseField.getFieldCode(), sourceEntityTypeCode);
                    }
                }
            } catch (Exception e) {
                log.warn("[convertBaseFieldToAssignmentRespVO][解析关联字段信息失败: fieldCode={}, error={}]",
                        baseField.getFieldCode(), e.getMessage(), e);
            }
        }
        
        return respVO;
    }


    private Long getTenantId() {
        return Objects.requireNonNullElse(TenantContextHolder.getRequiredTenantId(), 0L);
    }

    /**
     * 检查指定的字段ID是否是固定列字段
     * 
     * 固定列字段是通过 EntityTypeBaseFieldService 管理的,
     * 它们自动继承到该业务类型下的所有 Model,不允许删除或修改。
     * 
     * @param entityTypeCode 业务类型编码
     * @param fieldId 字段ID（可能是固定列字段的ID）
     * @return 是否是固定列字段
     */

    // ========== 自定义关联字段（不依赖关联字段库）==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCustomRelationField(CustomRelationFieldCreateReqVO reqVO) {
        // 1. 校验模型存在
        ModelDO model = modelMapper.selectById(reqVO.getModelId());
        if (model == null) {
            throw new ServiceException(404, "模型不存在");
        }

        // 2. 验证关联目标已在当前 Model 启用（FR-BDA-032）
        // 注意：由于不再从 reqVO 直接获取 targetEntityType,改为通过 modelRelationId 校验
        if (reqVO.getModelRelationId() != null) {
            ModelRelationDO modelRelation = modelRelationMapper.selectById(reqVO.getModelRelationId());
            if (modelRelation == null || !modelRelation.getSourceModelId().equals(reqVO.getModelId())) {
                throw new ServiceException(400, "指定的模型关联不存在或不匹配");
            }
        }

        // 3. 检查字段编码是否已存在
        FieldDO existFieldByCode = fieldMapper.selectOne(new LambdaQueryWrapperX<FieldDO>()
                .eq(FieldDO::getCode, RelationFieldCodes.toModelFieldCode(reqVO.getFieldCode()))
                .eq(FieldDO::getTenantId, getTenantId()));
        if (existFieldByCode != null) {
            throw exception(RELATION_FIELD_CODE_DUPLICATE);
        }

        // 4. 检查字段名称是否已存在（修复：复用已存在的字段而不是创建新的）
        FieldDO existFieldByName = fieldMapper.selectOne(new LambdaQueryWrapperX<FieldDO>()
                .eq(FieldDO::getName, reqVO.getFieldName())
                .eq(FieldDO::getTenantId, getTenantId())
                .eq(FieldDO::getType, FieldTypeEnum.ENTITY_REF.getCode()));
        
        FieldDO field;
        if (existFieldByName != null) {
            // 字段名称已存在,复用该字段
            field = existFieldByName;
            log.info("[createCustomRelationField][复用已存在的字段: fieldId={}, fieldName={}, fieldCode={}]",
                    field.getId(), field.getName(), field.getCode());
            
            // 检查该字段是否已经分配给当前Model
            ModelFieldAssignmentDO existingAssignment = modelFieldAssignmentMapper.selectByModelIdAndFieldId(
                    reqVO.getModelId(), field.getId());
            if (existingAssignment != null) {
                // 字段已分配给当前Model,直接返回
                log.info("[createCustomRelationField][字段已分配给当前模型,复用分配记录: modelId={}, fieldId={}, assignmentId={}]",
                        reqVO.getModelId(), field.getId(), existingAssignment.getId());
                return existingAssignment.getId();
            }
            
            // 字段存在但未分配给当前Model,创建新的分配记录
            Long assignmentId = createAssignmentForExistingField(
                    reqVO.getModelId(),
                    field.getId(),
                    reqVO.getModelRelationId());
            
            log.info("[createCustomRelationField][为已存在的字段创建分配记录: modelId={}, fieldId={}, assignmentId={}]",
                    reqVO.getModelId(), field.getId(), assignmentId);
            return assignmentId;
        }

        Long refLibraryId = null;

        // 5. 创建字段定义
        field = new FieldDO();
        field.setCode(RelationFieldCodes.toModelFieldCode(reqVO.getFieldCode()));
        field.setName(reqVO.getFieldName());
        // REF字段使用ENTITY_REF_MULTI类型,支持同时关联多个对象
        field.setType(FieldTypeEnum.ENTITY_REF_MULTI.getCode());
        field.setDescription(reqVO.getDescription());
        field.setSource("CUSTOM"); // 用户自定义创建
        field.setStatus(1);
        field.setTenantId(getTenantId());
        fieldMapper.insert(field);

        // 6. 创建字段分配
        ModelFieldAssignmentDO assignment = new ModelFieldAssignmentDO();
        syncAssignmentIdentity(assignment, model, field);
        assignment.setRefLibraryId(refLibraryId);
        assignment.setRequired(reqVO.getRequired() != null ? reqVO.getRequired() : false);
        assignment.setSort(reqVO.getSort());
        assignment.setTenantId(getTenantId());
        
        // 业务关联流程优化：只存储关联ID
        assignment.setModelRelationId(reqVO.getModelRelationId());
        
        modelFieldAssignmentMapper.insert(assignment);

        log.info("[createCustomRelationField][为模型 {} 创建自定义关联字段 {},字段ID={},modelRelationId={}]",
                reqVO.getModelId(), reqVO.getFieldName(), field.getId(), reqVO.getModelRelationId());

        notifyModelFieldDefinitionChanged(reqVO.getModelId());
        return assignment.getId();
    }

    @Override
    public List<ModelFieldAssignmentRespVO> getModelRelationFields(Long modelId) {
        // 1. 校验模型存在
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null) {
            throw new ServiceException(404, "模型不存在");
        }

        // 2. 获取所有关联字段分配（refLibraryId 不为空 或 字段类型为 ENTITY_REF/ENTITY_REF_MULTI）
        List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByModelId(modelId);
        
        List<ModelFieldAssignmentRespVO> result = new ArrayList<>();

        for (ModelFieldAssignmentDO assignment : assignments) {
            // 查询字段详情
            FieldDO field = fieldMapper.selectById(assignment.getFieldId());
            if (field == null) {
                continue;
            }

            // 只返回关联字段（ENTITY_REF 或 ENTITY_REF_MULTI 类型）
            if (!FieldTypeEnum.isEntityRef(field.getType())) {
                continue;
            }

            ModelFieldAssignmentRespVO respVO = new ModelFieldAssignmentRespVO();
            respVO.setField(FieldConvert.INSTANCE.convert(field));
            respVO.setRequired(assignment.getRequired());
            respVO.setDefaultValue(assignment.getDefaultValue());
            respVO.setValidationRules(assignment.getValidationRules());
            respVO.setSort(assignment.getSort());

            // 设置字段来源标识
            respVO.setFieldSource(ModelFieldAssignmentRespVO.FIELD_SOURCE_CUSTOM);
            respVO.setEditable(true);
            respVO.setDeletable(true);

            // 关联字段信息从权威来源获取：RelationFieldLibrary / ModelRelation
            if (assignment.getRefLibraryId() != null) {
                // 1. 从 RelationFieldLibrary 查询
                RelationFieldLibraryDO relationField = relationFieldLibraryMapper.selectById(assignment.getRefLibraryId());
                if (relationField != null) {
                    respVO.setRefLibraryId(assignment.getRefLibraryId());
                    respVO.setTargetEntityType(relationField.getRefEntityType());
                    respVO.setDisplayFieldCode(relationField.getDisplayFieldCode());
                    respVO.setFieldSource(ModelFieldAssignmentRespVO.FIELD_SOURCE_RELATION);
                    
                    String[] targetNames = relationFieldLibraryService.getTargetNames(
                            relationField.getRefEntityType(),
                            null);
                    if (targetNames != null) {
                        respVO.setTargetEntityTypeName(targetNames[0]);
                    }
                }
            } else if (assignment.getModelRelationId() != null) {
                // 2. 从 ModelRelation 查询
                ModelRelationDO modelRelation = modelRelationMapper.selectById(assignment.getModelRelationId());
                if (modelRelation != null) {
                    respVO.setFieldSource(ModelFieldAssignmentRespVO.FIELD_SOURCE_RELATION);
                    
                    ModelDO targetModel = modelMapper.selectOne(new LambdaQueryWrapperX<ModelDO>()
                            .eq(ModelDO::getCode, modelRelation.getTargetModelCode())
                            .eq(ModelDO::getDeleted, false));
                    if (targetModel != null) {
                        respVO.setTargetEntityType(targetModel.getEntityTypeCode());
                        respVO.setTargetModelName(targetModel.getName());
                        EntityTypeDO targetEntityType = entityTypeMapper.selectByCode(targetModel.getEntityTypeCode());
                        if (targetEntityType != null) {
                            respVO.setTargetEntityTypeName(targetEntityType.getName());
                        }
                    }
                }
            } else if (assignment.getTargetEntityType() != null) {
                // 3. 兜底：从字段分配记录中读取前端选择的目标业务类型
                respVO.setFieldSource(ModelFieldAssignmentRespVO.FIELD_SOURCE_RELATION);
                respVO.setTargetEntityType(assignment.getTargetEntityType());
                EntityTypeDO targetEntityType = entityTypeMapper.selectByCode(assignment.getTargetEntityType());
                if (targetEntityType != null) {
                    respVO.setTargetEntityTypeName(targetEntityType.getName());
                }
            } else {
                // 关联信息缺失,记录警告日志
                log.warn("[getModelRelationFields][关联字段缺少关联目标信息,fieldId={}, fieldCode={}, modelId={}]",
                        field.getId(), field.getCode(), modelId);
            }

            result.add(respVO);
        }

        // 3. 按 sort 排序
        result.sort((a, b) -> {
            Integer sortA = a.getSort();
            Integer sortB = b.getSort();
            if (sortA == null && sortB == null) return 0;
            if (sortA == null) return 1;
            if (sortB == null) return -1;
            return sortA.compareTo(sortB);
        });

        return result;
    }

    // ========== 业务关联流程优化：级联删除支持 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByModelRelationId(Long modelRelationId) {
        if (modelRelationId == null) {
            return 0;
        }
        
        // 1. 查询需要删除的字段分配记录
        List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByModelRelationId(modelRelationId);
        if (assignments.isEmpty()) {
            log.info("[deleteByModelRelationId][未找到需要删除的字段分配记录,modelRelationId={}]", modelRelationId);
            return 0;
        }
        Set<Long> affectedModelIds = assignments.stream()
                .map(ModelFieldAssignmentDO::getModelId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        
        // 2. 删除关联的字段定义（Field）
        for (ModelFieldAssignmentDO assignment : assignments) {
            if (assignment.getFieldId() != null) {
                fieldMapper.deleteById(assignment.getFieldId());
                log.debug("[deleteByModelRelationId][删除字段定义,fieldId={}]", assignment.getFieldId());
            }
        }
        
        // 3. 删除字段分配记录
        int count = modelFieldAssignmentMapper.deleteByModelRelationId(modelRelationId);
        log.info("[deleteByModelRelationId][删除字段分配记录,modelRelationId={},删除数量={}]", modelRelationId, count);
        for (Long affectedModelId : affectedModelIds) {
            notifyModelFieldDefinitionChanged(affectedModelId);
        }
        
        return count;
    }

    // ========== 业务关联流程优化：数据修复支持（需求 7.5）==========

    @Override
    public FieldDO findFieldByCode(String fieldCode) {
        if (fieldCode == null || fieldCode.isEmpty()) {
            return null;
        }
        return fieldMapper.selectByCode(fieldCode);
    }

    @Override
    public ModelFieldAssignmentDO findAssignmentByModelIdAndFieldId(Long modelId, Long fieldId) {
        if (modelId == null || fieldId == null) {
            return null;
        }
        return modelFieldAssignmentMapper.selectByModelIdAndFieldId(modelId, fieldId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAssignmentForExistingField(Long modelId, Long fieldId, Long modelRelationId) {
        // 1. 校验模型存在
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null) {
            throw new ServiceException(404, "模型不存在");
        }

        // 2. 校验字段存在
        FieldDO field = fieldMapper.selectById(fieldId);
        if (field == null) {
            throw new ServiceException(404, "字段不存在：" + fieldId);
        }

        // 3. 检查是否已存在分配记录
        ModelFieldAssignmentDO existingAssignment = modelFieldAssignmentMapper.selectByModelIdAndFieldId(modelId, fieldId);
        if (existingAssignment != null) {
            log.warn("[createAssignmentForExistingField][字段分配已存在,直接返回: modelId={}, fieldId={}, assignmentId={}]",
                    modelId, fieldId, existingAssignment.getId());
            return existingAssignment.getId();
        }

        // 4. 创建新的字段分配记录
        ModelFieldAssignmentDO assignment = new ModelFieldAssignmentDO();
        syncAssignmentIdentity(assignment, model, field);
        assignment.setRequired(false);
        assignment.setSort(999); // 排在最后
        assignment.setTenantId(getTenantId());
        
        // 填充关联信息（仅存 relationId）
        assignment.setModelRelationId(modelRelationId);
        
        modelFieldAssignmentMapper.insert(assignment);
        
        log.info("[createAssignmentForExistingField][为已存在的字段创建分配记录: modelId={}, fieldId={}, assignmentId={}, modelRelationId={}]",
                modelId, fieldId, assignment.getId(), modelRelationId);
        
        notifyModelFieldDefinitionChanged(modelId);
        return assignment.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAssignmentRelationInfo(Long assignmentId, Long modelRelationId) {
        // 1. 查询字段分配记录
        ModelFieldAssignmentDO assignment = modelFieldAssignmentMapper.selectById(assignmentId);
        if (assignment == null) {
            throw new ServiceException(404, "字段分配不存在：" + assignmentId);
        }

        // 2. 更新关联信息（仅存 relationId,目标信息通过 relationId 查询）
        assignment.setModelRelationId(modelRelationId);
        
        modelFieldAssignmentMapper.updateById(assignment);
        
        log.info("[updateAssignmentRelationInfo][更新字段分配关联信息: assignmentId={}, modelRelationId={}]", assignmentId, modelRelationId);
        notifyModelFieldDefinitionChanged(assignment.getModelId());
    }

    // ========== 批量查询接口（性能优化）==========

    @Override
    public List<ModelFilterFieldMetaRespVO> getModelFilterFieldMeta(Long modelId) {
        List<ModelFieldAssignmentRespVO> fields = getModelFields(modelId);
        if (fields == null || fields.isEmpty()) {
            return List.of();
        }

        List<ModelFilterFieldMetaRespVO> result = new ArrayList<>();
        for (ModelFieldAssignmentRespVO item : fields) {
            if (item == null || item.getField() == null) {
                continue;
            }
            FieldRespVO f = item.getField();
            // 文本类字段走 keyword 搜索，不进入筛选面板
            if (isTextLikeType(f.getType())) {
                continue;
            }
            // 仅显式 isFilterable=true 的字段进入筛选面板
            if (!Boolean.TRUE.equals(item.getIsFilterable())) {
                continue;
            }
            ModelFilterFieldMetaRespVO meta = new ModelFilterFieldMetaRespVO();
            meta.setFieldId(f.getId());
            meta.setFieldCode(f.getCode());
            meta.setFieldName(f.getName());
            meta.setFieldType(f.getType());
            meta.setSearchable(item.getIsSearchable());
            meta.setFilterable(item.getIsFilterable());
            meta.setSortable(item.getIsSortable());
            meta.setOptions(f.getOptions());
            meta.setOperators(resolveOperatorsByFieldType(f.getType()));
            result.add(meta);
        }
        return result;
    }

    private boolean isTextLikeType(String rawType) {
        String t = rawType == null ? "" : rawType.trim().toUpperCase();
        return "TEXT".equals(t) || "STRING".equals(t) || "LONG_TEXT".equals(t);
    }

    private List<String> resolveOperatorsByFieldType(String rawType) {
        String t = rawType == null ? "" : rawType.trim().toUpperCase();
        return switch (t) {
            case "NUMBER", "INTEGER", "DECIMAL", "DOUBLE", "FLOAT", "LONG" -> List.of("EQ", "NE", "IN", "NOT_IN", "GTE", "LTE", "GT", "LT", "BETWEEN");
            case "DATE", "DATETIME", "TIMESTAMP" -> List.of("EQ", "NE", "GTE", "LTE", "GT", "LT", "BETWEEN");
            case "BOOLEAN", "BOOL" -> List.of("EQ", "NE");
            case "ENUM", "OPTION", "SELECT", "REFERENCE", "ENTITY_REF" -> List.of("EQ", "NE", "IN", "NOT_IN");
            case "MULTI_SELECT" -> List.of("IN", "NOT_IN", "CONTAINS", "LIKE");
            default -> List.of("EQ", "NE", "IN", "NOT_IN", "CONTAINS", "LIKE");
        };
    }

    @Override
    public java.util.Map<Long, List<ModelFieldAssignmentRespVO>> getModelFieldsBatch(List<Long> modelIds) {
        if (modelIds == null || modelIds.isEmpty()) {
            return java.util.Collections.emptyMap();
        }

        java.util.Map<Long, List<ModelFieldAssignmentRespVO>> result = new java.util.HashMap<>();

        // 为每个模型获取字段列表
        for (Long modelId : modelIds) {
            try {
                List<ModelFieldAssignmentRespVO> fields = getModelFields(modelId);
                result.put(modelId, fields);
            } catch (Exception e) {
                log.warn("[getModelFieldsBatch][获取模型 {} 字段失败: {}]", modelId, e.getMessage());
                result.put(modelId, java.util.Collections.emptyList());
            }
        }

        return result;
    }

    // ========== 批量分配关联字段（性能优化）==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchAssignAssociationFieldToModels(List<Long> modelIds, Long fieldId, String targetEntityTypeCode) {
        if (modelIds == null || modelIds.isEmpty()) {
            log.warn("[batchAssignAssociationFieldToModels][Model列表为空,跳过]");
            return 0;
        }

        if (fieldId == null) {
            throw new ServiceException(400, "字段ID不能为空");
        }

        FieldDO field = fieldMapper.selectById(fieldId);
        if (field == null) {
            throw new ServiceException(404, "字段不存在：" + fieldId);
        }

        Long tenantId = getTenantId();

        // 1. 批量查询Model是否存在
        List<ModelDO> models = modelMapper.selectByIds(modelIds);
        if (models.size() != modelIds.size()) {
            Set<Long> existingModelIds = models.stream()
                    .map(ModelDO::getId)
                    .collect(Collectors.toSet());
            List<Long> missingModelIds = modelIds.stream()
                    .filter(id -> !existingModelIds.contains(id))
                    .collect(Collectors.toList());
            log.warn("[batchAssignAssociationFieldToModels][部分Model不存在,跳过: {}]", missingModelIds);
            // 只处理存在的Model
            modelIds = new ArrayList<>(existingModelIds);
        }

        if (modelIds.isEmpty()) {
            return 0;
        }

        // 2. 批量查询已分配的字段（避免重复分配）
        List<ModelFieldAssignmentDO> existingAssignments = modelFieldAssignmentMapper.selectList(
                new LambdaQueryWrapperX<ModelFieldAssignmentDO>()
                        .in(ModelFieldAssignmentDO::getModelId, modelIds)
                        .eq(ModelFieldAssignmentDO::getFieldId, fieldId)
        );

        Set<Long> assignedModelIds = existingAssignments.stream()
                .map(ModelFieldAssignmentDO::getModelId)
                .collect(Collectors.toSet());

        // 3. 过滤出需要分配的Model
        List<Long> needAssignModelIds = modelIds.stream()
                .filter(id -> !assignedModelIds.contains(id))
                .collect(Collectors.toList());

        if (needAssignModelIds.isEmpty()) {
            log.info("[batchAssignAssociationFieldToModels][所有Model已分配该关联字段,跳过: fieldId={}, modelCount={}]",
                    fieldId, modelIds.size());
            return 0;
        }

        // 4. 批量插入ModelFieldAssignment记录
        Map<Long, ModelDO> modelById = models.stream()
                .collect(Collectors.toMap(ModelDO::getId, m -> m, (a, b) -> a));
        List<ModelFieldAssignmentDO> assignments = new ArrayList<>();
        for (Long modelId : needAssignModelIds) {
            ModelDO model = modelById.get(modelId);
            ModelFieldAssignmentDO assignment = new ModelFieldAssignmentDO();
            syncAssignmentIdentity(assignment, model, field);
            assignment.setRequired(false);  // 关联字段默认非必填
            assignment.setIsSearchable(false);  // 关联字段默认不可搜索
            assignment.setIsFilterable(true);   // 关联字段默认可筛选（下拉选择关联对象）
            assignment.setIsSortable(false);    // 关联字段默认不可排序
            assignment.setDefaultValue(null);
            assignment.setValidationRules(null);
            assignment.setTenantId(tenantId);
            assignments.add(assignment);
        }

        // 5. 批量插入（使用循环插入,因为MyBatis Plus的BaseMapperX可能没有insertBatch方法）
        // 如果后续有批量插入方法,可以优化为真正的批量插入
        int successCount = 0;
        for (ModelFieldAssignmentDO assignment : assignments) {
            try {
                modelFieldAssignmentMapper.insert(assignment);
                successCount++;
            } catch (Exception e) {
                log.warn("[batchAssignAssociationFieldToModels][分配字段失败,跳过: modelId={}, fieldId={}, error={}]",
                        assignment.getModelId(), fieldId, e.getMessage());
            }
        }

        log.info("[batchAssignAssociationFieldToModels][批量分配关联字段完成: fieldId={}, targetEntityType={}, " +
                        "totalModelCount={}, assignedCount={}, successCount={}]",
                fieldId, targetEntityTypeCode, modelIds.size(), needAssignModelIds.size(), successCount);

        if (successCount > 0) {
            for (Long modelId : needAssignModelIds) {
                notifyModelFieldDefinitionChanged(modelId);
            }
        }
        return successCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int smartAssignAssociationFieldToModels(List<Long> modelIds, Long fieldId, String targetEntityTypeCode) {
        if (modelIds == null || modelIds.isEmpty()) {
            return 0;
        }

        int modelCount = modelIds.size();

        // 智能判断：根据Model数量选择处理方式
        if (modelCount <= 10) {
            // 小批量：同步批量处理
            log.info("[smartAssignAssociationFieldToModels][小批量同步处理: modelCount={}]", modelCount);
            return batchAssignAssociationFieldToModels(modelIds, fieldId, targetEntityTypeCode);
        } else {
            // 中大批量：同步批量处理（短期方案）
            // 后续可以改为异步处理,提升用户体验
            log.info("[smartAssignAssociationFieldToModels][中大批量同步处理: modelCount={}]", modelCount);
            return batchAssignAssociationFieldToModels(modelIds, fieldId, targetEntityTypeCode);
        }
    }

}
