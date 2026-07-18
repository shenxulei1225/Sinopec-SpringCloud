package cn.cheers.x.module.platform.topology.exchange;

import cn.cheers.x.module.platform.contract.dto.network.PathEdgeDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNodeDTO;
import cn.cheers.x.module.platform.contract.dto.topology.TopologyPointDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.contract.enums.NetworkLayer;
import cn.cheers.x.module.platform.contract.enums.NodeType;
import cn.cheers.x.module.platform.exchange.dto.GeoJsonImportResultDTO;
import cn.cheers.x.module.platform.topology.service.PathNetworkService;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeoJsonRoundTripTest {

    @InjectMocks
    private GeoJsonExchangeServiceImpl geoJsonExchangeService;

    @Mock
    private PathNetworkService pathNetworkService;

    @Test
    void roundTripPreservesNodeAndEdgeCounts() {
        PathNetworkDTO original = minimalNetwork();
        JsonNode exported = geoJsonExchangeService.toFeatureCollection(original);

        PathNetworkDTO parsed = geoJsonExchangeService.fromFeatureCollection(
                exported, 7L, NetworkKind.SITE);

        assertEquals(original.getNodes().size(), parsed.getNodes().size());
        assertEquals(original.getEdges().size(), parsed.getEdges().size());
        assertEquals(7L, parsed.getFacilityId());
        assertEquals(NetworkKind.SITE, parsed.getNetworkKind());
        assertEquals("n1", parsed.getNodes().get(0).getNodeId());
        assertEquals("e1", parsed.getEdges().get(0).getEdgeId());
    }

    @Test
    void importDelegatesToSaveDraft() {
        PathNetworkDTO original = minimalNetwork();
        JsonNode exported = geoJsonExchangeService.toFeatureCollection(original);
        when(pathNetworkService.saveDraft(any(PathNetworkDTO.class))).thenAnswer(invocation -> {
            PathNetworkDTO draft = invocation.getArgument(0);
            return PathNetworkDTO.builder()
                    .networkRef("net_7_site_draft")
                    .facilityId(draft.getFacilityId())
                    .networkKind(draft.getNetworkKind())
                    .nodes(draft.getNodes())
                    .edges(draft.getEdges())
                    .build();
        });

        GeoJsonImportResultDTO result = geoJsonExchangeService.importGeoJson(
                7L, NetworkKind.SITE, exported);

        assertEquals("net_7_site_draft", result.getNetworkRef());
        assertEquals(2, result.getNodeCount());
        assertEquals(1, result.getEdgeCount());
    }

    private static PathNetworkDTO minimalNetwork() {
        return PathNetworkDTO.builder()
                .facilityId(1L)
                .networkKind(NetworkKind.SITE)
                .nodes(List.of(
                        PathNodeDTO.builder()
                                .nodeId("n1")
                                .nodeType(NodeType.STATION)
                                .layer(NetworkLayer.GROUND)
                                .zoneId(10L)
                                .displayName("Station A")
                                .position(TopologyPointDTO.builder().x(1.0).y(2.0).build())
                                .build(),
                        PathNodeDTO.builder()
                                .nodeId("n2")
                                .nodeType(NodeType.TRAVERSAL)
                                .layer(NetworkLayer.GROUND)
                                .zoneId(10L)
                                .position(TopologyPointDTO.builder().x(3.0).y(4.0).z(0.5).build())
                                .build()))
                .edges(List.of(
                        PathEdgeDTO.builder()
                                .edgeId("e1")
                                .fromNodeId("n1")
                                .toNodeId("n2")
                                .layer(NetworkLayer.GROUND)
                                .waypoints(List.of(
                                        TopologyPointDTO.builder().x(1.0).y(2.0).build(),
                                        TopologyPointDTO.builder().x(3.0).y(4.0).z(0.5).build()))
                                .build()))
                .build();
    }
}
