package cn.cheers.x.module.platform.orchestration.service;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.orchestration.PatrolOrchestrationApi;
import cn.cheers.x.inspection.orchestration.dto.PatrolExpandReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolExpandRespDTO;
import cn.cheers.x.maintenance.api.MaintenanceApi;
import cn.cheers.x.module.platform.capability.api.MappingProfileApi;
import cn.cheers.x.module.platform.capability.api.ProcessCapabilityBindingApi;
import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewDTO;
import cn.cheers.x.module.platform.contract.dto.route.RouteRequestDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunRequest;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunResponse;
import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants;
import cn.cheers.x.module.platform.orchestration.enums.OrchestrationRefs;
import cn.cheers.x.module.platform.orchestration.handler.patrol.PatrolExpandMapHandler;
import cn.cheers.x.module.platform.orchestration.phase.BuiltinRoutePhaseHandler;
import cn.cheers.x.module.platform.orchestration.phase.PhaseHandlerRegistry;
import cn.cheers.x.module.platform.orchestration.route.RoutePayloadKeys;
import cn.cheers.x.module.platform.orchestration.template.OrchestrationTemplateRegistry;
import cn.cheers.x.module.platform.policy.api.PolicyResolveApi;
import cn.cheers.x.module.platform.routing.api.RoutePlanApi;
import cn.cheers.x.module.platform.runtime.api.RuntimePersistApi;
import cn.cheers.x.module.platform.runtime.api.RuntimeQueryApi;
import cn.cheers.x.module.platform.runtime.api.RuntimeSlotWriteApi;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimePersistReqDTO;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimeSlotReleaseReqDTO;
import cn.cheers.x.module.platform.runtime.enums.RuntimeSlotReleaseMode;
import cn.cheers.x.module.platform.scheduling.engine.SchedulingEngine;
import cn.cheers.x.workorder.api.WorkOrderApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrchestrationRunner replan occupied 单元测试")
class OrchestrationRunnerReplanOccupiedTest {

    private static final String SOURCE_JOB = "job-source-1";
    private static final OffsetDateTime FROM = OffsetDateTime.parse("2026-07-21T00:00:00+08:00");
    private static final OffsetDateTime TO = OffsetDateTime.parse("2026-07-29T00:00:00+08:00");

    @Mock private SchedulingEngine schedulingEngine;
    @Mock private RuntimePersistApi runtimePersistApi;
    @Mock private RuntimeQueryApi runtimeQueryApi;
    @Mock private RuntimeSlotWriteApi runtimeSlotWriteApi;
    @Mock private PolicyResolveApi policyResolveApi;
    @Mock private ProcessCapabilityBindingApi processCapabilityBindingApi;
    @Mock private MappingProfileApi mappingProfileApi;
    @Mock private WorkOrderApi workOrderApi;
    @Mock private MaintenanceApi maintenanceApi;
    @Mock private PatrolOrchestrationApi patrolOrchestrationApi;
    @Mock private RoutePlanApi routePlanApi;

    private OrchestrationRunner runner;

    @BeforeEach
    void setUp() {
        PatrolExpandMapHandler expandHandler = new PatrolExpandMapHandler();
        ReflectionTestUtils.setField(expandHandler, "patrolOrchestrationApi", patrolOrchestrationApi);

        BuiltinRoutePhaseHandler routeHandler = new BuiltinRoutePhaseHandler();
        ReflectionTestUtils.setField(routeHandler, "routePlanApi", routePlanApi);

        runner = new OrchestrationRunner();
        ReflectionTestUtils.setField(runner, "schedulingEngine", schedulingEngine);
        ReflectionTestUtils.setField(runner, "runtimePersistApi", runtimePersistApi);
        ReflectionTestUtils.setField(runner, "runtimeQueryApi", runtimeQueryApi);
        ReflectionTestUtils.setField(runner, "runtimeSlotWriteApi", runtimeSlotWriteApi);
        ReflectionTestUtils.setField(runner, "policyResolveApi", policyResolveApi);
        ReflectionTestUtils.setField(runner, "processCapabilityBindingApi", processCapabilityBindingApi);
        ReflectionTestUtils.setField(runner, "mappingProfileApi", mappingProfileApi);
        ReflectionTestUtils.setField(runner, "workOrderApi", workOrderApi);
        ReflectionTestUtils.setField(runner, "maintenanceApi", maintenanceApi);
        ReflectionTestUtils.setField(runner, "templateRegistry", new OrchestrationTemplateRegistry());
        ReflectionTestUtils.setField(runner, "phaseHandlerRegistry",
                new PhaseHandlerRegistry(List.of(expandHandler, routeHandler)));
    }

    @Test
    @DisplayName("排期类模板 solve 前加载 occupied 并传入引擎")
    void run_scheduleTemplate_loadsOccupiedAndPassesToSolve() {
        ScheduleSlotDTO foreignOccupied = ScheduleSlotDTO.builder()
                .slotId("slot-other")
                .runtimeJobId("job-other")
                .slotStatus(SlotStatus.PLANNED)
                .plannedStart("2026-07-21T09:00:00+08:00")
                .plannedEnd("2026-07-21T10:00:00+08:00")
                .build();
        when(runtimeQueryApi.listSlots(any(), any(), isNull(), isNull(), eq(1L), anyList()))
                .thenReturn(CommonResult.success(List.of(foreignOccupied)));
        when(schedulingEngine.solve(anyList(), any(), anyString(), anyList()))
                .thenReturn(List.of(ScheduleSlotDTO.builder().slotId("slot-new").workId("work-1").build()));
        when(runtimePersistApi.persist(any(RuntimePersistReqDTO.class)))
                .thenReturn(CommonResult.success(true));

        ScheduleRunRequest request = ScheduleRunRequest.builder()
                .orchestrationRef(OrchestrationRefs.STANDARD_EXPAND_SOLVE_PERSIST_V1)
                .schedulingSpec(schedulingSpec())
                .workItems(List.of(WorkItemDTO.builder().workId("work-1").durationEstimateMinutes(30).build()))
                .build();

        runner.run(request, 1L);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ScheduleSlotDTO>> occupiedCaptor = ArgumentCaptor.forClass(List.class);
        verify(schedulingEngine).solve(anyList(), any(), anyString(), occupiedCaptor.capture());
        assertEquals(1, occupiedCaptor.getValue().size());
        assertEquals("slot-other", occupiedCaptor.getValue().get(0).getSlotId());
        verify(runtimeQueryApi).listSlots(eq(FROM), eq(TO), isNull(), isNull(), eq(1L), anyList());
    }

    @Test
    @DisplayName("replan 缺 remainingStopIds 与 completedSlotIds 时显式失败")
    void run_replan_missingRemainingContext_failsExplicitly() {
        stubExpandOnly(List.of("sta_1", "sta_2", "sta_3"));

        ScheduleRunRequest request = replanRequest(null, null);

        ServiceException ex = assertThrows(ServiceException.class, () -> runner.run(request, 1L));

        assertEquals(ErrorCodeConstants.SCHEDULE_REPLAN_REMAINING_STOPS_REQUIRED.getCode(), ex.getCode());
        verify(schedulingEngine, never()).solve(anyList(), any(), anyString(), anyList());
        verify(runtimeSlotWriteApi, never()).releaseUnfinished(any(RuntimeSlotReleaseReqDTO.class));
    }

    @Test
    @DisplayName("replan 带 remainingStopIds：排除本任务未完成占用、释放后追加 persist")
    void run_replan_withRemainingStopIds_excludesSelfUnfinishedAndSafePersist() {
        stubExpandAndRoute(List.of("sta_1", "sta_2", "sta_3"));

        ScheduleSlotDTO selfUnfinished = ScheduleSlotDTO.builder()
                .slotId("slot-self-planned")
                .runtimeJobId(SOURCE_JOB)
                .slotStatus(SlotStatus.PLANNED)
                .plannedStart("2026-07-21T11:00:00+08:00")
                .plannedEnd("2026-07-21T12:00:00+08:00")
                .build();
        ScheduleSlotDTO selfCompleted = ScheduleSlotDTO.builder()
                .slotId("slot-self-done")
                .runtimeJobId(SOURCE_JOB)
                .slotStatus(SlotStatus.COMPLETED)
                .plannedStart("2026-07-21T08:00:00+08:00")
                .plannedEnd("2026-07-21T09:00:00+08:00")
                .build();
        ScheduleSlotDTO foreignOccupied = ScheduleSlotDTO.builder()
                .slotId("slot-foreign")
                .runtimeJobId("job-foreign")
                .slotStatus(SlotStatus.PLANNED)
                .plannedStart("2026-07-21T13:00:00+08:00")
                .plannedEnd("2026-07-21T14:00:00+08:00")
                .build();
        when(runtimeQueryApi.listSlots(any(), any(), isNull(), isNull(), eq(1L), anyList()))
                .thenReturn(CommonResult.success(List.of(selfUnfinished, selfCompleted, foreignOccupied)));
        when(schedulingEngine.solve(anyList(), any(), eq(SOURCE_JOB), anyList()))
                .thenReturn(List.of(ScheduleSlotDTO.builder()
                        .slotId("slot-replan-new")
                        .runtimeJobId(SOURCE_JOB)
                        .workId("patrol-work-1")
                        .build()));
        when(runtimeSlotWriteApi.releaseUnfinished(any(RuntimeSlotReleaseReqDTO.class)))
                .thenReturn(CommonResult.success(true));
        when(runtimePersistApi.persist(any(RuntimePersistReqDTO.class)))
                .thenReturn(CommonResult.success(true));

        ScheduleRunResponse response = runner.run(replanRequest(List.of("sta_2", "sta_3"), null), 1L);

        assertEquals(SOURCE_JOB, response.getRuntimeJobId());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ScheduleSlotDTO>> occupiedCaptor = ArgumentCaptor.forClass(List.class);
        verify(schedulingEngine).solve(anyList(), any(), eq(SOURCE_JOB), occupiedCaptor.capture());
        List<ScheduleSlotDTO> passedOccupied = occupiedCaptor.getValue();
        assertEquals(2, passedOccupied.size());
        assertTrue(passedOccupied.stream().anyMatch(s -> "slot-self-done".equals(s.getSlotId())));
        assertTrue(passedOccupied.stream().anyMatch(s -> "slot-foreign".equals(s.getSlotId())));
        assertFalse(passedOccupied.stream().anyMatch(s -> "slot-self-planned".equals(s.getSlotId())));

        ArgumentCaptor<RuntimeSlotReleaseReqDTO> releaseCaptor = ArgumentCaptor.forClass(RuntimeSlotReleaseReqDTO.class);
        verify(runtimeSlotWriteApi).releaseUnfinished(releaseCaptor.capture());
        assertEquals(SOURCE_JOB, releaseCaptor.getValue().getRuntimeJobId());
        assertEquals(RuntimeSlotReleaseMode.ABORT, releaseCaptor.getValue().getMode());

        ArgumentCaptor<RuntimePersistReqDTO> persistCaptor = ArgumentCaptor.forClass(RuntimePersistReqDTO.class);
        verify(runtimePersistApi).persist(persistCaptor.capture());
        assertTrue(Boolean.TRUE.equals(persistCaptor.getValue().getAppendSlotsOnly()));
        assertEquals(SOURCE_JOB, persistCaptor.getValue().getJob().getRuntimeJobId());
    }

    private void stubExpandOnly(List<String> allStopIds) {
        Map<String, Object> expandedPayload = new LinkedHashMap<>();
        expandedPayload.put(RoutePayloadKeys.NETWORK_REF, "net_patrol_1");
        expandedPayload.put(RoutePayloadKeys.STOP_IDS, allStopIds);
        expandedPayload.put(RoutePayloadKeys.INSPECTION_TYPE, "HUMAN");
        expandedPayload.put(RoutePayloadKeys.WORK_MINUTES, 10);
        WorkItemDTO expanded = WorkItemDTO.builder()
                .workId("patrol-work-1")
                .durationEstimateMinutes(10)
                .payload(expandedPayload)
                .build();
        when(patrolOrchestrationApi.expand(any(PatrolExpandReqDTO.class)))
                .thenReturn(CommonResult.success(PatrolExpandRespDTO.builder()
                        .workItems(List.of(expanded))
                        .networkRef("net_patrol_1")
                        .stopIds(allStopIds)
                        .inspectionType("HUMAN")
                        .build()));
    }

    private void stubExpandAndRoute(List<String> allStopIds) {
        stubExpandOnly(allStopIds);
        when(routePlanApi.plan(any(RouteRequestDTO.class)))
                .thenReturn(CommonResult.success(RoutePreviewDTO.builder()
                        .networkRef("net_patrol_1")
                        .totalDistanceMeters(120L)
                        .build()));
    }

    private static SchedulingSpecDTO schedulingSpec() {
        return SchedulingSpecDTO.builder()
                .mode("once")
                .conflictStrategy("none")
                .horizonStart("2026-07-21")
                .horizonEnd("2026-07-28")
                .build();
    }

    private static ScheduleRunRequest replanRequest(List<String> remainingStopIds, List<String> completedSlotIds) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("facilityId", 1L);
        payload.put("objectIds", List.of(10L, 11L));
        payload.put("preferredNetworkRef", "net_patrol_1");
        payload.put("taskId", 99L);
        return ScheduleRunRequest.builder()
                .orchestrationRef(OrchestrationRefs.PATROL_REPLAN_V1)
                .sourceRuntimeJobId(SOURCE_JOB)
                .remainingStopIds(remainingStopIds)
                .completedSlotIds(completedSlotIds)
                .schedulingSpec(schedulingSpec())
                .workItems(List.of(WorkItemDTO.builder().workId("seed-1").payload(payload).build()))
                .build();
    }
}
