package cn.cheers.x.inspection.task.service.task.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskCreateReqVO;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskUpdateReqVO;
import cn.cheers.x.inspection.task.dal.dataobject.task.InspectionTaskDO;
import cn.cheers.x.inspection.task.dal.mysql.task.InspectionTaskMapper;
import cn.cheers.x.inspection.task.service.task.InspectionTaskService;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;

/**
 * 巡检任务 Service 实现类。
 *
 * <p>任务的基础CRUD及编排相关的操作。</p>
 */
@Service
@RequiredArgsConstructor
public class InspectionTaskServiceImpl implements InspectionTaskService {

    private final InspectionTaskMapper inspectionTaskMapper;
    private final PatrolTaskEntityStore patrolTaskEntityStore;

    // ==================== 基础CRUD ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTask(InspectionTaskCreateReqVO reqVO) {
        validateExecutionDeviceBinding(reqVO.getExecutionDeviceBinding());
        return patrolTaskEntityStore.createDraft(reqVO);
    }

    /**
     * 生成任务编码。
     * 格式：TASK-yyyyMMdd-xxxx（日期 + 4位序号）
     */
    private String generateTaskCode() {
        String datePart = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        // 简单实现：使用时间戳后4位 + 随机数，确保唯一性
        String uniquePart = String.format("%04d", Math.abs((int) (System.currentTimeMillis() % 10000)));
        return "TASK-" + datePart + "-" + uniquePart;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTask(InspectionTaskUpdateReqVO reqVO) {
        validateExecutionDeviceBinding(reqVO.getExecutionDeviceBinding());
        patrolTaskEntityStore.updateDraft(reqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTask(Long id) {
        patrolTaskEntityStore.delete(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableTask(Long id) {
        validateTaskExists(id);
        InspectionTaskDO update = new InspectionTaskDO();
        update.setId(id);
        update.setEnabled(Boolean.TRUE);
        inspectionTaskMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableTask(Long id) {
        validateTaskExists(id);
        InspectionTaskDO update = new InspectionTaskDO();
        update.setId(id);
        update.setEnabled(Boolean.FALSE);
        inspectionTaskMapper.updateById(update);
    }

    @Override
    public InspectionTaskDO getTask(Long id) {
        return toLegacyDo(patrolTaskEntityStore.require(id));
    }

    @Override
    public List<InspectionTaskDO> getTasksByIds(List<Long> ids) {
        return inspectionTaskMapper.selectByIds(ids);
    }

    // ==================== 编排相关查询 ====================

    @Override
    public List<InspectionTaskDO> getTasksBySchedulePolicyId(Long schedulePolicyId) {
        return inspectionTaskMapper.selectBySchedulePolicyId(schedulePolicyId);
    }

    @Override
    public List<InspectionTaskDO> getActiveTasks() {
        return inspectionTaskMapper.selectActiveTasks();
    }

    @Override
    public List<InspectionTaskDO> getTasksByActivePlanId(Long activePlanId) {
        return inspectionTaskMapper.selectByActivePlanId(activePlanId);
    }

    @Override
    public List<InspectionTaskDO> getTasksWithoutSchedulePolicy() {
        return inspectionTaskMapper.selectWithoutSchedulePolicy();
    }

    // ==================== 树形结构查询 ====================

    @Override
    public List<InspectionTaskDO> getSubTasks(Long parentId) {
        return inspectionTaskMapper.selectByParentId(parentId);
    }

    @Override
    public List<InspectionTaskDO> getRootTasks() {
        return inspectionTaskMapper.selectRootTasks();
    }

    // ==================== 继承配置查询 ====================

    @Override
    public List<InspectionTaskDO> getTasksNeedInheritSchedule() {
        return inspectionTaskMapper.selectNeedInheritSchedule();
    }

    @Override
    public List<InspectionTaskDO> getTasksNeedInheritResourcePolicy() {
        return inspectionTaskMapper.selectNeedInheritResourcePolicy();
    }

    // ==================== 冲突检测 ====================

    @Override
    public List<InspectionTaskDO> getPotentialConflictTasks(Long schedulePolicyId, Long categoryId) {
        return inspectionTaskMapper.selectPotentialConflict(schedulePolicyId, categoryId);
    }

    // ==================== 统计 ====================

    @Override
    public long countByStatus(Integer status) {
        return inspectionTaskMapper.countByStatus(status);
    }

    @Override
    public long countBySchedulePolicyId(Long schedulePolicyId) {
        return inspectionTaskMapper.countBySchedulePolicyId(schedulePolicyId);
    }

    @Override
    public long countEnabled() {
        return inspectionTaskMapper.countEnabled();
    }

    // ==================== 私有方法 ====================

    private InspectionTaskDO validateTaskExists(Long id) {
        return toLegacyDo(patrolTaskEntityStore.require(id));
    }

    /**
     * 排期等旧调用方仍吃 InspectionTaskDO。只从总任务投影，不再读固定表。
     */
    public static InspectionTaskDO toLegacyDo(PatrolTaskDraft draft) {
        InspectionTaskDO task = new InspectionTaskDO();
        task.setId(draft.id());
        task.setTaskName(draft.name());
        task.setDomain(draft.domain());
        task.setInspectionContent(draft.inspectionContent());
        task.setExecutionDeviceBinding(draft.executionDeviceBinding());
        task.setResourcePolicy(draft.resourcePolicy());
        task.setStatus("draft".equals(draft.manageStatus()) ? 0 : 1);
        task.setEnabled(Boolean.FALSE);
        return task;
    }

    private void validateTaskCodeUnique(String taskCode, Long id) {
        if (taskCode == null) {
            return;
        }
        InspectionTaskDO existing = inspectionTaskMapper.selectByTaskCode(taskCode);
        if (existing != null && !Objects.equals(existing.getId(), id)) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "任务编码已存在");
        }
    }

    private void validateParentTask(Long parentId) {
        if (parentId == null) {
            return;
        }
        validateTaskExists(parentId);
    }

    /**
     * 排期校验（支持草稿：创建时可暂不配排期）。
     * <ul>
     *   <li>继承父任务排期时必须有父任务</li>
     *   <li>非继承时：排期需求/策略均可空（后续再配）；若只填其一则两者都必填</li>
     * </ul>
     */
    private void validateScheduleInheritance(Boolean inheritParentSchedule, Long parentId,
                                            Long scheduleRequirementId, Long schedulePolicyId) {
        if (Boolean.TRUE.equals(inheritParentSchedule) && parentId == null) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "未设置父任务时不能继承父任务排期");
        }
        if (Boolean.TRUE.equals(inheritParentSchedule)) {
            return;
        }
        // 草稿允许两者皆空；若已开始配置则需求与策略需成对出现
        boolean hasRequirement = scheduleRequirementId != null;
        boolean hasPolicy = schedulePolicyId != null;
        if (hasRequirement != hasPolicy) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST,
                    hasRequirement ? "已设置排期需求时必须同时设置排期策略"
                            : "已设置排期策略时必须同时设置排期需求");
        }
    }

    /**
     * 资源策略校验（支持草稿：创建时可暂不配资源策略）。
     * 仅在声明继承父任务资源策略时要求有父任务。
     */
    private void validateResourceInheritance(Boolean inheritParentResourcePolicy, Long parentId,
                                             Object resourcePolicy) {
        if (Boolean.TRUE.equals(inheritParentResourcePolicy) && parentId == null) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "未设置父任务时不能继承父任务资源策略");
        }
    }

    /**
     * 执行设备绑定：允许整段为空（草稿）；一旦出现任一字段则三者必须齐全。
     */
    private void validateExecutionDeviceBinding(ExecutionDeviceBinding binding) {
        if (binding == null) {
            return;
        }
        boolean hasEquipment = binding.getEquipmentId() != null;
        boolean hasProtocol = StringUtils.hasText(binding.getProtocolCode());
        boolean hasLogical = StringUtils.hasText(binding.getLogicalDeviceId());
        if (!hasEquipment && !hasProtocol && !hasLogical) {
            return;
        }
        if (!hasEquipment || !hasProtocol || !hasLogical) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST,
                    "执行设备绑定须同时提供 equipmentId、protocolCode、logicalDeviceId");
        }
    }
}
