package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.relation.RelationFieldLibraryDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.relation.RelationFieldLibraryMapper;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.enums.field.FieldTypeEnum;
import cn.cheers.x.module.dynamicbusiness.service.capability.form.ModelCrudFormFieldAssembler;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeRelationService;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import cn.cheers.x.module.dynamicbusiness.service.relation.BidirectionalRelationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Entity 关联同步服务实现
 *
 * <p>在 Entity 创建/更新/删除时,自动同步 ENTITY_REF 和 ENTITY_REF_MULTI 字段的关联关系到 EntityRelationDO 表。</p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EntityRelationSyncServiceImpl implements EntityRelationSyncService {

    private final EntityRelationMapper entityRelationMapper;
    private final ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    private final FieldMapper fieldMapper;
    private final ModelMapper modelMapper;
    private final ModelRelationMapper modelRelationMapper;
    private final RelationFieldLibraryMapper relationFieldLibraryMapper;
    private final EntityTypeBaseFieldMapper entityTypeBaseFieldMapper;

    @Lazy
    private final EntityCoreService entityCoreService;

    @Lazy // 避免循环依赖
    private final BidirectionalRelationService bidirectionalRelationService;

    private final EntityTypeRelationService entityTypeRelationService;

    private final ObjectProvider<EntityRelationSyncServiceImpl> selfProvider;

    private static final String RELATION_TYPE_ENTITY_REF = "ENTITY_REF";
    private static final String RELATION_TYPE_ENTITY_REF_MULTI = "ENTITY_REF_MULTI";

    /**
     * 场景：实体创建后，同步 ENTITY_REF / ENTITY_REF_MULTI 字段到关系表。
     *
     * <p>Step 1：参数与字段配置校验；</p>
     * <p>Step 2：提取当前模型的引用字段；</p>
     * <p>Step 3：逐字段解析目标实体并落库关系。</p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncRelationsOnCreate(EntityDO entity, ModelDO model, Map<String, Object> customFields) {
        if (entity == null || model == null || CollectionUtils.isEmpty(customFields)) {
            return;
        }

        List<EntityRefFieldInfo> refFields = getEntityRefFields(model.getId());
        if (CollectionUtils.isEmpty(refFields)) {
            return;
        }

        refFields.forEach(fieldInfo -> processFieldRelationOnCreate(entity, model, customFields, fieldInfo));
    }

    /**
     * 场景：处理“单个引用字段”的创建同步。
     *
     * <p>Step 1：从 customFields 读取字段值；</p>
     * <p>Step 2：按单/多引用解析目标实体ID；</p>
     * <p>Step 3：过滤无效目标并创建关系。</p>
     */
    private void processFieldRelationOnCreate(EntityDO entity, ModelDO model,
                                                Map<String, Object> customFields,
                                                EntityRefFieldInfo fieldInfo) {
        Object value = customFields.get(fieldInfo.getFieldCode());
        if (value == null) {
            return;
        }

        List<EntityRefValue> refs = fieldInfo.isMultiRef()
                ? parseEntityRefList(value)
                : Optional.ofNullable(parseEntityRef(value)).map(List::of).orElse(Collections.emptyList());

        if (CollectionUtils.isEmpty(refs)) {
            return;
        }

        List<EntityRefValue> validRefs = validateTargetEntitiesExist(refs, fieldInfo);
        validRefs.forEach(ref -> {
            createRelation(entity, model, fieldInfo, ref, fieldInfo.isMultiRef());
            clearStatisticsCacheForTarget(ref.getId());
        });
    }

    /**
     * 场景：实体更新后，同步引用字段的差异关系。
     *
     * <p>Step 1：归一化新旧字段值（防空）；</p>
     * <p>Step 2：提取模型中的引用字段；</p>
     * <p>Step 3：按单值/多值引用分别执行增删差异同步。</p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncRelationsOnUpdate(EntityDO entity, ModelDO model,
                                        Map<String, Object> newCustomFields,
                                        Map<String, Object> oldCustomFields) {
        if (entity == null || model == null) return;

        Map<String, Object> actualNewFields = newCustomFields != null ? newCustomFields : Collections.emptyMap();
        Map<String, Object> actualOldFields = oldCustomFields != null ? oldCustomFields : Collections.emptyMap();

        List<EntityRefFieldInfo> refFields = getEntityRefFields(model.getId());
        for (EntityRefFieldInfo fieldInfo : refFields) {
            Object newValue = actualNewFields.get(fieldInfo.getFieldCode());
            Object oldValue = actualOldFields.get(fieldInfo.getFieldCode());

            if (fieldInfo.isMultiRef()) {
                syncMultiRefFieldUpdate(entity, model, fieldInfo, newValue, oldValue);
            } else {
                syncSingleRefFieldUpdate(entity, model, fieldInfo, newValue, oldValue);
            }
        }
    }

    private void syncSingleRefFieldUpdate(EntityDO entity, ModelDO model,
                                            EntityRefFieldInfo fieldInfo,
                                            Object newValue, Object oldValue) {
        EntityRefValue newRef = parseEntityRef(newValue);
        EntityRefValue oldRef = parseEntityRef(oldValue);

        Long newTargetId = newRef == null ? null : newRef.getId();
        Long oldTargetId = oldRef == null ? null : oldRef.getId();

        if (Objects.equals(newTargetId, oldTargetId)) {
            // 目标未变时仍补齐缺失的关联行（例如历史只写了固定列）
            if (newRef != null && validateTargetEntityExists(newRef, fieldInfo)) {
                createRelation(entity, model, fieldInfo, newRef, false);
            }
            return;
        }

        if (oldTargetId != null) {
            deleteRelationByTarget(entity.getId(), fieldInfo.getFieldCode(), oldTargetId);
            clearStatisticsCacheForTarget(oldTargetId);
        }

        if (newRef != null && validateTargetEntityExists(newRef, fieldInfo)) {
            createRelation(entity, model, fieldInfo, newRef, false);
            clearStatisticsCacheForTarget(newRef.getId());
        }
    }

    private void syncMultiRefFieldUpdate(EntityDO entity, ModelDO model,
                                                EntityRefFieldInfo fieldInfo,
                                                Object newValue, Object oldValue) {
        List<EntityRefValue> newRefs = parseEntityRefList(newValue);
        List<EntityRefValue> oldRefs = parseEntityRefList(oldValue);

        Map<Long, EntityRefValue> newMap = new LinkedHashMap<>();
        for (EntityRefValue ref : newRefs) {
            if (ref != null && ref.getId() != null) {
                newMap.put(ref.getId(), ref);
            }
        }
        Map<Long, EntityRefValue> oldMap = new LinkedHashMap<>();
        for (EntityRefValue ref : oldRefs) {
            if (ref != null && ref.getId() != null) {
                oldMap.put(ref.getId(), ref);
            }
        }

        Set<Long> newIdSet = new HashSet<>(newMap.keySet());
        Set<Long> oldIdSet = new HashSet<>(oldMap.keySet());

        if (newIdSet.equals(oldIdSet)) return;

        Set<Long> toDelete = new HashSet<>(oldIdSet);
        toDelete.removeAll(newIdSet);

        Set<Long> toAdd = new HashSet<>(newIdSet);
        toAdd.removeAll(oldIdSet);

        toDelete.forEach(targetId -> {
            deleteRelationByTarget(entity.getId(), fieldInfo.getFieldCode(), targetId);
            clearStatisticsCacheForTarget(targetId);
        });

        toAdd.stream()
                .map(newMap::get)
                .filter(Objects::nonNull)
                .filter(ref -> validateTargetEntityExists(ref, fieldInfo))
                .forEach(ref -> {
                    createRelation(entity, model, fieldInfo, ref, true);
                    clearStatisticsCacheForTarget(ref.getId());
                });
    }

    /**
     * 场景：实体删除时清理其作为 source 的全部关系。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncRelationsOnDelete(Long entityId, String entityTypeCode) {
        if (entityId == null) return;
        entityRelationMapper.deleteBySourceEntityId(entityId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncRelationsForMigration(EntityDO entity, ModelDO model,
                                            Map<String, Object> customFields,
                                            boolean clearExisting) {
        if (entity == null || model == null) return;
        if (clearExisting) {
            entityRelationMapper.deleteBySourceEntityId(entity.getId());
        }
        if (!CollectionUtils.isEmpty(customFields)) {
            EntityRelationSyncServiceImpl self = selfProvider.getIfAvailable();
            if (self != null) {
                self.syncRelationsOnCreate(entity, model, customFields);
            } else {
                this.syncRelationsOnCreate(entity, model, customFields);
            }
        }
    }

    /**
     * 场景：按模型提取“引用类字段”元数据，用于后续关系同步。
     *
     * <p>Step 1：读取模型字段分配；</p>
     * <p>Step 2：批量读取字段定义；</p>
     * <p>Step 3：筛出 ENTITY_REF / ENTITY_REF_MULTI 并补全元数据。</p>
     */
    private List<EntityRefFieldInfo> getEntityRefFields(Long modelId) {
        List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByModelId(modelId);
        if (CollectionUtils.isEmpty(assignments)) return Collections.emptyList();

        ModelDO model = modelMapper.selectById(modelId);
        String entityTypeCode = model != null && StringUtils.hasText(model.getEntityTypeCode())
                ? model.getEntityTypeCode().trim()
                : null;
        Map<String, EntityTypeBaseFieldDO> baseFieldByCode = loadBaseFieldsByCode(entityTypeCode);

        List<Long> fieldIds = assignments.stream()
                .map(ModelFieldAssignmentDO::getFieldId)
                .filter(Objects::nonNull)
                .toList();

        if (CollectionUtils.isEmpty(fieldIds)) return Collections.emptyList();

        List<FieldDO> fields = fieldMapper.selectList(FieldDO::getId, fieldIds);
        Map<Long, FieldDO> fieldMap = new HashMap<>();
        fields.forEach(f -> fieldMap.put(f.getId(), f));

        List<EntityRefFieldInfo> result = new ArrayList<>();
        for (ModelFieldAssignmentDO assignment : assignments) {
            FieldDO field = fieldMap.get(assignment.getFieldId());
            if (field == null || !StringUtils.hasText(field.getCode())) {
                continue;
            }
            EntityTypeBaseFieldDO baseField = baseFieldByCode.get(field.getCode().trim());
            // 字段库或固定列配成引用，均纳入同步
            if (!FieldTypeEnum.isEntityRef(field.getType()) && !isBaseFieldRef(baseField)) {
                continue;
            }

            EntityRefFieldInfo info = new EntityRefFieldInfo();
            info.setFieldId(field.getId());
            info.setFieldCode(field.getCode());
            info.setFieldName(field.getName());
            info.setFieldType(field.getType());
            info.setMultiRef(resolveMultiRef(field, baseField));
            resolveMetadata(assignment, info);
            result.add(info);
        }
        return result;
    }

    private Map<String, EntityTypeBaseFieldDO> loadBaseFieldsByCode(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            return Map.of();
        }
        List<EntityTypeBaseFieldDO> baseFields = entityTypeBaseFieldMapper.selectByEntityTypeCode(entityTypeCode);
        if (CollectionUtils.isEmpty(baseFields)) {
            return Map.of();
        }
        Map<String, EntityTypeBaseFieldDO> byCode = new HashMap<>();
        for (EntityTypeBaseFieldDO baseField : baseFields) {
            if (baseField != null && StringUtils.hasText(baseField.getFieldCode())) {
                byCode.put(baseField.getFieldCode().trim(), baseField);
            }
        }
        return byCode;
    }

    private static boolean isBaseFieldRef(EntityTypeBaseFieldDO baseField) {
        if (baseField == null || !StringUtils.hasText(baseField.getDataType())) {
            return false;
        }
        String normalized = ModelCrudFormFieldAssembler.normalizeFieldType(baseField.getDataType());
        return FieldTypeEnum.isEntityRef(normalized) || "REFERENCE".equalsIgnoreCase(normalized);
    }

    /**
     * 多选判定：字段库 ENTITY_REF_MULTI，或固定列 data_type=REF_Multi。
     * 固定列配多选而字段库仍为单选时，以固定列为准，避免表单多选写入后同步按单选解析。
     */
    private static boolean resolveMultiRef(FieldDO field, EntityTypeBaseFieldDO baseField) {
        if (field != null && (FieldTypeEnum.isMultiEntityRef(field.getType())
                || "BATCH_ENTITY_REF".equalsIgnoreCase(field.getType()))) {
            return true;
        }
        if (baseField != null && StringUtils.hasText(baseField.getDataType())) {
            String normalized = ModelCrudFormFieldAssembler.normalizeFieldType(baseField.getDataType());
            return FieldTypeEnum.isMultiEntityRef(normalized)
                    || "BATCH_ENTITY_REF".equalsIgnoreCase(normalized);
        }
        return false;
    }

    private void resolveMetadata(ModelFieldAssignmentDO assignment, EntityRefFieldInfo info) {
        if (assignment.getRefLibraryId() != null) {
            RelationFieldLibraryDO lib = relationFieldLibraryMapper.selectById(assignment.getRefLibraryId());
            if (lib != null && StringUtils.hasText(lib.getRefEntityType())) {
                info.setRefEntityType(lib.getRefEntityType().trim());
            }
        } else if (assignment.getModelRelationId() != null) {
            ModelRelationDO rel = modelRelationMapper.selectById(assignment.getModelRelationId());
            if (rel != null) {
                info.setTargetModelCode(rel.getTargetModelCode());
                if (StringUtils.hasText(rel.getTargetModelCode())) {
                    ModelDO targetModel = modelMapper.selectByCode(rel.getTargetModelCode().trim());
                    if (targetModel != null && StringUtils.hasText(targetModel.getEntityTypeCode())) {
                        info.setRefEntityType(targetModel.getEntityTypeCode().trim());
                    }
                }
            }
        } else if (StringUtils.hasText(assignment.getTargetEntityType())) {
            info.setRefEntityType(assignment.getTargetEntityType().trim());
        }
    }

    /**
     * 场景：创建单条 EntityRelation 记录。
     *
     * <p>Step 1：解析目标模型与目标业务类型；</p>
     * <p>Step 2：组装 EntityRelationDO；</p>
     * <p>Step 3：写库并继承租户信息。</p>
     */
    private void createRelation(EntityDO entity, ModelDO model,
                                EntityRefFieldInfo fieldInfo, EntityRefValue targetRef,
                                boolean isMultiRef) {
        Long targetEntityId = targetRef.getId();
        String targetModelCode = fieldInfo.getTargetModelCode();
        String refEntityTypeCode = targetRef.getEntityTypeCode();

        if ((refEntityTypeCode == null || refEntityTypeCode.isBlank()) && fieldInfo.getRefEntityType() != null) {
            refEntityTypeCode = fieldInfo.getRefEntityType();
        }

        if (targetModelCode != null && (refEntityTypeCode == null || refEntityTypeCode.isBlank())) {
            ModelDO targetModel = modelMapper.selectByCode(targetModelCode);
            if (targetModel != null) {
                refEntityTypeCode = targetModel.getEntityTypeCode();
            }
        }

        // 1) 强校验：业务门禁必须存在
        if (model.getEntityTypeCode() == null || refEntityTypeCode == null || refEntityTypeCode.isBlank()) {
            throw new ServiceException(400, "ASSOC_PARAM_INVALID: 关联参数缺失");
        }
        if (!entityTypeRelationService.existsRelation(model.getEntityTypeCode(), refEntityTypeCode)) {
            throw new ServiceException(400, String.format(
                    "ASSOC_GATE_DENIED: 当前业务 %s 不允许关联目标业务 %s，请在「业务类型关联」中配置",
                    model.getEntityTypeCode(), refEntityTypeCode));
        }

        // 2) 去重校验：同 source + fieldCode + target 的有效关联不重复写入
        EntityRelationDO existing = entityRelationMapper.selectOne(new cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX<EntityRelationDO>()
                .eq(EntityRelationDO::getSourceEntityId, entity.getId())
                .eq(EntityRelationDO::getFieldCode, fieldInfo.getFieldCode())
                .eq(EntityRelationDO::getTargetEntityId, targetEntityId)
                .eq(EntityRelationDO::getDeleted, false));
        if (existing != null) {
            return;
        }

        EntityRelationDO relation = EntityRelationDO.builder()
                .sourceEntityId(entity.getId())
                .targetEntityId(targetEntityId)
                .relationType(isMultiRef ? RELATION_TYPE_ENTITY_REF_MULTI : RELATION_TYPE_ENTITY_REF)
                .relationName(fieldInfo.getFieldName())
                .fieldCode(fieldInfo.getFieldCode())
                .sourceModelCode(model.getCode())
                .targetModelCode(targetModelCode)
                .sourceEntityTypeCode(model.getEntityTypeCode())
                .targetEntityTypeCode(refEntityTypeCode)
                .status(1)
                .build();

        relation.setTenantId(entity.getTenantId());
        entityRelationMapper.insert(relation);
    }

    private void deleteRelationByTarget(Long sourceEntityId, String fieldCode, Long targetEntityId) {
        entityRelationMapper.deleteBySourceEntityIdAndFieldCodeAndTargetEntityId(
                sourceEntityId, fieldCode, targetEntityId);
    }

    private EntityRefValue parseEntityRef(Object value) {
        if (value == null) return null;
        // 空数组 / 空集合：视为未填写（多选控件清空时常提交 []）
        if (value instanceof Collection<?> collection) {
            if (collection.isEmpty()) {
                return null;
            }
            // 单选路径偶发收到单元素数组时，取首项
            return parseEntityRef(collection.iterator().next());
        }
        if (value instanceof Map<?, ?> map) {
            Object idObj = map.get("id");
            Object typeObj = firstNonBlankMapValue(map, "entityTypeCode", "bizCode");
            Long id = parseLong(idObj);
            String entityTypeCode = typeObj == null ? null : String.valueOf(typeObj).trim();
            if (id == null || entityTypeCode == null || entityTypeCode.isBlank()) {
                throw new ServiceException(400, "ASSOC_PARAM_INVALID: Ref 必须包含 entityTypeCode 与 id");
            }
            EntityRefValue ref = new EntityRefValue();
            ref.setId(id);
            ref.setEntityTypeCode(entityTypeCode);
            return ref;
        }
        throw new ServiceException(400, "ASSOC_PARAM_INVALID: Ref 字段格式错误，必须为 {entityTypeCode,id}");
    }

    private Object firstNonBlankMapValue(Map<?, ?> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value == null) {
                continue;
            }
            if (value instanceof String text && text.isBlank()) {
                continue;
            }
            return value;
        }
        return null;
    }

    private Long parseLong(Object value) {
        if (value == null) return null;
        if (value instanceof Number n) return n.longValue();
        if (value instanceof String s && !s.isEmpty()) {
            try { return Long.parseLong(s); } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    private void clearStatisticsCacheForTarget(Long targetEntityId) {
        if (targetEntityId == null || bidirectionalRelationService == null) return;
        try {
            bidirectionalRelationService.clearStatisticsCache(targetEntityId);
        } catch (Exception e) {
            log.warn("[clearStatisticsCacheForTarget] 清除缓存失败,targetEntityId={}", targetEntityId, e);
        }
    }

    @lombok.Data
    private static class EntityRefFieldInfo {
        private Long fieldId;
        private String fieldCode;
        private String fieldName;
        private String fieldType;
        private boolean multiRef;
        private Integer maxRelations;
        private String refEntityType;
        private String targetModelCode;
    }

    private List<EntityRefValue> parseEntityRefList(Object value) {
        if (value == null) return Collections.emptyList();
        // 后端配多选时，前端只传单个 {entityTypeCode,id} 也兼容
        if (value instanceof Map<?, ?>) {
            EntityRefValue ref = parseEntityRef(value);
            return ref == null ? Collections.emptyList() : List.of(ref);
        }
        if (value instanceof List<?> list) {
            if (list.isEmpty()) {
                return Collections.emptyList();
            }
            List<EntityRefValue> refs = new ArrayList<>();
            for (Object item : list) {
                EntityRefValue ref = parseEntityRef(item);
                if (ref != null) {
                    refs.add(ref);
                }
            }
            return refs;
        }
        throw new ServiceException(400, "ASSOC_PARAM_INVALID: RefMulti 字段格式错误，必须为 [{entityTypeCode,id},...]");
    }

    private boolean validateTargetEntityExists(EntityRefValue ref, EntityRefFieldInfo fieldInfo) {
        if (ref == null || ref.getId() == null || ref.getEntityTypeCode() == null || ref.getEntityTypeCode().isBlank()) {
            return false;
        }
        if (fieldInfo.getRefEntityType() != null && !fieldInfo.getRefEntityType().isBlank()
                && !fieldInfo.getRefEntityType().equals(ref.getEntityTypeCode())) {
            throw new ServiceException(400, "ASSOC_PARAM_INVALID: 关联业务类型与字段配置不一致");
        }
        return entityCoreService.existsById(ref.getId(), ref.getEntityTypeCode());
    }

    private List<EntityRefValue> validateTargetEntitiesExist(List<EntityRefValue> refs, EntityRefFieldInfo fieldInfo) {
        if (CollectionUtils.isEmpty(refs)) return Collections.emptyList();
        List<EntityRefValue> valid = new ArrayList<>();
        for (EntityRefValue ref : refs) {
            if (validateTargetEntityExists(ref, fieldInfo)) {
                valid.add(ref);
            }
        }
        return valid;
    }

    @lombok.Data
    private static class EntityRefValue {
        private Long id;
        private String entityTypeCode;
    }
}
