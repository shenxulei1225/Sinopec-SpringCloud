package cn.cheers.x.module.platform.topology.service;

import cn.cheers.x.module.platform.contract.dto.network.PathEdgeDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNodeDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.contract.enums.NetworkLayer;
import cn.cheers.x.module.platform.contract.enums.NodeType;
import cn.cheers.x.module.platform.topology.api.dto.PathNetworkSummaryDTO;
import cn.cheers.x.module.platform.topology.api.dto.TopologyValidateRespDTO;
import cn.cheers.x.module.platform.topology.dal.dataobject.PathNetworkDO;
import cn.cheers.x.module.platform.topology.dal.mysql.PathNetworkMapper;
import cn.cheers.x.module.platform.topology.dal.mysql.PathPortalMapper;
import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PathNetworkServiceTest {

    @InjectMocks
    private PathNetworkServiceImpl pathNetworkService;

    @Mock
    private PathNetworkMapper pathNetworkMapper;

    @Mock
    private PathPortalMapper pathPortalMapper;

    @Test
    void validateFailsWhenGroundCrossZoneWithoutDoor() {
        PathNodeDTO nodeA = node("a", NodeType.STATION, NetworkLayer.GROUND, 1L);
        PathNodeDTO nodeB = node("b", NodeType.STATION, NetworkLayer.GROUND, 2L);
        PathEdgeDTO edge = edge("e1", "a", "b", NetworkLayer.GROUND);
        PathNetworkDTO draft = network(List.of(nodeA, nodeB), List.of(edge));

        TopologyValidateRespDTO resp = pathNetworkService.validate(draft);

        assertTrue(resp.getPassed());
        assertTrue(resp.getIssues().stream()
                .anyMatch(issue -> "CROSS_ZONE_NO_DOOR".equals(issue.getCode())));
    }

    @Test
    void validateFailsWhenNullLayerCrossZoneWithoutDoor() {
        PathNodeDTO nodeA = node("a", NodeType.STATION, NetworkLayer.GROUND, 1L);
        PathNodeDTO nodeB = node("b", NodeType.STATION, NetworkLayer.GROUND, 2L);
        PathEdgeDTO edge = edge("e1", "a", "b", null);
        PathNetworkDTO draft = network(List.of(nodeA, nodeB), List.of(edge));

        TopologyValidateRespDTO resp = pathNetworkService.validate(draft);

        assertTrue(resp.getPassed());
        assertTrue(resp.getIssues().stream()
                .anyMatch(issue -> "CROSS_ZONE_NO_DOOR".equals(issue.getCode())));
    }

    @Test
    void validatePassesWhenGroundCrossZoneWithDoor() {
        PathNodeDTO door = node("door", NodeType.DOOR, NetworkLayer.GROUND, 1L);
        PathNodeDTO station = node("station", NodeType.STATION, NetworkLayer.GROUND, 2L);
        PathEdgeDTO edge = edge("e1", "door", "station", NetworkLayer.GROUND);
        PathNetworkDTO draft = network(List.of(door, station), List.of(edge));

        TopologyValidateRespDTO resp = pathNetworkService.validate(draft);

        assertTrue(resp.getPassed());
        assertFalse(resp.getIssues().stream()
                .anyMatch(issue -> "CROSS_ZONE_NO_DOOR".equals(issue.getCode())));
    }

    private static PathNetworkDTO network(List<PathNodeDTO> nodes, List<PathEdgeDTO> edges) {
        return PathNetworkDTO.builder()
                .facilityId(1L)
                .networkKind(NetworkKind.FACILITY)
                .nodes(nodes)
                .edges(edges)
                .build();
    }

    @Test
    void readPayloadDouble_prefersFullNameAndSkipsBlank() {
        assertEquals(
                117.0193,
                PathNetworkServiceImpl.readPayloadDouble(
                        Map.of("longitude", "117.0193", "lng", "1"), "longitude", "lng"));
        assertEquals(
                39.1,
                PathNetworkServiceImpl.readPayloadDouble(Map.of("lat", 39.1), "latitude", "lat"));
        assertNull(PathNetworkServiceImpl.readPayloadDouble(Map.of("longitude", ""), "longitude"));
        assertNull(PathNetworkServiceImpl.readPayloadDouble(null, "longitude"));
    }

    @Test
    void listPublishedExcludesHistoricalCompanions() {
        when(pathNetworkMapper.selectAllByFacilityId(44L)).thenReturn(List.of(
                published("net_44_5a30e669c4f1_draft"),
                published("net_44_5a30e669c4f1"),
                published("net_44_site_published"),
                published("net_44_site_v6")));

        List<PathNetworkSummaryDTO> published = pathNetworkService.listPublished(44L);

        assertEquals(2, published.size());
        assertEquals("net_44_5a30e669c4f1_draft", published.get(0).getNetworkRef());
        assertEquals("net_44_5a30e669c4f1", published.get(1).getNetworkRef());
    }

    private static PathNetworkDO published(String id) {
        PathNetworkDO row = new PathNetworkDO();
        row.setId(id);
        row.setFacilityId(44L);
        row.setStatus(cn.cheers.x.module.platform.topology.enums.GraphStatus.PUBLISHED);
        row.setApplicableEquipmentTypes("[\"GROUND_ROBOT\"]");
        row.setNodes("[]");
        row.setEdges("[]");
        return row;
    }

    private static PathNodeDTO node(String nodeId, NodeType nodeType, NetworkLayer layer, Long zoneId) {
        return PathNodeDTO.builder()
                .nodeId(nodeId)
                .nodeType(nodeType)
                .layer(layer)
                .zoneId(zoneId)
                .build();
    }

    private static PathEdgeDTO edge(String edgeId, String fromNodeId, String toNodeId, NetworkLayer layer) {
        return PathEdgeDTO.builder()
                .edgeId(edgeId)
                .fromNodeId(fromNodeId)
                .toNodeId(toNodeId)
                .layer(layer)
                .build();
    }
}
