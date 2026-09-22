package cn.cheers.x.module.platform.orchestration.handler.patrol;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.orchestration.PatrolOrchestrationApi;
import cn.cheers.x.inspection.orchestration.dto.PatrolSaveRouteReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolSaveRouteRespDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunRequest;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.orchestration.phase.PhaseContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatrolSaveRouteHandler 单元测试")
class PatrolSaveRouteHandlerTest {

    @Mock
    private PatrolOrchestrationApi patrolOrchestrationApi;

    @InjectMocks
    private PatrolSaveRouteHandler handler;

    @Test
    @DisplayName("调用 Feign confirm 并回填 saveRouteResult")
    void execute_putsConfirmResultOnContext() {
        PatrolSaveRouteRespDTO resp = PatrolSaveRouteRespDTO.builder()
                .taskId(100L)
                .dryRun(false)
                .build();
        when(patrolOrchestrationApi.saveRoute(any(PatrolSaveRouteReqDTO.class)))
                .thenReturn(CommonResult.success(resp));

        PhaseContext context = PhaseContext.builder()
                .request(seedRequest())
                .workItems(List.of(routedWorkItem()))
                .dryRun(false)
                .attributes(new LinkedHashMap<>())
                .build();

        handler.execute(context);

        PatrolSaveRouteRespDTO saveRouteResult = context.getAttr("saveRouteResult");
        assertNotNull(saveRouteResult);
        assertEquals(100L, saveRouteResult.getTaskId());
        verify(patrolOrchestrationApi).saveRoute(any(PatrolSaveRouteReqDTO.class));
    }

    private static ScheduleRunRequest seedRequest() {
        ScheduleRunRequest request = new ScheduleRunRequest();
        request.setWorkItems(List.of(seedWorkItem()));
        return request;
    }

    private static WorkItemDTO seedWorkItem() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("facilityId", 1L);
        payload.put("taskId", 100L);
        payload.put("name", "test-route");
        return WorkItemDTO.builder()
                .workId("seed-1")
                .payload(payload)
                .build();
    }

    private static WorkItemDTO routedWorkItem() {
        Map<String, Object> plannedRoute = Map.of(
                "networkRef", "net-a",
                "totalDistanceMeters", 1200L,
                "stopIds", List.of("s1"));
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("networkRef", "net-a");
        payload.put("stopIds", List.of("s1"));
        payload.put("inspectionType", "HUMAN");
        payload.put("plannedRoute", plannedRoute);
        return WorkItemDTO.builder()
                .workId("seed-1")
                .estimatedDuration(45)
                .payload(payload)
                .build();
    }
}
