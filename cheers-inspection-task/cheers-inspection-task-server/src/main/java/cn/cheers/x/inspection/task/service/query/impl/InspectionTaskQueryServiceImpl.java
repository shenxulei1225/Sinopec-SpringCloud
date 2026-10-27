package cn.cheers.x.inspection.task.service.query.impl;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.inspection.task.controller.admin.vo.task.*;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.query.InspectionTaskQueryService;
import cn.cheers.x.inspection.task.service.task.GeneratedCheckInputReviewService;
import cn.cheers.x.inspection.task.service.task.GeneratedCheckInputReviewReadContext;
import cn.cheers.x.inspection.task.service.task.GeneratedCheckInputReviewReadSupport;
import cn.cheers.x.inspection.task.service.task.PatrolPlannedRouteSupport;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDurationSupport;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.runtime.api.RuntimeQueryApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 巡检任务查询服务实现类。
 * <p>权威：总任务实体 plannedRoute + 草稿袋；不读旧固定表 inspection_task。
 * <p>已占窗：当前方式自己的试排优先；只有正在看的就是生成时那种方式才读 runtime 占窗。
 * 列表这一页上的已生成任务，用和打开这一条相同的对照。记下的说明是空的，不能当成已经更新过。
 * 排期窗口和计划点数读同一份已占窗。没生成的不填这两格。
 * <p>详情返回任务上已经写下的名称、巡检方式、对象、路线、计划点和步骤图。
 * 不在返回前补对象名称、耗时单位或动作参数。没写下的不在这里补，免得整页等这一遍。
 */
@Service
@RequiredArgsConstructor
public class InspectionTaskQueryServiceImpl implements InspectionTaskQueryService {

    private final PatrolTaskEntityStore patrolTaskEntityStore;
    private final RuntimeQueryApi runtimeQueryApi;
    private final ObjectMapper objectMapper;
    private final GeneratedCheckInputReviewService generatedCheckInputReviewService;
    private final GeneratedCheckInputReviewReadSupport generatedCheckInputReviewReadSupport;

    @Override
    public PageResult<InspectionTaskSimpleRespVO> getTaskPage(InspectionTaskPageReqVO pageReqVO) {
        int pageNo = pageReqVO.getPageNo() == null || pageReqVO.getPageNo() < 1 ? 1 : pageReqVO.getPageNo();
        int pageSize = pageReqVO.getPageSize() == null || pageReqVO.getPageSize() < 1 ? 10 : pageReqVO.getPageSize();
        PageResult<PatrolTaskDraft> page = patrolTaskEntityStore.page(
                pageReqVO.getTaskName(), pageReqVO.getStatus(), pageNo, pageSize);
        List<PatrolTaskDraft> generated = page.getList().stream()
                .filter(draft -> Boolean.TRUE.equals(draft.orchestrationCommitted()))
                .toList();
        GeneratedCheckInputReviewReadContext context =
                generatedCheckInputReviewReadSupport.load(generated);
        List<InspectionTaskSimpleRespVO> voList = page.getList().stream()
                .map(draft -> toSimple(draft, context))
                .toList();
        return new PageResult<>(voList, page.getTotal());
    }

    private InspectionTaskSimpleRespVO toSimple(
            PatrolTaskDraft draft,
            GeneratedCheckInputReviewReadContext context) {
        InspectionTaskSimpleRespVO vo = new InspectionTaskSimpleRespVO();
        vo.setId(draft.id());
        vo.setTaskName(draft.name());
        vo.setDomain(draft.domain());
        vo.setStatus(deriveDisplayStatus(draft));
        vo.setEnabled("enabled".equals(draft.manageStatus()));
        vo.setRuntimeJobId(draft.runtimeJobId());
        vo.setSubTaskCount(0);
        vo.setUpdateHint(liveUpdateHint(draft, context));
        fillOccupiedWindow(vo, draft);
        return vo;
    }

    /**
     * 已生成任务的排期窗口和计划点数，用和打开详情相同的已占窗。
     * 没生成的这两格留空，列表显示成横线。
     */
    private void fillOccupiedWindow(InspectionTaskSimpleRespVO vo, PatrolTaskDraft draft) {
        List<ScheduleSlotDTO> slots = slotsShownOnList(draft);
        if (slots.isEmpty()) {
            return;
        }
        LocalDate start = null;
        LocalDate end = null;
        int count = 0;
        for (ScheduleSlotDTO slot : slots) {
            if (slot == null || !StringUtils.hasText(slot.getSlotId())) {
                continue;
            }
            count++;
            LocalDate day = planDay(slot.getPlannedStart());
            if (day == null) {
                day = planDay(slot.getCandidateStart());
            }
            if (day == null) {
                continue;
            }
            if (start == null || day.isBefore(start)) {
                start = day;
            }
            if (end == null || day.isAfter(end)) {
                end = day;
            }
        }
        if (count > 0) {
            vo.setScheduleCount(count);
        }
        vo.setHorizonStartDate(start);
        vo.setHorizonEndDate(end);
    }

    /**
     * 和打开详情同一份计划点：这一页自己的试排优先。
     * 没有试排时，只有正在看的就是生成时那种方式，才读已经占好的窗。
     */
    private List<ScheduleSlotDTO> slotsShownOnList(PatrolTaskDraft draft) {
        if (!Boolean.TRUE.equals(draft.orchestrationCommitted()) || draft.id() == null) {
            return List.of();
        }
        List<ScheduleSlotDTO> preview = patrolTaskEntityStore.readOrchestrationPreviewSlots(draft.id());
        if (preview != null && !preview.isEmpty()) {
            return preview;
        }
        if (!StringUtils.hasText(draft.runtimeJobId()) || !sameMeansAsGenerated(draft)) {
            return List.of();
        }
        List<ScheduleSlotDTO> slots =
                runtimeQueryApi.listSlotsByJobId(draft.runtimeJobId().trim()).getCheckedData();
        return slots == null ? List.of() : slots;
    }

    /** 计划时刻按北京时间取日期。写坏的时刻不拿来撑窗口。 */
    private static LocalDate planDay(String text) {
        if (!StringUtils.hasText(text)) {
            return null;
        }
        try {
            return OffsetDateTime.parse(text.trim())
                    .atZoneSameInstant(ZoneId.of("Asia/Shanghai"))
                    .toLocalDate();
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    /**
     * 已生成任务用和打开详情相同的对照。有差异才显示需要更新。
     * 草稿不对照。记下的说明为空时，不再把这一条当成已经更新过。
     */
    private String liveUpdateHint(
            PatrolTaskDraft draft,
            GeneratedCheckInputReviewReadContext context) {
        if (!Boolean.TRUE.equals(draft.orchestrationCommitted())) {
            return null;
        }
        InspectionTaskGeneratedInputReviewRespVO review =
                generatedCheckInputReviewService.review(draft, context);
        if (review.getChanges() == null || review.getChanges().isEmpty()) {
            return null;
        }
        List<String> lines = new ArrayList<>();
        for (InspectionTaskGeneratedInputReviewRespVO.Change change : review.getChanges()) {
            String device = change.getEquipmentName() == null ? "" : change.getEquipmentName().trim();
            String item = change.getInspectionItemName() == null ? "" : change.getInspectionItemName().trim();
            String detail = change.getDetail() == null ? "" : change.getDetail().trim();
            String who = device.isEmpty() ? item : (item.isEmpty() ? device : device + " 的 " + item);
            if (!detail.isEmpty()) {
                lines.add(who.isEmpty() ? detail : who + "：" + detail);
            }
        }
        return lines.isEmpty() ? null : String.join("\n", lines);
    }

    @Override
    public InspectionTaskRespVO getTaskDetail(Long id) {
        PatrolTaskDraft draft = patrolTaskEntityStore.require(id);
        InspectionTaskRespVO respVO = new InspectionTaskRespVO();
        respVO.setId(draft.id());
        respVO.setTaskName(draft.name());
        respVO.setFacilityId(draft.facilityId());
        respVO.setDomain(draft.domain());
        respVO.setStatus(deriveDisplayStatus(draft));
        respVO.setEnabled("enabled".equals(draft.manageStatus()));
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
        java.util.Map<String, Object> unpublished = patrolTaskEntityStore.readUnpublishedModification(id);
        if (unpublished != null && !unpublished.isEmpty()) {
            respVO.setUnpublishedModification(unpublished);
        }

        InspectionContent content = draft.inspectionContent();
        if (content != null) {
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
        long total = patrolTaskEntityStore.countByManageStatus(null);
        long enabled = patrolTaskEntityStore.countByManageStatus(1);
        long draft = patrolTaskEntityStore.countByManageStatus(0);
        long disabled = patrolTaskEntityStore.countByManageStatus(2);
        InspectionTaskStatisticsRespVO vo = new InspectionTaskStatisticsRespVO();
        vo.setTaskTotal(total);
        vo.setEnabledCount(enabled);
        vo.setDisabledCount(disabled);
        vo.setDraftCount(draft);
        return vo;
    }

    /**
     * 派生任务的展示状态（列表状态列 / 详情状态）。
     * 没写下管理状态时返回空，列表仍显示这一条。不把空值猜成草稿。
     * 写下了但不认识的值仍报错。
     *
     * <ul>
     *   <li>enabled → 1 已启用</li>
     *   <li>disabled → 2 已停用</li>
     *   <li>draft → 0 草稿</li>
     * </ul>
     */
    public static Integer deriveDisplayStatus(PatrolTaskDraft draft) {
        String manageStatus = draft.manageStatus();
        if (!StringUtils.hasText(manageStatus)) {
            return null;
        }
        return switch (manageStatus) {
            case "draft" -> 0;
            case "enabled" -> 1;
            case "disabled" -> 2;
            default -> throw new IllegalStateException("任务管理状态无效：" + manageStatus);
        };
    }

    /**
     * 已保存路线（plannedRoute）填停靠序和预览。起点终点优先读 plannedRoute，没有再读草稿袋其它字段。
     */
    private void enrichSavedRouteFromDraft(InspectionTaskRespVO respVO, PatrolTaskDraft draft) {
        Map<String, Object> planned = PatrolPlannedRouteSupport.asPlannedMap(draft.plannedRoute());
        respVO.setTaskPreparation(draft.taskPreparation());
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
        preview.setEstimatedTravelDuration(
                PatrolPlannedRouteSupport.estimatedTravelDuration(draft.plannedRoute()));
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
