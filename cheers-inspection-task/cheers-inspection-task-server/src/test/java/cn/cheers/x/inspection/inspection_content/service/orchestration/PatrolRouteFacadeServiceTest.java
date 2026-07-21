package cn.cheers.x.inspection.inspection_content.service.orchestration;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolRouteRunReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolScheduleEnableReqVO;
import cn.cheers.x.inspection.inspection_content.service.orchestration.impl.PatrolRouteFacadeServiceImpl;
import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunRequest;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunResponse;
import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.orchestration.api.ScheduleRunApi;
import cn.cheers.x.module.platform.orchestration.enums.OrchestrationRefs;
import cn.cheers.x.module.platform.orchestration.phase.OrchestrationPhase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatrolRouteFacadeService 单元测试")
class PatrolRouteFacadeServiceTest {

    private static final Long FACILITY_ID = 7L;
    private static final List<Long> OBJECT_IDS = List.of(10L, 11L);

    @Mock
    private ScheduleRunApi scheduleRunApi;

    private PatrolRouteFacadeServiceImpl facadeService;

    @BeforeEach
    void setUp() {
        facadeService = new PatrolRouteFacadeServiceImpl();
        ReflectionTestUtils.setField(facadeService, "scheduleRunApi", scheduleRunApi);
    }

    @Test
    @DisplayName("preview：dryRun + stopAfter ROUTE + 种子 payload 键齐全")
    void previewRoute_buildsScheduleRunRequest() {
        PatrolRouteRunReqVO reqVO = routeReq("net-a");
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
    @DisplayName("confirm：dryRun=false，模板 route_confirm")
    void confirmRoute_buildsScheduleRunRequest() {
        PatrolRouteRunReqVO reqVO = routeReq(null);
        when(scheduleRunApi.run(any())).thenReturn(CommonResult.success(
                ScheduleRunResponse.builder().runtimeJobId("job-confirm").build()));

        facadeService.confirmRoute(reqVO);

        ScheduleRunRequest captured = captureRunRequest();
        assertEquals(OrchestrationRefs.PATROL_ROUTE_CONFIRM_V1, captured.getOrchestrationRef());
        assertEquals(Boolean.FALSE, captured.getDryRun());
        assertSeedPayload(captured.getWorkItems().get(0).getPayload(), reqVO, false);
    }

    @Test
    @DisplayName("enable：fromConfirmedSnapshot + taskId + schedulingSpec")
    void enableSchedule_buildsScheduleRunRequest() {
        PatrolScheduleEnableReqVO reqVO = new PatrolScheduleEnableReqVO();
        reqVO.setTaskId(99L);
        reqVO.setSchedulingSpec(SchedulingSpecDTO.builder()
                .mode("weekly")
                .conflictStrategy("defer_slot")
                .build());
        when(scheduleRunApi.run(any())).thenReturn(CommonResult.success(
                ScheduleRunResponse.builder().runtimeJobId("job-enable").build()));

        facadeService.enableSchedule(reqVO);

        ScheduleRunRequest captured = captureRunRequest();
        assertEquals(OrchestrationRefs.PATROL_SCHEDULE_ENABLE_V1, captured.getOrchestrationRef());
        assertEquals(Boolean.FALSE, captured.getDryRun());
        assertEquals("weekly", captured.getSchedulingSpec().getMode());
        Map<String, Object> payload = captured.getWorkItems().get(0).getPayload();
        assertEquals(99L, payload.get("taskId"));
        assertEquals(Boolean.TRUE, payload.get("fromConfirmedSnapshot"));
    }

    private ScheduleRunRequest captureRunRequest() {
        ArgumentCaptor<ScheduleRunRequest> captor = ArgumentCaptor.forClass(ScheduleRunRequest.class);
        verify(scheduleRunApi).run(captor.capture());
        return captor.getValue();
    }

    private static PatrolRouteRunReqVO routeReq(String preferredNetworkRef) {
        PatrolRouteRunReqVO reqVO = new PatrolRouteRunReqVO();
        reqVO.setFacilityId(FACILITY_ID);
        reqVO.setObjectIds(OBJECT_IDS);
        reqVO.setPreferredNetworkRef(preferredNetworkRef);
        return reqVO;
    }

    private static void assertSeedPayload(Map<String, Object> payload, PatrolRouteRunReqVO reqVO,
                                          boolean fromConfirmedSnapshot) {
        assertEquals(reqVO.getFacilityId(), payload.get("facilityId"));
        assertEquals(reqVO.getObjectIds(), payload.get("objectIds"));
        if (reqVO.getPreferredNetworkRef() != null) {
            assertEquals(reqVO.getPreferredNetworkRef(), payload.get("preferredNetworkRef"));
        }
        assertEquals(fromConfirmedSnapshot, payload.get("fromConfirmedSnapshot"));
    }
}
