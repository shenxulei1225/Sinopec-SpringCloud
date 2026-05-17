package cn.iocoder.yudao.module.inspection.task.service.schedule;

import cn.iocoder.yudao.module.inspection.task.dal.dataobject.schedule.InspectionTaskScheduleDO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 巡检任务执行计划点 Service 接口。
 *
 * <p>编排引擎核心服务，负责计划点的增删改查及冲突检测。</p>
 */
public interface InspectionTaskScheduleService {

    // ==================== 基础操作 ====================

    /**
     * 创建计划点。
     */
    Long createSchedule(InspectionTaskScheduleDO schedule);

    /**
     * 批量创建计划点。
     */
    List<Long> createSchedules(List<InspectionTaskScheduleDO> schedules);

    /**
     * 更新计划点。
     */
    void updateSchedule(InspectionTaskScheduleDO schedule);

    /**
     * 删除计划点。
     */
    void deleteSchedule(Long id);

    /**
     * 根据ID查询计划点。
     */
    InspectionTaskScheduleDO getSchedule(Long id);

    /**
     * 批量根据ID查询计划点。
     */
    List<InspectionTaskScheduleDO> getSchedulesByIds(List<Long> ids);

    // ==================== 任务维度操作 ====================

    /**
     * 根据任务ID查询所有计划点（按时间升序）。
     */
    List<InspectionTaskScheduleDO> getSchedulesByTaskId(Long taskId);

    /**
     * 根据任务ID查询待执行的计划点。
     */
    List<InspectionTaskScheduleDO> getPendingSchedulesByTaskId(Long taskId);

    /**
     * 批量根据任务ID查询计划点。
     */
    List<InspectionTaskScheduleDO> getSchedulesByTaskIds(List<Long> taskIds);

    // ==================== 批次维度操作 ====================

    /**
     * 根据批次ID查询所有计划点。
     */
    List<InspectionTaskScheduleDO> getSchedulesByPlanId(Long planId);

    /**
     * 批量根据批次ID查询计划点。
     */
    List<InspectionTaskScheduleDO> getSchedulesByPlanIds(List<Long> planIds);

    // ==================== 时间范围查询 ====================

    /**
     * 查询指定日期范围内的所有计划点。
     */
    List<InspectionTaskScheduleDO> getSchedulesByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 查询指定时间范围内的计划点。
     */
    List<InspectionTaskScheduleDO> getSchedulesByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查询过期的计划点。
     */
    List<InspectionTaskScheduleDO> getExpiredSchedules(LocalDateTime now);

    // ==================== 冲突检测 ====================

    /**
     * 检测与现有计划点的冲突。
     *
     * @param taskId    任务ID（排除自身）
     * @param policyId  排期策略ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 冲突的计划点列表
     */
    List<InspectionTaskScheduleDO> detectConflicts(Long taskId, Long policyId, LocalDateTime startTime, LocalDateTime endTime);

    // ==================== 状态变更 ====================

    /**
     * 取消计划点。
     */
    void cancelSchedule(Long id, Long operatorId, String reason);

    /**
     * 批量取消计划点。
     */
    void cancelSchedules(List<Long> ids, Long operatorId, String reason);

    /**
     * 标记计划点为已完成。
     */
    void completeSchedule(Long id);

    // ==================== 替代关系 ====================

    /**
     * 替代计划点（旧计划点标记为 superseded）。
     *
     * @param oldScheduleId 被替代的计划点ID
     * @param newSchedule   新的计划点
     * @return 新计划点ID
     */
    Long supersedeSchedule(Long oldScheduleId, InspectionTaskScheduleDO newSchedule);

    // ==================== 统计 ====================

    /**
     * 统计某任务在指定日期范围内的计划点数量。
     */
    long countSchedulesByTaskIdAndDateRange(Long taskId, LocalDate startDate, LocalDate endDate);

    /**
     * 统计某批次的计划点数量。
     */
    long countSchedulesByPlanId(Long planId);
}
