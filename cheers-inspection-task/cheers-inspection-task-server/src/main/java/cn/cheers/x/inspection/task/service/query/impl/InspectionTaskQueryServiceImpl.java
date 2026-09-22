package cn.cheers.x.inspection.task.service.query.impl;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.inspection.inspection_content.service.enhance.ContentEnhancementService;
import cn.cheers.x.inspection.task.controller.admin.vo.task.*;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.query.InspectionTaskQueryService;
import cn.cheers.x.inspection.task.service.task.PatrolPlannedRouteSupport;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDurationSupport;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.runtime.api.RuntimeQueryApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 巡检任务查询服务实现类。
 * <p>权威：总任务实体 plannedRoute + 草稿袋；不读旧固定表 inspection_task。
 * <p>已占窗：当前方式自己的试排优先；只有正在看的就是生成时那种方式才读 runtime 占窗。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionTaskQueryServiceImpl implements InspectionTaskQueryService {

    private final ContentEnhancementService contentEnhancementService;
    private final PatrolTaskEntityStore patrolTaskEntityStore;
    private final RuntimeQueryApi runtimeQueryApi;
    private final ObjectMapper objectMapper;

    @Override
    public PageResult<InspectionTaskSimpleRespVO> getTaskPage(InspectionTaskPageReqVO pageReqVO) {
        List<PatrolTaskDraft> all = patrolTaskEntityStore.listAll();
        String name = pageReqVO.getTaskName();
        List<PatrolTaskDraft> filtered = all.stream()
                .filter(draft -> !StringUtils.hasText(name)
                        || (draft.name() != null && draft.name().contains(name.trim())))
                .toList();
        int pageNo = pageReqVO.getPageNo() == null || pageReqVO.getPageNo() < 1 ? 1 : pageReqVO.getPageNo();
        int pageSize = pageReqVO.getPageSize() == null || pageReqVO.getPageSize() < 1 ? 10 : pageReqVO.getPageSize();
        int from = Math.min((pageNo - 1) * pageSize, filtered.size());
        int to = Math.min(from + pageSize, filtered.size());
        List<InspectionTaskSimpleRespVO> voList = filtered.subList(from, to).stream()
                .map(this::toSimple)
                .collect(Collectors.toList());
        return new PageResult<>(voList, (long) filtered.size());
    }

    private InspectionTaskSimpleRespVO toSimple(PatrolTaskDraft draft) {
        InspectionTaskSimpleRespVO vo = new InspectionTaskSimpleRespVO();
        vo.setId(draft.id());
        vo.setTaskName(draft.name());
        vo.setDomain(draft.domain());
        vo.setStatus(deriveDisplayStatus(draft));
        vo.setEnabled(Boolean.TRUE.equals(draft.orchestrationCommitted()));
        vo.setRuntimeJobId(draft.runtimeJobId());
        vo.setSubTaskCount(0);
        return vo;
    }

    @Override
    public InspectionTaskRespVO getTaskDetail(Long id) {
        PatrolTaskDraft draft = patrolTaskEntityStore.require(id);
        InspectionTaskRespVO respVO = new InspectionTaskRespVO();
        respVO.setId(draft.id());
        respVO.setTaskName(draft.name());
        respVO.setDomain(draft.domain());
        respVO.setStatus(deriveDisplayStatus(draft));
        respVO.setEnabled(Boolean.TRUE.equals(draft.orchestrationCommitted()));
        respVO.setRuntimeJobId(draft.runtimeJobId());
        respVO.setExecutionDeviceBinding(draft.executionDeviceBinding());
        respVO.setResourcePolicy(draft.resourcePolicy());
        respVO.setSchedulePolicyId(draft.schedulePolicyId());
        PatrolTaskEntityStore.ArrangePolicy arrangePolicy = patrolTaskEntityStore.readArrangePolicy(id);
        respVO.setConflictStrategy(arrangePolicy.conflictStrategy());
        respVO.setTaskGapMinutes(arrangePolicy.taskGapMinutes());
        respVO.setAllowShiftExisting(arrangePolicy.allowShiftExisting());
        respVO.setMaxShiftMinutes(arrangePolicy.maxShiftMinutes());
        respVO.setScheduleConfig(draft.scheduleConfig());
        respVO.setPatrolExecutionMode(draft.patrolExecutionMode());
        respVO.setCreateUnlockedStep(draft.createUnlockedStep());

        InspectionContent content = draft.inspectionContent();
        if (content != null) {
            content = contentEnhancementService.enhanceContent(content, true);
            respVO.setInspectionContent(content);
        }

        enrichSavedRouteFromDraft(respVO, draft);
        enrichScheduleSlotsFromRuntime(respVO, draft);
        respVO.setExecutionStepTree(draft.stepTree());
        respVO.setSubTasks(Collections.emptyList());
        return respVO;
    }

    /**
     * 打开任务时回显排期步。
     * 当前方式自己的试排优先；没有试排时，只有「正在看的就是生成任务时那种方式」才读已占窗。
     * 换到另一种方式：这一套没试排就空着，不能把上一套的占窗拿来报重叠。
     */
    private void enrichScheduleSlotsFromRuntime(InspectionTaskRespVO respVO, PatrolTaskDraft draft) {
        List<ScheduleSlotDTO> preview = patrolTaskEntityStore.readOrchestrationPreviewSlots(draft.id());
        if (preview != null && !preview.isEmpty()) {
            respVO.setScheduleSlots(preview);
            return;
        }
        if (!StringUtils.hasText(draft.runtimeJobId()) || !sameMeansAsGenerated(draft)) {
            respVO.setScheduleSlots(List.of());
            return;
        }
        List<ScheduleSlotDTO> slots =
                runtimeQueryApi.listSlotsByJobId(draft.runtimeJobId().trim()).getCheckedData();
        respVO.setScheduleSlots(slots == null ? List.of() : slots);
    }

    /**
     * 正在看的巡检方式是不是生成任务时记下的那一种。
     * 旧数据没记下时，仍按当前方式读已占窗（打开未换过方式的已生成任务）。
     */
    private boolean sameMeansAsGenerated(PatrolTaskDraft draft) {
        String current = draft.patrolExecutionMode();
        if (!StringUtils.hasText(current) || draft.id() == null) {
            return false;
        }
        String committed = patrolTaskEntityStore.readCommittedPatrolExecutionMode(draft.id());
        if (!StringUtils.hasText(committed)) {
            return true;
        }
        return committed.trim().equals(current.trim());
    }

    @Override
    public InspectionTaskStatisticsRespVO getTaskStatistics() {
        List<PatrolTaskDraft> all = patrolTaskEntityStore.listAll();
        long enabled = all.stream().filter(d -> Boolean.TRUE.equals(d.orchestrationCommitted())).count();
        InspectionTaskStatisticsRespVO vo = new InspectionTaskStatisticsRespVO();
        vo.setTaskTotal((long) all.size());
        vo.setEnabledCount(enabled);
        vo.setDisabledCount((long) all.size() - enabled);
        vo.setDraftCount(all.stream().filter(d -> deriveDisplayStatus(d) == 0).count());
        return vo;
    }

    /**
     * 派生任务的展示状态（列表状态列 / 详情状态）。
     *
     * <ul>
     *   <li>orchestrationCommitted = true → 1 已启用</li>
     *   <li>orchestrationCommitted ≠ true 且 runtimeJobId 有值 → 2 已停用</li>
     *   <li>其余（含仅有 orchestrationPreview 试排）→ 0 草稿</li>
     * </ul>
     */
    public static Integer deriveDisplayStatus(PatrolTaskDraft draft) {
        if (Boolean.TRUE.equals(draft.orchestrationCommitted())) {
            return 1;
        }
        if (StringUtils.hasText(draft.runtimeJobId())) {
            return 2;
        }
        return 0;
    }

    /**
     * 已保存路线（plannedRoute）填停靠序和预览。起点终点优先读 plannedRoute，没有再读草稿袋其它字段。
     */
    private void enrichSavedRouteFromDraft(InspectionTaskRespVO respVO, PatrolTaskDraft draft) {
        Map<String, Object> planned = PatrolPlannedRouteSupport.asPlannedMap(draft.plannedRoute());
        if (planned == null || planned.isEmpty()) {
            respVO.setStopSequence(Collections.emptyList());
            respVO.setStartStopId(draft.startStopId());
            respVO.setEndStopId(draft.endStopId());
            return;
        }
        List<String> stopSequence = PatrolPlannedRouteSupport.asStringList(planned.get("stopIds"));
        respVO.setStopSequence(stopSequence);
        respVO.setStartStopId(firstNonBlank(asString(planned.get("startStopId")), draft.startStopId()));
        respVO.setEndStopId(firstNonBlank(asString(planned.get("endStopId")), draft.endStopId()));
        respVO.setNetworkRef(PatrolPlannedRouteSupport.networkRef(draft.plannedRoute()));
        respVO.setInspectionType(PatrolPlannedRouteSupport.normalizeInspectionType(draft.patrolExecutionMode()));
        respVO.setEstimatedDuration(PatrolTaskDurationSupport.totalMinutes(
                draft.inspectionContent(), draft.plannedRoute(), draft.patrolExecutionMode()));

        TaskSavedRoutePreviewVO preview = new TaskSavedRoutePreviewVO();
        preview.setNetworkRef(respVO.getNetworkRef());
        preview.setStopIds(new ArrayList<>(stopSequence));
        preview.setTotalDistanceMeters(asLong(planned.get("totalDistanceMeters")));
        preview.setMobilityProfileId(asString(planned.get("mobilityProfileId")));
        preview.setStartStopId(asString(planned.get("startStopId")));
        preview.setEndStopId(asString(planned.get("endStopId")));
        preview.setReturnToStart(asBoolean(planned.get("returnToStart")));
        preview.setDecisionTraceId(asString(planned.get("decisionTraceId")));
        preview.setVisitNodeIds(new ArrayList<>(PatrolPlannedRouteSupport.asStringList(planned.get("visitNodeIds"))));
        preview.setVisitPositions(asObjectList(planned.get("visitPositions")));
        preview.setSegments(asObjectList(planned.get("segments")));
        respVO.setRoutePreview(preview);
    }

    private static String firstNonBlank(String preferred, String fallback) {
        return StringUtils.hasText(preferred) ? preferred : fallback;
    }

    private static String asString(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value);
        return StringUtils.hasText(text) ? text : null;
    }

    private static Long asLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static Boolean asBoolean(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }

    @SuppressWarnings("unchecked")
    private static List<Object> asObjectList(Object raw) {
        if (!(raw instanceof List<?> list) || list.isEmpty()) {
            return new ArrayList<>();
        }
        List<Object> out = new ArrayList<>(list.size());
        for (Object item : list) {
            if (item != null) {
                out.add(item);
            }
        }
        return out;
    }
}
