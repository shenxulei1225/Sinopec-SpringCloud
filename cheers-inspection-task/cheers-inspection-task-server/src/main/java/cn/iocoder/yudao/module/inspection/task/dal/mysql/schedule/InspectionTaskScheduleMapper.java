package cn.iocoder.yudao.module.inspection.task.dal.mysql.schedule;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.inspection.task.dal.dataobject.schedule.InspectionTaskScheduleDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 巡检任务排期 Mapper。
 */
@Mapper
public interface InspectionTaskScheduleMapper extends BaseMapperX<InspectionTaskScheduleDO> {

    default List<InspectionTaskScheduleDO> selectByTaskId(@Param("taskId") Long taskId) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskScheduleDO>()
                .eq(InspectionTaskScheduleDO::getTaskId, taskId)
                .orderByDesc(InspectionTaskScheduleDO::getScheduledTime));
    }

    default List<InspectionTaskScheduleDO> selectByPlanId(@Param("planId") Long planId) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskScheduleDO>()
                .eq(InspectionTaskScheduleDO::getPlanId, planId)
                .orderByDesc(InspectionTaskScheduleDO::getScheduledTime));
    }

    default List<InspectionTaskScheduleDO> selectByStatus(Integer status) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskScheduleDO>()
                .eq(InspectionTaskScheduleDO::getStatus, status)
                .orderByDesc(InspectionTaskScheduleDO::getScheduledTime));
    }

    default List<InspectionTaskScheduleDO> selectPendingSchedules(LocalDateTime beforeTime) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskScheduleDO>()
                .eq(InspectionTaskScheduleDO::getStatus, 1) // 待执行
                .le(InspectionTaskScheduleDO::getScheduledTime, beforeTime)
                .orderByAsc(InspectionTaskScheduleDO::getScheduledTime));
    }

    default List<InspectionTaskScheduleDO> selectByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskScheduleDO>()
                .between(InspectionTaskScheduleDO::getScheduledTime, startTime, endTime)
                .orderByAsc(InspectionTaskScheduleDO::getScheduledTime));
    }

    /**
     * 根据排期需求ID查询排期记录。
     * 通过关联编排批次，间接查询属于该需求的所有排期。
     *
     * @param requirementId 排期需求ID
     * @return 排期记录列表
     */
    default List<InspectionTaskScheduleDO> selectListByRequirementId(@Param("requirementId") Long requirementId) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskScheduleDO>()
                .exists("SELECT 1 FROM inspection_task_schedule_plan plan WHERE plan.id = inspection_task_schedule.plan_id AND plan.schedule_requirement_id = #{requirementId}")
                .orderByDesc(InspectionTaskScheduleDO::getScheduledTime));
    }
}
