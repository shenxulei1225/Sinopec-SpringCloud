package cn.cheers.x.inspection.task.service.query;

import cn.cheers.x.inspection.task.service.query.model.InspectionTaskSchedulePlanView;
import cn.cheers.x.inspection.task.service.query.model.InspectionTaskScheduleView;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 巡检任务排期查询服务。
 */
public interface InspectionTaskScheduleQueryService {

    /**
     * 获取编排批次详情。
     */
    InspectionTaskSchedulePlanView getSchedulePlanView(Long planId);

    /**
     * 根据批次编码获取编排批次详情。
     */
    InspectionTaskSchedulePlanView getSchedulePlanViewByCode(String planCode);

    /**
     * 根据时间范围查询编排批次。
     */
    List<InspectionTaskSchedulePlanView> getSchedulePlanViewListByHorizon(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据批次ID获取排期列表。
     */
    List<InspectionTaskScheduleView> getScheduleViewListByPlanId(Long planId);

    /**
     * 获取任务的排期列表。
     */
    List<InspectionTaskScheduleView> getScheduleViewListByTaskId(Long taskId);

    /**
     * 获取待执行的排期列表。
     */
    List<InspectionTaskScheduleView> getPendingSchedules(LocalDateTime beforeTime);

    /**
     * 根据计划时间范围查询排期列表。
     */
    List<InspectionTaskScheduleView> getScheduleViewListByPlannedTimeRange(LocalDateTime startTime, LocalDateTime endTime);
}
