package cn.cheers.x.module.dynamicbusiness.service.entity.refcategory;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryEntityLinkDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.relation.RelationFieldLibraryDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.relation.RelationFieldLibraryMapper;
import cn.cheers.x.module.dynamicbusiness.enums.field.FieldTypeEnum;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityBaseFieldColumnNames;
import cn.cheers.x.module.dynamicbusiness.service.capability.form.ModelCrudFormFieldAssembler;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryEntityLinkService;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityCategoryRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 通用 REF → 分类即实体投影：扫描主体上全部 REF 字段，目标有 link 则写分类–实体。
 */
@Service
@Slf4j
public class EntityRefCategoryProjectionServiceImpl implements EntityRefCategoryProjectionService {

    /** 历史回填仍用设施所属区域列。 */
    public static final String SUBJECT_FACILITY = "facility";

    private final CategoryEntityLinkService categoryEntityLinkService;
    private final EntityCategoryRelationService entityCategoryRelationService;
    private final JdbcTemplate jdbcTemplate;
    private final ModelMapper modelMapper;
    private final ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    private final FieldMapper fieldMapper;
    private final EntityTypeBaseFieldMapper entityTypeBaseFieldMapper;
    private final RelationFieldLibraryMapper relationFieldLibraryMapper;
    private final ModelRelationMapper modelRelationMapper;

    public EntityRefCategoryProjectionServiceImpl(
            CategoryEntityLinkService categoryEntityLinkService,
            @Lazy EntityCategoryRelationService entityCategoryRelationService,
            JdbcTemplate jdbcTemplate,
            ModelMapper modelMapper,
            ModelFieldAssignmentMapper modelFieldAssignmentMapper,
            FieldMapper fieldMapper,
            EntityTypeBaseFieldMapper entityTypeBaseFieldMapper,
            RelationFieldLibraryMapper relationFieldLibraryMapper,
            ModelRelationMapper modelRelationMapper) {
        this.categoryEntityLinkService = categoryEntityLinkService;
        this.entityCategoryRelationService = entityCategoryRelationService;
        this.jdbcTemplate = jdbcTemplate;
        this.modelMapper = modelMapper;
        this.modelFieldAssignmentMapper = modelFieldAssignmentMapper;
        this.fieldMapper = fieldMapper;
        this.entityTypeBaseFieldMapper = entityTypeBaseFieldMapper;
        this.relationFieldLibraryMapper = relationFieldLibraryMapper;
        this.modelRelationMapper = modelRelationMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void projectOnCreate(EntityDO entity, Map<String, Object> fieldValues) {
        if (!isProjectable(entity) || CollectionUtils.isEmpty(fieldValues)) {
            return;
        }
        String subjectType = entity.getEntityTypeCode().trim();
        for (RefFieldSpec field : listRefFields(entity.getModelId(), subjectType)) {
            if (!fieldValues.containsKey(field.fieldCode())) {
                continue;
            }
            Set<TargetRef> targets = extractTargets(fieldValues.get(field.fieldCode()), field.defaultTargetType());
            for (TargetRef target : targets) {
                associateIfLinked(entity.getId(), subjectType, target);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void projectOnUpdate(EntityDO entity, Map<String, Object> newFieldValues, Map<String, Object> oldFieldValues) {
        if (!isProjectable(entity) || CollectionUtils.isEmpty(newFieldValues)) {
            return;
        }
        String subjectType = entity.getEntityTypeCode().trim();
        Map<String, Object> oldBag = oldFieldValues != null ? oldFieldValues : Map.of();
        for (RefFieldSpec field : listRefFields(entity.getModelId(), subjectType)) {
            if (!newFieldValues.containsKey(field.fieldCode())) {
                continue;
            }
            Set<TargetRef> newTargets = extractTargets(newFieldValues.get(field.fieldCode()), field.defaultTargetType());
            Set<TargetRef> oldTargets = extractTargets(oldBag.get(field.fieldCode()), field.defaultTargetType());
            if (field.multiRef()) {
                for (TargetRef removed : difference(oldTargets, newTargets)) {
                    disassociateIfLinked(entity.getId(), subjectType, removed);
                }
                for (TargetRef added : difference(newTargets, oldTargets)) {
                    associateIfLinked(entity.getId(), subjectType, added);
                }
            } else {
                TargetRef oldOne = oldTargets.isEmpty() ? null : oldTargets.iterator().next();
                TargetRef newOne = newTargets.isEmpty() ? null : newTargets.iterator().next();
                if (sameTarget(oldOne, newOne)) {
                    continue;
                }
                if (oldOne != null) {
                    disassociateIfLinked(entity.getId(), subjectType, oldOne);
                }
                if (newOne != null) {
                    associateIfLinked(entity.getId(), subjectType, newOne);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int backfillFacilityRegionCategoryRelations() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                """
                SELECT id, fld_base_facility_ref_region AS fld_base_facility_ref_region
                FROM ent_facility
                WHERE deleted = false
                  AND fld_base_facility_ref_region IS NOT NULL
                """);
        if (CollectionUtils.isEmpty(rows)) {
            return 0;
        }
        int ok = 0;
        for (Map<String, Object> row : rows) {
            Long facilityEntityId = toLong(row.get("id"));
            Long regionEntityId = toLong(row.get("fld_base_facility_ref_region"));
            if (facilityEntityId == null || regionEntityId == null) {
                continue;
            }
            Long categoryId = resolveCategoryId(new TargetRef("region", regionEntityId));
            if (categoryId == null) {
                log.debug("[ref→category] 回填跳过：区域实体无分类即实体 link, facilityEntityId={}, regionEntityId={}",
                        facilityEntityId, regionEntityId);
                continue;
            }
            entityCategoryRelationService.associate(facilityEntityId, categoryId, SUBJECT_FACILITY);
            ok++;
        }
        log.info("[ref→category] 设施所属区域 → 区域分类 回填完成, count={}", ok);
        return ok;
    }

    private void associateIfLinked(Long subjectId, String subjectType, TargetRef target) {
        Long categoryId = resolveCategoryId(target);
        if (categoryId == null) {
            return;
        }
        entityCategoryRelationService.associate(subjectId, categoryId, subjectType);
        log.info("[ref→category] 关联: subjectId={}, subjectType={}, categoryId={}, targetType={}, targetId={}",
                subjectId, subjectType, categoryId, target.entityTypeCode(), target.id());
    }

    private void disassociateIfLinked(Long subjectId, String subjectType, TargetRef target) {
        Long categoryId = resolveCategoryId(target);
        if (categoryId == null) {
            return;
        }
        entityCategoryRelationService.disassociate(subjectId, categoryId, subjectType);
        log.info("[ref→category] 解绑: subjectId={}, subjectType={}, categoryId={}, targetType={}, targetId={}",
                subjectId, subjectType, categoryId, target.entityTypeCode(), target.id());
    }

    private Long resolveCategoryId(TargetRef target) {
        if (target == null || target.id() == null || target.id() <= 0) {
            return null;
        }
        CategoryEntityLinkDO link;
        if (StringUtils.hasText(target.entityTypeCode())) {
            link = categoryEntityLinkService.getLinkByEntityIdAndEntityTypeCode(
                    target.id(), target.entityTypeCode().trim());
        } else {
            link = categoryEntityLinkService.getLinkByEntityId(target.id());
        }
        if (link == null || link.getCategoryId() == null) {
            return null;
        }
        return link.getCategoryId();
    }

    private static boolean isProjectable(EntityDO entity) {
        return entity != null
                && entity.getId() != null
                && StringUtils.hasText(entity.getEntityTypeCode());
    }

    /**
     * 主体上的 REF 字段：基础字段 + 型号分配字段（同编码以分配侧为准）。
     */
    private List<RefFieldSpec> listRefFields(Long modelId, String subjectEntityTypeCode) {
        Map<String, RefFieldSpec> byCode = new LinkedHashMap<>();
        Map<String, EntityTypeBaseFieldDO> baseByCode = loadBaseFieldsByCode(subjectEntityTypeCode);

        for (EntityTypeBaseFieldDO base : baseByCode.values()) {
            RefKind kind = classifyBaseField(base);
            if (kind == null) {
                continue;
            }
            FieldDO library = base.getLibraryFieldId() != null
                    ? fieldMapper.selectById(base.getLibraryFieldId())
                    : null;
            String target = resolveFieldTargetType(library, base.getFieldCode(), null);
            byCode.put(base.getFieldCode().trim(),
                    new RefFieldSpec(base.getFieldCode().trim(), kind.multi(), target));
        }

        if (modelId == null) {
            return new ArrayList<>(byCode.values());
        }
        List<ModelFieldAssignmentDO> assigns = modelFieldAssignmentMapper.selectByModelId(modelId);
        if (CollectionUtils.isEmpty(assigns)) {
            return new ArrayList<>(byCode.values());
        }
        for (ModelFieldAssignmentDO assign : assigns) {
            if (assign == null || assign.getFieldId() == null) {
                continue;
            }
            FieldDO field = fieldMapper.selectById(assign.getFieldId());
            if (field == null || !StringUtils.hasText(field.getCode())) {
                continue;
            }
            String code = field.getCode().trim();
            EntityTypeBaseFieldDO baseField = baseByCode.get(code);
            RefKind kind = classifyAssignedField(field, baseField);
            if (kind == null) {
                continue;
            }
            String target = resolveFieldTargetType(field, code, assign);
            byCode.put(code, new RefFieldSpec(code, kind.multi(), target));
        }
        return new ArrayList<>(byCode.values());
    }

    private Map<String, EntityTypeBaseFieldDO> loadBaseFieldsByCode(String subjectEntityTypeCode) {
        Map<String, EntityTypeBaseFieldDO> byCode = new LinkedHashMap<>();
        if (!StringUtils.hasText(subjectEntityTypeCode)) {
            return byCode;
        }
        List<EntityTypeBaseFieldDO> baseFields =
                entityTypeBaseFieldMapper.selectByEntityTypeCode(subjectEntityTypeCode.trim());
        if (CollectionUtils.isEmpty(baseFields)) {
            return byCode;
        }
        for (EntityTypeBaseFieldDO base : baseFields) {
            if (base != null && base.isEnabled() && StringUtils.hasText(base.getFieldCode())) {
                byCode.put(base.getFieldCode().trim(), base);
            }
        }
        return byCode;
    }

    private static RefKind classifyBaseField(EntityTypeBaseFieldDO base) {
        if (base == null || !StringUtils.hasText(base.getDataType())) {
            return null;
        }
        String normalized = ModelCrudFormFieldAssembler.normalizeFieldType(base.getDataType());
        if (FieldTypeEnum.isMultiEntityRef(normalized) || "BATCH_ENTITY_REF".equalsIgnoreCase(normalized)) {
            return RefKind.MULTI;
        }
        if (FieldTypeEnum.isSingleEntityRef(normalized) || "REFERENCE".equalsIgnoreCase(normalized)) {
            return RefKind.SINGLE;
        }
        return null;
    }

    private static RefKind classifyAssignedField(FieldDO field, EntityTypeBaseFieldDO baseField) {
        String type = field.getType() != null ? field.getType().trim() : "";
        String normalized = ModelCrudFormFieldAssembler.normalizeFieldType(type);
        if (FieldTypeEnum.isMultiEntityRef(normalized) || "BATCH_ENTITY_REF".equalsIgnoreCase(type)) {
            return RefKind.MULTI;
        }
        if (baseField != null && StringUtils.hasText(baseField.getDataType())) {
            String bn = ModelCrudFormFieldAssembler.normalizeFieldType(baseField.getDataType());
            if (FieldTypeEnum.isMultiEntityRef(bn) || "BATCH_ENTITY_REF".equalsIgnoreCase(bn)) {
                return RefKind.MULTI;
            }
        }
        if (FieldTypeEnum.isSingleEntityRef(normalized) || "REFERENCE".equalsIgnoreCase(type)) {
            return RefKind.SINGLE;
        }
        if (baseField != null && classifyBaseField(baseField) != null) {
            return classifyBaseField(baseField);
        }
        return null;
    }

    private String resolveFieldTargetType(FieldDO field, String fieldCode, ModelFieldAssignmentDO assign) {
        if (assign != null) {
            if (assign.getRefLibraryId() != null) {
                RelationFieldLibraryDO lib = relationFieldLibraryMapper.selectById(assign.getRefLibraryId());
                if (lib != null && StringUtils.hasText(lib.getRefEntityType())) {
                    return lib.getRefEntityType().trim();
                }
            }
            if (assign.getModelRelationId() != null) {
                ModelRelationDO rel = modelRelationMapper.selectById(assign.getModelRelationId());
                if (rel != null && StringUtils.hasText(rel.getTargetModelCode())) {
                    ModelDO targetModel = modelMapper.selectByCode(rel.getTargetModelCode().trim());
                    if (targetModel != null && StringUtils.hasText(targetModel.getEntityTypeCode())) {
                        return targetModel.getEntityTypeCode().trim();
                    }
                }
            }
            if (StringUtils.hasText(assign.getTargetEntityType())) {
                return assign.getTargetEntityType().trim();
            }
        }
        String fromProvider = ModelCrudFormFieldAssembler.resolveTargetEntityTypeFromField(field);
        if (StringUtils.hasText(fromProvider)) {
            return fromProvider.trim();
        }
        return EntityBaseFieldColumnNames.inferRefTargetEntityType(fieldCode);
    }

    static Set<TargetRef> extractTargets(Object raw, String defaultTargetType) {
        Set<TargetRef> out = new LinkedHashSet<>();
        if (raw == null) {
            return out;
        }
        if (raw instanceof Collection<?> collection) {
            for (Object item : collection) {
                TargetRef one = extractOneTarget(item, defaultTargetType);
                if (one != null) {
                    out.add(one);
                }
            }
            return out;
        }
        TargetRef one = extractOneTarget(raw, defaultTargetType);
        if (one != null) {
            out.add(one);
        }
        return out;
    }

    private static TargetRef extractOneTarget(Object raw, String defaultTargetType) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Number number) {
            long id = number.longValue();
            return id > 0 ? new TargetRef(defaultTargetType, id) : null;
        }
        if (raw instanceof Map<?, ?> map) {
            Object type = map.get("entityTypeCode");
            if (type == null) {
                type = map.get("bizCode");
            }
            String typeCode = type != null && StringUtils.hasText(String.valueOf(type))
                    ? String.valueOf(type).trim()
                    : defaultTargetType;
            Object id = map.get("id");
            if (id == null) {
                id = map.get("entityId");
            }
            Long lid = toLong(id);
            if (lid == null) {
                return null;
            }
            return new TargetRef(typeCode, lid);
        }
        if (raw instanceof String text) {
            String t = text.trim();
            if (t.isEmpty()) {
                return null;
            }
            try {
                long id = Long.parseLong(t);
                return id > 0 ? new TargetRef(defaultTargetType, id) : null;
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    /**
     * 兼容 API 形状；目标是否分类即实体由 link 判定，不再按类型白名单过滤。
     */
    public static Long extractTargetEntityId(Object raw) {
        TargetRef ref = extractOneTarget(raw, null);
        return ref != null ? ref.id() : null;
    }

    private static Set<TargetRef> difference(Set<TargetRef> left, Set<TargetRef> right) {
        Set<TargetRef> out = new LinkedHashSet<>();
        for (TargetRef item : left) {
            if (!containsSame(right, item)) {
                out.add(item);
            }
        }
        return out;
    }

    private static boolean containsSame(Set<TargetRef> set, TargetRef item) {
        for (TargetRef other : set) {
            if (sameTarget(other, item)) {
                return true;
            }
        }
        return false;
    }

    private static boolean sameTarget(TargetRef a, TargetRef b) {
        if (a == null || b == null) {
            return a == b;
        }
        if (!Objects.equals(a.id(), b.id())) {
            return false;
        }
        if (!StringUtils.hasText(a.entityTypeCode()) || !StringUtils.hasText(b.entityTypeCode())) {
            return true;
        }
        return Objects.equals(a.entityTypeCode(), b.entityTypeCode());
    }

    private static Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            long id = number.longValue();
            return id > 0 ? id : null;
        }
        try {
            long id = Long.parseLong(String.valueOf(value).trim());
            return id > 0 ? id : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private enum RefKind {
        SINGLE(false),
        MULTI(true);

        private final boolean multi;

        RefKind(boolean multi) {
            this.multi = multi;
        }

        boolean multi() {
            return multi;
        }
    }

    private record RefFieldSpec(String fieldCode, boolean multiRef, String defaultTargetType) {
    }

    record TargetRef(String entityTypeCode, Long id) {
    }
}
