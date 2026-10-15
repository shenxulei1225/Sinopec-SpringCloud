package cn.cheers.x.module.dynamicbusiness.service.inspection;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.TaskExecutionBootstrapReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryEntityLinkDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityCategoryRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityCategoryRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryEntityLinkService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityCacheEvictionService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityDedicatedColumnService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.entity.refcategory.EntityRefCategoryCategoryProjectionService;
import cn.cheers.x.module.dynamicbusiness.service.execution.ExecutionAccountStepTree;
import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 生成任务时一次写下本批待执行账。
 * <p>管什么：每笔执行账一行。步骤图写在这行的 step_tree_json。所属场站写在场站列，并挂到该场站分类。
 * <p>不管什么：不把步骤拆成步骤行，不把所属场站写入实体关系表，不重算步骤图。
 */
@Component
@RequiredArgsConstructor
public class TaskExecutionStepBatchWriter {

    private final EntityDedicatedColumnService entityDedicatedColumnService;
    private final EntityCategoryRelationMapper entityCategoryRelationMapper;
    private final CategoryEntityLinkService categoryEntityLinkService;
    private final EntityRefCategoryCategoryProjectionService refCategoryCategoryProjectionService;
    private final EntityService entityService;
    private final ModelMapper modelMapper;
    private final EntityCacheEvictionService entityCacheEvictionService;

    /**
     * 所属场站只写在场站列，并挂到该场站分类。不写入实体关系表。
     * 计划执行时间写在 planned_start。步骤图写在 step_tree_json，不另建步骤行。
     */
    public List<Long> insertPendingRecords(
            String recordType,
            Long modelId,
            Long facilityId,
            Long taskDefinitionId,
            Object standardSnapshot,
            Object parameterSnapshot,
            List<TaskExecutionBootstrapService.PendingAccount> accounts,
            List<TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO> steps) {
        if (accounts == null || accounts.isEmpty()) {
            throw new ServiceException(400, "没有待写入的计划点");
        }
        if (facilityId == null || facilityId <= 0) {
            throw new ServiceException(400, "待执行账缺少所属场站");
        }
        if (modelId == null) {
            throw new ServiceException(400, "缺少执行账型号");
        }
        List<Map<String, Object>> stepTree = ExecutionAccountStepTree.freeze(steps);
        ModelDO recordModel = modelMapper.selectById(modelId);
        if (recordModel == null || recordModel.getId() == null) {
            throw new ServiceException(500, "缺少执行账型号：" + modelId);
        }
        requireFacility(facilityId);
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
            ExecutionAccountStepTree.putFrozen(custom, stepTree);
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
        attachFacilityCategory(recordModel, recordType, facilityId, ids);
        entityCacheEvictionService.evictEntityCaches(recordModel.getId(), recordType);
        return ids;
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

    /** 所属场站必须已经存在。不因此再写一条场站关系。 */
    private void requireFacility(Long facilityId) {
        EntityRespVO facility = entityService.get(facilityId, FacilityOwningFieldCodes.TARGET_ENTITY_TYPE);
        if (facility == null || facility.getId() == null) {
            throw new ServiceException(400, "所属场站不存在：" + facilityId);
        }
    }
}
