package cn.cheers.x.module.platform.orchestration.phase;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewDTO;
import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewSegmentDTO;
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
    @DisplayName("有距离与速度则写入 estimatedDuration")
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
        payload.put(RoutePayloadKeys.ESTIMATED_ACTION_DURATION, 300);
        WorkItemDTO item = WorkItemDTO.builder().workId("w1").payload(payload).build();
        PhaseContext context = PhaseContext.builder().workItems(List.of(item)).build();

        handler.execute(context);

        // 路径 120 米、每分钟 60 米 → 2 分钟，写下 120 秒。动作 300 秒。占窗 (120+300)/60 = 7 分钟。
        assertEquals(7, item.getEstimatedDuration());
        @SuppressWarnings("unchecked")
        Map<String, Object> planned = (Map<String, Object>) item.getPayload().get(RoutePayloadKeys.PLANNED_ROUTE);
        assertEquals(120, planned.get(RoutePayloadKeys.ESTIMATED_TRAVEL_DURATION));
        assertEquals("net_1", context.getRoutePreview().getNetworkRef());
        assertTrue(item.getPayload().containsKey(RoutePayloadKeys.PLANNED_ROUTE));

        ArgumentCaptor<RouteRequestDTO> captor = ArgumentCaptor.forClass(RouteRequestDTO.class);
        verify(routePlanApi).plan(captor.capture());
        assertEquals("person_walk", captor.getValue().getMobilityProfileId());
        assertEquals("net_1", captor.getValue().getNetworkRef());
    }

    @Test
    @DisplayName("每一段路程单独算秒数，路径耗时是各段之和")
    void execute_eachSegmentGetsItsOwnTravelSeconds() {
        RoutePreviewSegmentDTO first = RoutePreviewSegmentDTO.builder().distanceMeters(1L).build();
        RoutePreviewSegmentDTO second = RoutePreviewSegmentDTO.builder().distanceMeters(1L).build();
        when(routePlanApi.plan(any(RouteRequestDTO.class))).thenReturn(CommonResult.success(
                RoutePreviewDTO.builder()
                        .networkRef("net_1")
                        .totalDistanceMeters(2L)
                        .segments(List.of(first, second))
                        .build()));

        Map<String, Object> payload = new HashMap<>();
        payload.put(RoutePayloadKeys.NETWORK_REF, "net_1");
        payload.put(RoutePayloadKeys.STOP_IDS, List.of("s1", "s2", "s3"));
        payload.put(RoutePayloadKeys.INSPECTION_TYPE, "GROUND_ROBOT");
        WorkItemDTO item = WorkItemDTO.builder().workId("w1").payload(payload).build();

        handler.execute(PhaseContext.builder().workItems(List.of(item)).build());

        // 地面机器人每分钟 40 米。1 米 → 2 秒。两段相加 4 秒，不是把 2 米只算成 3 秒。
        assertEquals(2L, first.getDurationSeconds());
        assertEquals(2L, second.getDurationSeconds());
        @SuppressWarnings("unchecked")
        Map<String, Object> planned = (Map<String, Object>) item.getPayload().get(RoutePayloadKeys.PLANNED_ROUTE);
        assertEquals(4, planned.get(RoutePayloadKeys.ESTIMATED_TRAVEL_DURATION));
    }

    @Test
    @DisplayName("动作耗时带小数时，和路径耗时相加后向上取整")
    void execute_fractionalActionDuration_roundsWindowUp() {
        when(routePlanApi.plan(any(RouteRequestDTO.class))).thenReturn(CommonResult.success(
                RoutePreviewDTO.builder()
                        .networkRef("net_1")
                        .totalDistanceMeters(120L)
                        .build()));

        Map<String, Object> payload = new HashMap<>();
        payload.put(RoutePayloadKeys.NETWORK_REF, "net_1");
        payload.put(RoutePayloadKeys.STOP_IDS, List.of("s1", "s2"));
        payload.put(RoutePayloadKeys.INSPECTION_TYPE, "HUMAN");
        payload.put(RoutePayloadKeys.ESTIMATED_ACTION_DURATION, new java.math.BigDecimal("30"));
        WorkItemDTO item = WorkItemDTO.builder().workId("w1").payload(payload).build();
        PhaseContext context = PhaseContext.builder().workItems(List.of(item)).build();

        handler.execute(context);

        // 路径 120 秒 + 动作 30 秒 = 150 秒 → 占窗 3 分钟
        assertEquals(3, item.getEstimatedDuration());
    }

    @Test
    @DisplayName("规划结果写入算出的站序，不把请求里的检查点原样当结果")
    void execute_writesOrderedStopIdsAsPlannedStops() {
        when(routePlanApi.plan(any(RouteRequestDTO.class))).thenReturn(CommonResult.success(
                RoutePreviewDTO.builder()
                        .networkRef("net_1")
                        .totalDistanceMeters(60L)
                        .orderedStopIds(List.of("home", "s1", "s2", "home"))
                        .visitNodeIds(List.of("home", "via", "s1", "s2", "home"))
                        .build()));

        Map<String, Object> payload = new HashMap<>();
        payload.put(RoutePayloadKeys.NETWORK_REF, "net_1");
        payload.put(RoutePayloadKeys.STOP_IDS, List.of("s1", "s2"));
        payload.put(RoutePayloadKeys.START_STOP_ID, "home");
        payload.put(RoutePayloadKeys.END_STOP_ID, "home");
        payload.put(RoutePayloadKeys.INSPECTION_TYPE, "HUMAN");
        WorkItemDTO item = WorkItemDTO.builder().workId("w1").payload(payload).build();
        PhaseContext context = PhaseContext.builder().workItems(List.of(item)).build();

        handler.execute(context);

        @SuppressWarnings("unchecked")
        Map<String, Object> planned = (Map<String, Object>) item.getPayload().get(RoutePayloadKeys.PLANNED_ROUTE);
        assertEquals(List.of("home", "s1", "s2", "home"), planned.get("stopIds"));
        assertEquals(List.of("home", "via", "s1", "s2", "home"), planned.get(RoutePayloadKeys.VISIT_NODE_IDS));
    }

    @Test
    @DisplayName("handlerId 为 platform.route.plan_v1")
    void handlerId_isPlatformRoutePlanV1() {
        assertEquals(BuiltinRoutePhaseHandler.HANDLER_ID, handler.handlerId());
        assertEquals("platform.route.plan_v1", handler.handlerId());
    }
}
