package cn.cheers.x.module.platform.routing.service;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platform.contract.dto.network.MobilityProfileDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNodeDTO;
import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewDTO;
import cn.cheers.x.module.platform.contract.dto.route.RouteRequestDTO;
import cn.cheers.x.module.platform.contract.dto.topology.TopologyPointDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.contract.enums.NetworkLayer;
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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.when;

/**
 * 金桥已发布路网：规划步骤里的点位编号必须能对上路网自己的名称和坐标。
 * 不负责页面怎么标号；名称对不上时先修这里，禁止在读路径另编一套名字。
 */
@ExtendWith(MockitoExtension.class)
class JinqiaoVisitNameAlignmentTest {

    private static final String NETWORK_DUMP = "/tmp/jinqiao_full.json";
    private static final String NETWORK_REF = "net_44_5a30e669c4f1_draft";
    private static final String HOME = "GD-MBC-XJD-1001-1003-001";

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
    void visitStepNameAndPositionMatchNetworkNode() throws Exception {
        Path dump = Path.of(NETWORK_DUMP);
        assumeTrue(Files.exists(dump), "金桥路网快照不在本地，跳过");

        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        PathNetworkDTO network = mapper.readValue(Files.readString(dump), PathNetworkDTO.class);
        network.setNetworkRef(NETWORK_REF);
        network.setNetworkKind(NetworkKind.FACILITY);
        network.setFacilityId(44L);

        Map<String, PathNodeDTO> byId = network.getNodes().stream()
                .collect(Collectors.toMap(PathNodeDTO::getNodeId, Function.identity()));

        when(pathNetworkApi.getNetwork(NETWORK_REF)).thenReturn(CommonResult.success(network));
        when(mobilityProfileApi.getProfile("ground_robot")).thenReturn(CommonResult.success(
                MobilityProfileDTO.builder()
                        .profileId("ground_robot")
                        .allowedNetworkKinds(List.of(NetworkKind.FACILITY))
                        .layer(NetworkLayer.GROUND)
                        .respectDoors(true)
                        .build()));

        RoutePreviewDTO preview = routePlanService.plan(RouteRequestDTO.builder()
                .networkRef(NETWORK_REF)
                .stopIds(List.of(
                        "GDXJD-1001-1001-001",
                        "GDXJD-1001-1002-001",
                        "GDXJD-1002-1004-001",
                        "GDXJD-1002-1009-001"))
                .startStopId(HOME)
                .endStopId(HOME)
                .returnToStart(true)
                .mobilityProfileId("ground_robot")
                .strategy("as_given")
                .build());

        List<String> visits = preview.getVisitNodeIds();
        List<TopologyPointDTO> positions = preview.getVisitPositions();
        assertEquals(15, visits.size(), "金桥这条闭环应走出 15 个经过点");
        assertEquals(visits.size(), positions.size(), "visitPositions 必须和 visitNodeIds 对齐");
        assertEquals(HOME, visits.get(0));
        assertEquals("GDXJD-1002-1004-001", visits.get(5), "第 6 步应是南罐组西罐4顶");

        StringBuilder trace = new StringBuilder();
        for (int i = 0; i < visits.size(); i++) {
            String nodeId = visits.get(i);
            PathNodeDTO node = byId.get(nodeId);
            assertNotNull(node, "步骤里的点位不在路网中: " + nodeId);
            TopologyPointDTO planned = positions.get(i);
            TopologyPointDTO networkPos = node.getPosition();
            assertEquals(networkPos.getX(), planned.getX(), 1e-6, nodeId + " x");
            assertEquals(networkPos.getZ(), planned.getZ(), 1e-6, nodeId + " z");
            trace.append(String.format("%2d  %s  %s  x=%.3f z=%.3f%n",
                    i + 1, node.getDisplayName(), nodeId, networkPos.getX(), networkPos.getZ()));
        }
        System.out.print(trace);
        assertEquals("充电途径点", byId.get(visits.get(0)).getDisplayName());
        assertEquals("南罐组西罐4顶", byId.get(visits.get(5)).getDisplayName());
        assertEquals("北罐组西罐2顶", byId.get(visits.get(9)).getDisplayName());
    }
}
