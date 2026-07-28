package cn.cheers.x.module.platform.routing.planner;

import cn.cheers.x.module.platform.contract.dto.network.PathEdgeDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNodeDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.contract.enums.NetworkLayer;
import cn.cheers.x.module.platform.contract.enums.NodeType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphViewBlockedEdgeTest {

    @Test
    void blockedEdgeNotInAdjacency() {
        PathNetworkDTO net = PathNetworkDTO.builder()
                .networkRef("net_t")
                .networkKind(NetworkKind.FACILITY)
                .nodes(List.of(node("A"), node("B"), node("C")))
                .edges(List.of(
                        PathEdgeDTO.builder()
                                .edgeId("e1")
                                .fromNodeId("A")
                                .toNodeId("B")
                                .layer(NetworkLayer.GROUND)
                                .traversability("BLOCKED")
                                .impedanceByProfile(Map.of("person_walk", 1.0))
                                .build(),
                        PathEdgeDTO.builder()
                                .edgeId("e2")
                                .fromNodeId("A")
                                .toNodeId("C")
                                .layer(NetworkLayer.GROUND)
                                .traversability("TRAVERSABLE")
                                .impedanceByProfile(Map.of("person_walk", 1.0))
                                .build()
                ))
                .build();

        GraphView view = GraphView.from(net, "person_walk");
        assertTrue(view.outgoing("A").stream().noneMatch(e -> "B".equals(e.getToNodeId())));
        assertTrue(view.outgoing("A").stream().anyMatch(e -> "C".equals(e.getToNodeId())));
    }

    private static PathNodeDTO node(String id) {
        return PathNodeDTO.builder()
                .nodeId(id)
                .nodeType(NodeType.STATION)
                .layer(NetworkLayer.GROUND)
                .build();
    }
}
