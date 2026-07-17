package cn.cheers.x.module.platform.routing.planner;

import cn.cheers.x.module.platform.contract.dto.network.PathEdgeDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNodeDTO;
import cn.cheers.x.module.platform.contract.dto.topology.TopologyPointDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.contract.enums.NetworkLayer;
import cn.cheers.x.module.platform.contract.enums.NodeType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DepotTourSupportTest {

    @Test
    void assembleReturnsClosedTourOrientedTowardHome() {
        // 线性 h — c — b — a；中间序 a,b,c 应反转为 c,b,a 再闭环
        PathNetworkDTO network = PathNetworkDTO.builder()
                .networkRef("net_depot")
                .networkKind(NetworkKind.SITE)
                .nodes(List.of(
                        node("h", 0, 0),
                        node("c", 1, 0),
                        node("b", 2, 0),
                        node("a", 3, 0)
                ))
                .edges(bidirectional(List.of("h", "c", "b", "a")))
                .build();
        GraphView view = GraphView.from(network, "person_walk");
        CostMatrix matrix = CostMatrix.forOrdering(List.of("h", "a", "b", "c"), view, new DijkstraPlanner());

        List<String> tour = DepotTourSupport.assemble("h", List.of("a", "b", "c"), matrix, true);
        assertEquals(List.of("h", "c", "b", "a", "h"), tour);
    }

    private static PathNodeDTO node(String id, double x, double z) {
        return PathNodeDTO.builder()
                .nodeId(id)
                .nodeType(NodeType.STATION)
                .layer(NetworkLayer.GROUND)
                .position(TopologyPointDTO.builder().x(x).y(0D).z(z).build())
                .build();
    }

    private static List<PathEdgeDTO> bidirectional(List<String> chain) {
        List<PathEdgeDTO> edges = new ArrayList<>();
        for (int i = 0; i < chain.size() - 1; i++) {
            String from = chain.get(i);
            String to = chain.get(i + 1);
            edges.add(edge(from, to));
            edges.add(edge(to, from));
        }
        return edges;
    }

    private static PathEdgeDTO edge(String from, String to) {
        return PathEdgeDTO.builder()
                .edgeId("e_" + from + "_" + to)
                .fromNodeId(from)
                .toNodeId(to)
                .layer(NetworkLayer.GROUND)
                .impedanceByProfile(Map.of("person_walk", 1D))
                .build();
    }
}
