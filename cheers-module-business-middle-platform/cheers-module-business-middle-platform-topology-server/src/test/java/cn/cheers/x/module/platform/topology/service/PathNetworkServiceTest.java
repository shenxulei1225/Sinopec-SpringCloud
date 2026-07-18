package cn.cheers.x.module.platform.topology.service;

import cn.cheers.x.module.platform.contract.dto.network.PathEdgeDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNodeDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.contract.enums.NetworkLayer;
import cn.cheers.x.module.platform.contract.enums.NodeType;
import cn.cheers.x.module.platform.topology.api.dto.TopologyValidateRespDTO;
import cn.cheers.x.module.platform.topology.dal.dataobject.PathNetworkDO;
import cn.cheers.x.module.platform.topology.dal.mysql.PathNetworkMapper;
import cn.cheers.x.module.platform.topology.dal.mysql.PathPortalMapper;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static cn.cheers.x.module.platform.topology.enums.ErrorCodeConstants.NETWORK_VALIDATE_FAILED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        assertFalse(resp.getPassed());
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

        assertFalse(resp.getPassed());
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

    @Test
    void publishRejectsInvalidCrossZoneDraft() {
        Long facilityId = 42L;
        NetworkKind kind = NetworkKind.SITE;
        PathNetworkDO draft = PathNetworkDO.builder()
                .id("net_42_site_draft")
                .facilityId(facilityId)
                .networkKind("SITE")
                .status("DRAFT")
                .version(0)
                .nodes(JSON.toJSONString(List.of(
                        node("a", NodeType.STATION, NetworkLayer.GROUND, 1L),
                        node("b", NodeType.STATION, NetworkLayer.GROUND, 2L))))
                .edges(JSON.toJSONString(List.of(edge("e1", "a", "b", null))))
                .build();
        when(pathNetworkMapper.selectDraftByFacilityIdAndKind(facilityId, "SITE")).thenReturn(draft);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> pathNetworkService.publish(facilityId, kind));

        assertEquals(NETWORK_VALIDATE_FAILED.getCode(), ex.getCode());
        verify(pathNetworkMapper, never()).insert(any(PathNetworkDO.class));
    }

    @Test
    void publishInsertsPublishedVersionAndKeepsDraft() {
        Long facilityId = 42L;
        NetworkKind kind = NetworkKind.SITE;
        PathNetworkDO draft = PathNetworkDO.builder()
                .id("net_42_site_draft")
                .facilityId(facilityId)
                .networkKind("SITE")
                .status("DRAFT")
                .version(0)
                .nodes("[]")
                .edges("[]")
                .build();
        when(pathNetworkMapper.selectDraftByFacilityIdAndKind(facilityId, "SITE")).thenReturn(draft);
        when(pathNetworkMapper.selectLatestPublishedByFacilityIdAndKind(facilityId, "SITE")).thenReturn(null);

        pathNetworkService.publish(facilityId, kind);

        verify(pathNetworkMapper).insert(any(PathNetworkDO.class));
        verify(pathNetworkMapper, never()).deleteById(any());
        verify(pathNetworkMapper, never()).updateById(draft);
    }

    private static PathNetworkDTO network(List<PathNodeDTO> nodes, List<PathEdgeDTO> edges) {
        return PathNetworkDTO.builder()
                .facilityId(1L)
                .networkKind(NetworkKind.SITE)
                .nodes(nodes)
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

    private static PathEdgeDTO edge(String edgeId, String fromNodeId, String toNodeId, NetworkLayer layer) {
        return PathEdgeDTO.builder()
                .edgeId(edgeId)
                .fromNodeId(fromNodeId)
                .toNodeId(toNodeId)
                .layer(layer)
                .build();
    }
}
