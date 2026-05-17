package cn.iocoder.yudao.module.inspection.task.service.schedule;

import cn.iocoder.yudao.module.inspection.task.dal.dataobject.schedule.InspectionTaskSchedulePlanDO;

import java.time.LocalDate;
import java.util.List;

/**
 * 巡检任务编排批次 Service 接口。
 *
 * <p>编排批次的生命周期管理，包括创建、激活、停用等操作。</p>
 */
public interface InspectionTaskSchedulePlanService {

    // ==================== 基础操作 ====================

    /**
     * 创建编排批次。
     */
    Long createPlan(InspectionTaskSchedulePlanDO plan);

    /**
     * 更新编排批次。
     */
    void updatePlan(InspectionTaskSchedulePlanDO plan);

    /**
     * 删除编排批次。
     */
    void deletePlan(Long id);

    /**
     * 根据ID查询编排批次。
     */
    InspectionTaskSchedulePlanDO getPlan(Long id);

    /**
     * 根据编码查询编排批次。
     */
    InspectionTaskSchedulePlanDO getPlanByCode(String planCode);

    // ==================== 任务维度操作 ====================

    /**
     * 根据任务ID查询所有编排批次（按创建时间降序）。
     */
    List<InspectionTaskSchedulePlanDO> getPlansByTaskId(Long taskId);

    /**
     * 根据任务ID查询最新编排批次。
     */
    InspectionTaskSchedulePlanDO getLatestPlanByTaskId(Long taskId);

    /**
     * 根据任务ID查询已激活的编排批次。
     */
    InspectionTaskSchedulePlanDO getActivatedPlanByTaskId(Long taskId);

    /**
     * 根据任务ID查询待生效的编排批次。
     */
    List<InspectionTaskSchedulePlanDO> getPendingPlansByTaskId(Long taskId);

    // ==================== 激活/停用 ====================

    /**
     * 激活编排批次。
     *
     * <p>将指定批次设为已激活状态，同时将同一任务的其他批次设为已停用状态。</p>
     *
     * @param id         批次ID
     * @param operatorId 操作人ID
     */
    void activatePlan(Long id, Long operatorId);

    /**
     * 停用编排批次。
     *
     * @param id         批次ID
     * @param operatorId 操作人ID
     */
    void deactivatePlan(Long id, Long operatorId);

    /**
     * 根据任务ID停用所有批次。
     *
     * @param taskId     任务ID
     * @param operatorId 操作人ID
     */
    void deactivateAllPlansByTaskId(Long taskId, Long operatorId);

    // ==================== 时间范围查询 ====================

    /**
     * 查询指定编排时间范围内的批次。
     */
    List<InspectionTaskSchedulePlanDO> getPlansByHorizon(LocalDate startDate, LocalDate endDate);

    // ==================== 状态查询 ====================

    /**
     * 根据状态查询批次。
     *
     * @param planStatus 状态：1-待生效 2-已激活 3-已停用
     */
    List<InspectionTaskSchedulePlanDO> getPlansByStatus(Integer planStatus);

    // ==================== 统计 ====================

    /**
     * 统计某任务的编排批次数量。
     */
    long countPlansByTaskId(Long taskId);

    /**
     * 统计指定状态的批次数量。
     */
    long countPlansByStatus(Integer planStatus);
}
