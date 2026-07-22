package cn.iocoder.yudao.module.emergency.service.plan;

import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.plan.EmergencyPlanMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants.*;

/**
 * 预案状态管理 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class PlanStatusServiceImpl implements PlanStatusService {

    @Resource
    private EmergencyPlanMapper emergencyPlanMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishPlan(Long planId) {
        // 1. 查询预案
        EmergencyPlanDO plan = emergencyPlanMapper.selectById(planId);
        if (plan == null) {
            throw exception(PLAN_NOT_EXISTS);
        }

        // 2. 验证状态转换规则：只有草稿状态的预案才能发布
        if (!"DRAFT".equals(plan.getStatus())) {
            throw exception(PLAN_STATUS_TRANSITION_INVALID, "只有草稿状态的预案才能发布");
        }

        // 3. 更新状态
        plan.setStatus("PUBLISHED");
        emergencyPlanMapper.updateById(plan);

        log.info("[publishPlan][发布预案成功] planId={}", planId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disablePlan(Long planId) {
        // 1. 查询预案
        EmergencyPlanDO plan = emergencyPlanMapper.selectById(planId);
        if (plan == null) {
            throw exception(PLAN_NOT_EXISTS);
        }

        // 2. 验证状态转换规则：只有已发布状态的预案才能停用
        if (!"PUBLISHED".equals(plan.getStatus())) {
            throw exception(PLAN_STATUS_TRANSITION_INVALID, "只有已发布状态的预案才能停用");
        }

        // 3. 更新状态
        plan.setStatus("DISABLED");
        emergencyPlanMapper.updateById(plan);

        log.info("[disablePlan][停用预案成功] planId={}", planId);
    }

    @Override
    public boolean isPlanAvailable(Long planId) {
        EmergencyPlanDO plan = emergencyPlanMapper.selectById(planId);
        if (plan == null) {
            return false;
        }
        // 只有已发布的预案才能被用于创建应急响应
        return "PUBLISHED".equals(plan.getStatus());
    }
}






