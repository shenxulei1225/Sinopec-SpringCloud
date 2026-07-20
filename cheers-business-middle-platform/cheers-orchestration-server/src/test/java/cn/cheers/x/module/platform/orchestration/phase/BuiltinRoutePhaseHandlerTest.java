package cn.cheers.x.module.platform.orchestration.phase;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewDTO;
import cn.cheers.x.module.platform.contract.dto.route.RouteRequestDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.orchestration.route.RoutePayloadKeys;
import cn.cheers.x.module.platform.routing.api.RoutePlanApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuiltinRoutePhaseHandler 单元测试")
class BuiltinRoutePhaseHandlerTest {

    @Mock
    private RoutePlanApi routePlanApi;

    private BuiltinRoutePhaseHandler handler;

    @BeforeEach
    void setUp() {
        handler = new BuiltinRoutePhaseHandler();
        ReflectionTestUtils.setField(handler, "routePlanApi", routePlanApi);
    }

    @Test
    @DisplayName("缺 networkRef/stopIds 失败")
    void execute_missingNetworkRefOrStopIds_throws() {
        WorkItemDTO item = WorkItemDTO.builder()
                .workId("w1")
                .payload(new HashMap<>(Map.of(
                        RoutePayloadKeys.INSPECTION_TYPE, "HUMAN"
                )))
                .build();
        PhaseContext context = PhaseContext.builder().workItems(List.of(item)).build();

        assertThrows(ServiceException.class, () -> handler.execute(context));
    }

    @Test
    @DisplayName("有距离与速度则写入 durationEstimateMinutes")
    void execute_distanceAndSpeed_writesDurationEstimateMinutes() {
        when(routePlanApi.plan(any(RouteRequestDTO.class))).thenReturn(CommonResult.success(
                RoutePreviewDTO.builder()
                        .networkRef("net_1")
                        .totalDistanceMeters(120L)
                        .build()));

        Map<String, Object> payload = new HashMap<>();
        payload.put(RoutePayloadKeys.NETWORK_REF, "net_1");
        payload.put(RoutePayloadKeys.STOP_IDS, List.of("s1", "s2"));
        payload.put(RoutePayloadKeys.INSPECTION_TYPE, "HUMAN");
        payload.put(RoutePayloadKeys.WORK_MINUTES, 5);
        WorkItemDTO item = WorkItemDTO.builder().workId("w1").payload(payload).build();
        PhaseContext context = PhaseContext.builder().workItems(List.of(item)).build();

        handler.execute(context);

        // travel = ceil(120/60)=2; + workMinutes 5 => 7
        assertEquals(7, item.getDurationEstimateMinutes());
        assertEquals("net_1", context.getRoutePreview().getNetworkRef());
        assertTrue(item.getPayload().containsKey(RoutePayloadKeys.PLANNED_ROUTE));

        ArgumentCaptor<RouteRequestDTO> captor = ArgumentCaptor.forClass(RouteRequestDTO.class);
        verify(routePlanApi).plan(captor.capture());
        assertEquals("person_walk", captor.getValue().getMobilityProfileId());
        assertEquals("net_1", captor.getValue().getNetworkRef());
    }

    @Test
    @DisplayName("handlerId 为 platform.route.plan_v1")
    void handlerId_isPlatformRoutePlanV1() {
        assertEquals(BuiltinRoutePhaseHandler.HANDLER_ID, handler.handlerId());
        assertEquals("platform.route.plan_v1", handler.handlerId());
    }
}
