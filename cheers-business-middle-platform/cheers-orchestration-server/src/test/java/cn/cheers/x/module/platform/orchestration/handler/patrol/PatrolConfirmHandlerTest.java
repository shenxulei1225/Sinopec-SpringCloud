package cn.cheers.x.module.platform.orchestration.handler.patrol;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.orchestration.PatrolOrchestrationApi;
import cn.cheers.x.inspection.orchestration.dto.PatrolConfirmReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolConfirmRespDTO;
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
@DisplayName("PatrolConfirmHandler 单元测试")
class PatrolConfirmHandlerTest {

    @Mock
    private PatrolOrchestrationApi patrolOrchestrationApi;

    @InjectMocks
    private PatrolConfirmHandler handler;

    @Test
    @DisplayName("调用 Feign confirm 并回填 confirmResult")
    void execute_putsConfirmResultOnContext() {
        PatrolConfirmRespDTO resp = PatrolConfirmRespDTO.builder()
                .routePlanId(900L)
                .taskId(100L)
                .dryRun(false)
                .build();
        when(patrolOrchestrationApi.confirm(any(PatrolConfirmReqDTO.class)))
                .thenReturn(CommonResult.success(resp));

        PhaseContext context = PhaseContext.builder()
                .request(seedRequest())
                .workItems(List.of(routedWorkItem()))
                .dryRun(false)
                .attributes(new LinkedHashMap<>())
                .build();

        handler.execute(context);

        PatrolConfirmRespDTO confirmResult = context.getAttr("confirmResult");
        assertNotNull(confirmResult);
        assertEquals(900L, confirmResult.getRoutePlanId());
        assertEquals(100L, confirmResult.getTaskId());
        verify(patrolOrchestrationApi).confirm(any(PatrolConfirmReqDTO.class));
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
                .durationEstimateMinutes(45)
                .payload(payload)
                .build();
    }
}
