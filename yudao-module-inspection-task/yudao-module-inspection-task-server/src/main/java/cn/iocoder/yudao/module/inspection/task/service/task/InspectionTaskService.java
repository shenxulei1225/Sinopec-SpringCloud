package cn.iocoder.yudao.module.inspection.task.service.task;

import cn.iocoder.yudao.module.inspection.task.controller.admin.vo.task.InspectionTaskCreateReqVO;
import cn.iocoder.yudao.module.inspection.task.controller.admin.vo.task.InspectionTaskUpdateReqVO;
import cn.iocoder.yudao.module.inspection.task.dal.dataobject.task.InspectionTaskDO;

import java.util.List;

/**
 * 巡检任务服务。
 */
public interface InspectionTaskService {

    // ==================== 基础CRUD ====================

    /**
     * 创建任务。
     */
    Long createTask(InspectionTaskCreateReqVO reqVO);

    /**
     * 更新任务。
     *
     * <p>包含排期策略变更。</p>
     */
    void updateTask(InspectionTaskUpdateReqVO reqVO);

    /**
     * 删除任务。
     */
    void deleteTask(Long id);

    /**
     * 启用任务。
     */
    void enableTask(Long id);

    /**
     * 禁用任务。
     */
    void disableTask(Long id);

    /**
     * 获取任务详情。
     */
    InspectionTaskDO getTask(Long id);

    /**
     * 批量获取任务。
     */
    List<InspectionTaskDO> getTasksByIds(List<Long> ids);

    // ==================== 编排相关查询 ====================

    /**
     * 根据排期策略ID查询任务列表。
     */
    List<InspectionTaskDO> getTasksBySchedulePolicyId(Long schedulePolicyId);

    /**
     * 获取所有启用的任务。
     */
    List<InspectionTaskDO> getActiveTasks();

    /**
     * 根据激活的编排批次ID查询任务列表。
     */
    List<InspectionTaskDO> getTasksByActivePlanId(Long activePlanId);

    /**
     * 获取未设置排期策略的任务。
     */
    List<InspectionTaskDO> getTasksWithoutSchedulePolicy();

    // ==================== 树形结构查询 ====================

    /**
     * 获取子任务列表。
     */
    List<InspectionTaskDO> getSubTasks(Long parentId);

    /**
     * 获取根任务列表。
     */
    List<InspectionTaskDO> getRootTasks();

    // ==================== 继承配置查询 ====================

    /**
     * 获取需要继承排期的任务。
     */
    List<InspectionTaskDO> getTasksNeedInheritSchedule();

    /**
     * 获取需要继承资源策略的任务。
     */
    List<InspectionTaskDO> getTasksNeedInheritResourcePolicy();

    // ==================== 冲突检测 ====================

    /**
     * 获取潜在冲突的任务。
     */
    List<InspectionTaskDO> getPotentialConflictTasks(Long schedulePolicyId, Long categoryId);

    // ==================== 统计 ====================

    /**
     * 按状态统计任务数量。
     */
    long countByStatus(Integer status);

    /**
     * 按排期策略ID统计任务数量。
     */
    long countBySchedulePolicyId(Long schedulePolicyId);

    /**
     * 统计启用的任务数量。
     */
    long countEnabled();
}
