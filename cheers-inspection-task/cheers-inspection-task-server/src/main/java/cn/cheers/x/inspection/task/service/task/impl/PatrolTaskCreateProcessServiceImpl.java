package cn.cheers.x.inspection.task.service.task.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskCreateProgressRespVO;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskDurationRefreshReqVO;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskDurationRefreshRespVO;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.task.CreateWizardInvalidation;
import cn.cheers.x.inspection.task.service.task.PatrolItemActionDurationCalculator;
import cn.cheers.x.inspection.task.service.task.PatrolItemActionDurationSupport;
import cn.cheers.x.inspection.task.service.task.PatrolPlannedRouteSupport;
import cn.cheers.x.inspection.task.service.task.PatrolTaskCreateProcessService;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDurationSupport;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 建任务向导进度写在总任务草稿里。
 *
 * <p>用户在创建页点「下一步」时，由这里决定能不能往前走：已经做过的可以回头看和改，
 * 还没做到的不能跳过。前面事实变了时按 {@link CreateWizardInvalidation} 收回，
 * 禁止按具体巡检方式两两写死，也禁止用「本步没数据」报错再退一格冒充收回。</p>
 * <p>顺序：选对象 → 路线（saveRoute）→ 排期与资源 → 核对计划（生成任务）。
 * 离开选对象步只校验已选对象、检查项、巡检方式；规划路线只在 saveRoute 写入预览结果。
 * 第 3 步无冲突或智能编排会写试排快照；有快照才能放到核对计划。下一步只放行核查，不生成。
 * <p>禁止：按「已经有路线」反推已放到哪一步；一次跳过多步；把智能编排当成进入核对的唯一入口。</p>
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
    private final PatrolItemActionDurationCalculator itemActionDurationCalculator;

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
    public InspectionTaskCreateProgressRespVO invalidate(Long taskId, CreateWizardInvalidation reason) {
        if (reason == null) {
            throw ServiceExceptionUtil.invalidParamException("作废原因不能为空");
        }
        return invalidate(taskId, reason.keepThroughStep(), reason.clearPlannedRoute());
    }

    @Override
    public InspectionTaskCreateProgressRespVO invalidate(Long taskId, Integer keepThroughStep, Boolean clearPlannedRoute) {
        PatrolTaskDraft draft = patrolTaskEntityStore.require(taskId);
        int keepThrough = requireStep(keepThroughStep, "保留到哪一步不能为空");
        int unlocked = unlockedOf(draft);
        int next = Math.min(unlocked, keepThrough);
        boolean clearRoute = clearPlannedRoute == null
                ? keepThrough <= STEP_ROUTE
                : Boolean.TRUE.equals(clearPlannedRoute);
        if (clearRoute) {
            patrolTaskEntityStore.clearPlannedRoute(taskId);
        }
        if (next < unlocked || keepThrough <= STEP_SCHEDULE_RESOURCE) {
            patrolTaskEntityStore.clearLaterComputedResults(taskId);
        }
        if (next < unlocked) {
            patrolTaskEntityStore.mergeDraftFields(
                    taskId, Map.of(PatrolTaskEntityStore.DRAFT_KEY_UNLOCKED, next));
        }
        return progressOf(next);
    }

    @Override
    public InspectionTaskDurationRefreshRespVO refreshDurations(Long taskId, InspectionTaskDurationRefreshReqVO reqVO) {
        PatrolTaskDraft draft = patrolTaskEntityStore.require(taskId);
        InspectionContent content = draft.inspectionContent();
        if (content == null) {
            content = new InspectionContent();
        }
        Integer storedAction = PatrolItemActionDurationSupport.minutesOf(content);
        Integer migrated = PatrolItemActionDurationSupport.migrateFromPlannedRoute(content, draft.plannedRoute());
        if (migrated != null && storedAction == null) {
            patrolTaskEntityStore.writeInspectionContent(taskId, content);
            Map<String, Object> planned = PatrolPlannedRouteSupport.asPlannedMap(draft.plannedRoute());
            if (planned != null && !planned.isEmpty()) {
                PatrolItemActionDurationSupport.stripFromPlannedRoute(planned);
                patrolTaskEntityStore.writePlannedRoute(taskId, planned);
            }
            storedAction = migrated;
        }
        Integer liveAction = itemActionDurationCalculator
                .computeLiveMinutes(content, draft.patrolExecutionMode())
                .orElse(null);
        boolean itemActionChanged = liveAction != null && storedAction != null && !liveAction.equals(storedAction);
        if (liveAction != null && (storedAction == null || itemActionChanged)) {
            content.setItemActionDurationMinutes(liveAction);
            patrolTaskEntityStore.writeInspectionContent(taskId, content);
            storedAction = liveAction;
        }
        boolean pathChanged = pathInputsChanged(draft, reqVO);
        boolean alreadyGenerated = Boolean.TRUE.equals(draft.orchestrationCommitted());
        int unlocked = unlockedOf(draft);
        String message = null;
        if ((itemActionChanged || pathChanged) && !alreadyGenerated) {
            CreateWizardInvalidation reason = pathChanged
                    ? CreateWizardInvalidation.PATH_INPUTS_CHANGED
                    : CreateWizardInvalidation.ITEM_ACTION_DURATION_CHANGED;
            if (unlocked > reason.keepThroughStep()) {
                unlocked = invalidate(taskId, reason).getUnlockedStep();
                message = pathChanged
                        ? "到达位置或路径耗时已变化，请重新规划并保存"
                        : "检查项动作耗时已变化，请再走一遍排期";
            } else if (unlocked == reason.keepThroughStep()) {
                unlocked = invalidate(taskId, reason).getUnlockedStep();
            }
        } else if (itemActionChanged || pathChanged) {
            message = pathChanged
                    ? "到达位置或路径耗时已变化，是否按新数据重新排期？"
                    : "检查项动作耗时已变化，是否按新数据重新排期？";
        }
        InspectionTaskDurationRefreshRespVO resp = new InspectionTaskDurationRefreshRespVO();
        resp.setItemActionDurationMinutes(storedAction);
        resp.setTravelDurationMinutes(
                PatrolPlannedRouteSupport.resolveTravelMinutesOnly(draft.plannedRoute(), draft.patrolExecutionMode()));
        resp.setTotalDurationMinutes(
                PatrolTaskDurationSupport.totalMinutes(content, draft.plannedRoute(), draft.patrolExecutionMode()));
        resp.setItemActionChanged(itemActionChanged);
        resp.setPathChanged(pathChanged);
        resp.setUnlockedStep(unlocked);
        resp.setAlreadyGenerated(alreadyGenerated);
        resp.setMessage(message);
        return resp;
    }

    private static boolean pathInputsChanged(PatrolTaskDraft draft, InspectionTaskDurationRefreshReqVO reqVO) {
        if (!PatrolPlannedRouteSupport.hasSavedRoute(draft.plannedRoute())) {
            return false;
        }
        if (reqVO == null || CollectionUtils.isEmpty(reqVO.getLiveCheckItemStopIds())) {
            return false;
        }
        Map<String, Object> planned = PatrolPlannedRouteSupport.asPlannedMap(draft.plannedRoute());
        if (planned == null) {
            return false;
        }
        Set<String> live = new LinkedHashSet<>(PatrolPlannedRouteSupport.asStringList(reqVO.getLiveCheckItemStopIds()));
        Set<String> saved = new LinkedHashSet<>(PatrolPlannedRouteSupport.asStringList(planned.get("stopIds")));
        String start = PatrolPlannedRouteSupport.asText(planned.get("startStopId"));
        String end = PatrolPlannedRouteSupport.asText(planned.get("endStopId"));
        if (StringUtils.hasText(start)) {
            saved.remove(start);
        }
        if (StringUtils.hasText(end)) {
            saved.remove(end);
        }
        return !live.equals(saved);
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
        if (unlocked == STEP_ROUTE && !skipsRoute(draft) && !PatrolPlannedRouteSupport.hasSavedRoute(draft.plannedRoute())) {
            throw ServiceExceptionUtil.invalidParamException("请先保存路线");
        }
        if (unlocked == STEP_SCHEDULE_RESOURCE && !hasOrchestrationPreview(draft)) {
            throw ServiceExceptionUtil.invalidParamException("还没有试排计划。请在排期与资源选定设备并处理完冲突");
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

    private static boolean hasOrchestrationPreview(PatrolTaskDraft draft) {
        if (draft == null || draft.orchestrationPreviewSlots() == null) {
            return false;
        }
        if (draft.orchestrationPreviewSlots() instanceof List<?> list) {
            return !list.isEmpty();
        }
        return true;
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
