package cn.cheers.x.inspection.inspection_content.service.orchestration;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolRouteRunReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolOrchestrationRunReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolSlotWritebackReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolTaskPauseReqVO;
import cn.cheers.x.inspection.inspection_content.service.orchestration.impl.PatrolRouteFacadeServiceImpl;
import cn.cheers.x.inspection.inspection_content.service.orchestration.impl.PatrolScheduleConflictDetectService;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleConflictReportDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolSaveRouteReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolSaveRouteRespDTO;
import cn.cheers.x.inspection.task.service.execution.openrun.PatrolOpenRunMaterializeService;
import cn.cheers.x.inspection.task.service.execution.scheduleboard.PatrolReservationAutoStartScheduler;
import cn.cheers.x.inspection.task.service.execution.steptree.TaskStepTreeGenerateService;
import cn.cheers.x.inspection.task.service.task.CreateWizardInvalidation;
import cn.cheers.x.inspection.task.service.task.PatrolTaskCreateProcessService;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunRequest;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunResponse;
import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.contract.dto.slot.AssignedResourceDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.dto.work.TimePreferencesDTO;
import cn.cheers.x.module.platform.contract.dto.work.TimeWindowDTO;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatrolRouteFacadeService 单元测试")
class PatrolRouteFacadeServiceTest {

    private static final Long FACILITY_ID = 7L;
    private static final Long TASK_ID = 99L;
    private static final List<Long> OBJECT_IDS = List.of(10L, 11L);
    private static final String RUNTIME_JOB_ID = "job-reserved";

    @Mock
    private ScheduleRunApi scheduleRunApi;

    @Mock
    private RuntimePersistApi runtimePersistApi;

    @Mock
    private RuntimeQueryApi runtimeQueryApi;

    @Mock
    private RuntimeSlotWriteApi runtimeSlotWriteApi;

    @Mock
    private PatrolTaskEntityStore patrolTaskEntityStore;

    @Mock
    private PatrolOpenRunMaterializeService patrolOpenRunMaterializeService;

    @Mock
    private TaskStepTreeGenerateService taskStepTreeGenerateService;

    @Mock
    private PatrolReservationAutoStartScheduler patrolReservationAutoStartScheduler;

    @Mock
    private PatrolSaveRouteService patrolSaveRouteService;

    @Mock
    private PatrolScheduleConflictDetectService patrolScheduleConflictDetectService;

    @Mock
    private PatrolTaskCreateProcessService patrolTaskCreateProcessService;

    private PatrolRouteFacadeServiceImpl facadeService;

    @BeforeEach
    void setUp() {
        facadeService = new PatrolRouteFacadeServiceImpl();
        ReflectionTestUtils.setField(facadeService, "scheduleRunApi", scheduleRunApi);
        ReflectionTestUtils.setField(facadeService, "runtimePersistApi", runtimePersistApi);
        ReflectionTestUtils.setField(facadeService, "runtimeQueryApi", runtimeQueryApi);
        ReflectionTestUtils.setField(facadeService, "runtimeSlotWriteApi", runtimeSlotWriteApi);
        ReflectionTestUtils.setField(facadeService, "patrolTaskEntityStore", patrolTaskEntityStore);
        ReflectionTestUtils.setField(facadeService, "patrolOpenRunMaterializeService", patrolOpenRunMaterializeService);
        ReflectionTestUtils.setField(facadeService, "taskStepTreeGenerateService", taskStepTreeGenerateService);
        ReflectionTestUtils.setField(facadeService, "patrolReservationAutoStartScheduler", patrolReservationAutoStartScheduler);
        ReflectionTestUtils.setField(facadeService, "patrolSaveRouteService", patrolSaveRouteService);
        ReflectionTestUtils.setField(facadeService, "patrolScheduleConflictDetectService", patrolScheduleConflictDetectService);
        ReflectionTestUtils.setField(facadeService, "patrolTaskCreateProcessService", patrolTaskCreateProcessService);
        ReflectionTestUtils.setField(facadeService, "objectMapper", new ObjectMapper());
    }

    @Test
    @DisplayName("preview：dryRun + stopAfter ROUTE + 种子 payload 键齐全")
    void previewRoute_buildsScheduleRunRequest() {
        PatrolRouteRunReqVO reqVO = routeReq("net-a", null);
        when(scheduleRunApi.run(any())).thenReturn(CommonResult.success(
                ScheduleRunResponse.builder()
                        .runtimeJobId("job-preview")
                        .routePreview(RoutePreviewDTO.builder().networkRef("net-a").build())
                        .workItems(List.of(WorkItemDTO.builder().workId("expanded-1").build()))
                        .build()));

        ScheduleRunResponse response = facadeService.previewRoute(reqVO);

        assertEquals("job-preview", response.getRuntimeJobId());
        assertEquals("net-a", response.getRoutePreview().getNetworkRef());

        ScheduleRunRequest captured = captureRunRequest();
        assertEquals(OrchestrationRefs.PATROL_ROUTE_PREVIEW_V1, captured.getOrchestrationRef());
        assertEquals(Boolean.TRUE, captured.getDryRun());
        assertEquals(OrchestrationPhase.ROUTE.name(), captured.getStopAfterPhase());
        assertSeedPayload(captured.getWorkItems().get(0).getPayload(), reqVO, false);
    }

    @Test
    @DisplayName("saveRoute：直接写入请求里的 plannedRoute，不再重跑算路")
    void saveRoute_persistsPlannedRouteWithoutScheduleRun() {
        PatrolRouteRunReqVO reqVO = routeReq(null, TASK_ID);
        Map<String, Object> planned = new LinkedHashMap<>();
        planned.put("stopIds", List.of("sta-1", "sta-2"));
        reqVO.setPlannedRoute(planned);
        when(patrolSaveRouteService.saveRoute(any())).thenReturn(
                PatrolSaveRouteRespDTO.builder().taskId(TASK_ID).dryRun(false).build());

        facadeService.saveRoute(reqVO);

        ArgumentCaptor<PatrolSaveRouteReqDTO> captor = ArgumentCaptor.forClass(PatrolSaveRouteReqDTO.class);
        verify(patrolSaveRouteService).saveRoute(captor.capture());
        verify(patrolTaskCreateProcessService).invalidate(eq(TASK_ID), eq(CreateWizardInvalidation.ROUTE_SAVED));
        verify(scheduleRunApi, never()).run(any());
        assertEquals(TASK_ID, captor.getValue().getTaskId());
        assertEquals(planned, captor.getValue().getWorkItems().get(0).getPayload().get(RoutePayloadKeys.PLANNED_ROUTE));
    }

    @Test
    @DisplayName("preview：透传 startStopId / endStopId / returnToStart 到编排种子 payload")
    void previewRoute_forwardsStartStopId() {
        PatrolRouteRunReqVO reqVO = routeReq("net-a", null);
        reqVO.setStartStopId("sta-depot");
        reqVO.setEndStopId("sta-pad");
        reqVO.setReturnToStart(Boolean.FALSE);
        when(scheduleRunApi.run(any())).thenReturn(CommonResult.success(
                ScheduleRunResponse.builder().runtimeJobId("job-preview").build()));

        facadeService.previewRoute(reqVO);

        Map<String, Object> payload = captureRunRequest().getWorkItems().get(0).getPayload();
        assertEquals("sta-depot", payload.get("startStopId"));
        assertEquals("sta-pad", payload.get("endStopId"));
        assertEquals(Boolean.FALSE, payload.get("returnToStart"));
        assertSeedPayload(payload, reqVO, false);
    }

    @Test
    @DisplayName("saveRoute：缺 taskId 显式失败")
    void saveRoute_missingTaskId_throws() {
        assertThrows(ServiceException.class, () -> facadeService.saveRoute(routeReq(null, null)));
        verify(scheduleRunApi, never()).run(any());
    }

    @Test
    @DisplayName("previewOrchestration：同一任务已有存量 runtime 试排时先释放再重排")
    void previewOrchestration_replacesPreviousTrialJob() {
        PatrolOrchestrationRunReqVO reqVO = orchestrationReq();
        String previousJobId = "job-previous-trial";
        PatrolTaskDraft draft = draftWithConfirmedRoute(previousJobId, Boolean.FALSE);
        when(patrolTaskEntityStore.require(TASK_ID)).thenReturn(draft);
        stubArrangePolicy("priority_preempt", 15);
        when(runtimeSlotWriteApi.releaseUnfinished(any())).thenReturn(CommonResult.success(true));
        when(runtimeQueryApi.listSlots(any(), any(), any(), any(), any(), any()))
                .thenReturn(CommonResult.success(List.of()));
        ScheduleSlotDTO previewSlot = slot("slot-a", "ephemeral", "2026-07-21T08:00:00+08:00",
                "2026-07-21T09:00:00+08:00", "res-1");
        when(scheduleRunApi.run(any())).thenReturn(CommonResult.success(
                ScheduleRunResponse.builder().runtimeJobId("ephemeral").slots(List.of(previewSlot)).build()));

        ScheduleRunResponse response = facadeService.previewOrchestration(reqVO);

        assertEquals(1, response.getSlots().size());
        verify(runtimeSlotWriteApi).releaseUnfinished(any());
        verify(patrolTaskEntityStore).clearOrchestrationTrialState(TASK_ID);
        verify(patrolTaskEntityStore).writeOrchestrationPreview(eq(TASK_ID), eq(List.of(previewSlot)), isNull());
        verify(patrolTaskEntityStore, never()).writeOrchestrationCommittedState(any(), any(), any());
        verify(patrolOpenRunMaterializeService, never()).materializeAllSlots(any(), any());

        ScheduleRunRequest captured = captureRunRequest();
        assertEquals(Boolean.TRUE, captured.getDryRun());
        assertEquals(previousJobId, captured.getSourceRuntimeJobId());
    }

    @Test
    @DisplayName("previewOrchestration：已生成也可本页重排，此刻不释放未执行占窗")
    void previewOrchestration_whenAlreadyGenerated_allowsRearrangeWithoutRelease() {
        PatrolOrchestrationRunReqVO reqVO = orchestrationReq();
        when(patrolTaskEntityStore.require(TASK_ID))
                .thenReturn(draftWithConfirmedRoute(RUNTIME_JOB_ID, Boolean.TRUE));
        stubArrangePolicy("defer_slot", 20);
        ScheduleSlotDTO previewSlot = slot("slot-a", "ephemeral", "2026-07-21T08:00:00+08:00",
                "2026-07-21T09:00:00+08:00", "res-1");
        when(scheduleRunApi.run(any())).thenReturn(CommonResult.success(
                ScheduleRunResponse.builder().runtimeJobId("ephemeral").slots(List.of(previewSlot)).build()));

        ScheduleRunResponse response = facadeService.previewOrchestration(reqVO);

        assertEquals(1, response.getSlots().size());
        verify(runtimeSlotWriteApi, never()).releaseUnfinished(any());
        verify(patrolTaskEntityStore).writeOrchestrationPreview(eq(TASK_ID), eq(List.of(previewSlot)), isNull());
    }

    @Test
    @DisplayName("abort：释放占窗并清除试排草稿状态")
    void abort_clearsTrialArrangeState() {
        PatrolTaskPauseReqVO reqVO = pauseReq();
        stubTaskWithConfirmedPlannedRoute(RUNTIME_JOB_ID, Boolean.FALSE);
        when(runtimeSlotWriteApi.releaseUnfinished(any())).thenReturn(CommonResult.success(true));

        facadeService.abortOrchestration(reqVO);

        verify(patrolTaskEntityStore).clearOrchestrationTrialState(TASK_ID);
    }

    @Test
    @DisplayName("previewOrchestration：试排 dryRun，写 orchestrationPreview，不落 runtime")
    void previewOrchestration_buildsRequestAndUpdatesTask() {
        PatrolOrchestrationRunReqVO reqVO = orchestrationReq();
        PatrolTaskDraft draft = draftWithConfirmedRoute(null, null);
        stubTaskWithConfirmedPlannedRoute(null, null);
        when(patrolTaskEntityStore.require(TASK_ID)).thenReturn(draft);
        stubArrangePolicy("defer_slot", 20);
        when(runtimeQueryApi.listSlots(any(), any(), any(), any(), any(), any()))
                .thenReturn(CommonResult.success(List.of()));
        ScheduleSlotDTO previewSlot = slot("slot-a", "ephemeral", "2026-07-21T08:00:00+08:00",
                "2026-07-21T09:00:00+08:00", "res-1");
        when(scheduleRunApi.run(any())).thenReturn(CommonResult.success(
                ScheduleRunResponse.builder().runtimeJobId("ephemeral").slots(List.of(previewSlot)).build()));

        ScheduleRunResponse response = facadeService.previewOrchestration(reqVO);

        assertEquals(1, response.getSlots().size());
        ScheduleRunRequest captured = captureRunRequest();
        assertEquals(OrchestrationRefs.PATROL_ORCHESTRATION_V1, captured.getOrchestrationRef());
        assertEquals(Boolean.TRUE, captured.getDryRun());
        Map<String, Object> payload = captured.getWorkItems().get(0).getPayload();
        assertEquals(TASK_ID, payload.get("taskId"));
        assertEquals(Boolean.TRUE, payload.get("fromSavedRouteSnapshot"));
        assertEquals(Boolean.FALSE, payload.get("taskEnabled"));
        assertEquals(FACILITY_ID, payload.get("facilityId"));
        assertEquals(PatrolTaskEntityStore.TASK_TYPE, captured.getEntityTypeCode());
        assertEquals(PatrolTaskEntityStore.TASK_TYPE, captured.getWorkItems().get(0).getEntityTypeCode());
        assertEquals("defer_slot", captured.getSchedulingSpec().getConflictStrategy());
        assertEquals(20, captured.getSchedulingSpec().getTaskGapMinutes());

        verify(taskStepTreeGenerateService).generate(eq(TASK_ID), isNull(), eq("sta-start"), eq("sta-end"));
        verify(patrolTaskEntityStore).writeOrchestrationPreview(eq(TASK_ID), eq(List.of(previewSlot)), isNull());
        verify(patrolTaskEntityStore, never()).writeOrchestrationCommittedState(any(), any(), any());
        verify(patrolOpenRunMaterializeService, never()).materializeAllSlots(any(), any());
    }

    @Test
    @DisplayName("previewOrchestration：总任务无 plannedRoute 时拒绝排期")
    void previewOrchestration_withoutPlannedRoute_throws() {
        PatrolOrchestrationRunReqVO reqVO = orchestrationReq();
        when(patrolTaskEntityStore.require(TASK_ID)).thenReturn(draftWithoutRoute());
        assertThrows(ServiceException.class, () -> facadeService.previewOrchestration(reqVO));
        verify(scheduleRunApi, never()).run(any());
    }

    @Test
    @DisplayName("commit：有试排快照时直接 persist 快照，不再 re-SOLVE")
    void commitOrchestration_fromArrangePreview_commitsAndEnables() {
        PatrolOrchestrationRunReqVO reqVO = orchestrationReq();
        List<ScheduleSlotDTO> previewSlots = List.of(slot("slot-a", "ephemeral", "2026-07-21T08:00:00+08:00",
                "2026-07-21T09:00:00+08:00", "res-1"));
        Map<String, Object> planned = new LinkedHashMap<>();
        planned.put("stopIds", List.of("sta-1", "sta-2"));
        PatrolTaskDraft draft = new PatrolTaskDraft(
                TASK_ID, "task", "巡检", FACILITY_ID, "UAV", null, null, null,
                sampleStepTree(), planned, "draft", 2,
                "sta-start", "sta-end", null, Boolean.FALSE, null, null, previewSlots, null);
        when(patrolTaskEntityStore.require(TASK_ID)).thenReturn(draft);
        when(patrolTaskEntityStore.readOrchestrationPreviewSlots(TASK_ID)).thenReturn(previewSlots);
        stubArrangePolicy("priority_preempt", 15);
        when(runtimePersistApi.persist(any())).thenReturn(CommonResult.success(true));
        when(runtimeQueryApi.listSlotsByJobId(any())).thenAnswer(invocation -> {
            String jobId = invocation.getArgument(0);
            ScheduleSlotDTO own = slot("slot-a", jobId, "2026-07-21T08:00:00+08:00",
                    "2026-07-21T09:00:00+08:00", "res-1");
            return CommonResult.success(List.of(own));
        });
        when(runtimeQueryApi.listSlots(any(), any(), any(), any(), any(), any()))
                .thenReturn(CommonResult.success(List.of()));
        when(runtimeSlotWriteApi.finalizePlannedSchedule(any())).thenReturn(CommonResult.success(true));

        ScheduleRunResponse response = facadeService.commitOrchestration(reqVO);

        verify(scheduleRunApi, never()).run(any());
        ArgumentCaptor<RuntimePersistReqDTO> persistCaptor = ArgumentCaptor.forClass(RuntimePersistReqDTO.class);
        verify(runtimePersistApi).persist(persistCaptor.capture());
        String committedJobId = persistCaptor.getValue().getJob().getRuntimeJobId();
        assertEquals(committedJobId, response.getRuntimeJobId());
        assertEquals(FACILITY_ID, persistCaptor.getValue().getFacilityId());
        assertEquals(1, persistCaptor.getValue().getSlots().size());
        assertEquals(committedJobId, persistCaptor.getValue().getSlots().get(0).getRuntimeJobId());
        verify(patrolTaskEntityStore).clearOrchestrationPreview(TASK_ID);
        verify(patrolTaskEntityStore).writeOrchestrationCommittedState(TASK_ID, committedJobId, Boolean.TRUE);
        verify(patrolOpenRunMaterializeService).materializeAllSlots(any(), any());
    }

    @Test
    @DisplayName("generateTask：试排快照与他人占窗冲突时显式失败")
    void commitOrchestration_occupancyConflict_throws() {
        PatrolOrchestrationRunReqVO reqVO = orchestrationReq();
        List<ScheduleSlotDTO> previewSlots = List.of(slot("slot-a", "ephemeral", "2026-07-21T08:00:00+08:00",
                "2026-07-21T09:00:00+08:00", "res-1"));
        Map<String, Object> planned = new LinkedHashMap<>();
        planned.put("stopIds", List.of("sta-1", "sta-2"));
        PatrolTaskDraft draft = new PatrolTaskDraft(
                TASK_ID, "task", "巡检", FACILITY_ID, "UAV", null, null, null,
                sampleStepTree(), planned, "draft", 2,
                "sta-start", "sta-end", null, Boolean.FALSE, null, null, previewSlots, null);
        when(patrolTaskEntityStore.require(TASK_ID)).thenReturn(draft);
        when(patrolTaskEntityStore.readOrchestrationPreviewSlots(TASK_ID)).thenReturn(previewSlots);
        stubArrangePolicy("priority_preempt", 15);
        when(runtimePersistApi.persist(any())).thenReturn(CommonResult.success(true));
        when(runtimeQueryApi.listSlotsByJobId(any())).thenAnswer(invocation -> {
            String jobId = invocation.getArgument(0);
            return CommonResult.success(List.of(slot("slot-a", jobId, "2026-07-21T08:00:00+08:00",
                    "2026-07-21T09:00:00+08:00", "res-1")));
        });
        ScheduleSlotDTO foreignSlot = slot("slot-b", "job-other", "2026-07-21T08:30:00+08:00",
                "2026-07-21T09:30:00+08:00", "res-1");
        when(runtimeQueryApi.listSlots(any(), any(), any(), any(), any(), any()))
                .thenReturn(CommonResult.success(List.of(foreignSlot)));
        when(runtimeSlotWriteApi.releaseUnfinished(any())).thenReturn(CommonResult.success(true));

        assertThrows(ServiceException.class, () -> facadeService.commitOrchestration(reqVO));
        verify(patrolTaskEntityStore, never()).writeOrchestrationCommittedState(any(), any(), any());
        verify(runtimeSlotWriteApi, never()).finalizePlannedSchedule(any());
        verify(runtimeSlotWriteApi).releaseUnfinished(any());
    }

    @Test
    @DisplayName("generateTask：本草稿残留占窗不按别人占用失败")
    void commitOrchestration_ownLeftoverWorkId_doesNotCountAsForeign() {
        PatrolOrchestrationRunReqVO reqVO = orchestrationReq();
        List<ScheduleSlotDTO> previewSlots = List.of(slot("slot-a", "ephemeral", "2026-07-21T08:00:00+08:00",
                "2026-07-21T09:00:00+08:00", "res-1"));
        Map<String, Object> planned = new LinkedHashMap<>();
        planned.put("stopIds", List.of("sta-1", "sta-2"));
        PatrolTaskDraft draft = new PatrolTaskDraft(
                TASK_ID, "task", "巡检", FACILITY_ID, "UAV", null, null, null,
                sampleStepTree(), planned, "draft", 2,
                "sta-start", "sta-end", null, Boolean.FALSE, null, null, previewSlots, null);
        when(patrolTaskEntityStore.require(TASK_ID)).thenReturn(draft);
        when(patrolTaskEntityStore.readOrchestrationPreviewSlots(TASK_ID)).thenReturn(previewSlots);
        stubArrangePolicy("priority_preempt", 15);
        when(runtimePersistApi.persist(any())).thenReturn(CommonResult.success(true));
        when(runtimeQueryApi.listSlotsByJobId(any())).thenAnswer(invocation -> {
            String jobId = invocation.getArgument(0);
            return CommonResult.success(List.of(slot("slot-a", jobId, "2026-07-21T08:00:00+08:00",
                    "2026-07-21T09:00:00+08:00", "res-1")));
        });
        ScheduleSlotDTO leftover = slot("slot-left", "job-leftover", "2026-07-21T08:00:00+08:00",
                "2026-07-21T09:00:00+08:00", "res-1");
        leftover.setWorkId("patrol-task-" + TASK_ID + "-0");
        when(runtimeQueryApi.listSlots(any(), any(), any(), any(), any(), any()))
                .thenReturn(CommonResult.success(List.of(leftover)));
        when(runtimeSlotWriteApi.releaseUnfinished(any())).thenReturn(CommonResult.success(true));
        when(runtimeSlotWriteApi.finalizePlannedSchedule(any())).thenReturn(CommonResult.success(true));

        ScheduleRunResponse response = facadeService.commitOrchestration(reqVO);

        org.junit.jupiter.api.Assertions.assertNotNull(response.getRuntimeJobId());
        verify(runtimeSlotWriteApi).releaseUnfinished(any());
        verify(patrolTaskEntityStore).writeOrchestrationCommittedState(eq(TASK_ID), any(), eq(Boolean.TRUE));
    }

    @Test
    @DisplayName("previewOrchestration：允许挪已有但未填最大范围时拒绝试排")
    void previewOrchestration_allowShiftWithoutMax_throws() {
        PatrolOrchestrationRunReqVO reqVO = orchestrationReq();
        when(patrolTaskEntityStore.require(TASK_ID)).thenReturn(draftWithConfirmedRoute(null, null));
        when(patrolTaskEntityStore.readArrangePolicy(TASK_ID))
                .thenReturn(new PatrolTaskEntityStore.ArrangePolicy(null, null, true, null));

        assertThrows(ServiceException.class, () -> facadeService.previewOrchestration(reqVO));
        verify(scheduleRunApi, never()).run(any());
    }

    @Test
    @DisplayName("detectScheduleConflicts：只出报告，不跑智能编排")
    void detectScheduleConflicts_returnsReportWithoutSolve() {
        PatrolOrchestrationRunReqVO reqVO = orchestrationReq();
        when(patrolTaskEntityStore.require(TASK_ID)).thenReturn(draftWithConfirmedRoute(null, Boolean.FALSE));
        when(patrolScheduleConflictDetectService.detect(eq(TASK_ID), any()))
                .thenReturn(new PatrolScheduleConflictDetectService.DetectedPlan(
                        draftWithConfirmedRoute(null, Boolean.FALSE),
                        List.of(),
                        ScheduleConflictReportDTO.builder().hasConflict(false).plannedCount(3).conflictCount(0).devices(List.of()).build()));

        ScheduleConflictReportDTO report = facadeService.detectScheduleConflicts(reqVO);

        assertEquals(Boolean.FALSE, report.getHasConflict());
        verify(scheduleRunApi, never()).run(any());
        verify(taskStepTreeGenerateService, never()).generate(any(), any(), any(), any());
    }

    @Test
    @DisplayName("prepareConfirmPreview：仍有冲突则拒绝写快照")
    void prepareConfirmPreview_whenConflict_throws() {
        PatrolOrchestrationRunReqVO reqVO = orchestrationReq();
        when(patrolTaskEntityStore.require(TASK_ID)).thenReturn(draftWithConfirmedRoute(null, Boolean.FALSE));
        when(patrolScheduleConflictDetectService.detect(eq(TASK_ID), any()))
                .thenReturn(new PatrolScheduleConflictDetectService.DetectedPlan(
                        draftWithConfirmedRoute(null, Boolean.FALSE),
                        List.of(),
                        ScheduleConflictReportDTO.builder().hasConflict(true).plannedCount(3).conflictCount(2).devices(List.of()).build()));

        assertThrows(ServiceException.class, () -> facadeService.prepareConfirmPreview(reqVO));
        verify(patrolTaskEntityStore, never()).writeOrchestrationPreview(any(), any(), any());
        verify(taskStepTreeGenerateService, never()).generate(any(), any(), any(), any());
    }

    @Test
    @DisplayName("prepareConfirmPreview：无冲突时写试排快照并生成步骤图")
    void prepareConfirmPreview_writesPreviewAndGeneratesStepTree() {
        PatrolOrchestrationRunReqVO reqVO = orchestrationReq();
        PatrolTaskDraft ready = draftWithConfirmedRoute(null, Boolean.FALSE, null);
        Map<String, Object> tree = sampleStepTree();
        when(patrolTaskEntityStore.require(TASK_ID)).thenReturn(ready);
        when(taskStepTreeGenerateService.generate(eq(TASK_ID), isNull(), eq("sta-start"), eq("sta-end")))
                .thenReturn(tree);
        when(patrolScheduleConflictDetectService.detect(eq(TASK_ID), any()))
                .thenReturn(new PatrolScheduleConflictDetectService.DetectedPlan(
                        ready,
                        List.of(templateWorkItem()),
                        ScheduleConflictReportDTO.builder()
                                .hasConflict(false).plannedCount(1).conflictCount(0).devices(List.of()).build()));

        ScheduleRunResponse response = facadeService.prepareConfirmPreview(reqVO);

        assertEquals(1, response.getSlots().size());
        assertEquals(tree, response.getExecutionStepTree());
        verify(taskStepTreeGenerateService).generate(eq(TASK_ID), isNull(), eq("sta-start"), eq("sta-end"));
        verify(patrolTaskEntityStore).writeOrchestrationPreview(eq(TASK_ID), any(), any());
        verify(scheduleRunApi, never()).run(any());
    }

    @Test
    @DisplayName("writeback：转发 updateSlotStatus")
    void writebackSlot_forwardsUpdate() {
        PatrolSlotWritebackReqVO reqVO = new PatrolSlotWritebackReqVO();
        reqVO.setSlotId("slot-1");
        reqVO.setSlotStatus(SlotStatus.COMPLETED);
        when(runtimeSlotWriteApi.updateSlotStatus(any())).thenReturn(CommonResult.success(true));

        facadeService.writebackSlot(reqVO);

        ArgumentCaptor<RuntimeSlotStatusUpdateReqDTO> captor =
                ArgumentCaptor.forClass(RuntimeSlotStatusUpdateReqDTO.class);
        verify(runtimeSlotWriteApi).updateSlotStatus(captor.capture());
        assertEquals("slot-1", captor.getValue().getSlotId());
        assertEquals(SlotStatus.COMPLETED, captor.getValue().getSlotStatus());
    }

    private ScheduleRunRequest captureRunRequest() {
        ArgumentCaptor<ScheduleRunRequest> captor = ArgumentCaptor.forClass(ScheduleRunRequest.class);
        verify(scheduleRunApi).run(captor.capture());
        return captor.getValue();
    }

    private static PatrolRouteRunReqVO routeReq(String preferredNetworkRef, Long taskId) {
        PatrolRouteRunReqVO reqVO = new PatrolRouteRunReqVO();
        reqVO.setFacilityId(FACILITY_ID);
        reqVO.setObjectIds(OBJECT_IDS);
        reqVO.setPreferredNetworkRef(preferredNetworkRef);
        reqVO.setTaskId(taskId);
        reqVO.setStopIds(List.of("sta-1"));
        return reqVO;
    }

    private static PatrolOrchestrationRunReqVO orchestrationReq() {
        PatrolOrchestrationRunReqVO reqVO = new PatrolOrchestrationRunReqVO();
        reqVO.setTaskId(TASK_ID);
        reqVO.setSchedulingSpec(SchedulingSpecDTO.builder()
                .mode("weekly")
                .horizonStart("2026-07-21")
                .horizonEnd("2026-07-28")
                .conflictStrategy("priority_preempt")
                .build());
        return reqVO;
    }

    private static PatrolTaskPauseReqVO pauseReq() {
        PatrolTaskPauseReqVO reqVO = new PatrolTaskPauseReqVO();
        reqVO.setTaskId(TASK_ID);
        reqVO.setReason("yield for urgent task");
        return reqVO;
    }

    private void stubArrangePolicy(String conflictStrategy, Integer taskGapMinutes) {
        when(patrolTaskEntityStore.readArrangePolicy(TASK_ID))
                .thenReturn(new PatrolTaskEntityStore.ArrangePolicy(conflictStrategy, taskGapMinutes, null, null));
    }

    private void stubTaskWithConfirmedPlannedRoute(String runtimeJobId, Boolean orchestrationCommitted) {
        when(patrolTaskEntityStore.require(TASK_ID))
                .thenReturn(draftWithConfirmedRoute(runtimeJobId, orchestrationCommitted));
    }

    private static PatrolTaskDraft draftWithConfirmedRoute(String runtimeJobId, Boolean orchestrationCommitted) {
        Map<String, Object> planned = new LinkedHashMap<>();
        planned.put("stopIds", List.of("sta-1", "sta-2"));
        planned.put("totalDistanceMeters", 100L);
        return draftWithConfirmedRoute(runtimeJobId, orchestrationCommitted, sampleStepTree(), planned);
    }

    private static PatrolTaskDraft draftWithConfirmedRoute(
            String runtimeJobId, Boolean orchestrationCommitted, Object stepTree) {
        Map<String, Object> planned = new LinkedHashMap<>();
        planned.put("stopIds", List.of("sta-1", "sta-2"));
        planned.put("totalDistanceMeters", 100L);
        return draftWithConfirmedRoute(runtimeJobId, orchestrationCommitted, stepTree, planned);
    }

    private static PatrolTaskDraft draftWithConfirmedRoute(
            String runtimeJobId, Boolean orchestrationCommitted, Object stepTree, Map<String, Object> planned) {
        return new PatrolTaskDraft(
                TASK_ID, "task", "巡检", FACILITY_ID, "UAV", null, null, null,
                stepTree, planned, "draft", 2, "sta-start", "sta-end", runtimeJobId, orchestrationCommitted, null, null,
                null, null);
    }

    private static WorkItemDTO templateWorkItem() {
        return WorkItemDTO.builder()
                .workId("w1")
                .entityTypeCode("task")
                .timePreferences(TimePreferencesDTO.builder()
                        .allowedWindows(List.of(TimeWindowDTO.builder()
                                .start("2026-09-01T09:00:00+08:00")
                                .end("2026-09-01T10:00:00+08:00")
                                .build()))
                        .build())
                .build();
    }

    private static Map<String, Object> sampleStepTree() {
        Map<String, Object> node = new LinkedHashMap<>();
        node.put("nodeKey", "n1");
        node.put("order", 1);
        node.put("hangTypeCode", "equipment");
        node.put("refId", 10L);
        return Map.of("nodes", List.of(node));
    }

    private static PatrolTaskDraft draftWithoutRoute() {
        return new PatrolTaskDraft(
                TASK_ID, "task", "巡检", FACILITY_ID, "UAV", null, null, null,
                null, Map.of(), "draft", 2, null, null, null, null, null, null, null, null);
    }

    private static ScheduleSlotDTO slot(String slotId, String runtimeJobId, String start, String end,
                                         String resourceId) {
        return ScheduleSlotDTO.builder()
                .slotId(slotId)
                .runtimeJobId(runtimeJobId)
                .plannedStart(start)
                .plannedEnd(end)
                .slotStatus(SlotStatus.PLANNED)
                .assignedResources(List.of(AssignedResourceDTO.builder()
                        .resourceType("robot")
                        .resourceId(resourceId)
                        .build()))
                .build();
    }

    private static void assertSeedPayload(Map<String, Object> payload, PatrolRouteRunReqVO reqVO,
                                          boolean fromSavedRouteSnapshot) {
        assertEquals(reqVO.getFacilityId(), payload.get("facilityId"));
        assertEquals(reqVO.getObjectIds(), payload.get("objectIds"));
        if (reqVO.getPreferredNetworkRef() != null) {
            assertEquals(reqVO.getPreferredNetworkRef(), payload.get("preferredNetworkRef"));
        }
        if (reqVO.getTaskId() != null) {
            assertEquals(reqVO.getTaskId(), payload.get("taskId"));
        }
        if (reqVO.getStartStopId() != null) {
            assertEquals(reqVO.getStartStopId(), payload.get("startStopId"));
        }
        if (reqVO.getEndStopId() != null) {
            assertEquals(reqVO.getEndStopId(), payload.get("endStopId"));
        }
        if (reqVO.getReturnToStart() != null) {
            assertEquals(reqVO.getReturnToStart(), payload.get("returnToStart"));
        }
        if (reqVO.getStopIds() != null) {
            assertEquals(reqVO.getStopIds(), payload.get("stopIds"));
        }
        assertEquals(fromSavedRouteSnapshot, payload.get("fromSavedRouteSnapshot"));
    }
}
