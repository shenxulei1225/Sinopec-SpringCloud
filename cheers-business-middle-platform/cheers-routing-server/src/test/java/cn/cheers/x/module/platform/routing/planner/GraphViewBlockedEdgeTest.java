package cn.cheers.x.module.platform.routing.planner;

import cn.cheers.x.module.platform.contract.dto.network.PathEdgeDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNodeDTO;
import cn.cheers.x.module.platform.contract.dto.topology.TopologyPointDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.contract.enums.NetworkLayer;
import cn.cheers.x.module.platform.contract.enums.NodeType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void robotUsesStoredMetersOnSharedGroundEdge() {
        PathNetworkDTO net = PathNetworkDTO.builder()
                .networkRef("net_t")
                .networkKind(NetworkKind.FACILITY)
                .nodes(List.of(
                        nodeAt("A", 0, 0),
                        nodeAt("B", 23, 0)))
                .edges(List.of(
                        PathEdgeDTO.builder()
                                .edgeId("e1")
                                .fromNodeId("A")
                                .toNodeId("B")
                                .layer(NetworkLayer.GROUND)
                                .traversability("TRAVERSABLE")
                                .impedanceByProfile(Map.of("person_walk", 126D, "HUMAN", 126D))
                                .build()
                ))
                .build();

        GraphView view = GraphView.from(net, "ground_robot");
        assertEquals(126D, view.outgoing("A").get(0).getWeight());
    }

    @Test
    void edgeWithoutLengthIsLeftOut() {
        PathNetworkDTO net = PathNetworkDTO.builder()
                .networkRef("net_t")
                .networkKind(NetworkKind.FACILITY)
                .nodes(List.of(node("A"), node("B")))
                .edges(List.of(
                        PathEdgeDTO.builder()
                                .edgeId("e1")
                                .fromNodeId("A")
                                .toNodeId("B")
                                .layer(NetworkLayer.GROUND)
                                .traversability("TRAVERSABLE")
                                .build()
                ))
                .build();

        GraphView view = GraphView.from(net, "ground_robot");
        assertTrue(view.outgoing("A").isEmpty());
    }

    @Test
    void edgeWithoutStoredMetersUsesEndpointGps() {
        PathNetworkDTO net = PathNetworkDTO.builder()
                .networkRef("net_t")
                .networkKind(NetworkKind.FACILITY)
                .nodes(List.of(
                        nodeGps("A", 117.2000, 34.3000),
                        nodeGps("B", 117.2001, 34.3000)))
                .edges(List.of(
                        PathEdgeDTO.builder()
                                .edgeId("e1")
                                .fromNodeId("A")
                                .toNodeId("B")
                                .layer(NetworkLayer.GROUND)
                                .traversability("TRAVERSABLE")
                                .build()
                ))
                .build();

        double expected = Math.round(GraphView.gpsDistanceMeters(117.2000, 34.3000, 117.2001, 34.3000) * 100D) / 100D;
        GraphView view = GraphView.from(net, "ground_robot");
        assertEquals(expected, view.outgoing("A").get(0).getWeight());
    }

    @Test
    void uavPointIsNotOnRobotGraph() {
        PathNetworkDTO net = PathNetworkDTO.builder()
                .networkRef("net_t")
                .networkKind(NetworkKind.FACILITY)
                .nodes(List.of(
                        nodeWithMembership("ground", List.of("GROUND_ROBOT")),
                        nodeWithMembership("air", List.of("UAV"))))
                .edges(List.of(
                        PathEdgeDTO.builder()
                                .edgeId("e1")
                                .fromNodeId("ground")
                                .toNodeId("air")
                                .layer(NetworkLayer.GROUND)
                                .traversability("TRAVERSABLE")
                                .build()
                ))
                .build();

        GraphView robot = GraphView.from(net, "ground_robot");
        assertTrue(robot.getNode("ground") != null);
        assertTrue(robot.getNode("air") == null);
        assertTrue(robot.outgoing("ground").isEmpty());

        GraphView uav = GraphView.from(net, "uav_low");
        assertTrue(uav.getNode("air") != null);
        assertTrue(uav.getNode("ground") == null);
    }

    private static PathNodeDTO node(String id) {
        return PathNodeDTO.builder()
                .nodeId(id)
                .nodeType(NodeType.STATION)
                .layer(NetworkLayer.GROUND)
                .memberships(List.of("HUMAN", "GROUND_ROBOT"))
                .build();
    }

    private static PathNodeDTO nodeGps(String id, double longitude, double latitude) {
        return PathNodeDTO.builder()
                .nodeId(id)
                .nodeType(NodeType.STATION)
                .layer(NetworkLayer.GROUND)
                .memberships(List.of("HUMAN", "GROUND_ROBOT"))
                .position(TopologyPointDTO.builder().x(30D).y(0D).z(0D).build())
                .payload(Map.of("longitude", (Object) longitude, "latitude", (Object) latitude))
                .build();
    }

    private static PathNodeDTO nodeAt(String id, double x, double z) {
        return PathNodeDTO.builder()
                .nodeId(id)
                .nodeType(NodeType.STATION)
                .layer(NetworkLayer.GROUND)
                .memberships(List.of("HUMAN", "GROUND_ROBOT"))
                .position(TopologyPointDTO.builder().x(x).y(0D).z(z).build())
                .build();
    }

    private static PathNodeDTO nodeWithMembership(String id, List<String> memberships) {
        return PathNodeDTO.builder()
                .nodeId(id)
                .nodeType(NodeType.STATION)
                .memberships(memberships)
                .build();
    }
}
