package cn.cheers.x.module.platform.routing.planner;

import cn.cheers.x.module.platform.contract.dto.network.MobilityProfileDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathEdgeDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNodeDTO;
import cn.cheers.x.module.platform.contract.dto.network.PortalDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.contract.enums.NetworkLayer;
import cn.cheers.x.module.platform.contract.enums.NodeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PortalGraphAssemblerTest {

    private PortalGraphAssembler assembler;
    private DoorConstraintFilter doorConstraintFilter;

    @BeforeEach
    void setUp() {
        doorConstraintFilter = new DoorConstraintFilter();
        assembler = new PortalGraphAssembler();
        ReflectionTestUtils.setField(assembler, "doorConstraintFilter", doorConstraintFilter);
    }

    @Test
    void siteStopThroughPortalToRoadStopIsReachableWithMultilegProfiles() {
        PathNetworkDTO siteNetwork = PathNetworkDTO.builder()
                .networkRef("net_site")
                .networkKind(NetworkKind.FACILITY)
                .facilityId(100L)
                .nodes(List.of(
                        node("site_start"),
                        node("site_exit")
                ))
                .edges(List.of(
                        edge("e_site", "site_start", "site_exit", 10D, "person_walk")
                ))
                .build();

        PathNetworkDTO roadNetwork = PathNetworkDTO.builder()
                .networkRef("net_road")
                .networkKind(NetworkKind.ROAD)
                .facilityId(100L)
                .nodes(List.of(
                        node("road_entry"),
                        node("road_stop")
                ))
                .edges(List.of(
                        edge("e_road", "road_entry", "road_stop", 20D, "ground_vehicle")
                ))
                .build();

        PortalDTO portal = PortalDTO.builder()
                .portalId("p1")
                .fromNetworkRef("net_site")
                .fromNodeId("site_exit")
                .toNetworkRef("net_road")
                .toNodeId("road_entry")
                .build();

        Map<NetworkKind, String> profileIdByKind = Map.of(
                NetworkKind.FACILITY, "person_walk",
                NetworkKind.ROAD, "ground_vehicle"
        );
        Map<String, MobilityProfileDTO> profilesById = Map.of(
                "person_walk", walkProfile(),
                "ground_vehicle", vehicleProfile()
        );

        PortalGraphAssembler.AssembledGraph assembled = assembler.assemble(
                List.of(siteNetwork, roadNetwork),
                List.of(portal),
                profileIdByKind,
                "person_walk",
                profilesById
        );

        DijkstraPlanner planner = new DijkstraPlanner();
        String from = PortalGraphAssembler.resolveStopNodeId("site_start", assembled.networksByRef());
        String to = PortalGraphAssembler.resolveStopNodeId("road_stop", assembled.networksByRef());
        ShortestPathResult result = planner.shortestPath(from, to, assembled.view());

        assertEquals(
                List.of(
                        ShortestPathResult.prefixNodeId("net_site", "site_start"),
                        ShortestPathResult.prefixNodeId("net_site", "site_exit"),
                        ShortestPathResult.prefixNodeId("net_road", "road_entry"),
                        ShortestPathResult.prefixNodeId("net_road", "road_stop")
                ),
                result.getNodeIds()
        );
        assertEquals(30D, result.getTotalCost());
        assertTrue(assembled.portalEdgeIds().contains(PortalGraphAssembler.PORTAL_EDGE_PREFIX + "p1"));
    }

    private static MobilityProfileDTO walkProfile() {
        return MobilityProfileDTO.builder()
                .profileId("person_walk")
                .allowedNetworkKinds(List.of(NetworkKind.FACILITY))
                .layer(NetworkLayer.GROUND)
                .respectDoors(true)
                .build();
    }

    private static MobilityProfileDTO vehicleProfile() {
        return MobilityProfileDTO.builder()
                .profileId("ground_vehicle")
                .allowedNetworkKinds(List.of(NetworkKind.ROAD))
                .layer(NetworkLayer.GROUND)
                .respectDoors(true)
                .build();
    }

    private static PathNodeDTO node(String nodeId) {
        return PathNodeDTO.builder()
                .nodeId(nodeId)
                .nodeType(NodeType.STATION)
                .layer(NetworkLayer.GROUND)
                .build();
    }

    private static PathEdgeDTO edge(String edgeId, String from, String to, double impedance, String profileId) {
        return PathEdgeDTO.builder()
                .edgeId(edgeId)
                .fromNodeId(from)
                .toNodeId(to)
                .layer(NetworkLayer.GROUND)
                .impedanceByProfile(Map.of(profileId, impedance))
                .build();
    }
}
