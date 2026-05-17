package cn.iocoder.yudao.module.inspection.task.dal.mysql.schedule;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.inspection.task.dal.dataobject.schedule.InspectionTaskSchedulePlanDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 巡检任务编排批次 Mapper。
 */
@Mapper
public interface InspectionTaskSchedulePlanMapper extends BaseMapperX<InspectionTaskSchedulePlanDO> {

    default InspectionTaskSchedulePlanDO selectByPlanCode(@Param("planCode") String planCode) {
        return selectOne(new LambdaQueryWrapperX<InspectionTaskSchedulePlanDO>()
                .eq(InspectionTaskSchedulePlanDO::getPlanCode, planCode));
    }

    default List<InspectionTaskSchedulePlanDO> selectByTaskId(@Param("taskId") Long taskId) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskSchedulePlanDO>()
                .eq(InspectionTaskSchedulePlanDO::getTaskId, taskId)
                .orderByDesc(InspectionTaskSchedulePlanDO::getCreateTime));
    }

    default long countByTaskId(@Param("taskId") Long taskId) {
        return selectCount(new LambdaQueryWrapperX<InspectionTaskSchedulePlanDO>()
                .eq(InspectionTaskSchedulePlanDO::getTaskId, taskId));
    }

    default long countByStatus(@Param("planStatus") Integer planStatus) {
        return selectCount(new LambdaQueryWrapperX<InspectionTaskSchedulePlanDO>()
                .eq(InspectionTaskSchedulePlanDO::getPlanStatus, planStatus));
    }

    default InspectionTaskSchedulePlanDO selectLatestByTaskId(@Param("taskId") Long taskId) {
        return selectOne(new LambdaQueryWrapperX<InspectionTaskSchedulePlanDO>()
                .eq(InspectionTaskSchedulePlanDO::getTaskId, taskId)
                .orderByDesc(InspectionTaskSchedulePlanDO::getCreateTime)
                .last("LIMIT 1"));
    }

    default InspectionTaskSchedulePlanDO selectActivatedByTaskId(@Param("taskId") Long taskId) {
        return selectOne(new LambdaQueryWrapperX<InspectionTaskSchedulePlanDO>()
                .eq(InspectionTaskSchedulePlanDO::getTaskId, taskId)
                .eq(InspectionTaskSchedulePlanDO::getPlanStatus, 2) // 2 = 已激活
                .last("LIMIT 1"));
    }

    default List<InspectionTaskSchedulePlanDO> selectByStatus(@Param("planStatus") Integer planStatus) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskSchedulePlanDO>()
                .eq(InspectionTaskSchedulePlanDO::getPlanStatus, planStatus)
                .orderByDesc(InspectionTaskSchedulePlanDO::getCreateTime));
    }

    default List<InspectionTaskSchedulePlanDO> selectListByHorizon(LocalDate startDate, LocalDate endDate) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskSchedulePlanDO>()
                .le(InspectionTaskSchedulePlanDO::getHorizonStartDate, endDate)
                .ge(InspectionTaskSchedulePlanDO::getHorizonEndDate, startDate)
                .orderByDesc(InspectionTaskSchedulePlanDO::getCreateTime));
    }

    default List<InspectionTaskSchedulePlanDO> selectPendingByTaskId(@Param("taskId") Long taskId) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskSchedulePlanDO>()
                .eq(InspectionTaskSchedulePlanDO::getTaskId, taskId)
                .eq(InspectionTaskSchedulePlanDO::getPlanStatus, 1) // 1 = 待生效
                .orderByDesc(InspectionTaskSchedulePlanDO::getCreateTime));
    }

    /**
     * 根据任务ID列表批量查询激活状态的排期计划。
     * 用于列表页面填充排期结果信息。
     */
    default List<InspectionTaskSchedulePlanDO> selectActivatedPlansByTaskIds(@Param("taskIds") List<Long> taskIds) {
        if (taskIds == null || taskIds.isEmpty()) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapperX<InspectionTaskSchedulePlanDO>()
                .in(InspectionTaskSchedulePlanDO::getTaskId, taskIds)
                .eq(InspectionTaskSchedulePlanDO::getPlanStatus, 2) // 2 = 已激活
                .orderByDesc(InspectionTaskSchedulePlanDO::getCreateTime));
    }
}
