package cn.cheers.x.inspection.inspection_content.service.orchestration.impl;

import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolRouteRunReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolOrchestrationRunReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolSlotWritebackReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolTaskPauseReqVO;
import cn.cheers.x.inspection.inspection_content.service.orchestration.PatrolRouteFacadeService;
import cn.cheers.x.inspection.inspection_content.service.orchestration.PatrolSaveRouteService;
import cn.cheers.x.inspection.orchestration.dto.PatrolSaveRouteReqDTO;
import cn.cheers.x.inspection.task.service.execution.openrun.PatrolOpenRunMaterializeService;
import cn.cheers.x.inspection.task.service.execution.scheduleboard.PatrolReservationAutoStartScheduler;
import cn.cheers.x.inspection.task.service.execution.steptree.TaskStepTreeGenerateService;
import cn.cheers.x.module.platform.contract.dto.reservation.ResourceReservationDTO;
import cn.cheers.x.module.platform.contract.dto.runtime.RuntimeJobDTO;
import cn.cheers.x.module.platform.contract.enums.RuntimeJobStatus;
import cn.cheers.x.inspection.task.service.task.CreateWizardInvalidation;
import cn.cheers.x.inspection.task.service.task.PatrolPlannedRouteSupport;
import cn.cheers.x.inspection.task.service.task.PatrolTaskCreateProcessService;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.platform.contract.ContractVersions;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleConflictReportDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunRequest;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunResponse;
import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.contract.dto.work.ResourceRequirementDTO;
import cn.cheers.x.module.platform.contract.dto.work.TimePreferencesDTO;
import cn.cheers.x.module.platform.contract.dto.work.TimeWindowDTO;
import cn.cheers.x.module.platform.contract.enums.SlotLockState;
import cn.cheers.x.module.platform.contract.dto.slot.AssignedResourceDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import cn.cheers.x.module.platform.orchestration.api.ScheduleRunApi;
import cn.cheers.x.module.platform.orchestration.enums.OrchestrationRefs;
import cn.cheers.x.module.platform.orchestration.phase.OrchestrationPhase;
import cn.cheers.x.module.platform.orchestration.route.RoutePayloadKeys;
import cn.cheers.x.module.platform.runtime.api.RuntimePersistApi;
import cn.cheers.x.module.platform.runtime.api.RuntimeQueryApi;
import cn.cheers.x.module.platform.runtime.api.RuntimeSlotWriteApi;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimePersistReqDTO;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimeSlotReleaseReqDTO;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimeSlotStatusUpdateReqDTO;
import cn.cheers.x.module.platform.runtime.enums.RuntimeSlotReleaseMode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_SAVE_ROUTE_PLANNED_ROUTE_REQUIRED;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_SAVE_ROUTE_TASK_ID_REQUIRED;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_FACADE_NO_ACTIVE_SLOTS;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_FACADE_NOT_ARRANGED;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_FACADE_SCHEDULE_STILL_CONFLICTS;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_FACADE_OCCUPANCY_CONFLICT;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_FACADE_ROUTE_NOT_SAVED;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_FACADE_RUNTIME_JOB_REQUIRED;

/**
 * 巡检组路线门面：路径规划、冲突检测、第 3 步无冲突写试排、智能编排解冲突。
 * <p>权威：总任务 plannedRoute（saveRoute 快照）；试排计划点 orchestrationPreview*；
 * 生成任务后写入 runtimeJobId / orchestrationCommitted。
 * <p>不负责：冲突算法本身（标准检测在 ScheduleConflictReporter）；读写旧固定表 inspection_task。
 */
@Service
@Validated
public class PatrolRouteFacadeServiceImpl implements PatrolRouteFacadeService {

    static final String FACILITY_ID = "facilityId";
    static final String OBJECT_IDS = "objectIds";
    static final String PREFERRED_NETWORK_REF = "preferredNetworkRef";
    static final String TASK_ID = "taskId";
    static final String FROM_SAVED_ROUTE_SNAPSHOT = "fromSavedRouteSnapshot";
    static final String TASK_ENABLED = "taskEnabled";
    static final String START_STOP_ID = "startStopId";
    static final String END_STOP_ID = "endStopId";
    static final String RETURN_TO_START = "returnToStart";
    static final String STOP_IDS = "stopIds";
    static final String INSPECTION_TYPE = "inspectionType";

    private static final ZoneId DEFAULT_ZONE = ZoneId.of("Asia/Shanghai");

    private static final SchedulingSpecDTO PLACEHOLDER_SCHEDULING_SPEC = SchedulingSpecDTO.builder()
            .mode("once")
            .conflictStrategy("none")
            .build();

    @Resource
    private ScheduleRunApi scheduleRunApi;

    @Resource
    private RuntimePersistApi runtimePersistApi;

    @Resource
    private RuntimeQueryApi runtimeQueryApi;

    @Resource
    private RuntimeSlotWriteApi runtimeSlotWriteApi;

    @Resource
    private PatrolTaskEntityStore patrolTaskEntityStore;

    @Resource
    private PatrolOpenRunMaterializeService patrolOpenRunMaterializeService;

    @Resource
    private TaskStepTreeGenerateService taskStepTreeGenerateService;

    @Resource
    private PatrolReservationAutoStartScheduler patrolReservationAutoStartScheduler;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private PatrolSaveRouteService patrolSaveRouteService;

    @Resource
    private PatrolScheduleConflictDetectService patrolScheduleConflictDetectService;

    @Resource
    private PatrolTaskCreateProcessService patrolTaskCreateProcessService;

    @Override
    public ScheduleConflictReportDTO detectScheduleConflicts(PatrolOrchestrationRunReqVO reqVO) {
        requireTaskReadyForOrchestration(reqVO.getTaskId());
        return patrolScheduleConflictDetectService.detect(reqVO.getTaskId(), reqVO.getSchedulingSpec()).report();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScheduleRunResponse prepareConfirmPreview(PatrolOrchestrationRunReqVO reqVO) {
        PatrolTaskDraft draft = requireTaskReadyForOrchestration(reqVO.getTaskId());
        PatrolScheduleConflictDetectService.DetectedPlan detected =
                patrolScheduleConflictDetectService.detect(reqVO.getTaskId(), reqVO.getSchedulingSpec());
        if (Boolean.TRUE.equals(detected.report().getHasConflict())) {
            throw exception(PATROL_FACADE_SCHEDULE_STILL_CONFLICTS);
        }
        Object executionStepTree = generateExecutionSteps(detected.draft());
        List<ScheduleSlotDTO> slots = slotsFromTemplate(detected.workItems());
        patrolTaskEntityStore.writeOrchestrationPreview(
                reqVO.getTaskId(), slots, "按排期模板时间点写入试排，无冲突。");
        return ScheduleRunResponse.builder()
                .contractVersion(ContractVersions.MVP)
                .slots(slots)
                .plainSummary("按排期模板时间点写入试排，无冲突。")
                .workItems(detected.workItems())
                .executionStepTree(executionStepTree)
                .build();
    }

    @Override
    public ScheduleRunResponse previewRoute(PatrolRouteRunReqVO reqVO) {
        if (CollectionUtils.isEmpty(reqVO.getStopIds())) {
            throw invalidParamException("开始规划缺少停靠点");
        }
        return scheduleRunApi.run(ScheduleRunRequest.builder()
                .contractVersion(ContractVersions.MVP)
                .orchestrationRef(OrchestrationRefs.PATROL_ROUTE_PREVIEW_V1)
                .dryRun(true)
                .stopAfterPhase(OrchestrationPhase.ROUTE.name())
                .schedulingSpec(PLACEHOLDER_SCHEDULING_SPEC)
                .workItems(List.of(buildRoutePlanningWorkItem(reqVO, reqVO.getTaskId(), false)))
                .build()).getCheckedData();
    }

    @Override
    public ScheduleRunResponse saveRoute(PatrolRouteRunReqVO reqVO) {
        if (reqVO.getTaskId() == null) {
            throw exception(PATROL_SAVE_ROUTE_TASK_ID_REQUIRED);
        }
        Map<String, Object> planned = reqVO.getPlannedRoute();
        if (planned == null || planned.isEmpty() || !PatrolPlannedRouteSupport.hasSavedRoute(planned)) {
            throw exception(PATROL_SAVE_ROUTE_PLANNED_ROUTE_REQUIRED);
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put(RoutePayloadKeys.PLANNED_ROUTE, planned);
        patrolSaveRouteService.saveRoute(PatrolSaveRouteReqDTO.builder()
                .taskId(reqVO.getTaskId())
                .facilityId(reqVO.getFacilityId())
                .workItems(List.of(WorkItemDTO.builder().payload(payload).build()))
                .dryRun(false)
                .build());
        patrolTaskCreateProcessService.invalidate(reqVO.getTaskId(), CreateWizardInvalidation.ROUTE_SAVED);
        return ScheduleRunResponse.builder()
                .contractVersion(ContractVersions.MVP)
                .workItems(List.of(WorkItemDTO.builder().payload(payload).build()))
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScheduleRunResponse previewOrchestration(PatrolOrchestrationRunReqVO reqVO) {
        PatrolTaskDraft draft = requireTaskReadyForOrchestration(reqVO.getTaskId());
        String previousRuntimeJobId = draft.runtimeJobId();
        if (!Boolean.TRUE.equals(draft.orchestrationCommitted())) {
            // 未生成任务：智能编排前先让开本任务留下的未执行占窗，不能只认草稿上那一个作业号。
            releaseOwnUnexecutedOccupancy(
                    reqVO.getTaskId(),
                    previousRuntimeJobId,
                    draft.facilityId(),
                    resolveSchedulingSpec(reqVO.getTaskId(), reqVO.getSchedulingSpec()),
                    "previewOrchestration: replace previous trial for same task");
            if (StringUtils.hasText(previousRuntimeJobId)) {
                patrolTaskEntityStore.clearOrchestrationTrialState(reqVO.getTaskId());
            }
        }
        Object executionStepTree = generateExecutionSteps(draft);
        ScheduleRunResponse response = scheduleRunApi.run(
                buildOrchestrationRunRequest(reqVO, draft, previousRuntimeJobId, true)).getCheckedData();
        if (CollectionUtils.isEmpty(response.getSlots())) {
            throw exception(PATROL_FACADE_NO_ACTIVE_SLOTS);
        }
        patrolTaskEntityStore.writeOrchestrationPreview(
                reqVO.getTaskId(), response.getSlots(), response.getPlainSummary());
        return ScheduleRunResponse.builder()
                .contractVersion(response.getContractVersion())
                .runtimeJobId(response.getRuntimeJobId())
                .status(response.getStatus())
                .slots(response.getSlots())
                .decisionTraceId(response.getDecisionTraceId())
                .plainSummary(response.getPlainSummary())
                .workOrderIds(response.getWorkOrderIds())
                .routePreview(response.getRoutePreview())
                .workItems(response.getWorkItems())
                .executionStepTree(executionStepTree)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScheduleRunResponse commitOrchestration(PatrolOrchestrationRunReqVO reqVO) {
        PatrolTaskDraft draft = requireTaskReadyForOrchestration(reqVO.getTaskId());
        String previousJobId = draft.runtimeJobId();
        // 本任务未执行占窗先让开：草稿上的作业号，以及失败留下、作业号没写回草稿的同任务窗口。
        releaseOwnUnexecutedOccupancy(
                reqVO.getTaskId(),
                previousJobId,
                draft.facilityId(),
                resolveSchedulingSpec(reqVO.getTaskId(), reqVO.getSchedulingSpec()),
                "generateTask: replace unexecuted windows of the same task");

        if (!hasOrchestrationPreview(draft)) {
            throw exception(PATROL_FACADE_NOT_ARRANGED);
        }
        String runtimeJobId = commitArrangePreviewSnapshot(reqVO, draft, previousJobId);
        List<ScheduleSlotDTO> slots = runtimeQueryApi.listSlotsByJobId(runtimeJobId).getCheckedData();
        boolean clearPreviewAfterCommit = true;
        if (CollectionUtils.isEmpty(slots)) {
            throw exception(PATROL_FACADE_NOT_ARRANGED);
        }
        finishGenerateTaskCommit(reqVO.getTaskId(), runtimeJobId, slots, clearPreviewAfterCommit);
        registerAutoStart(reqVO.getTaskId(), slots);
        return ScheduleRunResponse.builder()
                .contractVersion(ContractVersions.MVP)
                .runtimeJobId(runtimeJobId)
                .slots(slots)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void abortOrchestration(PatrolTaskPauseReqVO reqVO) {
        releaseUnfinished(reqVO, RuntimeSlotReleaseMode.ABORT);
        requireTask(reqVO.getTaskId());
        patrolTaskEntityStore.clearOrchestrationTrialState(reqVO.getTaskId());
    }

    @Override
    public void writebackSlot(PatrolSlotWritebackReqVO reqVO) {
        runtimeSlotWriteApi.updateSlotStatus(RuntimeSlotStatusUpdateReqDTO.builder()
                .slotId(reqVO.getSlotId())
                .slotStatus(reqVO.getSlotStatus())
                .actualStart(reqVO.getActualStart())
                .actualEnd(reqVO.getActualEnd())
                .reason(reqVO.getReason())
                .build()).checkError();
    }

    /**
     * 试排快照落库：persist → finalize planned；待执行与 committed 由 {@link #finishGenerateTaskCommit} 统一完成。
     */
    private String commitArrangePreviewSnapshot(
            PatrolOrchestrationRunReqVO reqVO, PatrolTaskDraft draft, String previousJobId) {
        List<ScheduleSlotDTO> previewSlots = patrolTaskEntityStore.readOrchestrationPreviewSlots(reqVO.getTaskId());
        if (CollectionUtils.isEmpty(previewSlots)) {
            throw exception(PATROL_FACADE_NOT_ARRANGED);
        }
        String runtimeJobId = UUID.randomUUID().toString();
        List<ScheduleSlotDTO> slotsToPersist = bindPreviewSlotsToRuntimeJob(previewSlots, runtimeJobId);
        runtimePersistApi.persist(RuntimePersistReqDTO.builder()
                .job(buildPatrolOrchestrationRuntimeJob(runtimeJobId))
                .slots(slotsToPersist)
                .facilityId(draft.facilityId())
                .build()).checkError();
        try {
            verifyCommitWindow(
                    reqVO.getTaskId(),
                    runtimeJobId,
                    resolveSchedulingSpec(reqVO.getTaskId(), reqVO.getSchedulingSpec()),
                    previousJobId);
            runtimeSlotWriteApi.finalizePlannedSchedule(runtimeJobId).checkError();
            return runtimeJobId;
        } catch (RuntimeException ex) {
            runtimeSlotWriteApi.releaseUnfinished(RuntimeSlotReleaseReqDTO.builder()
                    .runtimeJobId(runtimeJobId)
                    .mode(RuntimeSlotReleaseMode.ABORT)
                    .reason("generateTask: persist succeeded but window check failed")
                    .facilityId(draft.facilityId())
                    .build());
            throw ex;
        }
    }

    /**
     * 生成任务（commitOrchestration）固定顺序：占窗已定稿后 → 写每计划点待执行 → 标记任务已生成并排期 → 由调用方登记到点开跑。
     * <p>不负责：试排预览、开跑下发；禁止在物化/调度模块内互相调用，只在本门面按序编排。
     */
    private void finishGenerateTaskCommit(
            Long taskId, String runtimeJobId, List<ScheduleSlotDTO> slots, boolean clearPreview) {
        PatrolTaskDraft task = patrolTaskEntityStore.require(taskId);
        patrolOpenRunMaterializeService.materializeAllSlots(task, slots);
        if (clearPreview) {
            patrolTaskEntityStore.clearOrchestrationPreview(taskId);
        }
        patrolTaskEntityStore.writeOrchestrationCommittedState(taskId, runtimeJobId, Boolean.TRUE);
    }

    private static RuntimeJobDTO buildPatrolOrchestrationRuntimeJob(String runtimeJobId) {
        return RuntimeJobDTO.builder()
                .contractVersion(ContractVersions.MVP)
                .runtimeJobId(runtimeJobId)
                .entityTypeCode(PatrolTaskEntityStore.TASK_TYPE)
                .triggerAction("schedule.run")
                .status(RuntimeJobStatus.SCHEDULED)
                .orchestrationRef(OrchestrationRefs.PATROL_ORCHESTRATION_V1)
                .createdAt(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(OffsetDateTime.now()))
                .build();
    }

    /**
     * 试排快照只有 candidate 时刻；落库前绑 runtimeJobId，planned 留空供 finalizePlannedSchedule 定稿。
     */
    private static List<ScheduleSlotDTO> bindPreviewSlotsToRuntimeJob(
            List<ScheduleSlotDTO> previewSlots, String runtimeJobId) {
        List<ScheduleSlotDTO> bound = new ArrayList<>(previewSlots.size());
        for (ScheduleSlotDTO source : previewSlots) {
            if (source == null) {
                continue;
            }
            String candidateStart = StringUtils.hasText(source.getCandidateStart())
                    ? source.getCandidateStart() : source.getPlannedStart();
            String candidateEnd = StringUtils.hasText(source.getCandidateEnd())
                    ? source.getCandidateEnd() : source.getPlannedEnd();
            bound.add(ScheduleSlotDTO.builder()
                    .contractVersion(StringUtils.hasText(source.getContractVersion())
                            ? source.getContractVersion() : ContractVersions.MVP)
                    .slotId(source.getSlotId())
                    .runtimeJobId(runtimeJobId)
                    .workId(source.getWorkId())
                    .entityTypeCode(source.getEntityTypeCode())
                    .candidateStart(candidateStart)
                    .candidateEnd(candidateEnd)
                    .assignedResources(source.getAssignedResources())
                    .lockState(source.getLockState())
                    .slotStatus(source.getSlotStatus() != null ? source.getSlotStatus() : SlotStatus.PLANNED)
                    .policySnapshotId(source.getPolicySnapshotId())
                    .decisionTraceId(source.getDecisionTraceId())
                    .build());
        }
        if (bound.isEmpty()) {
            throw exception(PATROL_FACADE_NOT_ARRANGED);
        }
        return bound;
    }

    private ScheduleRunRequest buildOrchestrationRunRequest(
            PatrolOrchestrationRunReqVO reqVO, PatrolTaskDraft draft, String previousRuntimeJobId, boolean previewOnly) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put(TASK_ID, reqVO.getTaskId());
        payload.put(FROM_SAVED_ROUTE_SNAPSHOT, true);
        payload.put(TASK_ENABLED, Boolean.FALSE);
        if (draft.facilityId() != null) {
            payload.put(FACILITY_ID, draft.facilityId());
        }

        WorkItemDTO seed = WorkItemDTO.builder()
                .contractVersion(ContractVersions.MVP)
                .workId("patrol-arrange-" + reqVO.getTaskId())
                .entityTypeCode(PatrolTaskEntityStore.TASK_TYPE)
                .sourceModelCode(PatrolTaskEntityStore.MODEL_PATROL)
                .sourceInstanceId(String.valueOf(reqVO.getTaskId()))
                .payload(payload)
                .build();

        ScheduleRunRequest.ScheduleRunRequestBuilder builder = ScheduleRunRequest.builder()
                .contractVersion(ContractVersions.MVP)
                .entityTypeCode(PatrolTaskEntityStore.TASK_TYPE)
                .orchestrationRef(OrchestrationRefs.PATROL_ORCHESTRATION_V1)
                .dryRun(previewOnly)
                .schedulingSpec(resolveSchedulingSpec(reqVO.getTaskId(), reqVO.getSchedulingSpec()))
                .workItems(List.of(seed));
        if (StringUtils.hasText(previousRuntimeJobId)) {
            // 存量试排曾落 runtime：重排时 SOLVE 不把本任务上一轮占窗当作外部冲突
            builder.sourceRuntimeJobId(previousRuntimeJobId);
        }
        return builder.build();
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

    /**
     * 释放这一条任务留下的未执行占窗：草稿上的作业号，以及作业号没写回、只靠工作项号能认出来的残留。
     * <p>禁止：按设备或场站清掉别人的占窗。
     */
    private void releaseOwnUnexecutedOccupancy(
            Long taskId,
            String previousJobId,
            Long facilityId,
            SchedulingSpecDTO schedulingSpec,
            String reason) {
        OffsetDateTime from = horizonStart(schedulingSpec);
        OffsetDateTime to = horizonEnd(schedulingSpec);
        List<ScheduleSlotDTO> horizonSlots = runtimeQueryApi.listSlots(
                from, to, null, null, facilityId,
                List.of(SlotStatus.PLANNED, SlotStatus.IN_PROGRESS)).getCheckedData();
        Set<String> jobIds = new HashSet<>();
        if (StringUtils.hasText(previousJobId)) {
            jobIds.add(previousJobId.trim());
        }
        if (horizonSlots != null) {
            for (ScheduleSlotDTO slot : horizonSlots) {
                if (!PatrolTaskOwnOccupancy.isOwnSlot(taskId, previousJobId, slot)) {
                    continue;
                }
                if (StringUtils.hasText(slot.getRuntimeJobId())) {
                    jobIds.add(slot.getRuntimeJobId().trim());
                }
            }
        }
        for (String jobId : jobIds) {
            runtimeSlotWriteApi.releaseUnfinished(RuntimeSlotReleaseReqDTO.builder()
                    .runtimeJobId(jobId)
                    .mode(RuntimeSlotReleaseMode.ABORT)
                    .reason(reason)
                    .facilityId(facilityId)
                    .build()).checkError();
        }
    }

    private void releaseUnfinished(PatrolTaskPauseReqVO reqVO, RuntimeSlotReleaseMode mode) {
        String runtimeJobId = resolveRuntimeJobId(reqVO);
        runtimeSlotWriteApi.releaseUnfinished(RuntimeSlotReleaseReqDTO.builder()
                .runtimeJobId(runtimeJobId)
                .mode(mode)
                .reason(reqVO.getReason())
                .build()).checkError();
    }

    private String resolveRuntimeJobId(PatrolTaskPauseReqVO reqVO) {
        if (StringUtils.hasText(reqVO.getRuntimeJobId())) {
            return reqVO.getRuntimeJobId();
        }
        PatrolTaskDraft draft = requireTask(reqVO.getTaskId());
        if (!StringUtils.hasText(draft.runtimeJobId())) {
            throw exception(PATROL_FACADE_RUNTIME_JOB_REQUIRED);
        }
        return draft.runtimeJobId();
    }

    private void verifyCommitWindow(
            Long taskId, String runtimeJobId, SchedulingSpecDTO schedulingSpec, String previousJobId) {
        List<ScheduleSlotDTO> ownSlots = runtimeQueryApi.listSlotsByJobId(runtimeJobId).getCheckedData();
        if (CollectionUtils.isEmpty(ownSlots)) {
            throw exception(PATROL_FACADE_NO_ACTIVE_SLOTS);
        }
        boolean hasActive = ownSlots.stream().anyMatch(this::isActiveSlot);
        if (!hasActive) {
            throw exception(PATROL_FACADE_NO_ACTIVE_SLOTS);
        }

        OffsetDateTime from = horizonStart(schedulingSpec);
        OffsetDateTime to = horizonEnd(schedulingSpec);
        List<ScheduleSlotDTO> horizonSlots = runtimeQueryApi.listSlots(
                from, to, null, null, null, null).getCheckedData();
        if (CollectionUtils.isEmpty(horizonSlots)) {
            return;
        }

        for (ScheduleSlotDTO own : ownSlots) {
            if (!isActiveSlot(own)) {
                continue;
            }
            for (ScheduleSlotDTO other : horizonSlots) {
                if (other == null || !StringUtils.hasText(other.getRuntimeJobId())) {
                    continue;
                }
                // 本任务残留占窗（作业号或工作项号对得上）不是别人占用。
                if (PatrolTaskOwnOccupancy.isOwnSlot(taskId, runtimeJobId, previousJobId, other)) {
                    continue;
                }
                if (!isActiveSlot(other)) {
                    continue;
                }
                if (slotsOverlapOnSharedResource(own, other)) {
                    throw exception(PATROL_FACADE_OCCUPANCY_CONFLICT);
                }
            }
        }
    }

    private boolean isActiveSlot(ScheduleSlotDTO slot) {
        return slot != null
                && (SlotStatus.PLANNED.equals(slot.getSlotStatus())
                || SlotStatus.IN_PROGRESS.equals(slot.getSlotStatus()));
    }

    private boolean slotsOverlapOnSharedResource(ScheduleSlotDTO a, ScheduleSlotDTO b) {
        OffsetDateTime aStart = effectiveStart(a);
        OffsetDateTime aEnd = effectiveEnd(a);
        OffsetDateTime bStart = effectiveStart(b);
        OffsetDateTime bEnd = effectiveEnd(b);
        if (aStart == null || aEnd == null || bStart == null || bEnd == null) {
            return false;
        }
        if (!aEnd.isAfter(bStart) || !bEnd.isAfter(aStart)) {
            return false;
        }
        Set<String> aResources = resourceKeys(a.getAssignedResources());
        Set<String> bResources = resourceKeys(b.getAssignedResources());
        if (aResources.isEmpty() || bResources.isEmpty()) {
            return true;
        }
        for (String key : aResources) {
            if (bResources.contains(key)) {
                return true;
            }
        }
        return false;
    }

    private static Set<String> resourceKeys(List<AssignedResourceDTO> resources) {
        Set<String> keys = new HashSet<>();
        if (resources == null) {
            return keys;
        }
        for (AssignedResourceDTO resource : resources) {
            if (resource == null || !StringUtils.hasText(resource.getResourceId())) {
                continue;
            }
            String type = StringUtils.hasText(resource.getResourceType()) ? resource.getResourceType() : "";
            keys.add(type + ":" + resource.getResourceId());
        }
        return keys;
    }

    private static OffsetDateTime parseOffset(String text) {
        if (!StringUtils.hasText(text)) {
            return null;
        }
        return OffsetDateTime.parse(text, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private OffsetDateTime horizonStart(SchedulingSpecDTO spec) {
        LocalDate start = parseHorizonDate(spec != null ? spec.getHorizonStart() : null);
        if (start == null) {
            start = LocalDate.now(DEFAULT_ZONE);
        }
        return start.atStartOfDay(DEFAULT_ZONE).toOffsetDateTime();
    }

    private OffsetDateTime horizonEnd(SchedulingSpecDTO spec) {
        LocalDate end = parseHorizonDate(spec != null ? spec.getHorizonEnd() : null);
        if (end == null) {
            LocalDate start = parseHorizonDate(spec != null ? spec.getHorizonStart() : null);
            end = start != null ? start.plusWeeks(4) : LocalDate.now(DEFAULT_ZONE).plusWeeks(4);
        }
        return end.plusDays(1).atStartOfDay(DEFAULT_ZONE).toOffsetDateTime();
    }

    private static LocalDate parseHorizonDate(String text) {
        if (!StringUtils.hasText(text)) {
            return null;
        }
        return LocalDate.parse(text.substring(0, Math.min(text.length(), 10)));
    }

    /**
     * 智能编排前置条件：普通方式须已 saveRoute；固定摄像机跳过算路则不要求 stopIds。
     */
    /**
     * 智能编排处理方法只认总任务上已保存的排期策略；窗口日期仍可用请求里的排期模板范围。
     */
    private SchedulingSpecDTO resolveSchedulingSpec(Long taskId, SchedulingSpecDTO incoming) {
        PatrolTaskEntityStore.ArrangePolicy policy = patrolTaskEntityStore.readArrangePolicy(taskId);
        if (Boolean.TRUE.equals(policy.allowShiftExisting()) && policy.maxShiftMinutes() == null) {
            throw invalidParamException("允许挪动已有任务排期时，请填写最大挪动范围");
        }
        SchedulingSpecDTO.SchedulingSpecDTOBuilder builder = SchedulingSpecDTO.builder()
                .conflictStrategy(StringUtils.hasText(policy.conflictStrategy())
                        ? policy.conflictStrategy().trim()
                        : (Boolean.TRUE.equals(policy.allowShiftExisting()) ? "defer_slot" : "reject_batch"))
                .allowShiftExisting(Boolean.TRUE.equals(policy.allowShiftExisting()))
                .maxShiftMinutes(policy.maxShiftMinutes())
                .taskGapMinutes(policy.taskGapMinutes());
        if (incoming != null) {
            builder.mode(incoming.getMode())
                    .horizonStart(incoming.getHorizonStart())
                    .horizonEnd(incoming.getHorizonEnd());
        }
        return builder.build();
    }

    private PatrolTaskDraft requireTaskReadyForOrchestration(Long taskId) {
        PatrolTaskDraft draft = patrolTaskEntityStore.require(taskId);
        if ("FIXED_CAMERA".equals(draft.patrolExecutionMode())) {
            return draft;
        }
        if (!PatrolPlannedRouteSupport.hasSavedRoute(draft.plannedRoute())) {
            throw exception(PATROL_FACADE_ROUTE_NOT_SAVED);
        }
        return draft;
    }

    private PatrolTaskDraft requireTask(Long taskId) {
        return patrolTaskEntityStore.require(taskId);
    }

    /**
     * 第 3 步无冲突或智能编排时调用步骤图生成；本门面不编树。
     */
    private Object generateExecutionSteps(PatrolTaskDraft draft) {
        Map<String, Object> planned = PatrolPlannedRouteSupport.asPlannedMap(draft.plannedRoute());
        String startStopId = firstStopId(
                planned != null ? PatrolPlannedRouteSupport.asText(planned.get(RoutePayloadKeys.START_STOP_ID)) : null,
                draft.startStopId());
        String endStopId = firstStopId(
                planned != null ? PatrolPlannedRouteSupport.asText(planned.get(RoutePayloadKeys.END_STOP_ID)) : null,
                draft.endStopId());
        if (!StringUtils.hasText(endStopId) && StringUtils.hasText(startStopId)
                && planned != null && Boolean.TRUE.equals(planned.get(RoutePayloadKeys.RETURN_TO_START))) {
            endStopId = startStopId;
        }
        return taskStepTreeGenerateService.generate(draft.id(), null, startStopId, endStopId);
    }

    private static String firstStopId(String preferred, String fallback) {
        return StringUtils.hasText(preferred) ? preferred : fallback;
    }

    /**
     * 无冲突时按作业项已带的计划时刻落试排快照，不再进引擎求解。
     */
    private static List<ScheduleSlotDTO> slotsFromTemplate(List<WorkItemDTO> workItems) {
        List<ScheduleSlotDTO> slots = new ArrayList<>();
        for (WorkItemDTO item : workItems) {
            if (item == null) {
                continue;
            }
            TimePreferencesDTO prefs = item.getTimePreferences();
            if (prefs == null || CollectionUtils.isEmpty(prefs.getAllowedWindows())) {
                continue;
            }
            TimeWindowDTO window = prefs.getAllowedWindows().get(0);
            if (window == null || !StringUtils.hasText(window.getStart()) || !StringUtils.hasText(window.getEnd())) {
                continue;
            }
            slots.add(ScheduleSlotDTO.builder()
                    .contractVersion(ContractVersions.MVP)
                    .slotId(UUID.randomUUID().toString())
                    .workId(item.getWorkId())
                    .entityTypeCode(item.getEntityTypeCode())
                    .candidateStart(window.getStart())
                    .candidateEnd(window.getEnd())
                    .assignedResources(assignedFrom(item))
                    .lockState(SlotLockState.NONE)
                    .slotStatus(SlotStatus.PLANNED)
                    .build());
        }
        if (slots.isEmpty()) {
            throw exception(PATROL_FACADE_NO_ACTIVE_SLOTS);
        }
        return slots;
    }

    private static List<AssignedResourceDTO> assignedFrom(WorkItemDTO item) {
        if (item.getResourceRequirements() == null) {
            return List.of();
        }
        for (ResourceRequirementDTO req : item.getResourceRequirements()) {
            if (req == null || !StringUtils.hasText(req.getFixedResourceId())) {
                continue;
            }
            return List.of(AssignedResourceDTO.builder()
                    .resourceId(req.getFixedResourceId().trim())
                    .resourceType(req.getResourceType())
                    .build());
        }
        return List.of();
    }

    private void registerAutoStart(Long taskId, List<ScheduleSlotDTO> slots) {
        if (taskId == null || CollectionUtils.isEmpty(slots)) {
            return;
        }
        for (ScheduleSlotDTO slot : slots) {
            ResourceReservationDTO reservation = ScheduleSlotDTO.toReservation(slot);
            if (reservation == null) {
                continue;
            }
            patrolReservationAutoStartScheduler.register(reservation, taskId);
        }
    }

    private static OffsetDateTime effectiveStart(ScheduleSlotDTO slot) {
        OffsetDateTime planned = parseOffsetStatic(slot.getPlannedStart());
        if (planned != null) {
            return planned;
        }
        return parseOffsetStatic(slot.getCandidateStart());
    }

    private static OffsetDateTime effectiveEnd(ScheduleSlotDTO slot) {
        OffsetDateTime planned = parseOffsetStatic(slot.getPlannedEnd());
        if (planned != null) {
            return planned;
        }
        return parseOffsetStatic(slot.getCandidateEnd());
    }

    private static OffsetDateTime parseOffsetStatic(String text) {
        if (!StringUtils.hasText(text)) {
            return null;
        }
        return OffsetDateTime.parse(text, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private WorkItemDTO buildRoutePlanningWorkItem(PatrolRouteRunReqVO reqVO, Long taskId, boolean fromSavedRouteSnapshot) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put(FACILITY_ID, reqVO.getFacilityId());
        payload.put(OBJECT_IDS, reqVO.getObjectIds());
        if (reqVO.getPreferredNetworkRef() != null) {
            payload.put(PREFERRED_NETWORK_REF, reqVO.getPreferredNetworkRef());
        }
        if (taskId != null) {
            payload.put(TASK_ID, taskId);
        }
        if (StringUtils.hasText(reqVO.getStartStopId())) {
            payload.put(START_STOP_ID, reqVO.getStartStopId().trim());
        }
        if (StringUtils.hasText(reqVO.getEndStopId())) {
            payload.put(END_STOP_ID, reqVO.getEndStopId().trim());
        }
        if (reqVO.getReturnToStart() != null) {
            payload.put(RETURN_TO_START, reqVO.getReturnToStart());
        }
        if (!CollectionUtils.isEmpty(reqVO.getStopIds())) {
            payload.put(STOP_IDS, reqVO.getStopIds());
        }
        if (StringUtils.hasText(reqVO.getInspectionType())) {
            payload.put(INSPECTION_TYPE, reqVO.getInspectionType().trim());
        }
        payload.put(FROM_SAVED_ROUTE_SNAPSHOT, fromSavedRouteSnapshot);

        return WorkItemDTO.builder()
                .contractVersion(ContractVersions.MVP)
                .workId("patrol-seed-" + UUID.randomUUID())
                .payload(payload)
                .build();
    }
}
