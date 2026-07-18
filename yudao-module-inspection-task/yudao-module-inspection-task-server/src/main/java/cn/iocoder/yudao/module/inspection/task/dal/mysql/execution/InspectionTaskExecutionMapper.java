package cn.iocoder.yudao.module.inspection.task.dal.mysql.execution;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.inspection.task.dal.dataobject.execution.InspectionTaskExecutionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 巡检任务执行记录 Mapper。
 *
 * <p>执行记录的管理，支持按任务、计划点、资源等多维度查询。</p>
 */
@Mapper
public interface InspectionTaskExecutionMapper extends BaseMapperX<InspectionTaskExecutionDO> {

    // ==================== 基础查询 ====================

    /**
     * 根据执行记录编码查询。
     */
    default InspectionTaskExecutionDO selectByExecutionCode(@Param("executionCode") String executionCode) {
        return selectOne(new LambdaQueryWrapperX<InspectionTaskExecutionDO>()
                .eq(InspectionTaskExecutionDO::getExecutionCode, executionCode));
    }

    // ==================== 任务维度查询 ====================

    /**
     * 根据任务ID查询执行记录（按执行时间降序）。
     */
    default List<InspectionTaskExecutionDO> selectListByTaskId(@Param("taskId") Long taskId) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskExecutionDO>()
                .eq(InspectionTaskExecutionDO::getTaskId, taskId)
                .orderByDesc(InspectionTaskExecutionDO::getExecutionStartTime)
                .orderByDesc(InspectionTaskExecutionDO::getId));
    }

    /**
     * 根据任务ID查询最新的执行记录。
     */
    default InspectionTaskExecutionDO selectLatestByTaskId(@Param("taskId") Long taskId) {
        return selectOne(new LambdaQueryWrapperX<InspectionTaskExecutionDO>()
                .eq(InspectionTaskExecutionDO::getTaskId, taskId)
                .orderByDesc(InspectionTaskExecutionDO::getExecutionStartTime)
                .last("LIMIT 1"));
    }

    /**
     * 批量根据任务ID查询执行记录。
     */
    default List<InspectionTaskExecutionDO> selectListByTaskIds(@Param("taskIds") List<Long> taskIds) {
        if (taskIds == null || taskIds.isEmpty()) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapperX<InspectionTaskExecutionDO>()
                .in(InspectionTaskExecutionDO::getTaskId, taskIds)
                .orderByDesc(InspectionTaskExecutionDO::getExecutionStartTime));
    }

    // ==================== 计划点维度查询 ====================

    /**
     * 根据计划点ID查询执行记录。
     */
    default InspectionTaskExecutionDO selectByScheduleId(@Param("scheduleId") Long scheduleId) {
        return selectOne(new LambdaQueryWrapperX<InspectionTaskExecutionDO>()
                .eq(InspectionTaskExecutionDO::getScheduleId, scheduleId));
    }

    /**
     * 批量根据计划点ID查询执行记录。
     */
    default List<InspectionTaskExecutionDO> selectListByScheduleIds(@Param("scheduleIds") List<Long> scheduleIds) {
        if (scheduleIds == null || scheduleIds.isEmpty()) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapperX<InspectionTaskExecutionDO>()
                .in(InspectionTaskExecutionDO::getScheduleId, scheduleIds)
                .orderByDesc(InspectionTaskExecutionDO::getExecutionStartTime));
    }

    // ==================== 资源维度查询 ====================

    /**
     * 根据资源ID查询执行记录（按执行时间降序）。
     */
    default List<InspectionTaskExecutionDO> selectListByResourceId(@Param("resourceId") Long resourceId) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskExecutionDO>()
                .eq(InspectionTaskExecutionDO::getResourceId, resourceId)
                .orderByDesc(InspectionTaskExecutionDO::getExecutionStartTime)
                .orderByDesc(InspectionTaskExecutionDO::getId));
    }

    /**
     * 批量根据资源ID查询执行记录。
     */
    default List<InspectionTaskExecutionDO> selectListByResourceIds(@Param("resourceIds") List<Long> resourceIds) {
        if (resourceIds == null || resourceIds.isEmpty()) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapperX<InspectionTaskExecutionDO>()
                .in(InspectionTaskExecutionDO::getResourceId, resourceIds)
                .orderByDesc(InspectionTaskExecutionDO::getExecutionStartTime));
    }

    // ==================== 批次维度查询 ====================

    /**
     * 根据批次ID查询执行记录。
     */
    default List<InspectionTaskExecutionDO> selectListByPlanId(@Param("planId") Long planId) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskExecutionDO>()
                .eq(InspectionTaskExecutionDO::getPlanId, planId)
                .orderByDesc(InspectionTaskExecutionDO::getExecutionStartTime));
    }

    // ==================== 状态查询 ====================

    /**
     * 根据执行状态查询执行记录。
     *
     * @param executionStatus 执行状态：1-待执行 2-执行中 3-已完成 4-已取消
     */
    default List<InspectionTaskExecutionDO> selectListByExecutionStatus(@Param("executionStatus") Integer executionStatus) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskExecutionDO>()
                .eq(InspectionTaskExecutionDO::getExecutionStatus, executionStatus)
                .orderByDesc(InspectionTaskExecutionDO::getExecutionStartTime));
    }

    /**
     * 根据结果状态查询执行记录。
     *
     * @param resultStatus 结果状态：1-正常 2-异常
     */
    default List<InspectionTaskExecutionDO> selectListByResultStatus(@Param("resultStatus") Integer resultStatus) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskExecutionDO>()
                .eq(InspectionTaskExecutionDO::getResultStatus, resultStatus)
                .orderByDesc(InspectionTaskExecutionDO::getExecutionStartTime));
    }

    // ==================== 批量操作 ====================

    /**
     * 批量根据ID查询。
     */
    default List<InspectionTaskExecutionDO> selectListByIds(@Param("ids") List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapperX<InspectionTaskExecutionDO>()
                .in(InspectionTaskExecutionDO::getId, ids));
    }

    // ==================== 统计查询 ====================

    /**
     * 统计某任务的执行记录数量。
     */
    default long countByTaskId(@Param("taskId") Long taskId) {
        return selectCount(new LambdaQueryWrapperX<InspectionTaskExecutionDO>()
                .eq(InspectionTaskExecutionDO::getTaskId, taskId));
    }

    /**
     * 统计某资源的所有执行记录数量。
     */
    default long countByResourceId(@Param("resourceId") Long resourceId) {
        return selectCount(new LambdaQueryWrapperX<InspectionTaskExecutionDO>()
                .eq(InspectionTaskExecutionDO::getResourceId, resourceId));
    }

    /**
     * 统计指定执行状态的执行记录数量。
     */
    default long countByExecutionStatus(@Param("executionStatus") Integer executionStatus) {
        return selectCount(new LambdaQueryWrapperX<InspectionTaskExecutionDO>()
                .eq(InspectionTaskExecutionDO::getExecutionStatus, executionStatus));
    }
}
