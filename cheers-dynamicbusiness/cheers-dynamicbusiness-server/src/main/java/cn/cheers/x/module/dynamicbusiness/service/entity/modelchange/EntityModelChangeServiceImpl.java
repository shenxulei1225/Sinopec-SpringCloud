package cn.cheers.x.module.dynamicbusiness.service.entity.modelchange;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityChangeModelBatchCommitReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityChangeModelBatchCommitRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityChangeModelBatchItemVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityChangeModelBatchPreviewReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityChangeModelBatchPreviewRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityChangeModelCommitReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityChangeModelCommitRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityChangeModelFieldItemVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityChangeModelModelSummaryVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityChangeModelPreviewReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityChangeModelPreviewRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldAssignmentRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityCategoryRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypePlatformFieldSupport;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeContext;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeResolver;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityCategoryRelationService;
import cn.cheers.x.module.dynamicbusiness.service.entity.sync.EntitySyncService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeService;
import cn.cheers.x.module.dynamicbusiness.service.model.relation.ModelCategoryRelationService;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 实体换型号（标准路径）。
 *
 * <p><b>管什么</b>：改 {@code modelId}、必要时抄写目标型号业务域、按扩展字段编码重整
 * {@code custom_fields}、重建扩展索引、分类关联业务域与型号–分类连带。</p>
 * <p><b>不管什么</b>：类型级基础字段（含所属场站等专用表固定列）不改、不重填、不进「需补填」。</p>
 * <p><b>禁止</b>：半截实体读路径再 merge 物理列冒充修根；批量里对每条重复加载同一目标型号字段清单。</p>
 *
 * <p>口径见 {@code docs/动态业务/实体换型号实现说明.md}。</p>
 */
@Service
@RequiredArgsConstructor
public class EntityModelChangeServiceImpl implements EntityModelChangeService {

    private static final String ARCHIVE_KEY = "_modelChangeArchive";
    private static final int BATCH_MAX_SIZE = 200;

    /** 扩展 JSON / 归档时跳过的键（核心列与元数据，不是型号扩展字段） */
    private static final Set<String> SKIP_FIELD_CODES = Set.of(
            "id", "tenantid", "tenant_id",
            "creator", "createtime", "create_time",
            "updater", "updatetime", "update_time",
            "deleted",
            "entitytypecode", "entity_type_code",
            "businesstypecode", "business_type_code",
            "modelid", "model_id",
            "customfields", "custom_fields",
            "parentid", "parent_id",
            "treepath", "tree_path",
            "sort", "guid", "attrs",
            "code", "domain",
            "name", "status",
            ARCHIVE_KEY.toLowerCase());

    private final EntityCoreService entityCoreService;
    private final EntityService entityService;
    private final EntitySyncService entitySyncService;
    private final ModelMapper modelMapper;
    private final ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    private final FieldMapper fieldMapper;
    private final EntityTypeScopeResolver entityTypeScopeResolver;
    private final EntityTypeService entityTypeService;
    private final EntityCategoryRelationService entityCategoryRelationService;
    private final EntityCategoryRelationMapper entityCategoryRelationMapper;
    private final ModelCategoryRelationService modelCategoryRelationService;

    // ==================== 单条 ====================

    @Override
    public EntityChangeModelPreviewRespVO preview(EntityChangeModelPreviewReqVO reqVO) {
        String storageType = resolveStorageType(reqVO.getEntityTypeCode());
        ModelDO targetModel = requireAliveModel(reqVO.getTargetModelId(), "目标");
        assertModelOnStorageType(targetModel, storageType, "目标");
        ExtensionSchema targetSchema = loadExtensionSchema(targetModel.getId());

        EntityDO entity = requireEntity(reqVO.getEntityId(), storageType);
        ModelDO sourceModel = requireAliveModel(entity.getModelId(), "源");
        assertModelOnStorageType(sourceModel, storageType, "源");
        if (Objects.equals(sourceModel.getId(), targetModel.getId())) {
            throw new ServiceException(400, "目标模型与当前模型相同，无需变更");
        }
        ExtensionSchema sourceSchema = loadExtensionSchema(sourceModel.getId());

        Map<String, Object> customFields = copyCustomFields(entity.getCustomFields());
        applyCurrentFieldsOverlay(customFields, reqVO.getCurrentFields());
        ExtensionPlan plan = computeExtensionPlan(customFields, sourceSchema, targetSchema, Map.of());

        return toPreviewResp(entity, sourceModel, targetModel, plan, storageType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EntityChangeModelCommitRespVO commit(EntityChangeModelCommitReqVO reqVO) {
        Map<String, Object> patchFields = reqVO.getPatchFields() != null ? reqVO.getPatchFields() : Map.of();
        boolean confirmArchive = reqVO.getConfirmArchive() == null || Boolean.TRUE.equals(reqVO.getConfirmArchive());

        String storageType = resolveStorageType(reqVO.getEntityTypeCode());
        ModelDO targetModel = requireAliveModel(reqVO.getTargetModelId(), "目标");
        assertModelOnStorageType(targetModel, storageType, "目标");
        assertTargetDomainRegistered(storageType, targetModel);
        ExtensionSchema targetSchema = loadExtensionSchema(targetModel.getId());

        EntityDO entity = requireEntity(reqVO.getEntityId(), storageType);
        ModelDO sourceModel = requireAliveModel(entity.getModelId(), "源");
        assertModelOnStorageType(sourceModel, storageType, "源");
        if (Objects.equals(sourceModel.getId(), targetModel.getId())) {
            throw new ServiceException(400, "目标模型与当前模型相同，无需变更");
        }
        ExtensionSchema sourceSchema = loadExtensionSchema(sourceModel.getId());

        ExtensionPlan plan = computeExtensionPlan(
                copyCustomFields(entity.getCustomFields()), sourceSchema, targetSchema, patchFields);
        return commitWithPlan(entity, sourceModel, targetModel, storageType, plan, confirmArchive);
    }

    // ==================== 批量（型号方案共享 + 实体批量读） ====================

    @Override
    public EntityChangeModelBatchPreviewRespVO batchPreview(EntityChangeModelBatchPreviewReqVO reqVO) {
        List<Long> entityIds = normalizeBatchEntityIds(reqVO.getEntityIds());
        String storageType = resolveStorageType(reqVO.getEntityTypeCode());
        ModelDO targetModel = requireAliveModel(reqVO.getTargetModelId(), "目标");
        assertModelOnStorageType(targetModel, storageType, "目标");
        ExtensionSchema targetSchema = loadExtensionSchema(targetModel.getId());

        EntityChangeModelBatchPreviewRespVO resp = new EntityChangeModelBatchPreviewRespVO();
        resp.setTargetModel(toModelSummary(targetModel));

        List<EntityDO> entities = entityCoreService.listByIds(entityIds, storageType);
        Map<Long, EntityDO> byId = new HashMap<>();
        if (entities != null) {
            for (EntityDO e : entities) {
                if (e != null && e.getId() != null) {
                    byId.put(e.getId(), e);
                }
            }
        }

        Map<Long, ExtensionSchema> sourceSchemaCache = new HashMap<>();
        Map<Long, ModelDO> sourceModelCache = new HashMap<>();

        for (Long entityId : entityIds) {
            EntityChangeModelBatchItemVO item = new EntityChangeModelBatchItemVO();
            item.setEntityId(entityId);
            try {
                EntityDO entity = byId.get(entityId);
                if (entity == null) {
                    throw new ServiceException(404, "实体不存在");
                }
                item.setEntityName(StringUtils.hasText(entity.getName()) ? entity.getName().trim() : String.valueOf(entityId));

                Long sourceModelId = entity.getModelId();
                if (sourceModelId == null) {
                    throw new ServiceException(400, "实体缺少 modelId");
                }
                if (Objects.equals(sourceModelId, targetModel.getId())) {
                    throw new ServiceException(400, "目标模型与当前模型相同，无需变更");
                }
                ModelDO sourceModel = sourceModelCache.computeIfAbsent(sourceModelId, id -> requireAliveModel(id, "源"));
                assertModelOnStorageType(sourceModel, storageType, "源");
                ExtensionSchema sourceSchema = sourceSchemaCache.computeIfAbsent(sourceModelId, this::loadExtensionSchema);

                item.setSourceModelName(sourceModel.getName());
                ExtensionPlan plan = computeExtensionPlan(
                        copyCustomFields(entity.getCustomFields()), sourceSchema, targetSchema, Map.of());
                item.setKeptFieldCount(plan.keptFieldItems.size());
                item.setArchivedFieldCount(plan.archivedFieldItems.size());
                item.setMissingRequired(plan.missingRequiredItems);
                item.setDomainChanged(!EntityTypeScopeContext.domainsEqual(
                        EntityTypeScopeContext.normalizeDomain(entity.getDomain()),
                        EntityTypeScopeContext.normalizeDomain(targetModel.getDomain())));

                if (!plan.missingRequiredItems.isEmpty()) {
                    item.setStatus(EntityChangeModelBatchItemVO.STATUS_NEEDS_PATCH);
                    item.setReason("目标型号必填字段未齐："
                            + plan.missingRequiredItems.stream()
                            .map(f -> StringUtils.hasText(f.getLabel()) ? f.getLabel() : f.getFieldCode())
                            .collect(Collectors.joining("、")));
                    resp.getNeedsPatch().add(item);
                } else {
                    item.setStatus(EntityChangeModelBatchItemVO.STATUS_READY);
                    resp.getReady().add(item);
                }
            } catch (ServiceException ex) {
                item.setStatus(EntityChangeModelBatchItemVO.STATUS_BLOCKED);
                item.setReason(ex.getMessage());
                if (!StringUtils.hasText(item.getEntityName())) {
                    item.setEntityName(String.valueOf(entityId));
                }
                resp.getBlocked().add(item);
            } catch (Exception ex) {
                item.setStatus(EntityChangeModelBatchItemVO.STATUS_BLOCKED);
                item.setReason(ex.getMessage() != null ? ex.getMessage() : "预览失败");
                if (!StringUtils.hasText(item.getEntityName())) {
                    item.setEntityName(String.valueOf(entityId));
                }
                resp.getBlocked().add(item);
            }
        }
        resp.setReadyCount(resp.getReady().size());
        resp.setNeedsPatchCount(resp.getNeedsPatch().size());
        resp.setBlockedCount(resp.getBlocked().size());
        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EntityChangeModelBatchCommitRespVO batchCommit(EntityChangeModelBatchCommitReqVO reqVO) {
        List<Long> entityIds = normalizeBatchEntityIds(reqVO.getEntityIds());
        Map<String, Map<String, Object>> patches =
                reqVO.getPatches() != null ? reqVO.getPatches() : Map.of();
        boolean confirmArchive = reqVO.getConfirmArchive() == null
                || Boolean.TRUE.equals(reqVO.getConfirmArchive());

        String storageType = resolveStorageType(reqVO.getEntityTypeCode());
        ModelDO targetModel = requireAliveModel(reqVO.getTargetModelId(), "目标");
        assertModelOnStorageType(targetModel, storageType, "目标");
        assertTargetDomainRegistered(storageType, targetModel);
        ExtensionSchema targetSchema = loadExtensionSchema(targetModel.getId());

        List<EntityDO> entities = entityCoreService.listByIds(entityIds, storageType);
        Map<Long, EntityDO> byId = new HashMap<>();
        if (entities != null) {
            for (EntityDO e : entities) {
                if (e != null && e.getId() != null) {
                    byId.put(e.getId(), e);
                }
            }
        }
        Map<Long, ExtensionSchema> sourceSchemaCache = new HashMap<>();
        Map<Long, ModelDO> sourceModelCache = new HashMap<>();

        EntityChangeModelBatchCommitRespVO resp = new EntityChangeModelBatchCommitRespVO();
        for (Long entityId : entityIds) {
            try {
                EntityDO entity = byId.get(entityId);
                if (entity == null) {
                    throw new ServiceException(404, "实体不存在");
                }
                Long sourceModelId = entity.getModelId();
                if (sourceModelId == null) {
                    throw new ServiceException(400, "实体缺少 modelId");
                }
                if (Objects.equals(sourceModelId, targetModel.getId())) {
                    throw new ServiceException(400, "目标模型与当前模型相同，无需变更");
                }
                ModelDO sourceModel = sourceModelCache.computeIfAbsent(sourceModelId, id -> requireAliveModel(id, "源"));
                assertModelOnStorageType(sourceModel, storageType, "源");
                ExtensionSchema sourceSchema = sourceSchemaCache.computeIfAbsent(sourceModelId, this::loadExtensionSchema);

                Map<String, Object> patch = patches.get(String.valueOf(entityId));
                ExtensionPlan plan = computeExtensionPlan(
                        copyCustomFields(entity.getCustomFields()),
                        sourceSchema,
                        targetSchema,
                        patch != null ? patch : Map.of());
                EntityChangeModelCommitRespVO committed =
                        commitWithPlan(entity, sourceModel, targetModel, storageType, plan, confirmArchive);
                resp.getSuccesses().add(committed);
            } catch (ServiceException ex) {
                EntityChangeModelBatchItemVO fail = new EntityChangeModelBatchItemVO();
                fail.setEntityId(entityId);
                fail.setStatus(EntityChangeModelBatchItemVO.STATUS_BLOCKED);
                fail.setReason(ex.getMessage());
                EntityDO e = byId.get(entityId);
                fail.setEntityName(e != null && StringUtils.hasText(e.getName())
                        ? e.getName().trim() : String.valueOf(entityId));
                resp.getFailures().add(fail);
            } catch (Exception ex) {
                EntityChangeModelBatchItemVO fail = new EntityChangeModelBatchItemVO();
                fail.setEntityId(entityId);
                fail.setStatus(EntityChangeModelBatchItemVO.STATUS_BLOCKED);
                fail.setReason(ex.getMessage() != null ? ex.getMessage() : "提交失败");
                EntityDO e = byId.get(entityId);
                fail.setEntityName(e != null && StringUtils.hasText(e.getName())
                        ? e.getName().trim() : String.valueOf(entityId));
                resp.getFailures().add(fail);
            }
        }
        resp.setSuccessCount(resp.getSuccesses().size());
        resp.setFailureCount(resp.getFailures().size());
        return resp;
    }

    // ==================== 写入 ====================

    private EntityChangeModelCommitRespVO commitWithPlan(
            EntityDO entity,
            ModelDO sourceModel,
            ModelDO targetModel,
            String storageType,
            ExtensionPlan plan,
            boolean confirmArchive) {
        if (!plan.missingRequiredItems.isEmpty()) {
            String codes = plan.missingRequiredItems.stream()
                    .map(EntityChangeModelFieldItemVO::getFieldCode)
                    .collect(Collectors.joining(", "));
            throw new ServiceException(400, "目标模型必填字段未补全: " + codes);
        }
        if (!plan.archivedFields.isEmpty() && !confirmArchive) {
            throw new ServiceException(400, "存在将归档的源模型专有字段，请确认 confirmArchive=true");
        }

        String sourceDomain = EntityTypeScopeContext.normalizeDomain(entity.getDomain());
        String targetDomain = EntityTypeScopeContext.normalizeDomain(targetModel.getDomain());
        boolean domainChanged = !EntityTypeScopeContext.domainsEqual(sourceDomain, targetDomain);

        EntityUpdateReqVO updateReq = buildUpdateRequest(
                entity, sourceModel, storageType, targetModel, targetDomain, plan);
        entityService.updateIncludingModelChange(updateReq);

        // 索引：按新型号 + 对齐后的 custom_fields 重建（派生，非差分本体）
        EntityDO indexed = entityCoreService.get(entity.getId(), storageType);
        if (indexed != null) {
            entitySyncService.syncEntity(indexed);
        }

        int syncedRelationCount = 0;
        if (domainChanged) {
            syncedRelationCount = entityCategoryRelationService.syncRelationDomainByEntityIds(
                    List.of(entity.getId()), storageType, targetDomain);
        }
        int linkedModelCategoryCount = ensureTargetModelLinkedToEntityCategories(
                entity.getId(), storageType, targetModel.getId());

        EntityChangeModelCommitRespVO resp = new EntityChangeModelCommitRespVO();
        resp.setEntityId(entity.getId());
        resp.setModelId(targetModel.getId());
        resp.setKeptFieldCount(plan.keptValues.size());
        resp.setArchivedFieldCount(plan.archivedFields.size());
        resp.setSourceDomain(sourceDomain);
        resp.setTargetDomain(targetDomain);
        resp.setDomainChanged(domainChanged);
        resp.setSyncedRelationCount(syncedRelationCount);
        resp.setLinkedModelCategoryCount(linkedModelCategoryCount);
        return resp;
    }

    /**
     * 只写型号身份、业务域与重整后的扩展字段；不带所属场站等固定列（更新路径会从库回填锁站）。
     */
    private EntityUpdateReqVO buildUpdateRequest(
            EntityDO entity,
            ModelDO sourceModel,
            String storageType,
            ModelDO targetModel,
            String targetDomain,
            ExtensionPlan plan) {
        Map<String, Object> baseFields = new LinkedHashMap<>();
        baseFields.put("entityTypeCode", storageType);
        baseFields.put("modelId", targetModel.getId());
        if (StringUtils.hasText(targetDomain)) {
            baseFields.put("domain", targetDomain);
        }
        if (entity.getName() != null) {
            baseFields.put("name", entity.getName());
        }
        if (entity.getStatus() != null) {
            baseFields.put("status", entity.getStatus());
        }
        if (entity.getParentId() != null) {
            baseFields.put("parentId", entity.getParentId());
        }
        if (StringUtils.hasText(entity.getCode())) {
            baseFields.put("code", entity.getCode());
        }

        Map<String, Object> customFields = new LinkedHashMap<>(plan.keptValues);
        if (!plan.archivedFields.isEmpty()) {
            JSONObject archive = new JSONObject();
            archive.put("fromModelId", sourceModel.getId());
            archive.put("fromModelCode", sourceModel.getCode());
            archive.put("toModelId", targetModel.getId());
            archive.put("toModelCode", targetModel.getCode());
            archive.put("changedAt", Instant.now().toString());
            archive.put("fields", new JSONObject(plan.archivedFields));
            customFields.put(ARCHIVE_KEY, archive);
        }

        EntityUpdateReqVO req = new EntityUpdateReqVO();
        req.setId(entity.getId());
        req.setBaseFields(baseFields);
        req.setCustomFields(customFields);
        return req;
    }

    private int ensureTargetModelLinkedToEntityCategories(Long entityId, String storageType, Long targetModelId) {
        if (entityId == null || targetModelId == null) {
            return 0;
        }
        List<Long> categoryIds = listEntityCategoryIdsForModelLink(entityId, storageType);
        if (categoryIds.isEmpty()) {
            return 0;
        }
        int linked = 0;
        for (Long categoryId : categoryIds) {
            if (categoryId == null || categoryId <= 0) {
                continue;
            }
            if (modelCategoryRelationService.existsRelation(targetModelId, categoryId, storageType)) {
                continue;
            }
            modelCategoryRelationService.associate(targetModelId, categoryId, storageType);
            linked++;
        }
        return linked;
    }

    private List<Long> listEntityCategoryIdsForModelLink(Long entityId, String storageEntityTypeCode) {
        if (entityId == null || !StringUtils.hasText(storageEntityTypeCode)) {
            return List.of();
        }
        var relations = entityCategoryRelationMapper.selectByEntityId(entityId);
        if (relations == null || relations.isEmpty()) {
            return List.of();
        }
        String type = storageEntityTypeCode.trim();
        LinkedHashSet<Long> ids = new LinkedHashSet<>();
        for (var relation : relations) {
            if (relation == null || relation.getCategoryId() == null) {
                continue;
            }
            String relType = relation.getEntityTypeCode();
            if (StringUtils.hasText(relType) && !type.equals(relType.trim())) {
                continue;
            }
            ids.add(relation.getCategoryId());
        }
        return List.copyOf(ids);
    }

    // ==================== 扩展字段差分（预览=提交同一算法） ====================

    /**
     * 只对型号扩展字段（非 BASE/SYSTEM）做保留 / 需补填 / 归档。
     * 最终有效 {@code custom_fields} = 目标扩展有值的键 + 可选归档块。
     */
    private ExtensionPlan computeExtensionPlan(
            Map<String, Object> customFields,
            ExtensionSchema sourceSchema,
            ExtensionSchema targetSchema,
            Map<String, Object> patchFields) {
        ExtensionPlan plan = new ExtensionPlan();
        Map<String, Object> working = customFields != null ? new LinkedHashMap<>(customFields) : new LinkedHashMap<>();
        working.remove(ARCHIVE_KEY);

        Set<String> consumed = new HashSet<>();

        for (ExtensionField field : targetSchema.fieldsByCode.values()) {
            Object value = resolveCustomValue(working, field);
            if (isEmptyValue(value)) {
                value = resolvePatchValue(patchFields, field);
            }
            if (!isEmptyValue(value)) {
                plan.keptValues.put(field.fieldCode, value);
                plan.keptFieldItems.add(toFieldItem(field, value));
                markConsumed(consumed, field, working);
                continue;
            }
            if (field.required) {
                plan.missingRequiredItems.add(toFieldItem(field, null));
            }
        }

        for (ExtensionField field : sourceSchema.fieldsByCode.values()) {
            if (targetSchema.fieldsByCode.containsKey(field.fieldCode)) {
                continue;
            }
            Object value = resolveCustomValue(working, field);
            if (isEmptyValue(value)) {
                continue;
            }
            plan.archivedFields.put(field.fieldCode, value);
            plan.archivedFieldItems.add(toFieldItem(field, value));
            markConsumed(consumed, field, working);
        }

        // 既非源也非目标扩展的残留键：归档，避免新型号 JSON 里留脏键
        for (Map.Entry<String, Object> entry : working.entrySet()) {
            String key = entry.getKey();
            if (!StringUtils.hasText(key) || shouldSkipFieldCode(key) || consumed.contains(key)) {
                continue;
            }
            if (targetSchema.fieldsByCode.containsKey(key) || sourceSchema.fieldsByCode.containsKey(key)) {
                continue;
            }
            if (isEmptyValue(entry.getValue())) {
                continue;
            }
            plan.archivedFields.put(key, entry.getValue());
            ExtensionField orphan = new ExtensionField(key, key, false, null);
            plan.archivedFieldItems.add(toFieldItem(orphan, entry.getValue()));
        }

        if (!plan.archivedFieldItems.isEmpty()) {
            plan.warnings.add("共 " + plan.archivedFieldItems.size() + " 个源型号专有扩展字段将写入归档，不在新模型表单中展示");
        }
        return plan;
    }

    private void applyCurrentFieldsOverlay(Map<String, Object> customFields, Map<String, Object> currentFields) {
        if (currentFields == null || currentFields.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : currentFields.entrySet()) {
            String key = entry.getKey();
            if (!StringUtils.hasText(key) || ARCHIVE_KEY.equals(key) || shouldSkipFieldCode(key)) {
                continue;
            }
            // 草稿覆盖只作用于扩展 JSON；基础列键忽略
            customFields.put(key, entry.getValue());
        }
    }

    private Object resolveCustomValue(Map<String, Object> customFields, ExtensionField field) {
        if (customFields == null || field == null) {
            return null;
        }
        Object direct = customFields.get(field.fieldCode);
        if (!isEmptyValue(direct)) {
            return direct;
        }
        for (String alias : field.aliases) {
            Object v = customFields.get(alias);
            if (!isEmptyValue(v)) {
                return v;
            }
        }
        return null;
    }

    private Object resolvePatchValue(Map<String, Object> patchFields, ExtensionField field) {
        if (patchFields == null || patchFields.isEmpty() || field == null) {
            return null;
        }
        Object direct = patchFields.get(field.fieldCode);
        if (!isEmptyValue(direct)) {
            return direct;
        }
        for (String alias : field.aliases) {
            Object v = patchFields.get(alias);
            if (!isEmptyValue(v)) {
                return v;
            }
        }
        return null;
    }

    private void markConsumed(Set<String> consumed, ExtensionField field, Map<String, Object> working) {
        consumed.add(field.fieldCode);
        consumed.addAll(field.aliases);
        for (String key : working.keySet()) {
            if (field.fieldCode.equals(key) || field.aliases.contains(key)) {
                consumed.add(key);
            }
        }
    }

    // ==================== 型号扩展字段方案（可缓存于一次批量请求） ====================

    /**
     * 加载型号上的<strong>扩展</strong>字段分配：排除 BASE / SYSTEM（类型级基础与平台系统字段）。
     */
    private ExtensionSchema loadExtensionSchema(Long modelId) {
        ExtensionSchema schema = new ExtensionSchema();
        if (modelId == null) {
            return schema;
        }
        List<ModelFieldAssignmentDO> assigns = modelFieldAssignmentMapper.selectByModelId(modelId);
        if (assigns == null || assigns.isEmpty()) {
            return schema;
        }
        Set<Long> fieldIds = assigns.stream()
                .map(ModelFieldAssignmentDO::getFieldId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Map<Long, FieldDO> fieldById = new HashMap<>();
        if (!fieldIds.isEmpty()) {
            for (FieldDO field : fieldMapper.selectList(
                    new cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX<FieldDO>()
                            .in(FieldDO::getId, fieldIds)
                            .eq(FieldDO::getDeleted, false))) {
                if (field != null && field.getId() != null) {
                    fieldById.put(field.getId(), field);
                }
            }
        }
        for (ModelFieldAssignmentDO assign : assigns) {
            if (assign == null || !isExtensionFieldSource(assign.getFieldSource())) {
                continue;
            }
            FieldDO field = fieldById.get(assign.getFieldId());
            String code = field != null && StringUtils.hasText(field.getCode())
                    ? field.getCode().trim()
                    : (StringUtils.hasText(assign.getFieldCode()) ? assign.getFieldCode().trim() : null);
            if (!StringUtils.hasText(code) || shouldSkipFieldCode(code)
                    || EntityTypePlatformFieldSupport.isPlatformOwnedPersistedFieldCode(code)) {
                continue;
            }
            String label = field != null && StringUtils.hasText(field.getName()) ? field.getName() : code;
            boolean required = Boolean.TRUE.equals(assign.getRequired());
            String dataType = field != null ? field.getType() : null;
            ExtensionField existing = schema.fieldsByCode.get(code);
            if (existing != null) {
                existing.required = existing.required || required;
                if (StringUtils.hasText(assign.getFieldCode())
                        && !code.equals(assign.getFieldCode().trim())) {
                    existing.aliases.add(assign.getFieldCode().trim());
                }
                continue;
            }
            ExtensionField ext = new ExtensionField(code, label, required, dataType);
            if (StringUtils.hasText(assign.getFieldCode()) && !code.equals(assign.getFieldCode().trim())) {
                ext.aliases.add(assign.getFieldCode().trim());
            }
            schema.fieldsByCode.put(code, ext);
        }
        return schema;
    }

    /** BASE / SYSTEM 为类型级或平台字段，不进换型号扩展差分。 */
    private static boolean isExtensionFieldSource(String fieldSource) {
        if (!StringUtils.hasText(fieldSource)) {
            // 历史空来源：按扩展处理，避免漏迁用户字段
            return true;
        }
        String s = fieldSource.trim();
        return !ModelFieldAssignmentRespVO.FIELD_SOURCE_BASE.equalsIgnoreCase(s)
                && !ModelFieldAssignmentRespVO.FIELD_SOURCE_SYSTEM.equalsIgnoreCase(s);
    }

    // ==================== 预览组装 ====================

    private EntityChangeModelPreviewRespVO toPreviewResp(
            EntityDO entity,
            ModelDO sourceModel,
            ModelDO targetModel,
            ExtensionPlan plan,
            String storageType) {
        String sourceDomain = EntityTypeScopeContext.normalizeDomain(entity.getDomain());
        String targetDomain = EntityTypeScopeContext.normalizeDomain(targetModel.getDomain());
        boolean domainChanged = !EntityTypeScopeContext.domainsEqual(sourceDomain, targetDomain);
        int relationCount = (int) entityCategoryRelationMapper.countByEntityIds(List.of(entity.getId()), storageType);

        EntityChangeModelPreviewRespVO resp = new EntityChangeModelPreviewRespVO();
        resp.setEntityId(entity.getId());
        resp.setSourceModel(toModelSummary(sourceModel));
        resp.setTargetModel(toModelSummary(targetModel));
        resp.setKeptFields(plan.keptFieldItems);
        resp.setMissingRequired(plan.missingRequiredItems);
        resp.setArchivedFields(plan.archivedFieldItems);
        resp.setSourceDomain(sourceDomain);
        resp.setTargetDomain(targetDomain);
        resp.setDomainChanged(domainChanged);
        resp.setRelationCount(relationCount);
        List<String> warnings = new ArrayList<>(plan.warnings);
        if (domainChanged) {
            warnings.add("变更后实体业务域为「" + describeDomain(targetDomain)
                    + "」，其 " + relationCount + " 条分类关联的业务域将同步迁移");
        }
        resp.setWarnings(warnings);
        return resp;
    }

    private EntityChangeModelModelSummaryVO toModelSummary(ModelDO model) {
        EntityChangeModelModelSummaryVO vo = new EntityChangeModelModelSummaryVO();
        vo.setId(model.getId());
        vo.setCode(model.getCode());
        vo.setName(model.getName());
        return vo;
    }

    private EntityChangeModelFieldItemVO toFieldItem(ExtensionField field, Object value) {
        EntityChangeModelFieldItemVO item = new EntityChangeModelFieldItemVO();
        item.setFieldCode(field.fieldCode);
        item.setLabel(field.label);
        item.setValue(value);
        item.setBucket("custom");
        item.setRequired(field.required);
        item.setDataType(field.dataType);
        return item;
    }

    private String describeDomain(String domain) {
        return StringUtils.hasText(domain) ? domain : "未划域";
    }

    // ==================== 校验 / 加载辅助 ====================

    private String resolveStorageType(String entityTypeCode) {
        String raw = entityTypeCode != null ? entityTypeCode.trim() : "";
        if (!StringUtils.hasText(raw)) {
            throw new ServiceException(400, "entityTypeCode 不能为空");
        }
        return entityTypeScopeResolver.resolveStorageEntityTypeCode(raw);
    }

    private EntityDO requireEntity(Long entityId, String storageType) {
        if (entityId == null || entityId <= 0) {
            throw new ServiceException(400, "entityId 无效");
        }
        EntityDO entity = entityCoreService.get(entityId, storageType);
        if (entity == null) {
            throw new ServiceException(404, "实体不存在");
        }
        if (entity.getModelId() == null) {
            throw new ServiceException(400, "实体缺少 modelId");
        }
        return entity;
    }

    private ModelDO requireAliveModel(Long modelId, String role) {
        ModelDO model = modelMapper.selectByIdIncludingDeleted(modelId);
        if (model == null) {
            throw new ServiceException(404, role + "模型不存在: " + modelId);
        }
        if (Boolean.TRUE.equals(model.getDeleted())) {
            throw new ServiceException(400, role + "模型已删除，禁止变更模型: " + modelId
                    + (StringUtils.hasText(model.getName()) ? "（" + model.getName() + "）" : ""));
        }
        return model;
    }

    private void assertModelOnStorageType(ModelDO model, String storageType, String role) {
        String modelType = entityTypeScopeResolver.resolveStorageEntityTypeCode(
                model.getEntityTypeCode() != null ? model.getEntityTypeCode() : "");
        if (!storageType.equals(modelType)) {
            throw new ServiceException(400, "仅允许在同一业务类型内变更模型（" + role + "型号类型不匹配）");
        }
    }

    private void assertTargetDomainRegistered(String storageType, ModelDO targetModel) {
        String targetDomain = EntityTypeScopeContext.normalizeDomain(targetModel.getDomain());
        if (StringUtils.hasText(targetDomain) && !entityTypeService.isRegisteredDomain(storageType, targetDomain)) {
            throw new ServiceException(400,
                    "目标型号的业务域未登记为该数据类型下的子数据类型：" + targetDomain);
        }
    }

    private List<Long> normalizeBatchEntityIds(List<Long> rawIds) {
        if (rawIds == null || rawIds.isEmpty()) {
            throw new ServiceException(400, "entityIds 不能为空");
        }
        LinkedHashSet<Long> unique = new LinkedHashSet<>();
        for (Long id : rawIds) {
            if (id == null || id <= 0) {
                continue;
            }
            unique.add(id);
        }
        if (unique.isEmpty()) {
            throw new ServiceException(400, "entityIds 无效");
        }
        if (unique.size() > BATCH_MAX_SIZE) {
            throw new ServiceException(400, "单次批量最多 " + BATCH_MAX_SIZE + " 条");
        }
        return List.copyOf(unique);
    }

    private static Map<String, Object> copyCustomFields(Map<String, Object> source) {
        if (source == null || source.isEmpty()) {
            return new LinkedHashMap<>();
        }
        return new LinkedHashMap<>(source);
    }

    private boolean shouldSkipFieldCode(String code) {
        return !StringUtils.hasText(code) || SKIP_FIELD_CODES.contains(code.trim().toLowerCase());
    }

    private boolean isEmptyValue(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String text) {
            return text.trim().isEmpty();
        }
        if (value instanceof Map<?, ?> map) {
            return map.isEmpty();
        }
        if (value instanceof List<?> list) {
            return list.isEmpty();
        }
        return false;
    }

    // ==================== 内部模型 ====================

    private static final class ExtensionSchema {
        private final LinkedHashMap<String, ExtensionField> fieldsByCode = new LinkedHashMap<>();
    }

    private static final class ExtensionField {
        private final String fieldCode;
        private final String label;
        private boolean required;
        private final String dataType;
        private final Set<String> aliases = new LinkedHashSet<>();

        private ExtensionField(String fieldCode, String label, boolean required, String dataType) {
            this.fieldCode = fieldCode;
            this.label = label;
            this.required = required;
            this.dataType = dataType;
        }
    }

    private static final class ExtensionPlan {
        private final Map<String, Object> keptValues = new LinkedHashMap<>();
        private final Map<String, Object> archivedFields = new LinkedHashMap<>();
        private final List<EntityChangeModelFieldItemVO> keptFieldItems = new ArrayList<>();
        private final List<EntityChangeModelFieldItemVO> missingRequiredItems = new ArrayList<>();
        private final List<EntityChangeModelFieldItemVO> archivedFieldItems = new ArrayList<>();
        private final List<String> warnings = new ArrayList<>();
    }
}
