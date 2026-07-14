package cn.cheers.x.module.platform.routing.planner;

import cn.cheers.x.module.platform.contract.dto.network.PathEdgeDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNodeDTO;
import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewDTO;
import cn.cheers.x.module.platform.contract.dto.route.RouteRequestDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.contract.enums.NetworkLayer;
import cn.cheers.x.module.platform.contract.enums.NodeType;
import cn.cheers.x.module.platform.routing.service.RoutePlanServiceImpl;
import cn.cheers.x.module.platform.topology.api.MobilityProfileApi;
import cn.cheers.x.module.platform.topology.api.PathNetworkApi;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StopOrderStrategyTest {

    @Mock
    private PathNetworkApi pathNetworkApi;
    @Mock
    private MobilityProfileApi mobilityProfileApi;

    private RoutePlanServiceImpl routePlanService;

    @BeforeEach
    void setUp() {
        routePlanService = new RoutePlanServiceImpl();
        ReflectionTestUtils.setField(routePlanService, "pathNetworkApi", pathNetworkApi);
        ReflectionTestUtils.setField(routePlanService, "mobilityProfileApi", mobilityProfileApi);
        ReflectionTestUtils.setField(routePlanService, "profileGate", new ProfileGate());
        ReflectionTestUtils.setField(routePlanService, "doorConstraintFilter", new DoorConstraintFilter());
        ReflectionTestUtils.setField(routePlanService, "dijkstraPlanner", new DijkstraPlanner());
        ReflectionTestUtils.setField(routePlanService, "stopOrderStrategyRegistry",
                new StopOrderStrategyRegistry(
                        new AsGivenOrderStrategy(),
                        new OptimizeOrderStrategy(),
                        new RefineOrderStrategy()));
    }

    @Test
    void optimizeOrderBeatsZigzagAsGivenOnFourStops() {
        PathNetworkDTO network = linearNetwork();
        when(pathNetworkApi.getNetwork("net_site")).thenReturn(CommonResult.success(network));
        when(mobilityProfileApi.getProfile("person_walk")).thenReturn(CommonResult.success(
                cn.cheers.x.module.platform.contract.dto.network.MobilityProfileDTO.builder()
                        .profileId("person_walk")
                        .allowedNetworkKinds(List.of(NetworkKind.SITE))
                        .layer(NetworkLayer.GROUND)
                        .respectDoors(true)
                        .build()));

        RouteRequestDTO asGivenRequest = RouteRequestDTO.builder()
                .networkRef("net_site")
                .stopIds(List.of("w", "y", "x", "z"))
                .mobilityProfileId("person_walk")
                .strategy("as_given")
                .build();

        RouteRequestDTO optimizeRequest = RouteRequestDTO.builder()
                .networkRef("net_site")
                .stopIds(List.of("w", "y", "x", "z"))
                .mobilityProfileId("person_walk")
                .strategy("optimize_order")
                .build();

        RoutePreviewDTO asGivenPreview = routePlanService.plan(asGivenRequest);
        RoutePreviewDTO optimizePreview = routePlanService.plan(optimizeRequest);

        assertEquals(5L, asGivenPreview.getTotalDistanceMeters());
        assertEquals(3L, optimizePreview.getTotalDistanceMeters());
        assertTrue(optimizePreview.getTotalDistanceMeters() <= asGivenPreview.getTotalDistanceMeters());
    }

    @Test
    void refineOrderDoesNotIncreaseCostVersusAsGiven() {
        PathNetworkDTO network = linearNetwork();
        when(pathNetworkApi.getNetwork("net_site")).thenReturn(CommonResult.success(network));
        when(mobilityProfileApi.getProfile("person_walk")).thenReturn(CommonResult.success(
                cn.cheers.x.module.platform.contract.dto.network.MobilityProfileDTO.builder()
                        .profileId("person_walk")
                        .allowedNetworkKinds(List.of(NetworkKind.SITE))
                        .layer(NetworkLayer.GROUND)
                        .respectDoors(true)
                        .build()));

        RouteRequestDTO asGivenRequest = RouteRequestDTO.builder()
                .networkRef("net_site")
                .stopIds(List.of("w", "y", "x", "z"))
                .mobilityProfileId("person_walk")
                .strategy("as_given")
                .build();

        RouteRequestDTO refineRequest = RouteRequestDTO.builder()
                .networkRef("net_site")
                .stopIds(List.of("w", "y", "x", "z"))
                .mobilityProfileId("person_walk")
                .strategy("refine_order")
                .build();

        RoutePreviewDTO asGivenPreview = routePlanService.plan(asGivenRequest);
        RoutePreviewDTO refinePreview = routePlanService.plan(refineRequest);

        assertTrue(refinePreview.getTotalDistanceMeters() <= asGivenPreview.getTotalDistanceMeters());
    }

    @Test
    void optimizeOrderStrategyUnitTestFindsBestPermutation() {
        GraphView view = GraphView.from(linearNetwork(), "person_walk");
        CostMatrix matrix = CostMatrix.from(List.of("w", "y", "x", "z"), view, new DijkstraPlanner());
        OptimizeOrderStrategy strategy = new OptimizeOrderStrategy();

        List<String> ordered = strategy.order(List.of("w", "y", "x", "z"), matrix);

        assertEquals(3D, matrix.totalPathCost(ordered));
        assertTrue(matrix.totalPathCost(ordered) < matrix.totalPathCost(List.of("w", "y", "x", "z")));
    }

    private static PathNetworkDTO linearNetwork() {
        return PathNetworkDTO.builder()
                .networkRef("net_site")
                .networkKind(NetworkKind.SITE)
                .nodes(List.of(
                        node("w"),
                        node("x"),
                        node("y"),
                        node("z")
                ))
                .edges(List.of(
                        edge("e_wx", "w", "x", 1D),
                        edge("e_xw", "x", "w", 1D),
                        edge("e_xy", "x", "y", 1D),
                        edge("e_yx", "y", "x", 1D),
                        edge("e_yz", "y", "z", 1D),
                        edge("e_zy", "z", "y", 1D)
                ))
                .build();
    }

    private static PathNodeDTO node(String nodeId) {
        return PathNodeDTO.builder()
                .nodeId(nodeId)
                .nodeType(NodeType.STATION)
                .layer(NetworkLayer.GROUND)
                .build();
    }

    private static PathEdgeDTO edge(String edgeId, String from, String to, double impedance) {
        return PathEdgeDTO.builder()
                .edgeId(edgeId)
                .fromNodeId(from)
                .toNodeId(to)
                .layer(NetworkLayer.GROUND)
                .impedanceByProfile(Map.of("person_walk", impedance))
                .build();
    }

}
