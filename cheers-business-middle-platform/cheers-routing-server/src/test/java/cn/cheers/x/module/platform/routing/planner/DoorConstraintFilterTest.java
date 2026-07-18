package cn.cheers.x.module.platform.routing.planner;

import cn.cheers.x.module.platform.contract.dto.network.MobilityProfileDTO;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

class DoorConstraintFilterTest {

    private final DoorConstraintFilter filter = new DoorConstraintFilter();
    private final DijkstraPlanner planner = new DijkstraPlanner();

    @Test
    void groundWalkCrossZoneWithoutDoorRemovesEdgeAndPathIsUnreachable() {
        PathNetworkDTO network = PathNetworkDTO.builder()
                .networkRef("net_site")
                .networkKind(NetworkKind.SITE)
                .nodes(List.of(
                        node("a", NodeType.STATION, NetworkLayer.GROUND, 1L),
                        node("b", NodeType.STATION, NetworkLayer.GROUND, 2L)
                ))
                .edges(List.of(edge("e_ab", "a", "b", NetworkLayer.GROUND, 10D)))
                .build();
        MobilityProfileDTO profile = groundProfile("person_walk");

        List<PathEdgeDTO> filtered = filter.filter(network, profile);
        assertTrue(filtered.isEmpty());

        GraphView view = GraphView.from(withEdges(network, filtered), profile.getProfileId());
        ServiceException ex = assertThrows(ServiceException.class,
                () -> planner.shortestPath("a", "b", view));
        assertEquals(ROUTE_UNREACHABLE.getCode(), ex.getCode());
    }

    @Test
    void groundWalkCrossZoneViaDoorKeepsEdgesAndPathIsReachable() {
        PathNetworkDTO network = PathNetworkDTO.builder()
                .networkRef("net_site")
                .networkKind(NetworkKind.SITE)
                .nodes(List.of(
                        node("a", NodeType.STATION, NetworkLayer.GROUND, 1L),
                        node("d", NodeType.DOOR, NetworkLayer.GROUND, 1L),
                        node("b", NodeType.STATION, NetworkLayer.GROUND, 2L)
                ))
                .edges(List.of(
                        edge("e_ad", "a", "d", NetworkLayer.GROUND, 5D),
                        edge("e_db", "d", "b", NetworkLayer.GROUND, 5D)
                ))
                .build();
        MobilityProfileDTO profile = groundProfile("person_walk");

        List<PathEdgeDTO> filtered = filter.filter(network, profile);
        assertEquals(2, filtered.size());

        GraphView view = GraphView.from(withEdges(network, filtered), profile.getProfileId());
        ShortestPathResult result = planner.shortestPath("a", "b", view);

        assertEquals(List.of("a", "d", "b"), result.getNodeIds());
        assertEquals(List.of("e_ad", "e_db"), result.getEdgeIds());
        assertEquals(10D, result.getTotalCost());
    }

    @Test
    void uavLowIgnoresGroundCrossZoneAndKeepsOnlyAirEdges() {
        PathNetworkDTO network = PathNetworkDTO.builder()
                .networkRef("net_site")
                .networkKind(NetworkKind.SITE)
                .nodes(List.of(
                        node("a", NodeType.STATION, NetworkLayer.GROUND, 1L),
                        node("b", NodeType.STATION, NetworkLayer.GROUND, 2L)
                ))
                .edges(List.of(
                        edge("e_ground", "a", "b", NetworkLayer.GROUND, 100D),
                        edge("e_air", "a", "b", NetworkLayer.AIR, 20D)
                ))
                .build();
        MobilityProfileDTO profile = MobilityProfileDTO.builder()
                .profileId("uav_low")
                .allowedNetworkKinds(List.of(NetworkKind.SITE))
                .layer(NetworkLayer.AIR)
                .respectDoors(false)
                .build();

        List<PathEdgeDTO> filtered = filter.filter(network, profile);
        assertEquals(1, filtered.size());
        assertEquals("e_air", filtered.get(0).getEdgeId());

        GraphView view = GraphView.from(withEdges(network, filtered), profile.getProfileId());
        ShortestPathResult result = planner.shortestPath("a", "b", view);

        assertEquals(List.of("a", "b"), result.getNodeIds());
        assertEquals(List.of("e_air"), result.getEdgeIds());
        assertEquals(20D, result.getTotalCost());
    }

    private static MobilityProfileDTO groundProfile(String profileId) {
        return MobilityProfileDTO.builder()
                .profileId(profileId)
                .allowedNetworkKinds(List.of(NetworkKind.SITE))
                .layer(NetworkLayer.GROUND)
                .respectDoors(true)
                .build();
    }

    private static PathNetworkDTO withEdges(PathNetworkDTO network, List<PathEdgeDTO> edges) {
        return PathNetworkDTO.builder()
                .networkRef(network.getNetworkRef())
                .networkKind(network.getNetworkKind())
                .nodes(network.getNodes())
                .edges(edges)
                .build();
    }

    private static PathNodeDTO node(String nodeId, NodeType nodeType, NetworkLayer layer, Long zoneId) {
        return PathNodeDTO.builder()
                .nodeId(nodeId)
                .nodeType(nodeType)
                .layer(layer)
                .zoneId(zoneId)
                .build();
    }

    private static PathEdgeDTO edge(String edgeId, String from, String to, NetworkLayer layer, double impedance) {
        return PathEdgeDTO.builder()
                .edgeId(edgeId)
                .fromNodeId(from)
                .toNodeId(to)
                .layer(layer)
                .impedanceByProfile(Map.of("person_walk", impedance, "uav_low", impedance))
                .build();
    }
}
