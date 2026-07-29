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
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityDoVoHelper;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityFieldMapsSupport;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityCategoryRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeContext;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeResolver;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityCategoryRelationService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeService;
import cn.cheers.x.module.dynamicbusiness.service.field.CustomFieldValidationService;
import cn.cheers.x.module.dynamicbusiness.service.model.relation.ModelCategoryRelationService;
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
 * 实体变更模型：按 fieldCode 交集迁移字段值，源专有字段写入 {@code _modelChangeArchive}。
 */
@Service
@RequiredArgsConstructor
public class EntityModelChangeServiceImpl implements EntityModelChangeService {

    private static final String ARCHIVE_KEY = "_modelChangeArchive";
    /** 单次批量上限，避免一次拖垮预览/提交 */
    private static final int BATCH_MAX_SIZE = 200;

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
            // code 与 domain 是实体表的核心列，不是型号字段：换型号时随核心列原样带过去，
            // 不能当成源型号专有字段归档（domain 另由目标型号改写）。
            "code", "domain",
            ARCHIVE_KEY.toLowerCase());

    private static final Set<String> CORE_PRESERVED = Set.of(
            "name", "status", "entitytypecode", "entity_type_code", "parentid", "parent_id");

    private final EntityCoreService entityCoreService;
    private final EntityService entityService;
    private final ModelMapper modelMapper;
    private final ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    private final FieldMapper fieldMapper;
    private final EntityTypeBaseFieldMapper entityTypeBaseFieldMapper;
    private final CustomFieldValidationService customFieldValidationService;
    private final EntityTypeScopeResolver entityTypeScopeResolver;
    private final EntityTypeService entityTypeService;
    private final EntityCategoryRelationService entityCategoryRelationService;
    private final EntityCategoryRelationMapper entityCategoryRelationMapper;
    private final ModelCategoryRelationService modelCategoryRelationService;

    @Override
    public EntityChangeModelPreviewRespVO preview(EntityChangeModelPreviewReqVO reqVO) {
        MigrationContext ctx = buildContext(reqVO.getEntityTypeCode(), reqVO.getEntityId(), reqVO.getTargetModelId());
        applyCurrentFieldsOverlay(ctx, reqVO.getCurrentFields());
        MigrationPlan plan = computePlan(ctx, Map.of());
        return toPreviewResp(ctx, plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EntityChangeModelCommitRespVO commit(EntityChangeModelCommitReqVO reqVO) {
        Map<String, Object> patchFields = reqVO.getPatchFields() != null ? reqVO.getPatchFields() : Map.of();
        boolean confirmArchive = reqVO.getConfirmArchive() == null || Boolean.TRUE.equals(reqVO.getConfirmArchive());

        MigrationContext ctx = buildContext(reqVO.getEntityTypeCode(), reqVO.getEntityId(), reqVO.getTargetModelId());
        MigrationPlan plan = computePlan(ctx, patchFields);

        if (!plan.missingRequired.isEmpty()) {
            String codes = plan.missingRequired.stream()
                    .map(d -> d.fieldCode)
                    .collect(Collectors.joining(", "));
            throw new ServiceException(400, "目标模型必填字段未补全: " + codes);
        }
        if (!plan.archivedFields.isEmpty() && !confirmArchive) {
            throw new ServiceException(400, "存在将归档的源模型专有字段，请确认 confirmArchive=true");
        }

        // 实体本体：业务域由 prepareUpdateEntity 从目标型号抄写；源专有字段随 customFields 整列覆盖而清空
        EntityUpdateReqVO updateReq = buildUpdateRequest(ctx, plan);
        entityService.updateIncludingModelChange(updateReq);

        // 分类关联的业务域只是实体行的镜像，必须在同一事务内跟着迁移，否则按业务域筛分类会漏掉本实体
        int syncedRelationCount = 0;
        if (ctx.domainChanged) {
            syncedRelationCount = entityCategoryRelationService.syncRelationDomainByEntityIds(
                    List.of(ctx.entityId), ctx.entityTypeCode, ctx.targetDomain);
        }

        // 实体已挂的分类须能在型号列看到新型号：补建 目标型号 ↔ 这些分类 的关联
        int linkedModelCategoryCount = ensureTargetModelLinkedToEntityCategories(ctx);

        EntityChangeModelCommitRespVO resp = new EntityChangeModelCommitRespVO();
        resp.setEntityId(ctx.entityId);
        resp.setModelId(ctx.targetModel.getId());
        resp.setKeptFieldCount(plan.keptValues.size());
        resp.setArchivedFieldCount(plan.archivedFields.size());
        resp.setSourceDomain(ctx.sourceDomain);
        resp.setTargetDomain(ctx.targetDomain);
        resp.setDomainChanged(ctx.domainChanged);
        resp.setSyncedRelationCount(syncedRelationCount);
        resp.setLinkedModelCategoryCount(linkedModelCategoryCount);
        return resp;
    }

    /**
     * 按实体当前分类关联，把目标型号关联到这些分类（已存在则跳过）。
     * 否则分类浏览时型号列只有旧型号，新型号下的实体会「看不见型号入口」。
     */
    private int ensureTargetModelLinkedToEntityCategories(MigrationContext ctx) {
        if (ctx == null || ctx.targetModel == null || ctx.targetModel.getId() == null) {
            return 0;
        }
        List<Long> categoryIds = entityCategoryRelationService.listCategoryIdsByEntityId(
                ctx.entityId, ctx.entityTypeCode);
        if (categoryIds == null || categoryIds.isEmpty()) {
            return 0;
        }
        Long targetModelId = ctx.targetModel.getId();
        List<Long> missing = new ArrayList<>();
        for (Long categoryId : categoryIds) {
            if (categoryId == null || categoryId <= 0) {
                continue;
            }
            if (!modelCategoryRelationService.existsRelation(targetModelId, categoryId, ctx.entityTypeCode)) {
                missing.add(categoryId);
            }
        }
        if (missing.isEmpty()) {
            return 0;
        }
        modelCategoryRelationService.batchAssociateModelToCategories(
                targetModelId, missing, ctx.entityTypeCode);
        return missing.size();
    }

    @Override
    public EntityChangeModelBatchPreviewRespVO batchPreview(EntityChangeModelBatchPreviewReqVO reqVO) {
        List<Long> entityIds = normalizeBatchEntityIds(reqVO.getEntityIds());
        Long targetModelId = reqVO.getTargetModelId();
        String entityTypeCode = reqVO.getEntityTypeCode();

        EntityChangeModelBatchPreviewRespVO resp = new EntityChangeModelBatchPreviewRespVO();
        ModelDO targetModel = requireAliveModel(targetModelId, "目标");
        EntityChangeModelModelSummaryVO targetSummary = new EntityChangeModelModelSummaryVO();
        targetSummary.setId(targetModel.getId());
        targetSummary.setCode(targetModel.getCode());
        targetSummary.setName(targetModel.getName());
        resp.setTargetModel(targetSummary);

        for (Long entityId : entityIds) {
            EntityChangeModelBatchItemVO item = new EntityChangeModelBatchItemVO();
            item.setEntityId(entityId);
            try {
                EntityChangeModelPreviewReqVO one = new EntityChangeModelPreviewReqVO();
                one.setEntityTypeCode(entityTypeCode);
                one.setEntityId(entityId);
                one.setTargetModelId(targetModelId);
                EntityChangeModelPreviewRespVO preview = preview(one);
                item.setEntityName(resolveEntityDisplayName(entityTypeCode, entityId, preview));
                item.setSourceModelName(preview.getSourceModel() != null ? preview.getSourceModel().getName() : null);
                item.setKeptFieldCount(preview.getKeptFields() != null ? preview.getKeptFields().size() : 0);
                item.setArchivedFieldCount(preview.getArchivedFields() != null ? preview.getArchivedFields().size() : 0);
                item.setMissingRequired(preview.getMissingRequired() != null
                        ? preview.getMissingRequired() : List.of());
                item.setDomainChanged(preview.isDomainChanged());
                if (item.getMissingRequired() != null && !item.getMissingRequired().isEmpty()) {
                    item.setStatus(EntityChangeModelBatchItemVO.STATUS_NEEDS_PATCH);
                    item.setReason("目标型号必填字段未齐："
                            + item.getMissingRequired().stream()
                            .map(f -> f.getLabel() != null && !f.getLabel().isBlank()
                                    ? f.getLabel() : f.getFieldCode())
                            .collect(Collectors.joining("、")));
                    resp.getNeedsPatch().add(item);
                } else {
                    item.setStatus(EntityChangeModelBatchItemVO.STATUS_READY);
                    resp.getReady().add(item);
                }
            } catch (ServiceException ex) {
                item.setStatus(EntityChangeModelBatchItemVO.STATUS_BLOCKED);
                item.setReason(ex.getMessage());
                item.setEntityName(resolveEntityDisplayNameSafe(entityTypeCode, entityId));
                resp.getBlocked().add(item);
            } catch (Exception ex) {
                item.setStatus(EntityChangeModelBatchItemVO.STATUS_BLOCKED);
                item.setReason(ex.getMessage() != null ? ex.getMessage() : "预览失败");
                item.setEntityName(resolveEntityDisplayNameSafe(entityTypeCode, entityId));
                resp.getBlocked().add(item);
            }
        }
        resp.setReadyCount(resp.getReady().size());
        resp.setNeedsPatchCount(resp.getNeedsPatch().size());
        resp.setBlockedCount(resp.getBlocked().size());
        return resp;
    }

    @Override
    public EntityChangeModelBatchCommitRespVO batchCommit(EntityChangeModelBatchCommitReqVO reqVO) {
        List<Long> entityIds = normalizeBatchEntityIds(reqVO.getEntityIds());
        Map<String, Map<String, Object>> patches =
                reqVO.getPatches() != null ? reqVO.getPatches() : Map.of();
        boolean confirmArchive = reqVO.getConfirmArchive() == null
                || Boolean.TRUE.equals(reqVO.getConfirmArchive());

        EntityChangeModelBatchCommitRespVO resp = new EntityChangeModelBatchCommitRespVO();
        for (Long entityId : entityIds) {
            try {
                EntityChangeModelCommitReqVO one = new EntityChangeModelCommitReqVO();
                one.setEntityTypeCode(reqVO.getEntityTypeCode());
                one.setEntityId(entityId);
                one.setTargetModelId(reqVO.getTargetModelId());
                one.setConfirmArchive(confirmArchive);
                Map<String, Object> patch = patches.get(String.valueOf(entityId));
                if (patch != null && !patch.isEmpty()) {
                    one.setPatchFields(patch);
                }
                EntityChangeModelCommitRespVO committed = commit(one);
                resp.getSuccesses().add(committed);
            } catch (ServiceException ex) {
                EntityChangeModelBatchItemVO fail = new EntityChangeModelBatchItemVO();
                fail.setEntityId(entityId);
                fail.setStatus(EntityChangeModelBatchItemVO.STATUS_BLOCKED);
                fail.setReason(ex.getMessage());
                fail.setEntityName(resolveEntityDisplayNameSafe(reqVO.getEntityTypeCode(), entityId));
                resp.getFailures().add(fail);
            } catch (Exception ex) {
                EntityChangeModelBatchItemVO fail = new EntityChangeModelBatchItemVO();
                fail.setEntityId(entityId);
                fail.setStatus(EntityChangeModelBatchItemVO.STATUS_BLOCKED);
                fail.setReason(ex.getMessage() != null ? ex.getMessage() : "提交失败");
                fail.setEntityName(resolveEntityDisplayNameSafe(reqVO.getEntityTypeCode(), entityId));
                resp.getFailures().add(fail);
            }
        }
        resp.setSuccessCount(resp.getSuccesses().size());
        resp.setFailureCount(resp.getFailures().size());
        return resp;
    }

    private List<Long> normalizeBatchEntityIds(List<Long> rawIds) {
        if (rawIds == null || rawIds.isEmpty()) {
            throw new ServiceException(400, "entityIds 不能为空");
        }
        LinkedHashSet<Long> unique = new LinkedHashSet<>();
        for (Long id : rawIds) {
            if (id != null && id > 0) {
                unique.add(id);
            }
        }
        if (unique.isEmpty()) {
            throw new ServiceException(400, "entityIds 无有效 ID");
        }
        if (unique.size() > BATCH_MAX_SIZE) {
            throw new ServiceException(400, "单次最多处理 " + BATCH_MAX_SIZE + " 条，请缩小勾选范围");
        }
        return List.copyOf(unique);
    }

    private String resolveEntityDisplayName(String entityTypeCode, Long entityId,
                                            EntityChangeModelPreviewRespVO preview) {
        return resolveEntityDisplayNameSafe(entityTypeCode, entityId);
    }

    private String resolveEntityDisplayNameSafe(String entityTypeCode, Long entityId) {
        try {
            String trimmedType = entityTypeScopeResolver.resolveStorageEntityTypeCode(
                    entityTypeCode != null ? entityTypeCode.trim() : "");
            EntityDO entity = entityCoreService.get(entityId, trimmedType);
            if (entity != null && StringUtils.hasText(entity.getName())) {
                return entity.getName().trim();
            }
        } catch (Exception ignored) {
            // 名称仅展示用
        }
        return entityId != null ? String.valueOf(entityId) : null;
    }

    private MigrationContext buildContext(String entityTypeCode, Long entityId, Long targetModelId) {
        String rawType = entityTypeCode != null ? entityTypeCode.trim() : "";
        if (!StringUtils.hasText(rawType)) {
            throw new ServiceException(400, "entityTypeCode 不能为空");
        }
        // 侧边栏可能传子数据类型入口编码（如 task_patrol）；实体、型号、分类关联一律按实际存储类型定位
        String trimmedType = entityTypeScopeResolver.resolveStorageEntityTypeCode(rawType);
        if (entityId == null || entityId <= 0) {
            throw new ServiceException(400, "entityId 无效");
        }
        if (targetModelId == null || targetModelId <= 0) {
            throw new ServiceException(400, "targetModelId 无效");
        }

        EntityDO entity = entityCoreService.get(entityId, trimmedType);
        if (entity == null) {
            throw new ServiceException(404, "实体不存在");
        }

        Long sourceModelId = entity.getModelId();
        if (sourceModelId == null) {
            throw new ServiceException(400, "实体缺少 modelId");
        }
        if (Objects.equals(sourceModelId, targetModelId)) {
            throw new ServiceException(400, "目标模型与当前模型相同，无需变更");
        }

        ModelDO sourceModel = requireAliveModel(sourceModelId, "源");
        ModelDO targetModel = requireAliveModel(targetModelId, "目标");
        if (!trimmedType.equals(entityTypeScopeResolver.resolveStorageEntityTypeCode(sourceModel.getEntityTypeCode()))
                || !trimmedType.equals(entityTypeScopeResolver.resolveStorageEntityTypeCode(targetModel.getEntityTypeCode()))) {
            throw new ServiceException(400, "仅允许在同一业务类型内变更模型");
        }

        // 业务域权威链：目标型号的业务域即迁移后的实体业务域；跨业务域时必须是该存储类型下已登记的子数据类型
        String targetDomain = EntityTypeScopeContext.normalizeDomain(targetModel.getDomain());
        if (StringUtils.hasText(targetDomain) && !entityTypeService.isRegisteredDomain(trimmedType, targetDomain)) {
            throw new ServiceException(400,
                    "目标型号的业务域未登记为该数据类型下的子数据类型：" + targetDomain);
        }

        EntityRespVO entityVo = EntityDoVoHelper.toRespVO(entity, customFieldValidationService);
        Map<String, Object> rawSnapshot = buildSnapshot(entityVo);

        List<ModelFieldDescriptor> sourceFields = loadModelFieldDescriptors(trimmedType, sourceModelId);
        List<ModelFieldDescriptor> targetFields = loadModelFieldDescriptors(trimmedType, targetModelId);

        MigrationContext ctx = new MigrationContext();
        ctx.entityTypeCode = trimmedType;
        ctx.entityId = entityId;
        ctx.sourceModel = sourceModel;
        ctx.targetModel = targetModel;
        ctx.entityVo = entityVo;
        ctx.rawSnapshot = rawSnapshot;
        ctx.snapshotLookup = buildSnapshotLookup(trimmedType, rawSnapshot);
        ctx.sourceFieldCodes = descriptorCodes(sourceFields);
        ctx.targetFields = targetFields;
        ctx.targetFieldCodes = descriptorCodes(targetFields);
        ctx.sourceDomain = EntityTypeScopeContext.normalizeDomain(entity.getDomain());
        ctx.targetDomain = targetDomain;
        ctx.domainChanged = !EntityTypeScopeContext.domainsEqual(ctx.sourceDomain, targetDomain);
        ctx.relationCount = (int) entityCategoryRelationMapper.countByEntityIds(List.of(entityId), trimmedType);
        return ctx;
    }

    /**
     * 用前端当前表单草稿覆盖库快照，使预览反映未保存修改。
     */
    private void applyCurrentFieldsOverlay(MigrationContext ctx, Map<String, Object> currentFields) {
        if (currentFields == null || currentFields.isEmpty()) {
            return;
        }
        if (ctx.rawSnapshot == null) {
            ctx.rawSnapshot = new LinkedHashMap<>();
        }
        for (Map.Entry<String, Object> entry : currentFields.entrySet()) {
            String key = entry.getKey();
            if (key == null || key.isBlank()) {
                continue;
            }
            if (ARCHIVE_KEY.equals(key) || SKIP_FIELD_CODES.contains(key.toLowerCase())) {
                continue;
            }
            ctx.rawSnapshot.put(key, entry.getValue());
        }
        ctx.snapshotLookup = buildSnapshotLookup(ctx.entityTypeCode, ctx.rawSnapshot);
    }

    private MigrationPlan computePlan(MigrationContext ctx, Map<String, Object> patchFields) {
        MigrationPlan plan = new MigrationPlan();
        Set<String> consumedSnapshotKeys = new HashSet<>();

        for (ModelFieldDescriptor descriptor : ctx.targetFields) {
            String code = descriptor.fieldCode;
            if (isCorePreserved(code)) {
                continue;
            }
            Object value = resolveSnapshotValue(ctx, code);
            if (isEmptyValue(value)) {
                value = resolvePatchValue(ctx.entityTypeCode, patchFields, code);
            }
            if (!isEmptyValue(value)) {
                plan.keptValues.put(code, value);
                consumedSnapshotKeys.add(code);
                markSnapshotKeysConsumed(ctx, code, consumedSnapshotKeys);
                plan.keptFieldItems.add(toFieldItem(descriptor, value));
                continue;
            }
            if (Boolean.TRUE.equals(descriptor.required)) {
                plan.missingRequired.add(descriptor);
                plan.missingRequiredItems.add(toFieldItem(descriptor, null));
            }
        }

        for (Map.Entry<String, Object> entry : ctx.rawSnapshot.entrySet()) {
            String code = entry.getKey();
            if (isCorePreserved(code) || SKIP_FIELD_CODES.contains(code.toLowerCase())) {
                continue;
            }
            if (code.startsWith("_")) {
                continue;
            }
            String canonical = canonicalFieldCode(ctx.entityTypeCode, code);
            if (ctx.targetFieldCodes.contains(canonical) || ctx.targetFieldCodes.contains(code)) {
                continue;
            }
            if (consumedSnapshotKeys.contains(code) || consumedSnapshotKeys.contains(canonical)) {
                continue;
            }
            if (!ctx.sourceFieldCodes.contains(canonical) && !ctx.sourceFieldCodes.contains(code)
                    && consumedSnapshotKeys.contains(canonical)) {
                continue;
            }
            Object value = entry.getValue();
            if (isEmptyValue(value)) {
                continue;
            }
            plan.archivedFields.put(code, value);
            EntityChangeModelFieldItemVO item = new EntityChangeModelFieldItemVO();
            item.setFieldCode(code);
            item.setLabel(code);
            item.setValue(value);
            item.setBucket("custom");
            plan.archivedFieldItems.add(item);
        }

        if (!plan.archivedFields.isEmpty()) {
            plan.warnings.add("共 " + plan.archivedFields.size() + " 个源模型专有字段将写入归档，不在新模型表单中展示");
        }
        return plan;
    }

    private EntityUpdateReqVO buildUpdateRequest(MigrationContext ctx, MigrationPlan plan) {
        Map<String, Object> baseFields = new LinkedHashMap<>();
        Map<String, Object> customFields = new LinkedHashMap<>();

        if (ctx.entityVo.getBaseFields() != null) {
            for (Map.Entry<String, Object> e : ctx.entityVo.getBaseFields().entrySet()) {
                if (EntityFieldMapsSupport.isCoreBaseFieldKey(e.getKey())) {
                    baseFields.put(e.getKey(), e.getValue());
                }
            }
        }
        baseFields.put("entityTypeCode", ctx.entityTypeCode);
        baseFields.put("modelId", ctx.targetModel.getId());
        // 快照里带的是源业务域，换型号后必须改写为目标型号的业务域，否则会被「请求业务域与型号业务域不一致」拦下
        if (StringUtils.hasText(ctx.targetDomain)) {
            baseFields.put("domain", ctx.targetDomain);
        } else {
            baseFields.remove("domain");
        }
        if (ctx.entityVo.getName() != null) {
            baseFields.put("name", ctx.entityVo.getName());
        }
        if (ctx.entityVo.getStatus() != null) {
            baseFields.put("status", ctx.entityVo.getStatus());
        }
        if (ctx.entityVo.getParentId() != null) {
            baseFields.put("parentId", ctx.entityVo.getParentId());
        }

        for (ModelFieldDescriptor descriptor : ctx.targetFields) {
            String code = descriptor.fieldCode;
            if (isCorePreserved(code)) {
                continue;
            }
            if (!plan.keptValues.containsKey(code)) {
                continue;
            }
            Object value = plan.keptValues.get(code);
            if ("base".equals(descriptor.bucket)) {
                baseFields.put(code, value);
            } else {
                customFields.put(code, value);
            }
        }

        if (!plan.archivedFields.isEmpty()) {
            JSONObject archive = new JSONObject();
            archive.put("fromModelId", ctx.sourceModel.getId());
            archive.put("fromModelCode", ctx.sourceModel.getCode());
            archive.put("toModelId", ctx.targetModel.getId());
            archive.put("toModelCode", ctx.targetModel.getCode());
            archive.put("changedAt", Instant.now().toString());
            archive.put("fields", new JSONObject(plan.archivedFields));
            customFields.put(ARCHIVE_KEY, archive);
        }

        EntityUpdateReqVO req = new EntityUpdateReqVO();
        req.setId(ctx.entityId);
        req.setBaseFields(baseFields);
        req.setCustomFields(customFields);
        return req;
    }

    private EntityChangeModelPreviewRespVO toPreviewResp(MigrationContext ctx, MigrationPlan plan) {
        EntityChangeModelPreviewRespVO resp = new EntityChangeModelPreviewRespVO();
        resp.setEntityId(ctx.entityId);
        resp.setSourceModel(toModelSummary(ctx.sourceModel));
        resp.setTargetModel(toModelSummary(ctx.targetModel));
        resp.setKeptFields(plan.keptFieldItems);
        resp.setMissingRequired(plan.missingRequiredItems);
        resp.setArchivedFields(plan.archivedFieldItems);
        resp.setSourceDomain(ctx.sourceDomain);
        resp.setTargetDomain(ctx.targetDomain);
        resp.setDomainChanged(ctx.domainChanged);
        resp.setRelationCount(ctx.relationCount);
        List<String> warnings = new ArrayList<>(plan.warnings);
        if (ctx.domainChanged) {
            warnings.add("变更后实体业务域为「" + describeDomain(ctx.targetDomain)
                    + "」，其 " + ctx.relationCount + " 条分类关联的业务域将同步迁移");
        }
        resp.setWarnings(warnings);
        return resp;
    }

    private String describeDomain(String domain) {
        return StringUtils.hasText(domain) ? domain : "未划域";
    }

    private EntityChangeModelModelSummaryVO toModelSummary(ModelDO model) {
        EntityChangeModelModelSummaryVO vo = new EntityChangeModelModelSummaryVO();
        vo.setId(model.getId());
        vo.setCode(model.getCode());
        vo.setName(model.getName());
        return vo;
    }

    private EntityChangeModelFieldItemVO toFieldItem(ModelFieldDescriptor descriptor, Object value) {
        EntityChangeModelFieldItemVO item = new EntityChangeModelFieldItemVO();
        item.setFieldCode(descriptor.fieldCode);
        item.setLabel(descriptor.label);
        item.setValue(value);
        item.setBucket(descriptor.bucket);
        item.setRequired(descriptor.required);
        item.setDataType(descriptor.dataType);
        return item;
    }

    /**
     * 变更模型仅允许存活型号：源、目标任一已软删一律拒绝。
     * <p>先查含软删记录，以便区分「不存在」与「已删除」，避免把已删型号当成可迁移源。</p>
     */
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

    private Map<String, Object> buildSnapshot(EntityRespVO entityVo) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        mergeSnapshot(snapshot, entityVo.getBaseFields());
        mergeSnapshot(snapshot, entityVo.getCustomFields());
        return snapshot;
    }

    /**
     * 构建字段查找表：键仅为字段编码（基础字段 fieldCode / 字段库 code），不做物理列名或短名别名。
     */
    private Map<String, Object> buildSnapshotLookup(String entityTypeCode, Map<String, Object> rawSnapshot) {
        Map<String, Object> lookup = new LinkedHashMap<>();
        if (rawSnapshot == null || rawSnapshot.isEmpty()) {
            return lookup;
        }
        for (Map.Entry<String, Object> entry : rawSnapshot.entrySet()) {
            registerSnapshotLookupKeys(lookup, entityTypeCode, entry.getKey(), entry.getValue());
        }
        return lookup;
    }

    private void registerSnapshotLookupKeys(
            Map<String, Object> lookup, String entityTypeCode, String key, Object value) {
        if (!StringUtils.hasText(key) || isEmptyValue(value)) {
            return;
        }
        lookup.putIfAbsent(key.trim(), value);

        String canonical = canonicalFieldCode(entityTypeCode, key);
        if (StringUtils.hasText(canonical)) {
            lookup.putIfAbsent(canonical, value);
            String libraryCode = libraryCodeForRegisteredField(entityTypeCode, canonical);
            if (libraryCode != null) {
                lookup.putIfAbsent(libraryCode, value);
            }
        }
    }

    private Object resolveSnapshotValue(MigrationContext ctx, String fieldCode) {
        if (ctx.snapshotLookup == null || !StringUtils.hasText(fieldCode)) {
            return null;
        }
        Object direct = ctx.snapshotLookup.get(fieldCode);
        if (!isEmptyValue(direct)) {
            return direct;
        }
        String canonical = canonicalFieldCode(ctx.entityTypeCode, fieldCode);
        if (!fieldCode.equals(canonical)) {
            return ctx.snapshotLookup.get(canonical);
        }
        return null;
    }

    private Object resolvePatchValue(String entityTypeCode, Map<String, Object> patchFields, String fieldCode) {
        if (patchFields == null || patchFields.isEmpty() || !StringUtils.hasText(fieldCode)) {
            return null;
        }
        Object direct = patchFields.get(fieldCode);
        if (!isEmptyValue(direct)) {
            return direct;
        }
        String canonical = canonicalFieldCode(entityTypeCode, fieldCode);
        if (!fieldCode.equals(canonical)) {
            Object fromCanonical = patchFields.get(canonical);
            if (!isEmptyValue(fromCanonical)) {
                return fromCanonical;
            }
        }
        String libraryCode = libraryCodeForRegisteredField(entityTypeCode, canonical);
        if (libraryCode != null) {
            Object fromLibrary = patchFields.get(libraryCode);
            if (!isEmptyValue(fromLibrary)) {
                return fromLibrary;
            }
        }
        return null;
    }

    private void markSnapshotKeysConsumed(MigrationContext ctx, String fieldCode, Set<String> consumedSnapshotKeys) {
        String canonical = canonicalFieldCode(ctx.entityTypeCode, fieldCode);
        consumedSnapshotKeys.add(canonical);
        String libraryCode = libraryCodeForRegisteredField(ctx.entityTypeCode, canonical);
        if (libraryCode != null) {
            consumedSnapshotKeys.add(libraryCode);
        }
        if (ctx.rawSnapshot != null) {
            for (String rawKey : ctx.rawSnapshot.keySet()) {
                if (canonical.equals(canonicalFieldCode(ctx.entityTypeCode, rawKey))) {
                    consumedSnapshotKeys.add(rawKey);
                }
            }
        }
    }

    private String canonicalFieldCode(String entityTypeCode, String code) {
        if (!StringUtils.hasText(code)) {
            return code;
        }
        String trimmed = code.trim();
        EntityTypeBaseFieldDO registered =
                entityTypeBaseFieldMapper.selectByEntityTypeCodeAndFieldCode(entityTypeCode, trimmed);
        if (registered != null) {
            return registered.getFieldCode();
        }
        FieldDO libraryField = fieldMapper.selectByCode(trimmed);
        if (libraryField != null) {
            for (EntityTypeBaseFieldDO baseField : entityTypeBaseFieldMapper.selectByEntityTypeCode(entityTypeCode)) {
                if (Objects.equals(baseField.getLibraryFieldId(), libraryField.getId())) {
                    return baseField.getFieldCode();
                }
            }
        }
        return trimmed;
    }

    private String libraryCodeForRegisteredField(String entityTypeCode, String registeredFieldCode) {
        EntityTypeBaseFieldDO baseField =
                entityTypeBaseFieldMapper.selectByEntityTypeCodeAndFieldCode(entityTypeCode, registeredFieldCode);
        if (baseField != null && baseField.getLibraryFieldId() != null) {
            FieldDO libraryField = fieldMapper.selectById(baseField.getLibraryFieldId());
            return libraryField != null ? libraryField.getCode() : null;
        }
        FieldDO libraryField = fieldMapper.selectByCode(registeredFieldCode);
        return libraryField != null ? libraryField.getCode() : null;
    }

    private void mergeSnapshot(Map<String, Object> target, Map<String, Object> source) {
        if (source == null || source.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            if (key == null || key.isBlank()) {
                continue;
            }
            if (ARCHIVE_KEY.equals(key)) {
                continue;
            }
            if (SKIP_FIELD_CODES.contains(key.toLowerCase())) {
                continue;
            }
            target.put(key, entry.getValue());
        }
    }

    private List<ModelFieldDescriptor> loadModelFieldDescriptors(String entityTypeCode, Long modelId) {
        LinkedHashMap<String, ModelFieldDescriptor> byCode = new LinkedHashMap<>();

        List<EntityTypeBaseFieldDO> baseFields = entityTypeBaseFieldMapper.selectByEntityTypeCode(entityTypeCode);
        for (EntityTypeBaseFieldDO baseField : baseFields) {
            if (baseField == null || !baseField.isEnabled() || !StringUtils.hasText(baseField.getFieldCode())) {
                continue;
            }
            String code = baseField.getFieldCode().trim();
            if (shouldSkipFieldCode(code)) {
                continue;
            }
            byCode.putIfAbsent(code, new ModelFieldDescriptor(
                    code,
                    StringUtils.hasText(baseField.getFieldName()) ? baseField.getFieldName() : code,
                    "base",
                    Boolean.TRUE.equals(baseField.getRequired()),
                    baseField.getDataType()));
        }

        List<ModelFieldAssignmentDO> assigns = modelFieldAssignmentMapper.selectByModelId(modelId);
        if (assigns == null || assigns.isEmpty()) {
            return new ArrayList<>(byCode.values());
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
            FieldDO field = fieldById.get(assign.getFieldId());
            String rawCode = field != null && StringUtils.hasText(field.getCode())
                    ? field.getCode().trim()
                    : (StringUtils.hasText(assign.getFieldCode()) ? assign.getFieldCode().trim() : null);
            String code = canonicalFieldCode(entityTypeCode, rawCode);
            if (!StringUtils.hasText(code) || shouldSkipFieldCode(code)) {
                continue;
            }
            String label = field != null && StringUtils.hasText(field.getName()) ? field.getName() : code;
            boolean required = Boolean.TRUE.equals(assign.getRequired());
            String dataType = field != null ? field.getType() : null;
            ModelFieldDescriptor existing = byCode.get(code);
            if (existing != null) {
                existing.required = existing.required || required;
                continue;
            }
            String bucket = "BASE".equalsIgnoreCase(assign.getFieldSource()) ? "base" : "custom";
            byCode.put(code, new ModelFieldDescriptor(code, label, bucket, required, dataType));
        }

        return new ArrayList<>(byCode.values());
    }

    private Set<String> descriptorCodes(List<ModelFieldDescriptor> descriptors) {
        return descriptors.stream().map(d -> d.fieldCode).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private boolean shouldSkipFieldCode(String code) {
        return !StringUtils.hasText(code) || SKIP_FIELD_CODES.contains(code.trim().toLowerCase());
    }

    private boolean isCorePreserved(String code) {
        return code != null && CORE_PRESERVED.contains(code.toLowerCase());
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

    private static final class ModelFieldDescriptor {
        private final String fieldCode;
        private final String label;
        private final String bucket;
        private boolean required;
        private final String dataType;

        private ModelFieldDescriptor(String fieldCode, String label, String bucket, boolean required, String dataType) {
            this.fieldCode = fieldCode;
            this.label = label;
            this.bucket = bucket;
            this.required = required;
            this.dataType = dataType;
        }
    }

    private static final class MigrationContext {
        private String entityTypeCode;
        private Long entityId;
        private ModelDO sourceModel;
        private ModelDO targetModel;
        private EntityRespVO entityVo;
        private Map<String, Object> rawSnapshot;
        private Map<String, Object> snapshotLookup;
        private Set<String> sourceFieldCodes;
        private List<ModelFieldDescriptor> targetFields;
        private Set<String> targetFieldCodes;
        private String sourceDomain;
        private String targetDomain;
        private boolean domainChanged;
        private int relationCount;
    }

    private static final class MigrationPlan {
        private final Map<String, Object> keptValues = new LinkedHashMap<>();
        private final List<EntityChangeModelFieldItemVO> keptFieldItems = new ArrayList<>();
        private final List<ModelFieldDescriptor> missingRequired = new ArrayList<>();
        private final List<EntityChangeModelFieldItemVO> missingRequiredItems = new ArrayList<>();
        private final Map<String, Object> archivedFields = new LinkedHashMap<>();
        private final List<EntityChangeModelFieldItemVO> archivedFieldItems = new ArrayList<>();
        private final List<String> warnings = new ArrayList<>();
    }
}
