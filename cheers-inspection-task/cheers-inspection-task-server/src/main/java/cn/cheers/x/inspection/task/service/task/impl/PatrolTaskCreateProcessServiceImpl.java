package cn.cheers.x.inspection.task.service.task.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskCreateProgressRespVO;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.task.PatrolTaskCreateProcessService;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * 建任务向导进度写在总任务草稿里。
 *
 * <p>用户在创建页点「下一步」时，由这里决定能不能往前走：已经做过的可以回头看和改，
 * 还没做到的不能跳过。改了对象或检查项时，把后面步骤收回，已保存路线作废。</p>
 * <p>顺序：选对象 → 路线 → 排期与资源 → 编排确认。
 * 智能编排在排期与资源步触发；编排确认步只展示占窗结果并确认，不负责开跑。
 * 以后会交给 Flowable 同一条建任务流程；现在先写在草稿里让页面能用。</p>
 * <p>禁止：按「已经有路线」反推已放到哪一步；一次跳过多步。</p>
 */
@Service
@RequiredArgsConstructor
public class PatrolTaskCreateProcessServiceImpl implements PatrolTaskCreateProcessService {

    public static final int STEP_OBJECTS = 0;
    public static final int STEP_ROUTE = 1;
    public static final int STEP_SCHEDULE_RESOURCE = 2;
    public static final int STEP_ORCHESTRATION_CONFIRM = 3;
    public static final int LAST_STEP = STEP_ORCHESTRATION_CONFIRM;
    public static final String MODE_FIXED_CAMERA = "FIXED_CAMERA";

    private final PatrolTaskEntityStore patrolTaskEntityStore;

    @Override
    public InspectionTaskCreateProgressRespVO getProgress(Long taskId) {
        return toProgress(patrolTaskEntityStore.require(taskId));
    }

    @Override
    public InspectionTaskCreateProgressRespVO advance(Long taskId, Integer toStep) {
        PatrolTaskDraft draft = patrolTaskEntityStore.require(taskId);
        int target = requireStep(toStep, "要放到哪一步不能为空");
        int unlocked = unlockedOf(draft);
        if (target <= unlocked) {
            return toProgress(draft);
        }
        if (target > unlocked + 1) {
            throw ServiceExceptionUtil.invalidParamException("还没做到这一步，不能跳过");
        }
        assertLeavingStepReady(draft, unlocked);
        patrolTaskEntityStore.mergeDraftFields(
                taskId, Map.of(PatrolTaskEntityStore.DRAFT_KEY_UNLOCKED, target));
        return progressOf(target);
    }

    @Override
    public InspectionTaskCreateProgressRespVO invalidate(Long taskId, Integer keepThroughStep) {
        PatrolTaskDraft draft = patrolTaskEntityStore.require(taskId);
        int keepThrough = requireStep(keepThroughStep, "保留到哪一步不能为空");
        int unlocked = unlockedOf(draft);
        int next = Math.min(unlocked, keepThrough);
        if (keepThrough <= STEP_ROUTE) {
            patrolTaskEntityStore.clearPlannedRoute(taskId);
        }
        if (next < unlocked) {
            patrolTaskEntityStore.mergeDraftFields(
                    taskId, Map.of(PatrolTaskEntityStore.DRAFT_KEY_UNLOCKED, next));
        }
        return progressOf(next);
    }

    /**
     * 离开当前已放行步骤、进入下一格前：这一步该齐的数据必须已经写在总任务上。
     */
    private static void assertLeavingStepReady(PatrolTaskDraft draft, int unlocked) {
        if (unlocked == STEP_OBJECTS) {
            if (!hasObjectsAndItems(draft.inspectionContent())) {
                throw ServiceExceptionUtil.invalidParamException("请先选择巡检对象并勾选检查项目");
            }
            if (!StringUtils.hasText(draft.patrolExecutionMode())) {
                throw ServiceExceptionUtil.invalidParamException("请先选择巡检方式");
            }
            return;
        }
        if (unlocked == STEP_ROUTE && !skipsRoute(draft) && !hasSavedRoute(draft.plannedRoute())) {
            throw ServiceExceptionUtil.invalidParamException("请先保存路线");
        }
    }

    private static boolean hasObjectsAndItems(InspectionContent content) {
        if (content == null) {
            return false;
        }
        boolean hasObject = false;
        boolean hasItem = false;
        if (content.getCustomObjects() != null) {
            for (InspectionContent.ObjectContent object : content.getCustomObjects()) {
                if (object.getObjectId() != null) {
                    hasObject = true;
                }
                if (hasAnyItem(object)) {
                    hasItem = true;
                }
            }
        }
        if (content.getGroups() != null) {
            for (InspectionContent.ObjectGroup group : content.getGroups()) {
                if (group.getObjects() == null) {
                    continue;
                }
                for (InspectionContent.ObjectContent object : group.getObjects()) {
                    if (object.getObjectId() != null) {
                        hasObject = true;
                    }
                    if (hasAnyItem(object)) {
                        hasItem = true;
                    }
                }
            }
        }
        return hasObject && hasItem;
    }

    private static boolean hasAnyItem(InspectionContent.ObjectContent object) {
        if (object.getItems() == null) {
            return false;
        }
        for (InspectionContent.ItemContent item : object.getItems()) {
            if (item.getItemId() != null) {
                return true;
            }
        }
        return false;
    }

    private static boolean skipsRoute(PatrolTaskDraft draft) {
        return MODE_FIXED_CAMERA.equals(draft.patrolExecutionMode());
    }

    private static boolean hasSavedRoute(Object planned) {
        if (!(planned instanceof Map<?, ?> map)) {
            return false;
        }
        Object stops = map.get("stopIds");
        return stops instanceof Collection<?> collection && !collection.isEmpty();
    }

    private static int requireStep(Integer step, String emptyMessage) {
        if (step == null) {
            throw ServiceExceptionUtil.invalidParamException(emptyMessage);
        }
        if (step < STEP_OBJECTS || step > LAST_STEP) {
            throw ServiceExceptionUtil.invalidParamException("建任务步骤不在 0 到 3 之间");
        }
        return step;
    }

    private static int unlockedOf(PatrolTaskDraft draft) {
        Integer unlocked = draft.createUnlockedStep();
        if (unlocked == null) {
            return STEP_OBJECTS;
        }
        // 旧草稿步号大于当前最后一步时，收到最后一步，避免 Tab 越界。
        return Math.min(unlocked, LAST_STEP);
    }

    private static InspectionTaskCreateProgressRespVO toProgress(PatrolTaskDraft draft) {
        return progressOf(unlockedOf(draft));
    }

    private static InspectionTaskCreateProgressRespVO progressOf(int unlocked) {
        InspectionTaskCreateProgressRespVO vo = new InspectionTaskCreateProgressRespVO();
        vo.setUnlockedStep(unlocked);
        vo.setLastStep(LAST_STEP);
        return vo;
    }
}
