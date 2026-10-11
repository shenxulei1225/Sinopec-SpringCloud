package cn.cheers.x.inspection.task.service.task.impl;

import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskDurationRefreshReqVO;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskDurationRefreshRespVO;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.task.PatrolItemActionDurationCalculator;
import cn.cheers.x.inspection.task.service.task.PatrolItemActionDurationSupport;
import cn.cheers.x.inspection.task.service.task.PatrolPlannedRouteSupport;
import cn.cheers.x.inspection.task.service.task.PatrolTaskCreateProcessService;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDurationSupport;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 打开巡检任务时按当前动作定义计算时长，只把结果带回本次页面。
 * <p>权威：已保存修改稿和已发布内容仍由总任务持有。本服务不保存页面重算结果，也不管理向导步骤。</p>
 */
@Service
@RequiredArgsConstructor
public class PatrolTaskCreateProcessServiceImpl implements PatrolTaskCreateProcessService {

    private static final int STEP_OBJECTS = 0;
    private static final int LAST_STEP = 3;

    private final PatrolTaskEntityStore patrolTaskEntityStore;
    private final PatrolItemActionDurationCalculator itemActionDurationCalculator;

    @Override
    public InspectionTaskDurationRefreshRespVO refreshDurations(Long taskId, InspectionTaskDurationRefreshReqVO reqVO) {
        PatrolTaskDraft draft = patrolTaskEntityStore.require(taskId);
        InspectionContent content = draft.inspectionContent();
        if (content == null) {
            content = new InspectionContent();
        }
        BigDecimal storedAction = PatrolItemActionDurationSupport.minutesOf(content);
        Long executionEquipmentId = draft.executionDeviceBinding() == null
                ? null
                : draft.executionDeviceBinding().getEquipmentId();
        BigDecimal liveAction = itemActionDurationCalculator
                .computeLiveSecondsForEquipment(content, draft.patrolExecutionMode(), executionEquipmentId)
                .orElse(null);
        // 打开任务只把分钟数带回这一次响应。不把现算结果写进已发布字段或草稿。
        if (liveAction != null) {
            content.setItemActionDurationMinutes(liveAction);
            storedAction = liveAction;
        }
        int unlocked = unlockedOf(draft);
        InspectionTaskDurationRefreshRespVO resp = new InspectionTaskDurationRefreshRespVO();
        resp.setItemActionDurationMinutes(storedAction);
        resp.setTravelDurationMinutes(
                PatrolPlannedRouteSupport.resolveTravelMinutesOnly(draft.plannedRoute(), draft.patrolExecutionMode()));
        resp.setTotalDurationMinutes(
                PatrolTaskDurationSupport.totalMinutes(content, draft.plannedRoute(), draft.patrolExecutionMode()));
        resp.setItemActionChanged(Boolean.FALSE);
        resp.setPathChanged(Boolean.FALSE);
        resp.setUnlockedStep(unlocked);
        resp.setAlreadyGenerated(Boolean.TRUE.equals(draft.orchestrationCommitted()));
        return resp;
    }

    private static int unlockedOf(PatrolTaskDraft draft) {
        Integer unlocked = draft.createUnlockedStep();
        if (unlocked == null) {
            return STEP_OBJECTS;
        }
        // 旧草稿步号大于当前最后一步时，收到最后一步，避免 Tab 越界。
        return Math.min(unlocked, LAST_STEP);
    }

}
