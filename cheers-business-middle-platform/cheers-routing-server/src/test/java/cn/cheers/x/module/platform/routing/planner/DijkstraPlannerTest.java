package cn.cheers.x.module.platform.routing.planner;

import cn.cheers.x.module.platform.contract.dto.network.PathEdgeDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNodeDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.contract.enums.NetworkLayer;
import cn.cheers.x.module.platform.contract.enums.NodeType;
import cn.cheers.x.framework.common.exception.ServiceException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static cn.cheers.x.module.platform.routing.enums.ErrorCodeConstants.ROUTE_UNREACHABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DijkstraPlannerTest {

    private final DijkstraPlanner planner = new DijkstraPlanner();

    @Test
    void unreachableThrowsRouteUnreachable() {
        PathNetworkDTO network = PathNetworkDTO.builder()
                .networkRef("net_test")
                .networkKind(NetworkKind.SITE)
                .nodes(List.of(node("a"), node("b")))
                .edges(List.of())
                .build();
        GraphView view = GraphView.from(network, "person_walk");

        ServiceException ex = assertThrows(ServiceException.class,
                () -> planner.shortestPath("a", "b", view));
        assertEquals(ROUTE_UNREACHABLE.getCode(), ex.getCode());
    }

    @Test
    void threeEdgePathCostSumsImpedances() {
        PathNetworkDTO network = PathNetworkDTO.builder()
                .networkRef("net_test")
                .networkKind(NetworkKind.SITE)
                .nodes(List.of(node("a"), node("b"), node("c"), node("d")))
                .edges(List.of(
                        edge("e1", "a", "b", 10D),
                        edge("e2", "b", "c", 20D),
                        edge("e3", "c", "d", 30D)
                ))
                .build();
        GraphView view = GraphView.from(network, "person_walk");

        ShortestPathResult result = planner.shortestPath("a", "d", view);

        assertEquals(List.of("a", "b", "c", "d"), result.getNodeIds());
        assertEquals(List.of("e1", "e2", "e3"), result.getEdgeIds());
        assertEquals(60D, result.getTotalCost());
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
