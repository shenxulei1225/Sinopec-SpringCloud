package cn.cheers.x.module.platform.orchestration.service;

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
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.orchestration.enums.OrchestrationRefs;
import cn.cheers.x.module.platform.orchestration.handler.patrol.PatrolExpandMapHandler;
import cn.cheers.x.module.platform.orchestration.phase.BuiltinRoutePhaseHandler;
import cn.cheers.x.module.platform.orchestration.phase.PhaseHandlerRegistry;
import cn.cheers.x.module.platform.orchestration.route.RoutePayloadKeys;
import cn.cheers.x.module.platform.orchestration.template.OrchestrationTemplateRegistry;
import cn.cheers.x.module.platform.policy.api.PolicyResolveApi;
import cn.cheers.x.module.platform.routing.api.RoutePlanApi;
import cn.cheers.x.module.platform.runtime.api.RuntimePersistApi;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimePersistReqDTO;
import cn.cheers.x.module.platform.scheduling.engine.SchedulingEngine;
import cn.cheers.x.workorder.api.WorkOrderApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Patrol route preview orchestration integration test")
class PatrolRoutePreviewOrchestrationTest {

    @Mock private SchedulingEngine schedulingEngine;
    @Mock private RuntimePersistApi runtimePersistApi;
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
    @DisplayName("orch.patrol.route_preview_v1 dryRun stopAfter ROUTE 返回 routePreview 与 durationEstimateMinutes")
    void run_routePreviewTemplate_dryRunStopAfterRoute_returnsPreviewAndDuration() {
        Map<String, Object> expandedPayload = new LinkedHashMap<>();
        expandedPayload.put(RoutePayloadKeys.NETWORK_REF, "net_patrol_1");
        expandedPayload.put(RoutePayloadKeys.STOP_IDS, List.of("sta_1", "sta_2"));
        expandedPayload.put(RoutePayloadKeys.INSPECTION_TYPE, "HUMAN");
        expandedPayload.put(RoutePayloadKeys.WORK_MINUTES, 10);
        WorkItemDTO expanded = WorkItemDTO.builder()
                .workId("patrol-work-1")
                .payload(expandedPayload)
                .build();
        when(patrolOrchestrationApi.expand(any(PatrolExpandReqDTO.class)))
                .thenReturn(CommonResult.success(PatrolExpandRespDTO.builder()
                        .workItems(List.of(expanded))
                        .networkRef("net_patrol_1")
                        .stopIds(List.of("sta_1", "sta_2"))
                        .inspectionType("HUMAN")
                        .build()));

        when(routePlanApi.plan(any(RouteRequestDTO.class)))
                .thenReturn(CommonResult.success(RoutePreviewDTO.builder()
                        .networkRef("net_patrol_1")
                        .totalDistanceMeters(180L)
                        .build()));

        ScheduleRunRequest request = ScheduleRunRequest.builder()
                .orchestrationRef(OrchestrationRefs.PATROL_ROUTE_PREVIEW_V1)
                .dryRun(true)
                .stopAfterPhase("ROUTE")
                .schedulingSpec(SchedulingSpecDTO.builder().mode("once").conflictStrategy("none").build())
                .workItems(List.of(seedWorkItem()))
                .build();

        ScheduleRunResponse response = runner.run(request, 1L);

        assertNotNull(response.getRoutePreview());
        assertEquals("net_patrol_1", response.getRoutePreview().getNetworkRef());
        assertNotNull(response.getWorkItems());
        assertEquals(1, response.getWorkItems().size());
        // travel ceil(180/60)=3 + workMinutes 10 => 13
        assertEquals(13, response.getWorkItems().get(0).getDurationEstimateMinutes());
        assertNotNull(response.getWorkItems().get(0).getPayload().get(RoutePayloadKeys.PLANNED_ROUTE));

        verify(patrolOrchestrationApi).expand(any(PatrolExpandReqDTO.class));
        verify(routePlanApi).plan(any(RouteRequestDTO.class));
        verify(schedulingEngine, never()).solve(anyList(), any(), anyString(), anyList());
        verify(runtimePersistApi, never()).persist(any(RuntimePersistReqDTO.class));
    }

    private static WorkItemDTO seedWorkItem() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("facilityId", 1L);
        payload.put("objectIds", List.of(10L, 11L));
        payload.put("preferredNetworkRef", "net_patrol_1");
        return WorkItemDTO.builder()
                .workId("seed-1")
                .payload(payload)
                .build();
    }
}
