package cn.iocoder.yudao.module.emergency.service.plan;

import cn.cheers.x.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanStepDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.plan.EmergencyPlanMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.plan.EmergencyPlanStepMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants.PLAN_NOT_EXISTS;

/**
 * 预案版本管理 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class PlanVersionServiceImpl implements PlanVersionService {

    @Resource
    private EmergencyPlanMapper emergencyPlanMapper;

    @Resource
    private EmergencyPlanStepMapper emergencyPlanStepMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishVersion(Long planId, String versionNumber) {
        // 1. 查询预案
        EmergencyPlanDO plan = emergencyPlanMapper.selectById(planId);
        if (plan == null) {
            throw exception(PLAN_NOT_EXISTS);
        }

        // 2. 设置版本号和锁定状态
        plan.setVersionNumber(versionNumber);
        plan.setIsVersionLocked(true);
        emergencyPlanMapper.updateById(plan);

        log.info("[publishVersion][发布版本并锁定成功] planId={}, versionNumber={}", planId, versionNumber);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlockVersion(Long planId) {
        // 1. 查询预案
        EmergencyPlanDO plan = emergencyPlanMapper.selectById(planId);
        if (plan == null) {
            throw exception(PLAN_NOT_EXISTS);
        }

        // 2. 解锁版本（恢复实时引用）
        plan.setIsVersionLocked(false);
        emergencyPlanMapper.updateById(plan);

        log.info("[unlockVersion][解锁版本成功] planId={}", planId);
    }

    @Override
    public String getPlanSnapshot(Long planId) {
        // 1. 查询预案
        EmergencyPlanDO plan = emergencyPlanMapper.selectById(planId);
        if (plan == null) {
            throw exception(PLAN_NOT_EXISTS);
        }

        // 2. 如果版本未锁定，返回null（使用实时引用）
        if (!Boolean.TRUE.equals(plan.getIsVersionLocked())) {
            return null;
        }

        // 3. 构建预案快照（包含预案基本信息和步骤信息）
        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put("planId", plan.getId());
        snapshot.put("planNo", plan.getPlanNo());
        snapshot.put("planName", plan.getPlanName());
        snapshot.put("versionNumber", plan.getVersionNumber());
        snapshot.put("planType", plan.getPlanType());
        snapshot.put("planLevels", plan.getPlanLevels());
        snapshot.put("planGroupId", plan.getPlanGroupId());
        snapshot.put("customConfig", plan.getCustomConfig());
        snapshot.put("reviewInfo", plan.getReviewInfo());
        snapshot.put("status", plan.getStatus());

        // 4. 获取预案步骤信息
        List<EmergencyPlanStepDO> steps = emergencyPlanStepMapper.selectList(
            new LambdaQueryWrapper<EmergencyPlanStepDO>()
                .eq(EmergencyPlanStepDO::getPlanId, planId)
                .eq(EmergencyPlanStepDO::getDeleted, false)
                .orderByAsc(EmergencyPlanStepDO::getStepOrder)
        );
        snapshot.put("steps", steps.stream()
            .map(step -> {
                Map<String, Object> stepMap = new HashMap<>();
                stepMap.put("id", step.getId());
                stepMap.put("stepTitle", step.getStepTitle());
                stepMap.put("stepDescription", step.getStepDescription());
                stepMap.put("stepOrder", step.getStepOrder());
                stepMap.put("parentId", step.getParentId());
                stepMap.put("planLevel", step.getPlanLevel());
                stepMap.put("stage", step.getStepStage());
                stepMap.put("commandIdList", step.getCommandIdList());
                stepMap.put("timeLimit", step.getTimeLimit());
                return stepMap;
            })
            .collect(Collectors.toList()));

        return JsonUtils.toJsonString(snapshot);
    }
}

