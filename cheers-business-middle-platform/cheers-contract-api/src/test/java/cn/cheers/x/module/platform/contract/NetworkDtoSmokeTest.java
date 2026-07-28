package cn.cheers.x.module.platform.contract;

import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNodeDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.contract.enums.NetworkLayer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NetworkDtoSmokeTest {

    @Test
    void pathNetworkHoldsKindAndLayer() {
        PathNetworkDTO n = PathNetworkDTO.builder()
                .networkRef("net_fac_1_v1")
                .networkKind(NetworkKind.FACILITY)
                .facilityId(1L)
                .status("PUBLISHED")
                .version(1)
                .build();
        assertEquals(NetworkKind.FACILITY, n.getNetworkKind());
        PathNodeDTO node = PathNodeDTO.builder()
                .nodeId("n1")
                .layer(NetworkLayer.GROUND)
                .build();
        assertEquals(NetworkLayer.GROUND, node.getLayer());
    }
}
