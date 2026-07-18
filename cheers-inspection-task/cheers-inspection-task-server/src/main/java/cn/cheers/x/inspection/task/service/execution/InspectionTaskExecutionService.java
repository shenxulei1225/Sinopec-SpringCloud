package cn.cheers.x.inspection.task.service.execution;

import cn.cheers.x.inspection.task.dal.dataobject.execution.InspectionTaskExecutionDO;

import java.util.List;

/**
 * 巡检任务执行记录 Service 接口。
 *
 * <p>执行记录的管理，包括创建、更新、查询等操作。</p>
 */
public interface InspectionTaskExecutionService {

    // ==================== 基础操作 ====================

    /**
     * 创建执行记录。
     */
    Long createExecution(InspectionTaskExecutionDO execution);

    /**
     * 更新执行记录。
     */
    void updateExecution(InspectionTaskExecutionDO execution);

    /**
     * 删除执行记录。
     */
    void deleteExecution(Long id);

    /**
     * 根据ID查询执行记录。
     */
    InspectionTaskExecutionDO getExecution(Long id);

    /**
     * 批量根据ID查询执行记录。
     */
    List<InspectionTaskExecutionDO> getExecutionsByIds(List<Long> ids);

    // ==================== 任务维度操作 ====================

    /**
     * 根据任务ID查询执行记录（按执行时间降序）。
     */
    List<InspectionTaskExecutionDO> getExecutionsByTaskId(Long taskId);

    // ==================== 计划点维度操作 ====================

    /**
     * 根据计划点ID查询执行记录。
     */
    InspectionTaskExecutionDO getExecutionByScheduleId(Long scheduleId);

    // ==================== 资源维度操作 ====================

    /**
     * 根据资源ID查询执行记录（按执行时间降序）。
     */
    List<InspectionTaskExecutionDO> getExecutionsByResourceId(Long resourceId);
}
