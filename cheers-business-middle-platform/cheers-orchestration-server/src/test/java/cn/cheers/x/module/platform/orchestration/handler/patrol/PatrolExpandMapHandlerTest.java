package cn.cheers.x.module.platform.orchestration.handler.patrol;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.orchestration.PatrolOrchestrationApi;
import cn.cheers.x.inspection.orchestration.dto.PatrolExpandReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolExpandRespDTO;
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

import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatrolExpandMapHandler 单元测试")
class PatrolExpandMapHandlerTest {

    @Mock
    private PatrolOrchestrationApi patrolOrchestrationApi;

    @InjectMocks
    private PatrolExpandMapHandler handler;

    @Test
    @DisplayName("调用 Feign expand 并回填 workItems 与 expandResult")
    void execute_setsWorkItemsOnContext() {
        WorkItemDTO expanded = WorkItemDTO.builder()
                .workId("seed-1")
                .payload(Map.of("networkRef", "net-a", "stopIds", List.of("s1")))
                .build();
        PatrolExpandRespDTO resp = PatrolExpandRespDTO.builder()
                .workItems(List.of(expanded))
                .networkRef("net-a")
                .stopIds(List.of("s1"))
                .inspectionType("HUMAN")
                .build();
        when(patrolOrchestrationApi.expand(any(PatrolExpandReqDTO.class)))
                .thenReturn(CommonResult.success(resp));

        PhaseContext context = PhaseContext.builder()
                .request(seedRequest())
                .workItems(List.of(seedWorkItem()))
                .attributes(new LinkedHashMap<>())
                .build();

        handler.execute(context);

        assertEquals(1, context.getWorkItems().size());
        assertEquals("net-a", context.getWorkItems().get(0).getPayload().get("networkRef"));
        @SuppressWarnings("unchecked")
        Map<String, Object> expandResult = context.getAttr("expandResult");
        assertNotNull(expandResult);
        assertEquals("net-a", expandResult.get("networkRef"));
        assertEquals(List.of("s1"), expandResult.get("stopIds"));
        assertEquals("HUMAN", expandResult.get("inspectionType"));
        verify(patrolOrchestrationApi).expand(any(PatrolExpandReqDTO.class));
    }

    @Test
    @DisplayName("种子 startStopId 透传到 PatrolExpandReqDTO")
    void execute_forwardsStartStopIdToExpandReq() {
        when(patrolOrchestrationApi.expand(any(PatrolExpandReqDTO.class)))
                .thenReturn(CommonResult.success(PatrolExpandRespDTO.builder()
                        .workItems(List.of(WorkItemDTO.builder().workId("seed-1").payload(Map.of()).build()))
                        .networkRef("net-a")
                        .stopIds(List.of("s1"))
                        .inspectionType("HUMAN")
                        .build()));

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("facilityId", 1L);
        payload.put("objectIds", List.of(10L));
        payload.put("startStopId", "sta-depot");
        payload.put("returnToStart", Boolean.FALSE);
        WorkItemDTO seed = WorkItemDTO.builder().workId("seed-1").payload(payload).build();
        PhaseContext context = PhaseContext.builder()
                .request(ScheduleRunRequest.builder().workItems(List.of(seed)).build())
                .workItems(List.of(seed))
                .attributes(new LinkedHashMap<>())
                .build();

        handler.execute(context);

        ArgumentCaptor<PatrolExpandReqDTO> captor = ArgumentCaptor.forClass(PatrolExpandReqDTO.class);
        verify(patrolOrchestrationApi).expand(captor.capture());
        assertEquals("sta-depot", captor.getValue().getStartStopId());
        assertEquals(Boolean.FALSE, captor.getValue().getReturnToStart());
    }

    private static ScheduleRunRequest seedRequest() {
        ScheduleRunRequest request = new ScheduleRunRequest();
        request.setWorkItems(List.of(seedWorkItem()));
        return request;
    }

    private static WorkItemDTO seedWorkItem() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("facilityId", 1L);
        payload.put("objectIds", List.of(10L));
        payload.put("preferredNetworkRef", "net-a");
        return WorkItemDTO.builder()
                .workId("seed-1")
                .payload(payload)
                .build();
    }
}
