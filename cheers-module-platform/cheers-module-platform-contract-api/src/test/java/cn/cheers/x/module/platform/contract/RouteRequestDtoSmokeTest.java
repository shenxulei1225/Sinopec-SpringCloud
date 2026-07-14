package cn.cheers.x.module.platform.contract;

import cn.cheers.x.module.platform.contract.dto.route.RouteLegDTO;
import cn.cheers.x.module.platform.contract.dto.route.RouteRequestDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RouteRequestDtoSmokeTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesLegsAndStrategyAsGiven() throws Exception {
        String json = """
                {
                  "contractVersion": "1.0",
                  "networkRef": "net_fac_1_v1",
                  "stopIds": ["s1", "s2", "s3"],
                  "strategy": "as_given",
                  "legs": [
                    {"mobilityProfileId": "ground_robot", "networkKind": "SITE"},
                    {"mobilityProfileId": "ground_vehicle", "networkKind": "ROAD"}
                  ]
                }
                """;

        RouteRequestDTO request = objectMapper.readValue(json, RouteRequestDTO.class);

        assertEquals("1.0", request.getContractVersion());
        assertEquals("net_fac_1_v1", request.getNetworkRef());
        assertEquals("as_given", request.getStrategy());
        assertEquals(List.of("s1", "s2", "s3"), request.getStopIds());
        assertEquals(2, request.getLegs().size());
        assertEquals("ground_robot", request.getLegs().get(0).getMobilityProfileId());
        assertEquals(NetworkKind.SITE, request.getLegs().get(0).getNetworkKind());
        assertEquals("ground_vehicle", request.getLegs().get(1).getMobilityProfileId());
        assertEquals(NetworkKind.ROAD, request.getLegs().get(1).getNetworkKind());
    }

    @Test
    void resolvedNetworkRefPrefersNetworkRef() {
        RouteRequestDTO request = RouteRequestDTO.builder()
                .networkRef("net_new")
                .topologyRef("topo_legacy")
                .build();
        assertEquals("net_new", request.resolvedNetworkRef());
    }

    @Test
    void resolvedNetworkRefFallsBackToTopologyRef() {
        RouteRequestDTO request = RouteRequestDTO.builder()
                .topologyRef("topo_legacy")
                .build();
        assertEquals("topo_legacy", request.resolvedNetworkRef());
    }

    @Test
    void resolvedNetworkRefReturnsNullWhenBothAbsent() {
        RouteRequestDTO request = RouteRequestDTO.builder()
                .networkRef("  ")
                .build();
        assertNull(request.resolvedNetworkRef());
    }
}
