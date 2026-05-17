package cn.iocoder.yudao.module.inspection.task.service.task.impl;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.inspection.task.controller.admin.vo.task.InspectionTaskCreateReqVO;
import cn.iocoder.yudao.module.inspection.task.controller.admin.vo.task.InspectionTaskUpdateReqVO;
import cn.iocoder.yudao.module.inspection.task.dal.dataobject.task.InspectionTaskDO;
import cn.iocoder.yudao.module.inspection.task.dal.mysql.task.InspectionTaskMapper;
import cn.iocoder.yudao.module.inspection.task.service.task.InspectionTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;

/**
 * 巡检任务 Service 实现类。
 *
 * <p>任务的基础CRUD及编排相关的操作。</p>
 */
@Service
@RequiredArgsConstructor
public class InspectionTaskServiceImpl implements InspectionTaskService {

    private final InspectionTaskMapper inspectionTaskMapper;

    // ==================== 基础CRUD ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTask(InspectionTaskCreateReqVO reqVO) {
        // 自动生成任务编码（如果未提供）
        if (reqVO.getTaskCode() == null || reqVO.getTaskCode().isBlank()) {
            reqVO.setTaskCode(generateTaskCode());
        } else {
            validateTaskCodeUnique(reqVO.getTaskCode(), null);
        }

        validateParentTask(reqVO.getParentId());
        validateScheduleInheritance(reqVO.getInheritParentSchedule(), reqVO.getParentId(),
                reqVO.getScheduleRequirementId(), reqVO.getSchedulePolicyId());
        validateResourceInheritance(reqVO.getInheritParentResourcePolicy(), reqVO.getParentId(),
                reqVO.getResourcePolicy());

        InspectionTaskDO taskDO = BeanUtils.toBean(reqVO, InspectionTaskDO.class);
        if (taskDO.getStatus() == null) {
            taskDO.setStatus(1);
        }
        if (taskDO.getEnabled() == null) {
            taskDO.setEnabled(Boolean.TRUE);
        }
        inspectionTaskMapper.insert(taskDO);
        return taskDO.getId();
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
        validateTaskExists(reqVO.getId());
        // 注意：taskCode 不允许修改，不调用 validateTaskCodeUnique
        validateParentTask(reqVO.getParentId());
        if (Objects.equals(reqVO.getId(), reqVO.getParentId())) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "任务不能将自己设置为父任务");
        }
        validateScheduleInheritance(reqVO.getInheritParentSchedule(), reqVO.getParentId(),
                reqVO.getScheduleRequirementId(), reqVO.getSchedulePolicyId());
        validateResourceInheritance(reqVO.getInheritParentResourcePolicy(), reqVO.getParentId(),
                reqVO.getResourcePolicy());

        InspectionTaskDO updateDO = BeanUtils.toBean(reqVO, InspectionTaskDO.class);
        inspectionTaskMapper.updateById(updateDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTask(Long id) {
        validateTaskExists(id);
        inspectionTaskMapper.deleteById(id);
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
        return inspectionTaskMapper.selectById(id);
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
        InspectionTaskDO task = inspectionTaskMapper.selectById(id);
        if (task == null) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "任务不存在");
        }
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

    private void validateScheduleInheritance(Boolean inheritParentSchedule, Long parentId,
                                            Long scheduleRequirementId, Long schedulePolicyId) {
        if (Boolean.TRUE.equals(inheritParentSchedule) && parentId == null) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "未设置父任务时不能继承父任务排期");
        }
        if (!Boolean.TRUE.equals(inheritParentSchedule)) {
            if (scheduleRequirementId == null) {
                throw ServiceExceptionUtil.exception(BAD_REQUEST, "未继承父任务排期时必须设置排期需求");
            }
            if (schedulePolicyId == null) {
                throw ServiceExceptionUtil.exception(BAD_REQUEST, "未继承父任务排期时必须设置排期策略");
            }
        }
    }

    private void validateResourceInheritance(Boolean inheritParentResourcePolicy, Long parentId,
                                             Object resourcePolicy) {
        if (Boolean.TRUE.equals(inheritParentResourcePolicy) && parentId == null) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "未设置父任务时不能继承父任务资源策略");
        }
        if (!Boolean.TRUE.equals(inheritParentResourcePolicy) && resourcePolicy == null) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "未继承父任务资源策略时必须设置资源策略");
        }
    }
}
