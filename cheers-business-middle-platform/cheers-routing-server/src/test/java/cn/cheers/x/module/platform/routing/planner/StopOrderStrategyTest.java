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
import cn.cheers.x.framework.common.pojo.CommonResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
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
        RefineOrderStrategy refineOrderStrategy = new RefineOrderStrategy();
        ReflectionTestUtils.setField(routePlanService, "stopOrderStrategyRegistry",
                new StopOrderStrategyRegistry(
                        new AsGivenOrderStrategy(),
                        new OptimizeOrderStrategy(refineOrderStrategy),
                        refineOrderStrategy));
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
        OptimizeOrderStrategy strategy = new OptimizeOrderStrategy(new RefineOrderStrategy());

        List<String> ordered = strategy.order(List.of("w", "y", "x", "z"), matrix, view);

        assertEquals(3D, matrix.totalPathCost(ordered));
        assertTrue(matrix.totalPathCost(ordered) < matrix.totalPathCost(List.of("w", "y", "x", "z")));
    }

    @Test
    void optimizeOrderHeuristicHandlesMoreThanExactLimit() {
        // 20 点线性网，超过 Held-Karp 上限，走最近邻+2-opt
        int n = 20;
        List<String> ids = new ArrayList<>();
        List<PathNodeDTO> nodes = new ArrayList<>();
        List<PathEdgeDTO> edges = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String id = "n" + i;
            ids.add(id);
            nodes.add(node(id));
            if (i > 0) {
                String prev = "n" + (i - 1);
                edges.add(edge("e_" + prev + "_" + id, prev, id, 1D));
                edges.add(edge("e_" + id + "_" + prev, id, prev, 1D));
            }
        }
        PathNetworkDTO network = PathNetworkDTO.builder()
                .networkRef("net_long")
                .networkKind(NetworkKind.SITE)
                .nodes(nodes)
                .edges(edges)
                .build();
        GraphView view = GraphView.from(network, "person_walk");
        List<String> shuffled = new ArrayList<>();
        for (int i = 0; i < n; i += 2) {
            shuffled.add(ids.get(i));
        }
        for (int i = n - 1; i >= 0; i -= 2) {
            if (!shuffled.contains(ids.get(i))) {
                shuffled.add(ids.get(i));
            }
        }
        CostMatrix matrix = CostMatrix.from(shuffled, view, new DijkstraPlanner());
        OptimizeOrderStrategy strategy = new OptimizeOrderStrategy(new RefineOrderStrategy());

        List<String> ordered = strategy.order(shuffled, matrix, view);

        assertEquals(n, ordered.size());
        assertTrue(Double.isFinite(matrix.totalPathCost(ordered)));
        assertTrue(matrix.totalPathCost(ordered) <= matrix.totalPathCost(shuffled));
    }

    @Test
    void spatialOrderingAvoidsHopCostZigzagInsideCluster() {
        // 区内四点成矩形；图上两两代价=1（跳数无区分度）；空间上应走不交叉周界
        List<PathNodeDTO> nodes = List.of(
                positioned("a", 0, 0, 1L),
                positioned("b", 10, 0, 1L),
                positioned("c", 10, 10, 1L),
                positioned("d", 0, 10, 1L)
        );
        List<PathEdgeDTO> edges = new ArrayList<>();
        List<String> ids = List.of("a", "b", "c", "d");
        for (String from : ids) {
            for (String to : ids) {
                if (!from.equals(to)) {
                    edges.add(edge("e_" + from + "_" + to, from, to, 1D));
                }
            }
        }
        PathNetworkDTO network = PathNetworkDTO.builder()
                .networkRef("net_square")
                .networkKind(NetworkKind.SITE)
                .nodes(nodes)
                .edges(edges)
                .build();
        GraphView view = GraphView.from(network, "person_walk");
        DijkstraPlanner planner = new DijkstraPlanner();
        List<String> zigzag = List.of("a", "c", "b", "d");
        CostMatrix graph = CostMatrix.from(zigzag, view, planner);
        CostMatrix spatial = CostMatrix.forOrdering(zigzag, view, planner);
        OptimizeOrderStrategy strategy = new OptimizeOrderStrategy(new RefineOrderStrategy());

        List<String> ordered = strategy.order(zigzag, spatial, view);

        assertTrue(spatial.totalPathCost(ordered) + 1e-6 < spatial.totalPathCost(zigzag));
        // 纯图代价下 zigzag 与周界同为 3，空间矩阵才能拉开
        assertEquals(3D, graph.totalPathCost(zigzag));
        assertEquals(3D, graph.totalPathCost(List.of("a", "b", "c", "d")));
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

    private static PathNodeDTO positioned(String nodeId, double x, double z, Long zoneId) {
        return PathNodeDTO.builder()
                .nodeId(nodeId)
                .nodeType(NodeType.STATION)
                .layer(NetworkLayer.GROUND)
                .zoneId(zoneId)
                .position(cn.cheers.x.module.platform.contract.dto.topology.TopologyPointDTO.builder()
                        .x(x)
                        .y(0D)
                        .z(z)
                        .build())
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
