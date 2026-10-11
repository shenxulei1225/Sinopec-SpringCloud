package cn.cheers.x.module.dynamicbusiness.service.inspection;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.TaskExecutionBootstrapReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryEntityLinkDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityCategoryRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityCategoryRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.framework.hierarchy.IdTreeHierarchy;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryEntityLinkService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityCacheEvictionService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityDedicatedColumnService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.entity.refcategory.EntityRefCategoryCategoryProjectionService;
import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 生成任务时，把同一份步骤清单写入多条待执行账。
 * <p>管什么：步骤行、步骤指向哪条执行账、所属场站、场站分类。同一层步骤一次插入。
 * <p>不管什么：单条执行任务仍走原来的逐步新建；这里不重算步骤图，也不逐条补所属场站、不逐条挂分类。
 * 步骤编码、顺序、标题、类型、是否必做、状态、结果写在步骤表对应列，不另存进扩展字段。
 */
@Component
@RequiredArgsConstructor
public class TaskExecutionStepBatchWriter {

    private static final String STEP_ENTITY_TYPE = TaskExecutionBootstrapServiceImpl.STEP_ENTITY_TYPE;
    private static final String STEP_MODEL_CODE = TaskExecutionBootstrapServiceImpl.STEP_MODEL_CODE;
    private static final String RELATION_TYPE_ENTITY_REF = "ENTITY_REF";

    private final EntityDedicatedColumnService entityDedicatedColumnService;
    private final EntityRelationMapper entityRelationMapper;
    private final EntityCategoryRelationMapper entityCategoryRelationMapper;
    private final CategoryEntityLinkService categoryEntityLinkService;
    private final EntityRefCategoryCategoryProjectionService refCategoryCategoryProjectionService;
    private final EntityService entityService;
    private final ModelMapper modelMapper;
    private final EntityCacheEvictionService entityCacheEvictionService;

    public void write(
            String recordType,
            String recordModelCode,
            Long facilityId,
            List<Long> executionRecordIds,
            List<TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO> steps) {
        if (facilityId == null || facilityId <= 0) {
            throw new ServiceException(400, "步骤缺少所属场站");
        }
        if (executionRecordIds == null || executionRecordIds.isEmpty()) {
            throw new ServiceException(400, "没有待写入的执行账");
        }
        if (steps == null || steps.isEmpty()) {
            throw new ServiceException(400, "步骤清单为空");
        }
        ModelDO stepModel = modelMapper.selectByCode(STEP_MODEL_CODE);
        if (stepModel == null || stepModel.getId() == null) {
            throw new ServiceException(500, "缺少型号 task_execution_step，请先跑 task-execution seed");
        }
        String facilityModelCode = facilityModelCode(facilityId);
        Map<Long, Map<String, Long>> stepIdByRecord = new LinkedHashMap<>();
        Map<Long, String> treePathByStepId = new LinkedHashMap<>();
        for (Long recordId : executionRecordIds) {
            if (recordId == null) {
                throw new ServiceException(400, "执行账编号为空");
            }
            stepIdByRecord.put(recordId, new LinkedHashMap<>());
        }
        List<PlacedStep> placed = new ArrayList<>();
        for (TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO draft : steps) {
            placed.addAll(insertOneStepAcrossRecords(
                    draft, stepModel, facilityId, executionRecordIds, stepIdByRecord, treePathByStepId));
        }
        insertRecordAndFacilityRelations(
                placed, stepModel, recordType, recordModelCode, facilityId, facilityModelCode);
        attachFacilityCategoryOnce(stepModel, facilityId, placed);
        entityCacheEvictionService.evictEntityCaches(stepModel.getId(), STEP_ENTITY_TYPE);
    }

    /**
     * 一次插入本批待执行账。编号顺序与 accounts 一致。
     * 所属场站写在场站列，并挂到该场站分类。计划执行时间写在 planned_start。不写步骤。
     */
    public List<Long> insertPendingRecords(
            String recordType,
            Long modelId,
            Long facilityId,
            Long taskDefinitionId,
            Object standardSnapshot,
            Object parameterSnapshot,
            List<TaskExecutionBootstrapService.PendingAccount> accounts) {
        if (accounts == null || accounts.isEmpty()) {
            throw new ServiceException(400, "没有待写入的计划点");
        }
        if (facilityId == null || facilityId <= 0) {
            throw new ServiceException(400, "待执行账缺少所属场站");
        }
        if (modelId == null) {
            throw new ServiceException(400, "缺少执行账型号");
        }
        ModelDO recordModel = modelMapper.selectById(modelId);
        if (recordModel == null || recordModel.getId() == null) {
            throw new ServiceException(500, "缺少执行账型号：" + modelId);
        }
        String facilityModelCode = facilityModelCode(facilityId);
        List<EntityDO> rows = new ArrayList<>(accounts.size());
        List<Map<String, Object>> physical = new ArrayList<>(accounts.size());
        for (int i = 0; i < accounts.size(); i++) {
            TaskExecutionBootstrapService.PendingAccount account = accounts.get(i);
            if (account == null || !StringUtils.hasText(account.pendingRef())) {
                throw new ServiceException(400, "计划点编号不能为空");
            }
            EntityDO row = new EntityDO();
            row.setTenantId(TenantContextHolder.getTenantId());
            row.setEntityTypeCode(recordType);
            row.setModelId(modelId);
            row.setName(StringUtils.hasText(account.name())
                    ? account.name().trim()
                    : "执行-" + taskDefinitionId);
            row.setCode(recordType + "-" + IdUtil.fastSimpleUUID());
            row.setStatus(1);
            row.setParentId(0L);
            row.setSort(i);
            row.setDomain(recordModel.getDomain());
            Map<String, Object> custom = new LinkedHashMap<>();
            custom.put("task_id", taskDefinitionId);
            custom.put("pending_execution_id", account.pendingRef().trim());
            custom.put("execution_status", "pending");
            if (StringUtils.hasText(account.plannedStart())) {
                custom.put("planned_start", account.plannedStart().trim());
            }
            custom.put("standard_snapshot", standardSnapshot);
            if (parameterSnapshot != null) {
                custom.put("parameter_snapshot", parameterSnapshot);
            }
            custom.put("gap_codes", List.of());
            row.setCustomFields(custom);
            rows.add(row);
            Map<String, Object> columns = new LinkedHashMap<>();
            columns.put(FacilityOwningFieldCodes.FIELD_CODE, facilityId);
            physical.add(columns);
        }
        List<Long> ids = entityDedicatedColumnService.insertEntityRows(rows, physical);
        if (ids.size() != accounts.size()) {
            throw new ServiceException(500, "待执行账写入数量与计划点不一致");
        }
        insertRecordFacilityRelations(ids, recordType, recordModel, facilityId, facilityModelCode);
        attachFacilityCategory(recordModel, recordType, facilityId, ids);
        entityCacheEvictionService.evictEntityCaches(recordModel.getId(), recordType);
        return ids;
    }

    private List<PlacedStep> insertOneStepAcrossRecords(
            TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO draft,
            ModelDO stepModel,
            Long facilityId,
            List<Long> executionRecordIds,
            Map<Long, Map<String, Long>> stepIdByRecord,
            Map<Long, String> treePathByStepId) {
        if (draft == null || !StringUtils.hasText(draft.getStepCode())) {
            throw new ServiceException(400, "步骤 draft 缺少 stepCode");
        }
        String stepCode = draft.getStepCode().trim();
        List<EntityDO> rows = new ArrayList<>(executionRecordIds.size());
        List<Map<String, Object>> physical = new ArrayList<>(executionRecordIds.size());
        List<Long> parentIds = new ArrayList<>(executionRecordIds.size());
        for (Long recordId : executionRecordIds) {
            Map<String, Long> codes = stepIdByRecord.get(recordId);
            if (codes.containsKey(stepCode)) {
                throw new ServiceException(400, "步骤 stepCode 重复：" + stepCode);
            }
            Long parentId = parentIdOf(draft, codes);
            parentIds.add(parentId);
            rows.add(stepRow(draft, stepModel, parentId));
            physical.add(stepPhysicalColumns(draft, stepCode, facilityId, recordId));
        }
        List<Long> ids = entityDedicatedColumnService.insertEntityRows(rows, physical);
        Map<Long, String> treePaths = new LinkedHashMap<>();
        List<PlacedStep> placed = new ArrayList<>(ids.size());
        for (int i = 0; i < ids.size(); i++) {
            Long recordId = executionRecordIds.get(i);
            Long stepId = ids.get(i);
            Long parentId = parentIds.get(i);
            stepIdByRecord.get(recordId).put(stepCode, stepId);
            String parentPath = parentId == null || parentId == 0L
                    ? null
                    : treePathByStepId.get(parentId);
            String treePath = IdTreeHierarchy.buildPath(parentPath, stepId);
            treePaths.put(stepId, treePath);
            treePathByStepId.put(stepId, treePath);
            placed.add(new PlacedStep(stepId, recordId, draft.getStepOrder()));
        }
        entityDedicatedColumnService.updateTreePaths(STEP_ENTITY_TYPE, treePaths);
        return placed;
    }

    private static Long parentIdOf(
            TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO draft,
            Map<String, Long> codes) {
        if (!StringUtils.hasText(draft.getParentStepCode())) {
            return 0L;
        }
        Long parentId = codes.get(draft.getParentStepCode().trim());
        if (parentId == null) {
            throw new ServiceException(400,
                    "步骤 parentStepCode 未找到或顺序错误（父须在子之前）：" + draft.getParentStepCode());
        }
        return parentId;
    }

    private EntityDO stepRow(
            TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO draft,
            ModelDO stepModel,
            Long parentId) {
        EntityDO row = new EntityDO();
        row.setTenantId(TenantContextHolder.getTenantId());
        row.setEntityTypeCode(STEP_ENTITY_TYPE);
        row.setModelId(stepModel.getId());
        row.setName(draft.getName());
        row.setCode(STEP_ENTITY_TYPE + "-" + IdUtil.fastSimpleUUID());
        row.setStatus(1);
        row.setParentId(parentId == null ? 0L : parentId);
        row.setSort(draft.getStepOrder() == null ? 0 : draft.getStepOrder());
        row.setDomain(stepModel.getDomain());
        row.setCustomFields(new LinkedHashMap<>());
        return row;
    }

    /**
     * 与逐条创建写进步骤表的列相同。缺 step_code 时数据库会拒绝插入。
     * 每一行的列必须一样，没有结果时也带上 result_payload（空）。
     */
    static Map<String, Object> stepPhysicalColumns(
            TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO draft,
            String stepCode,
            Long facilityId,
            Long recordId) {
        Map<String, Object> columns = new LinkedHashMap<>();
        columns.put(FacilityOwningFieldCodes.FIELD_CODE, facilityId);
        columns.put("execution_record_id", recordId);
        columns.put("step_code", stepCode);
        columns.put("step_order", draft.getStepOrder() == null ? 0 : draft.getStepOrder());
        columns.put("step_title", draft.getStepTitle() != null ? draft.getStepTitle() : draft.getName());
        columns.put("step_type", draft.getStepType() != null ? draft.getStepType() : "sop_step");
        columns.put("step_required", draft.getStepRequired() == null || draft.getStepRequired());
        columns.put("step_status", "pending");
        if (draft.getSource() == null) {
            columns.put("result_payload", null);
        } else {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("source", draft.getSource());
            columns.put("result_payload", payload);
        }
        return columns;
    }

    private void insertRecordAndFacilityRelations(
            List<PlacedStep> placed,
            ModelDO stepModel,
            String recordType,
            String recordModelCode,
            Long facilityId,
            String facilityModelCode) {
        List<EntityRelationDO> relations = new ArrayList<>(placed.size() * 2);
        Long tenantId = TenantContextHolder.getTenantId();
        for (PlacedStep step : placed) {
            relations.add(refRelation(
                    step.stepId(), step.recordId(), "execution_record_id", "执行记录",
                    stepModel.getCode(), recordModelCode, recordType, tenantId));
            relations.add(refRelation(
                    step.stepId(), facilityId, FacilityOwningFieldCodes.FIELD_CODE,
                    FacilityOwningFieldCodes.DISPLAY_NAME,
                    stepModel.getCode(), facilityModelCode,
                    FacilityOwningFieldCodes.TARGET_ENTITY_TYPE, tenantId));
        }
        entityRelationMapper.insertBatch(relations, 200);
    }

    private static EntityRelationDO refRelation(
            Long sourceId,
            Long targetId,
            String fieldCode,
            String relationName,
            String sourceModelCode,
            String targetModelCode,
            String targetType,
            Long tenantId) {
        EntityRelationDO relation = EntityRelationDO.builder()
                .sourceEntityId(sourceId)
                .targetEntityId(targetId)
                .relationType(RELATION_TYPE_ENTITY_REF)
                .relationName(relationName)
                .fieldCode(fieldCode)
                .sourceModelCode(sourceModelCode)
                .targetModelCode(targetModelCode)
                .sourceEntityTypeCode(STEP_ENTITY_TYPE)
                .targetEntityTypeCode(targetType)
                .status(1)
                .build();
        relation.setTenantId(tenantId);
        return relation;
    }

    private void attachFacilityCategoryOnce(ModelDO stepModel, Long facilityId, List<PlacedStep> placed) {
        CategoryEntityLinkDO link = categoryEntityLinkService.getLinkByEntityIdAndEntityTypeCode(
                facilityId, FacilityOwningFieldCodes.TARGET_ENTITY_TYPE);
        if (link == null || link.getCategoryId() == null) {
            return;
        }
        Long tenantId = TenantContextHolder.getTenantId();
        List<EntityCategoryRelationDO> categoryRows = new ArrayList<>(placed.size());
        for (PlacedStep step : placed) {
            EntityCategoryRelationDO row = EntityCategoryRelationDO.builder()
                    .entityId(step.stepId())
                    .categoryId(link.getCategoryId())
                    .entityTypeCode(STEP_ENTITY_TYPE)
                    .domain(stepModel.getDomain())
                    .sort(step.stepOrder() == null ? 0 : step.stepOrder())
                    .build();
            row.setTenantId(tenantId);
            categoryRows.add(row);
        }
        entityCategoryRelationMapper.insertBatchRelations(categoryRows);
        refCategoryCategoryProjectionService.syncCategoryCategoryOnRefAssociate(
                stepModel.getId(),
                STEP_ENTITY_TYPE,
                link.getCategoryId(),
                FacilityOwningFieldCodes.TARGET_ENTITY_TYPE);
    }

    private void insertRecordFacilityRelations(
            List<Long> recordIds,
            String recordType,
            ModelDO recordModel,
            Long facilityId,
            String facilityModelCode) {
        List<EntityRelationDO> relations = new ArrayList<>(recordIds.size());
        Long tenantId = TenantContextHolder.getTenantId();
        for (Long recordId : recordIds) {
            relations.add(recordFacilityRelation(
                    recordId, facilityId, recordType, recordModel.getCode(), facilityModelCode, tenantId));
        }
        entityRelationMapper.insertBatch(relations, 200);
    }

    private static EntityRelationDO recordFacilityRelation(
            Long recordId,
            Long facilityId,
            String recordType,
            String recordModelCode,
            String facilityModelCode,
            Long tenantId) {
        EntityRelationDO relation = EntityRelationDO.builder()
                .sourceEntityId(recordId)
                .targetEntityId(facilityId)
                .relationType(RELATION_TYPE_ENTITY_REF)
                .relationName(FacilityOwningFieldCodes.DISPLAY_NAME)
                .fieldCode(FacilityOwningFieldCodes.FIELD_CODE)
                .sourceModelCode(recordModelCode)
                .targetModelCode(facilityModelCode)
                .sourceEntityTypeCode(recordType)
                .targetEntityTypeCode(FacilityOwningFieldCodes.TARGET_ENTITY_TYPE)
                .status(1)
                .build();
        relation.setTenantId(tenantId);
        return relation;
    }

    private void attachFacilityCategory(
            ModelDO recordModel, String recordType, Long facilityId, List<Long> recordIds) {
        CategoryEntityLinkDO link = categoryEntityLinkService.getLinkByEntityIdAndEntityTypeCode(
                facilityId, FacilityOwningFieldCodes.TARGET_ENTITY_TYPE);
        if (link == null || link.getCategoryId() == null) {
            return;
        }
        Long tenantId = TenantContextHolder.getTenantId();
        List<EntityCategoryRelationDO> categoryRows = new ArrayList<>(recordIds.size());
        for (int i = 0; i < recordIds.size(); i++) {
            EntityCategoryRelationDO row = EntityCategoryRelationDO.builder()
                    .entityId(recordIds.get(i))
                    .categoryId(link.getCategoryId())
                    .entityTypeCode(recordType)
                    .domain(recordModel.getDomain())
                    .sort(i)
                    .build();
            row.setTenantId(tenantId);
            categoryRows.add(row);
        }
        entityCategoryRelationMapper.insertBatchRelations(categoryRows);
        refCategoryCategoryProjectionService.syncCategoryCategoryOnRefAssociate(
                recordModel.getId(),
                recordType,
                link.getCategoryId(),
                FacilityOwningFieldCodes.TARGET_ENTITY_TYPE);
    }

    private String facilityModelCode(Long facilityId) {
        EntityRespVO facility = entityService.get(facilityId, FacilityOwningFieldCodes.TARGET_ENTITY_TYPE);
        if (facility == null || facility.getId() == null) {
            throw new ServiceException(400, "所属场站不存在：" + facilityId);
        }
        if (facility.getModelId() == null) {
            return null;
        }
        ModelDO model = modelMapper.selectById(facility.getModelId());
        return model == null ? null : model.getCode();
    }

    private record PlacedStep(Long stepId, Long recordId, Integer stepOrder) {
    }
}
