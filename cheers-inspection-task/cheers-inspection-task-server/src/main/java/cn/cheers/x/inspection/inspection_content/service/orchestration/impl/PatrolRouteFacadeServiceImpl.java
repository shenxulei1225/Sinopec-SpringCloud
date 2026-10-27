package cn.cheers.x.inspection.inspection_content.service.orchestration.impl;

import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolOrchestrationRunReqVO.WizardPage;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolRouteRunReqVO;
import cn.cheers.x.inspection.orchestration.dto.PatrolScheduleMapReqDTO;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskUpdateReqVO;
import cn.cheers.x.inspection.task.service.task.PatrolItemActionDurationCalculator;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolOrchestrationRunReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolSlotWritebackReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolTaskPauseReqVO;
import cn.cheers.x.inspection.inspection_content.service.orchestration.PatrolRouteFacadeService;
import cn.cheers.x.inspection.task.service.execution.openrun.PatrolMissionTranslateService;
import cn.cheers.x.inspection.task.service.execution.openrun.PatrolOpenRunMaterializeService;
import cn.cheers.x.inspection.task.service.execution.scheduleboard.PatrolReservationAutoStartScheduler;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolInspectionConfigReadinessRespVO;
import cn.cheers.x.inspection.task.service.execution.steptree.PatrolInspectionConfigReadinessService;
import cn.cheers.x.inspection.task.service.execution.steptree.TaskStepTreeGenerateService;
import cn.cheers.x.module.platform.contract.dto.reservation.ResourceReservationDTO;
import cn.cheers.x.module.platform.contract.dto.runtime.RuntimeJobDTO;
import cn.cheers.x.module.platform.contract.enums.RuntimeJobStatus;
import cn.cheers.x.inspection.task.service.task.PatrolPlannedRouteSupport;
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
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_FACADE_NO_ACTIVE_SLOTS;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_FACADE_NOT_ARRANGED;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_FACADE_OCCUPANCY_CONFLICT;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_FACADE_ROUTE_NOT_SAVED;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_FACADE_ROUTE_PLAN_UNUSABLE;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_FACADE_RUNTIME_JOB_REQUIRED;

/**
 * 巡检组路线门面：路径规划、冲突检测、步骤图生成、智能编排解冲突。
 * <p>权威：页面路线随保存修改稿或更新任务写入；试排计划点 orchestrationPreview*；
 * 生成任务整段成功后才写入 runtimeJobId / orchestrationCommitted。中途失败收掉生成标记。
 * 向导上的开始编排先按这一页停靠点重算路径，再排计划点，路线只回到这一页。列表启用不带这一页，仍只读已保存路线。
 * 待执行账被清掉、计划点还在时，按还没开跑的计划点把账写回去，不重新排期。
 * 已经执行过看执行账。占窗完成但没有开跑账的，不算已执行，补写待执行。
 * <p>不负责：冲突算法本身（标准检测在 ScheduleConflictReporter）；读写旧固定表 inspection_task。
 * 不写已保存路线。已保存路线要等更新任务。
 */
@Service
@Validated
public class PatrolRouteFacadeServiceImpl implements PatrolRouteFacadeService {

    private static final Logger log = LoggerFactory.getLogger(PatrolRouteFacadeServiceImpl.class);

    /** 路径规划服务 ErrorCodeConstants 里 1_004_056_000 到 1_004_056_007 这一段。 */
    private static final int ROUTE_PLAN_ERROR_CODE_START = 1_004_056_000;
    private static final int ROUTE_PLAN_ERROR_CODE_END = 1_004_056_100;

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

    /** 自动：优化检查点先后。手工、以及按已保存顺序重算走线：不改先后。 */
    private static final String STRATEGY_AS_GIVEN = "as_given";

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
    private PatrolMissionTranslateService patrolMissionTranslateService;

    @Resource
    private TaskStepTreeGenerateService taskStepTreeGenerateService;

    @Resource
    private PatrolInspectionConfigReadinessService patrolInspectionConfigReadinessService;

    @Resource
    private PatrolReservationAutoStartScheduler patrolReservationAutoStartScheduler;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private PatrolScheduleConflictDetectService patrolScheduleConflictDetectService;

    @Resource
    private PatrolItemActionDurationCalculator itemActionDurationCalculator;

    @Override
    public ScheduleConflictReportDTO detectScheduleConflicts(PatrolOrchestrationRunReqVO reqVO) {
        PatrolTaskDraft working = resolveWorkingDraft(reqVO);
        PatrolScheduleConflictDetectService.DetectedPlan detected =
                patrolScheduleConflictDetectService.detect(working, reqVO.getSchedulingSpec());
        ScheduleConflictReportDTO report = detected.report();
        if (report != null && !Boolean.TRUE.equals(report.getHasConflict())) {
            report.setSlots(collectPlanTimeSlots(detected.workItems()));
        }
        return report;
    }

    @Override
    public PatrolInspectionConfigReadinessRespVO validateInspectionConfig(PatrolOrchestrationRunReqVO reqVO) {
        PatrolTaskDraft working = resolveWorkingDraft(reqVO);
        return patrolInspectionConfigReadinessService.evaluate(working);
    }

    @Override
    public ScheduleRunResponse composeExecutionStepTree(PatrolOrchestrationRunReqVO reqVO) {
        PatrolTaskDraft working = resolveWorkingDraft(reqVO);
        requireConfigReady(working);
        Object executionStepTree = composeExecutionSteps(working);
        return ScheduleRunResponse.builder()
                .contractVersion(ContractVersions.MVP)
                .executionStepTree(executionStepTree)
                .build();
    }

    @Override
    public ScheduleRunResponse previewPageInstructions(PatrolOrchestrationRunReqVO reqVO) {
        // 指令只读页面上已经生成的步骤图，试译一次。不重新编树，不写出计划点，不写草稿，不替换已发布步骤图。
        // 时间重叠不参与这一步。页面上已有计划点时，只用第一个点分配收流地址，各点指令相同。
        Map<String, Object> executionStepTree = reqVO.getExecutionStepTree();
        if (executionStepTree == null || executionStepTree.isEmpty()) {
            throw invalidParamException("还没有步骤图，不能生成指令");
        }
        PatrolTaskDraft working = resolveWorkingDraft(reqVO);
        requireConfigReady(working);
        List<String> slotIds = reqVO.getSlots() == null ? List.of() : reqVO.getSlots().stream()
                .map(ScheduleSlotDTO::getSlotId)
                .filter(StringUtils::hasText)
                .map(String::trim)
                .toList();
        patrolMissionTranslateService.assertPreviewTranslate(working, executionStepTree, slotIds);
        return ScheduleRunResponse.builder()
                .contractVersion(ContractVersions.MVP)
                .plainSummary("指令已按这一页的步骤图生成，没有写入任务。")
                .executionStepTree(executionStepTree)
                .build();
    }

    /**
     * 按停靠点算一条路线。算出可用路线就返回。
     * 规划服务拒绝停靠点、路网走不到，或没有返回路线时，交回「路线不可用」。
     * 其它失败原样抛出。不改写成向导步骤的提示。
     */
    @Override
    public ScheduleRunResponse previewRoute(PatrolRouteRunReqVO reqVO) {
        if (CollectionUtils.isEmpty(reqVO.getStopIds())) {
            throw invalidParamException("开始规划缺少停靠点");
        }
        ScheduleRunResponse routed;
        try {
            routed = scheduleRunApi.run(ScheduleRunRequest.builder()
                    .contractVersion(ContractVersions.MVP)
                    .orchestrationRef(OrchestrationRefs.PATROL_ROUTE_PREVIEW_V1)
                    .dryRun(true)
                    .stopAfterPhase(OrchestrationPhase.ROUTE.name())
                    .schedulingSpec(PLACEHOLDER_SCHEDULING_SPEC)
                    .workItems(List.of(buildRoutePlanningWorkItem(reqVO, reqVO.getTaskId(), false)))
                    .build()).getCheckedData();
        } catch (ServiceException ex) {
            if (!isRouteCalculationFailure(ex)) {
                throw ex;
            }
            log.warn("路径没有算出可用路线: {}", ex.getMessage());
            throw exception(PATROL_FACADE_ROUTE_PLAN_UNUSABLE);
        }
        Map<String, Object> planned = plannedRouteOf(routed);
        if (planned == null || planned.isEmpty()) {
            log.warn("路径没有算出可用路线");
            throw exception(PATROL_FACADE_ROUTE_PLAN_UNUSABLE);
        }
        return routed;
    }

    /**
     * 这次失败是路径没有算出可用路线。
     * 只认规划服务那一段错误码，以及规划时的参数不正确（停靠点对不上当前方式、路网走不到）。
     */
    private static boolean isRouteCalculationFailure(ServiceException ex) {
        Integer code = ex.getCode();
        if (code == null) {
            return false;
        }
        // 错误码是 Integer。400 超出缓存，先取出数值再比。
        int value = code;
        if (value == GlobalErrorCodeConstants.BAD_REQUEST.getCode()) {
            return true;
        }
        return value >= ROUTE_PLAN_ERROR_CODE_START && value < ROUTE_PLAN_ERROR_CODE_END;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScheduleRunResponse previewOrchestration(PatrolOrchestrationRunReqVO reqVO) {
        if (reqVO.getPage() != null) {
            // 向导上的开始编排：先按这一页的停靠点重算路径，再用新的路径耗时排计划。不写已保存路线，不释放占用。
            ScheduleRunResponse routed = refreshWizardRoute(reqVO);
            PatrolTaskDraft working = resolveWorkingDraft(reqVO);
            requireConfigReady(working);
            ScheduleRunResponse response = scheduleRunApi.run(
                    buildOrchestrationRunRequest(reqVO, working, working.runtimeJobId(), true)).getCheckedData();
            if (CollectionUtils.isEmpty(response.getSlots())) {
                throw exception(PATROL_FACADE_NO_ACTIVE_SLOTS);
            }
            // 开始编排只把计划点和重算后的路线放回这一页。指令由单独一步试译。
            return ScheduleRunResponse.builder()
                    .contractVersion(response.getContractVersion())
                    .slots(response.getSlots())
                    .shiftedSlots(response.getShiftedSlots())
                    .plainSummary(response.getPlainSummary())
                    .workItems(response.getWorkItems())
                    .routePreview(routed == null ? response.getRoutePreview() : routed.getRoutePreview())
                    .plannedRoute(reqVO.getPage().getPlannedRoute())
                    .executionStepTree(providedStepTree(reqVO))
                    .build();
        }
        PatrolTaskDraft draft = requireTaskReadyForOrchestration(reqVO.getTaskId());
        requireConfigReady(draft);
        String previousRuntimeJobId = draft.runtimeJobId();
        if (!Boolean.TRUE.equals(draft.orchestrationCommitted())) {
            // 未生成任务：智能编排前先让开本任务留下的未执行占窗，不能只认草稿上那一个作业号。
            releaseOwnUnexecutedOccupancy(
                    reqVO.getTaskId(),
                    previousRuntimeJobId,
                    draft.facilityId(),
                    resolveSchedulingSpec(reqVO),
                    "previewOrchestration: replace previous trial for same task");
            if (StringUtils.hasText(previousRuntimeJobId)) {
                patrolTaskEntityStore.clearOrchestrationTrialState(reqVO.getTaskId());
            }
        }
        ScheduleRunResponse response = scheduleRunApi.run(
                buildOrchestrationRunRequest(reqVO, draft, previousRuntimeJobId, true)).getCheckedData();
        if (CollectionUtils.isEmpty(response.getSlots())) {
            throw exception(PATROL_FACADE_NO_ACTIVE_SLOTS);
        }
        Object executionStepTree = generateExecutionSteps(draft);
        PatrolTaskDraft draftWithTree = PatrolMissionTranslateService.withStepTree(draft, executionStepTree);
        patrolMissionTranslateService.assertPreviewTranslate(
                draftWithTree, executionStepTree, slotIdsOf(response.getSlots()));
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
        Long taskId = reqVO.getTaskId();
        // 先记下是不是已经发布。后面成功才把当前方式写进草稿。第一次生成不写草稿。
        boolean updatingPublished = taskId != null
                && Boolean.TRUE.equals(patrolTaskEntityStore.require(taskId).orchestrationCommitted());
        if (reqVO.getPage() != null) {
            // 生成任务 / 更新任务：把这一页当前方式写入已发布字段，占用的是页面上已经算出的计划点。
            writePublishedFromPage(reqVO);
        }
        boolean replacing = false;
        try {
            PatrolTaskDraft draft = requireTaskReadyForOrchestration(taskId);
            // 计划点必须已经在这一页上，并由写入已发布时放进试排。这里不再检测、不再重算。
            draft = ensurePreviewBeforeRelease(draft);
            String previousJobId = draft.runtimeJobId();
            replacing = true;
            // 本任务未执行占窗先让开：草稿上的作业号，以及失败留下、作业号没写回草稿的同任务窗口。
            releaseOwnUnexecutedOccupancy(
                    taskId,
                    previousJobId,
                    draft.facilityId(),
                    resolveSchedulingSpec(reqVO),
                    "generateTask: replace unexecuted windows of the same task");

            String runtimeJobId = commitArrangePreviewSnapshot(reqVO, draft, previousJobId);
            List<ScheduleSlotDTO> slots = runtimeQueryApi.listSlotsByJobId(runtimeJobId).getCheckedData();
            if (CollectionUtils.isEmpty(slots)) {
                throw exception(PATROL_FACADE_NOT_ARRANGED);
            }
            finishGenerateTaskCommit(taskId, slots, true);
            registerAutoStart(taskId, slots);
            patrolTaskEntityStore.writeOrchestrationCommittedState(taskId, runtimeJobId, Boolean.TRUE);
            if (updatingPublished) {
                syncSavedDraft(reqVO);
            }
            return ScheduleRunResponse.builder()
                    .contractVersion(ContractVersions.MVP)
                    .runtimeJobId(runtimeJobId)
                    .slots(slots)
                    .build();
        } catch (RuntimeException ex) {
            if (replacing && taskId != null) {
                try {
                    patrolTaskEntityStore.clearGeneratedMark(taskId);
                } catch (RuntimeException clearEx) {
                    ex.addSuppressed(clearEx);
                }
            }
            throw ex;
        }
    }

    /**
     * 已生成任务的待执行账不在了，按已经占好、还没开跑的计划点把账写回去。
     * 不重新排期，不换作业号。还在的待执行账不删。已经开跑或已经结束的执行账不另写一条待执行。
     * 占窗标成完成但没有开跑账的，先改回计划中，再写待执行。图层按这份待执行显示还没开始。
     * 只有还没到的计划时刻才登记到点开跑，已经过去的时刻不立刻开跑。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rewriteMissingPendingLedgers(Long taskId) {
        PatrolTaskDraft task = patrolTaskEntityStore.require(taskId);
        if (!Boolean.TRUE.equals(task.orchestrationCommitted())
                || !StringUtils.hasText(task.runtimeJobId())) {
            throw invalidParamException("这条任务还没有生成，不能补写待执行账");
        }
        List<ScheduleSlotDTO> slots = runtimeQueryApi.listSlotsByJobId(task.runtimeJobId().trim()).getCheckedData();
        if (CollectionUtils.isEmpty(slots)) {
            throw exception(PATROL_FACADE_NOT_ARRANGED);
        }
        List<ScheduleSlotDTO> planned = new ArrayList<>();
        List<ScheduleSlotDTO> upcoming = new ArrayList<>();
        OffsetDateTime now = OffsetDateTime.now(DEFAULT_ZONE);
        for (ScheduleSlotDTO slot : slots) {
            if (slot == null || !StringUtils.hasText(slot.getSlotId())) {
                continue;
            }
            if (patrolOpenRunMaterializeService.hasStartedLedger(taskId, slot.getSlotId())) {
                continue;
            }
            if (SlotStatus.COMPLETED.equals(slot.getSlotStatus())) {
                resetLeftoverCompletedOccupancy(slot);
                slot.setSlotStatus(SlotStatus.PLANNED);
            } else if (!SlotStatus.PLANNED.equals(slot.getSlotStatus())) {
                continue;
            }
            planned.add(slot);
            OffsetDateTime start = effectiveStart(slot);
            if (start != null && start.isAfter(now)) {
                upcoming.add(slot);
            }
        }
        if (planned.isEmpty()) {
            return;
        }
        patrolOpenRunMaterializeService.writeMissingPendingLedgers(task, planned);
        registerAutoStart(taskId, upcoming);
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
                    resolveSchedulingSpec(reqVO),
                    previousJobId,
                    reqVO.getShiftedSlots());
            writeShiftedOccupancy(reqVO.getShiftedSlots(), draft.facilityId());
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
     * 生成任务（commitOrchestration）固定顺序：占窗已定稿后 → 写每计划点待执行 → 登记到点 → 才标记已生成。
     * 标记若写在登记之前，后面失败时远程写入收不回，列表会留下已启用。
     * <p>不负责：试排预览、执行任务下发；禁止在物化/调度模块内互相调用，只在本门面按序编排。
     */
    private void finishGenerateTaskCommit(
            Long taskId, List<ScheduleSlotDTO> slots, boolean clearPreview) {
        PatrolTaskDraft task = patrolTaskEntityStore.require(taskId);
        patrolOpenRunMaterializeService.materializeAllSlots(task, slots);
        if (clearPreview) {
            patrolTaskEntityStore.clearOrchestrationPreview(taskId);
        }
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
     * 写下的是这一页新计划，状态固定计划中。不把旧占窗的完成态和实测结束时刻抄进来。
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
                    .lockState(SlotLockState.NONE)
                    .slotStatus(SlotStatus.PLANNED)
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
        WizardPage page = reqVO.getPage();
        if (page != null) {
            payload.put("pagePlannedRoute", page.getPlannedRoute());
            payload.put("pageScheduleConfig", page.getScheduleConfig());
            payload.put("pageInspectionContent", page.getInspectionContent());
            payload.put("pageExecutionDeviceBinding", page.getExecutionDeviceBinding());
            payload.put("pagePatrolExecutionMode", page.getPatrolExecutionMode());
            payload.put("pageStartStopId", page.getStartStopId());
            payload.put("pageEndStopId", page.getEndStopId());
        }
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
                .schedulingSpec(resolveSchedulingSpec(reqVO))
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
     * 写入之前，这一页的计划点必须已经在试排里。
     * 没有试排就拒绝，并且此时还没有释放占窗。不在这里再做冲突检测，也不按原时间重算计划点。
     */
    private PatrolTaskDraft ensurePreviewBeforeRelease(PatrolTaskDraft draft) {
        if (hasOrchestrationPreview(draft)) {
            return draft;
        }
        throw exception(PATROL_FACADE_NOT_ARRANGED);
    }

    /**
     * 释放这一条任务留下的未执行占窗：草稿上的作业号，以及作业号没写回、只靠工作项号能认出来的残留。
     * 占窗标成完成但没有开跑执行账的，同样中止。已经开跑或已经结束的执行账对应占窗留下。
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
                List.of(SlotStatus.PLANNED, SlotStatus.IN_PROGRESS, SlotStatus.COMPLETED)).getCheckedData();
        Set<String> jobIds = new HashSet<>();
        if (StringUtils.hasText(previousJobId)) {
            jobIds.add(previousJobId.trim());
        }
        if (horizonSlots != null) {
            for (ScheduleSlotDTO slot : horizonSlots) {
                if (!PatrolTaskOwnOccupancy.isOwnSlot(taskId, previousJobId, slot)) {
                    continue;
                }
                if (SlotStatus.COMPLETED.equals(slot.getSlotStatus())
                        && !patrolOpenRunMaterializeService.hasStartedLedger(taskId, slot.getSlotId())) {
                    resetLeftoverCompletedOccupancy(slot);
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

    /**
     * 占窗标成完成、却没有开跑执行账：这是残留，不是已执行。
     * 改回计划中并清掉错误的实测结束时刻，随后按未执行占窗处理。
     */
    private void resetLeftoverCompletedOccupancy(ScheduleSlotDTO slot) {
        if (slot == null || !StringUtils.hasText(slot.getSlotId())) {
            return;
        }
        runtimeSlotWriteApi.updateSlotStatus(RuntimeSlotStatusUpdateReqDTO.builder()
                .slotId(slot.getSlotId().trim())
                .slotStatus(SlotStatus.PLANNED)
                .reason("leftover occupancy marked completed without execution ledger")
                .build()).checkError();
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

    /**
     * 生成任务时，把页面上已经算出的已确认任务新时间写回原占窗。不新建一条，也不改它们的作业号。
     */
    private void writeShiftedOccupancy(List<ScheduleSlotDTO> shiftedSlots, Long facilityId) {
        if (CollectionUtils.isEmpty(shiftedSlots)) {
            return;
        }
        Map<String, List<ScheduleSlotDTO>> byJob = new LinkedHashMap<>();
        for (ScheduleSlotDTO slot : shiftedSlots) {
            if (slot == null || !StringUtils.hasText(slot.getRuntimeJobId()) || !StringUtils.hasText(slot.getSlotId())) {
                continue;
            }
            byJob.computeIfAbsent(slot.getRuntimeJobId().trim(), key -> new ArrayList<>()).add(slot);
        }
        for (Map.Entry<String, List<ScheduleSlotDTO>> entry : byJob.entrySet()) {
            runtimePersistApi.persist(RuntimePersistReqDTO.builder()
                    .appendSlotsOnly(true)
                    .job(RuntimeJobDTO.builder().runtimeJobId(entry.getKey()).build())
                    .slots(entry.getValue())
                    .facilityId(facilityId)
                    .build()).checkError();
        }
    }

    private static ScheduleSlotDTO overlayShift(ScheduleSlotDTO stored, List<ScheduleSlotDTO> shiftedSlots) {
        if (stored == null || CollectionUtils.isEmpty(shiftedSlots) || !StringUtils.hasText(stored.getSlotId())) {
            return stored;
        }
        for (ScheduleSlotDTO shifted : shiftedSlots) {
            if (shifted != null && stored.getSlotId().equals(shifted.getSlotId())) {
                return shifted;
            }
        }
        return stored;
    }

    private void verifyCommitWindow(
            Long taskId, String runtimeJobId, SchedulingSpecDTO schedulingSpec, String previousJobId,
            List<ScheduleSlotDTO> shiftedSlots) {
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
                if (slotsOverlapOnSharedResource(own, overlayShift(other, shiftedSlots))) {
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
     * 向导带了这一页：挪动策略认这一页上刚填的。没带这一页（列表启用）才认已发布字段上的策略。
     * 窗口日期始终认请求里的排期范围。
     */
    private SchedulingSpecDTO resolveSchedulingSpec(PatrolOrchestrationRunReqVO reqVO) {
        Boolean allowShift;
        Integer maxShiftMinutes;
        Integer taskGapMinutes;
        String conflictStrategy;
        WizardPage page = reqVO.getPage();
        if (page != null) {
            allowShift = page.getAllowShiftExisting();
            maxShiftMinutes = page.getMaxShiftMinutes();
            taskGapMinutes = page.getTaskGapMinutes();
            conflictStrategy = page.getConflictStrategy();
        } else {
            PatrolTaskEntityStore.ArrangePolicy policy = patrolTaskEntityStore.readArrangePolicy(reqVO.getTaskId());
            allowShift = policy.allowShiftExisting();
            maxShiftMinutes = policy.maxShiftMinutes();
            taskGapMinutes = policy.taskGapMinutes();
            conflictStrategy = policy.conflictStrategy();
        }
        if (Boolean.TRUE.equals(allowShift) && maxShiftMinutes == null) {
            throw invalidParamException("允许挪动已有任务排期时，请填写最大挪动范围");
        }
        SchedulingSpecDTO incoming = reqVO.getSchedulingSpec();
        // 向导这一页没再带旧的「允许挪动」开关。填了单侧分钟数就按空窗口顺序编排，不能因开关缺失写成拒绝本批。
        String resolvedStrategy = StringUtils.hasText(conflictStrategy)
                ? conflictStrategy.trim()
                : (page != null || Boolean.TRUE.equals(allowShift) ? "defer_slot" : "reject_batch");
        SchedulingSpecDTO.SchedulingSpecDTOBuilder builder = SchedulingSpecDTO.builder()
                .conflictStrategy(resolvedStrategy)
                .allowShiftExisting(page != null || Boolean.TRUE.equals(allowShift))
                .maxShiftMinutes(maxShiftMinutes)
                .taskGapMinutes(taskGapMinutes);
        if (incoming != null) {
            builder.mode(incoming.getMode())
                    .horizonStart(incoming.getHorizonStart())
                    .horizonEnd(incoming.getHorizonEnd());
        }
        return builder.build();
    }

    private PatrolTaskDraft requireTaskReadyForOrchestration(Long taskId) {
        return requireRouteReady(patrolTaskEntityStore.require(taskId));
    }

    /**
     * 开始编排前按这一页已经选中的停靠点重算路径，把新的路径耗时放回这一页。
     * 固定摄像机不算路。不调用保存路线，已保存路线要等更新任务才写。
     * 没有停靠点或起终点时直接失败，不沿用上一次的路径耗时。
     * 路径结果只转交。路线不可用由路径规划交回，这里不改写原文，也不把其它失败收成同一种。
     */
    private ScheduleRunResponse refreshWizardRoute(PatrolOrchestrationRunReqVO reqVO) {
        WizardPage page = reqVO.getPage();
        PatrolTaskDraft stored = patrolTaskEntityStore.require(reqVO.getTaskId());
        String mode = StringUtils.hasText(page.getPatrolExecutionMode())
                ? page.getPatrolExecutionMode().trim()
                : stored.patrolExecutionMode();
        if ("FIXED_CAMERA".equals(mode)) {
            return null;
        }
        Map<String, Object> current = page.getPlannedRoute();
        if (current == null || current.isEmpty()) {
            current = PatrolPlannedRouteSupport.asPlannedMap(stored.plannedRoute());
        }
        List<String> stopIds = stringList(current == null ? null : current.get("stopIds"));
        if (stopIds.isEmpty()) {
            throw invalidParamException("请先开始规划");
        }
        String start = firstText(page.getStartStopId(), textOf(current, RoutePayloadKeys.START_STOP_ID), stored.startStopId());
        String end = firstText(page.getEndStopId(), textOf(current, RoutePayloadKeys.END_STOP_ID), stored.endStopId());
        if (!StringUtils.hasText(start) || !StringUtils.hasText(end)) {
            throw invalidParamException("请先在任务中设置起点和终点");
        }
        InspectionContent content = page.getInspectionContent() != null
                ? page.getInspectionContent() : stored.inspectionContent();
        List<Long> objectIds = objectIdsOf(content);
        if (objectIds.isEmpty()) {
            throw invalidParamException("请先选择巡检对象");
        }
        PatrolRouteRunReqVO routeReq = new PatrolRouteRunReqVO();
        routeReq.setTaskId(reqVO.getTaskId());
        routeReq.setFacilityId(page.getFacilityId() != null ? page.getFacilityId() : stored.facilityId());
        routeReq.setObjectIds(objectIds);
        routeReq.setStopIds(stopIds);
        routeReq.setStartStopId(start);
        routeReq.setEndStopId(end);
        routeReq.setReturnToStart(start.equals(end));
        routeReq.setInspectionType(PatrolScheduleMapServiceImpl.normalizeInspectionType(mode));
        if (current != null && current.get("networkRef") != null) {
            routeReq.setPreferredNetworkRef(String.valueOf(current.get("networkRef")));
        }
        routeReq.setStrategy(STRATEGY_AS_GIVEN);
        ScheduleRunResponse routed = previewRoute(routeReq);
        page.setPlannedRoute(plannedRouteOf(routed));
        return routed;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> plannedRouteOf(ScheduleRunResponse routed) {
        if (routed == null || CollectionUtils.isEmpty(routed.getWorkItems())) {
            return null;
        }
        Map<String, Object> payload = routed.getWorkItems().get(0).getPayload();
        if (payload == null) {
            return null;
        }
        Object raw = payload.get(RoutePayloadKeys.PLANNED_ROUTE);
        if (raw instanceof Map<?, ?> map) {
            return new LinkedHashMap<>((Map<String, Object>) map);
        }
        return null;
    }

    private static List<Long> objectIdsOf(InspectionContent content) {
        List<Long> ids = new ArrayList<>();
        if (content == null) {
            return ids;
        }
        collectObjectIds(content.getCustomObjects(), ids);
        if (content.getGroups() != null) {
            for (InspectionContent.ObjectGroup group : content.getGroups()) {
                collectObjectIds(group.getObjects(), ids);
            }
        }
        return ids;
    }

    private static void collectObjectIds(List<InspectionContent.ObjectContent> objects, List<Long> ids) {
        if (objects == null) {
            return;
        }
        for (InspectionContent.ObjectContent object : objects) {
            if (object != null && object.getObjectId() != null) {
                ids.add(object.getObjectId());
            }
        }
    }

    private static List<String> stringList(Object raw) {
        if (!(raw instanceof List<?> list)) {
            return List.of();
        }
        List<String> values = new ArrayList<>();
        for (Object row : list) {
            if (row != null && StringUtils.hasText(String.valueOf(row))) {
                values.add(String.valueOf(row).trim());
            }
        }
        return values;
    }

    private static String textOf(Map<String, Object> map, String key) {
        if (map == null || key == null) {
            return null;
        }
        Object raw = map.get(key);
        if (raw == null) {
            return null;
        }
        String text = String.valueOf(raw).trim();
        return text.isEmpty() ? null : text;
    }

    private static String firstText(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value.trim();
            }
        }
        return null;
    }

    /**
     * 向导请求带这一页时，计划点按这一页算。不带时读已发布字段（列表启用）。
     * 普通方式必须已经有路线结果；固定摄像机不要求路线。
     */
    private PatrolTaskDraft resolveWorkingDraft(PatrolOrchestrationRunReqVO reqVO) {
        PatrolTaskDraft stored = patrolTaskEntityStore.require(reqVO.getTaskId());
        if (reqVO.getPage() == null) {
            return requireRouteReady(stored);
        }
        WizardPage page = reqVO.getPage();
        PatrolScheduleMapReqDTO pageReq = new PatrolScheduleMapReqDTO();
        pageReq.setPageInspectionContent(page.getInspectionContent());
        pageReq.setPagePlannedRoute(page.getPlannedRoute());
        pageReq.setPageScheduleConfig(page.getScheduleConfig());
        pageReq.setPageExecutionDeviceBinding(page.getExecutionDeviceBinding());
        pageReq.setPagePatrolExecutionMode(page.getPatrolExecutionMode());
        pageReq.setPageStartStopId(page.getStartStopId());
        pageReq.setPageEndStopId(page.getEndStopId());
        return requireRouteReady(PatrolWizardPageDraft.overlay(stored, pageReq, itemActionDurationCalculator));
    }

    private PatrolTaskDraft requireRouteReady(PatrolTaskDraft draft) {
        if ("FIXED_CAMERA".equals(draft.patrolExecutionMode())) {
            return draft;
        }
        if (!PatrolPlannedRouteSupport.hasSavedRoute(draft.plannedRoute())) {
            throw exception(PATROL_FACADE_ROUTE_NOT_SAVED);
        }
        return draft;
    }

    /**
     * 生成任务或更新任务：当前方式从这一页写入已发布字段，并记下页面上已经生成的计划点和步骤图。
     * 本方法不写草稿。更新任务在整段成功后再把当前方式写入已保存草稿。
     */
    private void writePublishedFromPage(PatrolOrchestrationRunReqVO reqVO) {
        if (CollectionUtils.isEmpty(reqVO.getSlots())) {
            throw exception(PATROL_FACADE_NOT_ARRANGED);
        }
        WizardPage page = reqVO.getPage();
        Long taskId = reqVO.getTaskId();
        InspectionTaskUpdateReqVO update = new InspectionTaskUpdateReqVO();
        update.setId(taskId);
        update.setTaskName(page.getTaskName());
        // 所属设施在这一页上。不带上的话，任务更新会因必填的所属设施为空被拒绝。
        update.setFacilityId(page.getFacilityId());
        update.setPatrolExecutionMode(page.getPatrolExecutionMode());
        update.setInspectionContent(page.getInspectionContent());
        update.setExecutionDeviceBinding(page.getExecutionDeviceBinding());
        update.setScheduleConfig(page.getScheduleConfig());
        update.setStartStopId(page.getStartStopId());
        update.setEndStopId(page.getEndStopId());
        update.setApplyToPublished(Boolean.TRUE);
        // 检查项动作耗时合计写动作库现在算出的分钟。算不出来就不写，不用步骤上已有的分钟顶上。
        if (page.getInspectionContent() != null
                && itemActionDurationCalculator != null
                && StringUtils.hasText(page.getPatrolExecutionMode())) {
            Long executionEquipmentId = page.getExecutionDeviceBinding() == null
                    ? null
                    : page.getExecutionDeviceBinding().getEquipmentId();
            itemActionDurationCalculator
                    .computeLiveSecondsForEquipment(
                            page.getInspectionContent(),
                            page.getPatrolExecutionMode().trim(),
                            executionEquipmentId)
                    .ifPresent(page.getInspectionContent()::setItemActionDurationMinutes);
        }
        patrolTaskEntityStore.updateDraft(update);
        if (page.getPlannedRoute() != null && !page.getPlannedRoute().isEmpty()) {
            patrolTaskEntityStore.writePlannedRoute(taskId, page.getPlannedRoute());
        }
        if (reqVO.getExecutionStepTree() != null && !reqVO.getExecutionStepTree().isEmpty()) {
            patrolTaskEntityStore.writeStepTree(taskId, reqVO.getExecutionStepTree());
        }
        patrolTaskEntityStore.writeOrchestrationPreview(
                taskId, reqVO.getSlots(), "按这一页已生成的计划点写入。");
    }

    /**
     * 更新任务成功后：已保存草稿里当前这一种巡检方式换成刚发布的这一页。
     * 草稿里其他巡检方式留下。第一次生成任务不调用本方法。
     */
    private void syncSavedDraft(PatrolOrchestrationRunReqVO reqVO) {
        Map<String, Object> incoming = reqVO.getSavedDraft();
        if (incoming == null || incoming.isEmpty()) {
            incoming = snapshotFromPublishedPage(reqVO);
        }
        Map<String, Object> existing = patrolTaskEntityStore.readUnpublishedModification(reqVO.getTaskId());
        Map<String, Object> merged = new LinkedHashMap<>();
        if (existing != null) {
            merged.putAll(existing);
        }
        merged.putAll(incoming);
        merged.put("modes", mergeDraftModes(existing, incoming, currentMode(reqVO)));
        patrolTaskEntityStore.saveUnpublishedModification(reqVO.getTaskId(), merged);
    }

    /**
     * 页面没带草稿快照时，用刚写入已发布的这一页拼一份。
     * 只含当前方式。其他方式由合并时从原草稿留下。
     */
    private static Map<String, Object> snapshotFromPublishedPage(PatrolOrchestrationRunReqVO reqVO) {
        WizardPage page = reqVO.getPage();
        Map<String, Object> snapshot = new LinkedHashMap<>();
        if (page == null) {
            return snapshot;
        }
        if (StringUtils.hasText(page.getTaskName())) {
            snapshot.put("taskName", page.getTaskName());
        }
        if (page.getFacilityId() != null) {
            snapshot.put("facilityId", page.getFacilityId());
        }
        if (StringUtils.hasText(page.getPatrolExecutionMode())) {
            snapshot.put("patrolExecutionMode", page.getPatrolExecutionMode());
        }
        if (page.getInspectionContent() != null) {
            snapshot.put("inspectionContent", page.getInspectionContent());
        }
        if (page.getExecutionDeviceBinding() != null) {
            snapshot.put("executionDeviceBinding", page.getExecutionDeviceBinding());
        }
        if (page.getScheduleConfig() != null) {
            snapshot.put("scheduleConfig", page.getScheduleConfig());
        }
        if (page.getPlannedRoute() != null && !page.getPlannedRoute().isEmpty()) {
            snapshot.put("plannedRoute", page.getPlannedRoute());
        }
        if (StringUtils.hasText(page.getStartStopId())) {
            snapshot.put("startStopId", page.getStartStopId());
        }
        if (StringUtils.hasText(page.getEndStopId())) {
            snapshot.put("endStopId", page.getEndStopId());
        }
        if (reqVO.getExecutionStepTree() != null && !reqVO.getExecutionStepTree().isEmpty()) {
            snapshot.put("executionStepTree", reqVO.getExecutionStepTree());
        }
        if (reqVO.getSlots() != null && !reqVO.getSlots().isEmpty()) {
            snapshot.put("slots", reqVO.getSlots());
        }
        return snapshot;
    }

    /**
     * 当前方式用这一页的切片盖掉。其他方式留在原草稿里。
     * 页面没带各种方式时，去掉当前方式的旧切片，打开草稿就用刚写入的这一页。
     */
    private static Map<String, Object> mergeDraftModes(
            Map<String, Object> existing, Map<String, Object> incoming, String currentMode) {
        Map<String, Object> modes = new LinkedHashMap<>();
        copyModeMap(modes, existing == null ? null : existing.get("modes"));
        Object incomingModes = incoming.get("modes");
        if (incomingModes instanceof Map<?, ?>) {
            copyModeMap(modes, incomingModes);
        } else if (StringUtils.hasText(currentMode)) {
            modes.remove(currentMode.trim());
        }
        return modes;
    }

    private static void copyModeMap(Map<String, Object> target, Object raw) {
        if (!(raw instanceof Map<?, ?> source)) {
            return;
        }
        source.forEach((key, value) -> {
            if (key != null) {
                target.put(String.valueOf(key), value);
            }
        });
    }

    private static String currentMode(PatrolOrchestrationRunReqVO reqVO) {
        WizardPage page = reqVO.getPage();
        return page == null ? null : page.getPatrolExecutionMode();
    }

    private PatrolTaskDraft requireTask(Long taskId) {
        return patrolTaskEntityStore.require(taskId);
    }

    /**
     * 第 3 步无冲突或智能编排时调用步骤图生成；本门面不编树。
     */
    /** 页面已经生成的步骤图。没有则空，调用方再编。禁止在这里重新编一棵。 */
    private static Map<String, Object> providedStepTree(PatrolOrchestrationRunReqVO reqVO) {
        Map<String, Object> tree = reqVO.getExecutionStepTree();
        if (tree == null || tree.isEmpty()) {
            return null;
        }
        return tree;
    }

    /** 编出步骤图放进响应，不写任务。向导排期和向导上的智能编排用这个。 */
    private Object composeExecutionSteps(PatrolTaskDraft draft) {
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
        return taskStepTreeGenerateService.compose(draft, null, startStopId, endStopId);
    }

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
        List<ScheduleSlotDTO> slots = collectPlanTimeSlots(workItems);
        if (slots.isEmpty()) {
            throw exception(PATROL_FACADE_NO_ACTIVE_SLOTS);
        }
        return slots;
    }

    /** 按排期模板上的时间窗收集计划点。没有时间窗时为空，不抛错。 */
    private static List<ScheduleSlotDTO> collectPlanTimeSlots(List<WorkItemDTO> workItems) {
        List<ScheduleSlotDTO> slots = new ArrayList<>();
        if (workItems == null) {
            return slots;
        }
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
                    .slotId(stableSlotId(item, window))
                    .workId(item.getWorkId())
                    .entityTypeCode(item.getEntityTypeCode())
                    .candidateStart(window.getStart())
                    .candidateEnd(window.getEnd())
                    .assignedResources(assignedFrom(item))
                    .lockState(SlotLockState.NONE)
                    .slotStatus(SlotStatus.PLANNED)
                    .build());
        }
        return slots;
    }

    private void requireConfigReady(PatrolTaskDraft draft) {
        PatrolInspectionConfigReadinessRespVO readiness = patrolInspectionConfigReadinessService.evaluate(draft);
        if (readiness.isReady()) {
            return;
        }
        String message = readiness.getGaps().isEmpty()
                ? "步骤参数还没填完"
                : readiness.getGaps().get(0).getMessage();
        throw invalidParamException(message);
    }

    private static List<String> slotIdsOf(List<ScheduleSlotDTO> slots) {
        if (CollectionUtils.isEmpty(slots)) {
            return List.of();
        }
        List<String> ids = new ArrayList<>();
        for (ScheduleSlotDTO slot : slots) {
            if (slot != null && StringUtils.hasText(slot.getSlotId())) {
                ids.add(slot.getSlotId().trim());
            }
        }
        return List.copyOf(ids);
    }

    /**
     * 试排计划点 id 须与生成任务物化收流一致：优先 workId，否则由作业项与窗口确定性生成。
     */
    private static String stableSlotId(WorkItemDTO item, TimeWindowDTO window) {
        if (item != null && StringUtils.hasText(item.getWorkId())) {
            return item.getWorkId().trim();
        }
        String seed = (item != null && StringUtils.hasText(item.getEntityTypeCode())
                ? item.getEntityTypeCode().trim()
                : "work")
                + "|"
                + window.getStart()
                + "|"
                + window.getEnd();
        return UUID.nameUUIDFromBytes(seed.getBytes(StandardCharsets.UTF_8)).toString();
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

    /** 排序方法必须由调用方带上。自动是优化先后，手工是按给定顺序。缺了就报错。 */
    private static String requireRouteStrategy(PatrolRouteRunReqVO reqVO) {
        if (reqVO == null || !StringUtils.hasText(reqVO.getStrategy())) {
            throw invalidParamException("请选择规划模式：自动或手工");
        }
        return reqVO.getStrategy().trim();
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
        payload.put(RoutePayloadKeys.STRATEGY, requireRouteStrategy(reqVO));
        payload.put(FROM_SAVED_ROUTE_SNAPSHOT, fromSavedRouteSnapshot);

        return WorkItemDTO.builder()
                .contractVersion(ContractVersions.MVP)
                .workId("patrol-seed-" + UUID.randomUUID())
                .payload(payload)
                .build();
    }
}
