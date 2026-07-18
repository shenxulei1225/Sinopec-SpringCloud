package cn.iocoder.yudao.module.inspection.task.service.schedule.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.module.inspection.task.dal.dataobject.schedule.InspectionTaskSchedulePlanDO;
import cn.iocoder.yudao.module.inspection.task.dal.mysql.schedule.InspectionTaskSchedulePlanMapper;
import cn.iocoder.yudao.module.inspection.task.service.schedule.InspectionTaskSchedulePlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;

/**
 * 巡检任务编排批次 Service 实现类。
 *
 * <p>编排批次的生命周期管理，包括创建、激活、停用等操作。</p>
 */
@Service
@RequiredArgsConstructor
public class InspectionTaskSchedulePlanServiceImpl implements InspectionTaskSchedulePlanService {

    private final InspectionTaskSchedulePlanMapper planMapper;

    // ==================== 基础操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPlan(InspectionTaskSchedulePlanDO plan) {
        planMapper.insert(plan);
        return plan.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePlan(InspectionTaskSchedulePlanDO plan) {
        validatePlanExists(plan.getId());
        planMapper.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePlan(Long id) {
        validatePlanExists(id);
        planMapper.deleteById(id);
    }

    @Override
    public InspectionTaskSchedulePlanDO getPlan(Long id) {
        return planMapper.selectById(id);
    }

    @Override
    public InspectionTaskSchedulePlanDO getPlanByCode(String planCode) {
        return planMapper.selectByPlanCode(planCode);
    }

    // ==================== 任务维度操作 ====================

    @Override
    public List<InspectionTaskSchedulePlanDO> getPlansByTaskId(Long taskId) {
        return planMapper.selectByTaskId(taskId);
    }

    @Override
    public InspectionTaskSchedulePlanDO getLatestPlanByTaskId(Long taskId) {
        return planMapper.selectLatestByTaskId(taskId);
    }

    @Override
    public InspectionTaskSchedulePlanDO getActivatedPlanByTaskId(Long taskId) {
        return planMapper.selectActivatedByTaskId(taskId);
    }

    @Override
    public List<InspectionTaskSchedulePlanDO> getPendingPlansByTaskId(Long taskId) {
        return planMapper.selectPendingByTaskId(taskId);
    }

    // ==================== 激活/停用 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activatePlan(Long id, Long operatorId) {
        InspectionTaskSchedulePlanDO plan = validatePlanExists(id);

        // 将同一任务的其他批次设为已停用
        List<InspectionTaskSchedulePlanDO> existingActivated = planMapper.selectByTaskId(plan.getTaskId());
        existingActivated.stream()
                .filter(p -> p.getId().equals(id))
                .forEach(p -> {
                    p.setPlanStatus(3); // 已停用
                    planMapper.updateById(p);
                });

        // 将当前批次设为已激活
        plan.setPlanStatus(2); // 已激活
        plan.setActivatedBy(operatorId);
        plan.setActivatedAt(LocalDateTime.now());
        planMapper.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deactivatePlan(Long id, Long operatorId) {
        InspectionTaskSchedulePlanDO plan = validatePlanExists(id);
        plan.setPlanStatus(3); // 已停用
        planMapper.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deactivateAllPlansByTaskId(Long taskId, Long operatorId) {
        List<InspectionTaskSchedulePlanDO> plans = planMapper.selectByTaskId(taskId);
        plans.forEach(plan -> {
            plan.setPlanStatus(3); // 已停用
            planMapper.updateById(plan);
        });
    }

    // ==================== 时间范围查询 ====================

    @Override
    public List<InspectionTaskSchedulePlanDO> getPlansByHorizon(LocalDate startDate, LocalDate endDate) {
        return planMapper.selectListByHorizon(startDate, endDate);
    }

    // ==================== 状态查询 ====================

    @Override
    public List<InspectionTaskSchedulePlanDO> getPlansByStatus(Integer planStatus) {
        return planMapper.selectByStatus(planStatus);
    }

    // ==================== 统计 ====================

    @Override
    public long countPlansByTaskId(Long taskId) {
        return planMapper.countByTaskId(taskId);
    }

    @Override
    public long countPlansByStatus(Integer planStatus) {
        return planMapper.countByStatus(planStatus);
    }

    // ==================== 私有方法 ====================

    private InspectionTaskSchedulePlanDO validatePlanExists(Long id) {
        InspectionTaskSchedulePlanDO plan = planMapper.selectById(id);
        if (plan == null) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "编排批次不存在");
        }
        return plan;
    }
}
