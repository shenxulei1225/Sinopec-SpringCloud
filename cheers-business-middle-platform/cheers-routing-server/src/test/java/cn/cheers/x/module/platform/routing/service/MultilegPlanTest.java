package cn.cheers.x.module.platform.routing.service;

import cn.cheers.x.module.platform.contract.dto.network.MobilityProfileDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathEdgeDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNodeDTO;
import cn.cheers.x.module.platform.contract.dto.network.PortalDTO;
import cn.cheers.x.module.platform.contract.dto.route.RouteLegDTO;
import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewDTO;
import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewSegmentDTO;
import cn.cheers.x.module.platform.contract.dto.route.RouteRequestDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.contract.enums.NetworkLayer;
import cn.cheers.x.module.platform.contract.enums.NodeType;
import cn.cheers.x.module.platform.routing.planner.AsGivenOrderStrategy;
import cn.cheers.x.module.platform.routing.planner.DijkstraPlanner;
import cn.cheers.x.module.platform.routing.planner.DoorConstraintFilter;
import cn.cheers.x.module.platform.routing.planner.OptimizeOrderStrategy;
import cn.cheers.x.module.platform.routing.planner.PortalGraphAssembler;
import cn.cheers.x.module.platform.routing.planner.ProfileGate;
import cn.cheers.x.module.platform.routing.planner.RefineOrderStrategy;
import cn.cheers.x.module.platform.routing.planner.StopOrderStrategyRegistry;
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
class MultilegPlanTest {

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
        PortalGraphAssembler portalGraphAssembler = new PortalGraphAssembler();
        ReflectionTestUtils.setField(portalGraphAssembler, "doorConstraintFilter", new DoorConstraintFilter());
        ReflectionTestUtils.setField(routePlanService, "portalGraphAssembler", portalGraphAssembler);
    }

    @Test
    void multimodalPlanProducesSegmentsPerNetworkKindAndProfile() {
        PathNetworkDTO siteNetwork = siteNetwork();
        PathNetworkDTO roadNetwork = roadNetwork();
        PortalDTO portal = PortalDTO.builder()
                .portalId("p1")
                .fromNetworkRef("net_site")
                .fromNodeId("site_exit")
                .toNetworkRef("net_road")
                .toNodeId("road_entry")
                .build();

        when(pathNetworkApi.getNetwork("net_site")).thenReturn(CommonResult.success(siteNetwork));
        when(pathNetworkApi.getNetwork("net_road")).thenReturn(CommonResult.success(roadNetwork));
        when(pathNetworkApi.listPortals(100L)).thenReturn(CommonResult.success(List.of(portal)));
        when(mobilityProfileApi.getProfile("person_walk")).thenReturn(CommonResult.success(walkProfile()));
        when(mobilityProfileApi.getProfile("ground_vehicle")).thenReturn(CommonResult.success(vehicleProfile()));

        RouteRequestDTO request = RouteRequestDTO.builder()
                .networkRefs(List.of("net_site", "net_road"))
                .stopIds(List.of("site_start", "road_stop"))
                .facilityId(100L)
                .legs(List.of(
                        RouteLegDTO.builder()
                                .networkKind(NetworkKind.SITE)
                                .mobilityProfileId("person_walk")
                                .build(),
                        RouteLegDTO.builder()
                                .networkKind(NetworkKind.ROAD)
                                .mobilityProfileId("ground_vehicle")
                                .build()
                ))
                .strategy("as_given")
                .build();

        RoutePreviewDTO preview = routePlanService.plan(request);

        assertEquals(30L, preview.getTotalDistanceMeters());
        assertEquals(2, preview.getSegments().size());

        RoutePreviewSegmentDTO siteSegment = preview.getSegments().get(0);
        assertEquals(NetworkKind.SITE, siteSegment.getNetworkKind());
        assertEquals("person_walk", siteSegment.getMobilityProfileId());
        assertEquals(List.of("site_start", "site_exit"), siteSegment.getNodeIds());
        assertEquals(10L, siteSegment.getDistanceMeters());

        RoutePreviewSegmentDTO roadSegment = preview.getSegments().get(1);
        assertEquals(NetworkKind.ROAD, roadSegment.getNetworkKind());
        assertEquals("ground_vehicle", roadSegment.getMobilityProfileId());
        assertEquals(List.of("road_entry", "road_stop"), roadSegment.getNodeIds());
        assertEquals(20L, roadSegment.getDistanceMeters());
        assertTrue(preview.getSegments().stream()
                .allMatch(segment -> segment.getFromStopIndex() == 0 && segment.getToStopIndex() == 1));
    }

    @Test
    void singleNetworkAsGivenStillWorksWithoutPortals() {
        PathNetworkDTO siteNetwork = PathNetworkDTO.builder()
                .networkRef("net_site")
                .networkKind(NetworkKind.SITE)
                .nodes(List.of(
                        node("a"),
                        node("b")
                ))
                .edges(List.of(
                        edge("e1", "a", "b", 15D)
                ))
                .build();

        when(pathNetworkApi.getNetwork("net_site")).thenReturn(CommonResult.success(siteNetwork));
        when(mobilityProfileApi.getProfile("person_walk")).thenReturn(CommonResult.success(walkProfile()));

        RouteRequestDTO request = RouteRequestDTO.builder()
                .networkRef("net_site")
                .stopIds(List.of("a", "b"))
                .mobilityProfileId("person_walk")
                .strategy("as_given")
                .build();

        RoutePreviewDTO preview = routePlanService.plan(request);

        assertEquals(15L, preview.getTotalDistanceMeters());
        assertEquals(1, preview.getSegments().size());
        assertEquals(NetworkKind.SITE, preview.getSegments().get(0).getNetworkKind());
        assertEquals("person_walk", preview.getSegments().get(0).getMobilityProfileId());
        assertEquals(List.of("a", "b"), preview.getSegments().get(0).getNodeIds());
    }

    private static PathNetworkDTO siteNetwork() {
        return PathNetworkDTO.builder()
                .networkRef("net_site")
                .networkKind(NetworkKind.SITE)
                .facilityId(100L)
                .nodes(List.of(
                        node("site_start"),
                        node("site_exit")
                ))
                .edges(List.of(
                        edge("e_site", "site_start", "site_exit", 10D)
                ))
                .build();
    }

    private static PathNetworkDTO roadNetwork() {
        return PathNetworkDTO.builder()
                .networkRef("net_road")
                .networkKind(NetworkKind.ROAD)
                .facilityId(100L)
                .nodes(List.of(
                        node("road_entry"),
                        node("road_stop")
                ))
                .edges(List.of(
                        edge("e_road", "road_entry", "road_stop", 20D)
                ))
                .build();
    }

    private static MobilityProfileDTO walkProfile() {
        return MobilityProfileDTO.builder()
                .profileId("person_walk")
                .allowedNetworkKinds(List.of(NetworkKind.SITE))
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

    private static PathEdgeDTO edge(String edgeId, String from, String to, double impedance) {
        return PathEdgeDTO.builder()
                .edgeId(edgeId)
                .fromNodeId(from)
                .toNodeId(to)
                .layer(NetworkLayer.GROUND)
                .impedanceByProfile(Map.of("person_walk", impedance, "ground_vehicle", impedance))
                .build();
    }
}
