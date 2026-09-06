package cn.cheers.x.module.dynamicbusiness.service.entity.refcategory;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryEntityLinkDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.enums.field.FieldTypeEnum;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityBaseFieldColumnNames;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryEntityLinkService;
import cn.cheers.x.module.dynamicbusiness.service.category.EntityAssociationModeSupport;
import cn.cheers.x.module.dynamicbusiness.service.capability.form.ModelCrudFormFieldAssembler;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityDedicatedColumnService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityRelationSyncService;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 分类–实体变更后回写引用，与引用字段保持一致。
 *
 * <p>管什么：挂/卸分类后，若主体上存在应对齐本次分类的引用，则写成或清掉。
 * 现网路径：节点有分类–台账绑定，且有唯一匹配该台账类型的引用 → 写绑定实体 id。
 * 扩展预留：将来「引用普通分类」字段应对齐 categoryId；届时在本服务增分支，禁止用简单/高级一刀切跳过。
 * 不管什么：引用保存后的分类投影（见 {@link EntityRefCategoryProjectionService}）。
 * 禁止：无匹配引用时编造回写；把简单分类归类误写成分类–台账绑定。</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EntityCategoryRefWritebackServiceImpl implements EntityCategoryRefWritebackService {

    private final CategoryMapper categoryMapper;
    private final CategoryTypeMapper categoryTypeMapper;
    private final CategoryEntityLinkService categoryEntityLinkService;
    private final EntityCoreService entityCoreService;
    private final EntityDedicatedColumnService entityDedicatedColumnService;
    private final EntityRelationSyncService entityRelationSyncService;
    private final ModelMapper modelMapper;
    private final ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    private final FieldMapper fieldMapper;
    private final EntityTypeBaseFieldMapper entityTypeBaseFieldMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void afterAssociated(Long entityId, Long categoryId, String entityTypeCode) {
        afterAssociated(entityId, categoryId, entityTypeCode, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void afterAssociated(Long entityId, Long categoryId, String entityTypeCode,
                                String associationModeOverride) {
        WritebackContext ctx = resolveContext(entityId, categoryId, entityTypeCode, associationModeOverride);
        if (ctx == null || ctx.link() == null || ctx.link().getEntityId() == null) {
            return;
        }
        if (!StringUtils.hasText(ctx.refFieldCode())) {
            log.debug("[category→REF] 跳过：无唯一匹配 REF, entityId={}, categoryId={}", entityId, categoryId);
            return;
        }
        Long targetId = ctx.link().getEntityId();
        if (!ctx.multiRef()) {
            // 单选 REF：写成该分类绑定实体（与分类多归属/单归属无关）
            persistRefValue(ctx, refObject(ctx.targetEntityTypeCode(), targetId));
            return;
        }
        // MultiRef：单归属时多选里只留这一个；多归属则加减
        if (ctx.singleOwnership()) {
            persistRefValue(ctx, List.of(refObject(ctx.targetEntityTypeCode(), targetId)));
            return;
        }
        List<Map<String, Object>> list = new ArrayList<>(readMultiRefList(ctx));
        if (list.stream().noneMatch(item -> Objects.equals(refItemId(item), targetId))) {
            list.add(refObject(ctx.targetEntityTypeCode(), targetId));
        }
        persistRefValue(ctx, list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void afterDisassociated(Long entityId, Long categoryId, String entityTypeCode) {
        WritebackContext ctx = resolveContext(entityId, categoryId, entityTypeCode, null);
        if (ctx == null || ctx.link() == null || ctx.link().getEntityId() == null
                || !StringUtils.hasText(ctx.refFieldCode())) {
            return;
        }
        Long targetId = ctx.link().getEntityId();
        if (!ctx.multiRef()) {
            // 单选 REF：当前值仍指向该节点绑定实体时整字段清空
            Long current = EntityRefCategoryProjectionServiceImpl.extractTargetEntityId(readFieldRaw(ctx));
            if (!Objects.equals(current, targetId)) {
                return;
            }
            persistRefValue(ctx, null);
            return;
        }
        // MultiRef：只删解绑分类对应的那一条
        List<Map<String, Object>> list = new ArrayList<>(readMultiRefList(ctx));
        boolean removed = list.removeIf(item -> Objects.equals(refItemId(item), targetId));
        if (!removed) {
            return;
        }
        persistRefValue(ctx, list);
    }

    private WritebackContext resolveContext(Long entityId, Long categoryId, String entityTypeCode,
                                            String associationModeOverride) {
        if (entityId == null || categoryId == null || !StringUtils.hasText(entityTypeCode)) {
            return null;
        }
        String storageType = entityTypeCode.trim();
        CategoryDO category = categoryMapper.selectById(categoryId);
        if (category == null || !StringUtils.hasText(category.getCategoryTypeCode())) {
            return null;
        }
        CategoryTypeDO categoryType = categoryTypeMapper.selectByCategoryTypeCode(category.getCategoryTypeCode().trim());
        boolean single = StringUtils.hasText(associationModeOverride)
                ? EntityAssociationModeSupport.isSingle(associationModeOverride)
                : EntityAssociationModeSupport.isSingle(categoryType);
        // 现网：仅当节点有分类–台账绑定时，才能把引用写成绑定实体。
        // 无绑定 → 跳过（普通分类归类、或尚未配置「引用普通分类」字段时的正确空态）。
        // 将来若主体有引用普通分类字段，在此之后增「按 categoryId 对齐」分支，不要改成按 categoryMode 直接 return。
        CategoryEntityLinkDO link = categoryEntityLinkService.getLinkByCategoryId(categoryId);
        String targetType = link != null && StringUtils.hasText(link.getEntityTypeCode())
                ? link.getEntityTypeCode().trim()
                : null;
        EntityDO entity = entityCoreService.get(entityId, storageType);
        if (entity == null) {
            return null;
        }
        MatchedRefField matched = null;
        if (StringUtils.hasText(targetType)) {
            matched = resolveUniqueRefField(storageType, entity.getModelId(), targetType);
        }
        return new WritebackContext(
                entity,
                storageType,
                category,
                categoryType,
                single,
                link,
                targetType,
                matched != null ? matched.fieldCode() : null,
                matched != null && matched.multiRef());
    }

    /**
     * 目标类型匹配的 REF（单选或 MultiRef）；0 或 ≥2 个字段则 null。
     */
    MatchedRefField resolveUniqueRefField(String subjectEntityTypeCode, Long modelId, String targetEntityTypeCode) {
        Map<String, Boolean> matched = new LinkedHashMap<>();
        List<EntityTypeBaseFieldDO> baseFields =
                entityTypeBaseFieldMapper.selectByEntityTypeCode(subjectEntityTypeCode);
        if (!CollectionUtils.isEmpty(baseFields)) {
            for (EntityTypeBaseFieldDO base : baseFields) {
                if (base == null || !base.isEnabled() || !StringUtils.hasText(base.getFieldCode())) {
                    continue;
                }
                String normalized = ModelCrudFormFieldAssembler.normalizeFieldType(
                        base.getDataType() != null ? base.getDataType() : "");
                boolean multi = FieldTypeEnum.isMultiEntityRef(normalized);
                boolean single = FieldTypeEnum.isSingleEntityRef(normalized) || "REFERENCE".equals(normalized);
                if (!multi && !single) {
                    continue;
                }
                FieldDO library = base.getLibraryFieldId() != null
                        ? fieldMapper.selectById(base.getLibraryFieldId())
                        : null;
                String target = resolveFieldTargetType(library, base.getFieldCode());
                if (targetEntityTypeCode.equals(target)) {
                    matched.put(base.getFieldCode().trim(), multi);
                }
            }
        }
        if (modelId != null) {
            List<ModelFieldAssignmentDO> assigns = modelFieldAssignmentMapper.selectByModelId(modelId);
            if (!CollectionUtils.isEmpty(assigns)) {
                for (ModelFieldAssignmentDO assign : assigns) {
                    if (assign == null || assign.getFieldId() == null) {
                        continue;
                    }
                    FieldDO field = fieldMapper.selectById(assign.getFieldId());
                    if (field == null || !StringUtils.hasText(field.getCode())) {
                        continue;
                    }
                    String type = field.getType() != null ? field.getType().trim() : "";
                    String normalized = ModelCrudFormFieldAssembler.normalizeFieldType(type);
                    boolean multi = FieldTypeEnum.isMultiEntityRef(normalized);
                    boolean single = FieldTypeEnum.isSingleEntityRef(normalized) || "REFERENCE".equalsIgnoreCase(type);
                    if (!multi && !single) {
                        continue;
                    }
                    String target = resolveFieldTargetType(field, field.getCode());
                    if (assign.getTargetEntityType() != null && StringUtils.hasText(assign.getTargetEntityType())) {
                        target = assign.getTargetEntityType().trim();
                    }
                    if (targetEntityTypeCode.equals(target)) {
                        matched.put(field.getCode().trim(), multi);
                    }
                }
            }
        }
        if (matched.size() != 1) {
            if (matched.size() > 1) {
                log.warn("[category→REF] 多个 REF 指向 {}，跳过回写: {}", targetEntityTypeCode, matched.keySet());
            }
            return null;
        }
        Map.Entry<String, Boolean> only = matched.entrySet().iterator().next();
        return new MatchedRefField(only.getKey(), Boolean.TRUE.equals(only.getValue()));
    }

    private static String resolveFieldTargetType(FieldDO field, String fieldCode) {
        String fromProvider = ModelCrudFormFieldAssembler.resolveTargetEntityTypeFromField(field);
        if (StringUtils.hasText(fromProvider)) {
            return fromProvider.trim();
        }
        return EntityBaseFieldColumnNames.inferRefTargetEntityType(fieldCode);
    }

    private Object readFieldRaw(WritebackContext ctx) {
        Map<String, Object> bag = new LinkedHashMap<>();
        if (ctx.entity().getCustomFields() != null) {
            bag.putAll(ctx.entity().getCustomFields());
        }
        entityDedicatedColumnService.mergePhysicalColumnsIntoBaseFields(ctx.entity(), bag);
        return bag.get(ctx.refFieldCode());
    }

    private List<Map<String, Object>> readMultiRefList(WritebackContext ctx) {
        Object raw = readFieldRaw(ctx);
        List<Map<String, Object>> out = new ArrayList<>();
        if (raw == null) {
            return out;
        }
        if (raw instanceof List<?> list) {
            for (Object item : list) {
                Map<String, Object> obj = asRefObject(item, ctx.targetEntityTypeCode());
                if (obj != null) {
                    out.add(obj);
                }
            }
            return out;
        }
        Map<String, Object> one = asRefObject(raw, ctx.targetEntityTypeCode());
        if (one != null) {
            out.add(one);
        }
        return out;
    }

    private static Map<String, Object> asRefObject(Object raw, String defaultType) {
        if (raw instanceof Map<?, ?> map) {
            Object id = map.get("id");
            if (id == null) {
                id = map.get("entityId");
            }
            Long lid = toLong(id);
            if (lid == null) {
                return null;
            }
            Object type = map.get("entityTypeCode");
            if (type == null) {
                type = map.get("bizCode");
            }
            String typeCode = type != null && StringUtils.hasText(String.valueOf(type))
                    ? String.valueOf(type).trim()
                    : defaultType;
            return refObject(typeCode, lid);
        }
        Long lid = toLong(raw);
        if (lid == null) {
            return null;
        }
        return refObject(defaultType, lid);
    }

    private static Map<String, Object> refObject(String entityTypeCode, Long id) {
        Map<String, Object> ref = new LinkedHashMap<>();
        if (StringUtils.hasText(entityTypeCode)) {
            ref.put("entityTypeCode", entityTypeCode);
        }
        ref.put("id", id);
        return ref;
    }

    private static Long refItemId(Map<String, Object> item) {
        if (item == null) {
            return null;
        }
        return toLong(item.get("id"));
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

    private void persistRefValue(WritebackContext ctx, Object newValue) {
        EntityDO entity = ctx.entity();
        ModelDO model = entity.getModelId() != null ? modelMapper.selectById(entity.getModelId()) : null;
        if (model == null) {
            log.warn("[category→REF] 实体无型号，无法同步关系表: entityId={}", entity.getId());
            return;
        }

        Map<String, Object> oldFields = new LinkedHashMap<>();
        if (entity.getCustomFields() != null) {
            oldFields.putAll(entity.getCustomFields());
        }
        entityDedicatedColumnService.mergePhysicalColumnsIntoBaseFields(entity, oldFields);

        Map<String, Object> newFields = new LinkedHashMap<>(oldFields);
        newFields.put(ctx.refFieldCode(), newValue);

        if (!ctx.multiRef()) {
            Map<String, Object> forPhysical = new LinkedHashMap<>();
            forPhysical.put(ctx.refFieldCode(), newValue);
            Map<String, Object> physical = entityDedicatedColumnService.extractPhysicalValuesAndStrip(
                    ctx.storageEntityTypeCode(), forPhysical);
            if (!physical.isEmpty()) {
                entityDedicatedColumnService.writePhysicalColumns(
                        ctx.storageEntityTypeCode(), entity.getId(), physical);
            }
        }

        Map<String, Object> custom = entity.getCustomFields() != null
                ? new LinkedHashMap<>(entity.getCustomFields())
                : new LinkedHashMap<>();
        if (ctx.multiRef()) {
            custom.put(ctx.refFieldCode(), newValue != null ? newValue : List.of());
        } else if (newValue == null) {
            // 单选清空：去掉 JSON 键（物理列已在上方写 null）
            custom.remove(ctx.refFieldCode());
        } else {
            // 单选若已落固定列，避免 JSON 双写
            Map<String, Object> stripProbe = new LinkedHashMap<>();
            stripProbe.put(ctx.refFieldCode(), newValue);
            Map<String, Object> physical = entityDedicatedColumnService.extractPhysicalValuesAndStrip(
                    ctx.storageEntityTypeCode(), stripProbe);
            if (physical.isEmpty()) {
                custom.put(ctx.refFieldCode(), newValue);
            } else {
                custom.remove(ctx.refFieldCode());
            }
        }
        entity.setCustomFields(custom);
        entityCoreService.update(entity);

        entityRelationSyncService.syncRelationsOnUpdate(entity, model, newFields, oldFields);
        log.info("[category→REF] 回写完成: entityId={}, field={}, multi={}, singleOwnership={}, value={}",
                entity.getId(), ctx.refFieldCode(), ctx.multiRef(), ctx.singleOwnership(), newValue);
    }

    private record MatchedRefField(String fieldCode, boolean multiRef) {
    }

    private record WritebackContext(
            EntityDO entity,
            String storageEntityTypeCode,
            CategoryDO category,
            CategoryTypeDO categoryType,
            boolean singleOwnership,
            CategoryEntityLinkDO link,
            String targetEntityTypeCode,
            String refFieldCode,
            boolean multiRef
    ) {
    }
}
