package cn.cheers.x.module.platform.routing.planner;

import cn.cheers.x.module.platform.contract.dto.network.MobilityProfileDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.contract.enums.NetworkLayer;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static cn.cheers.x.module.platform.routing.enums.ErrorCodeConstants.ROUTE_PROFILE_NETWORK_FORBIDDEN;
import static cn.cheers.x.module.platform.routing.enums.ErrorCodeConstants.ROUTE_PROFILE_PORTAL_FORBIDDEN;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProfileGateTest {

    private final ProfileGate profileGate = new ProfileGate();

    @Test
    void siteOnlyProfileRejectsPipelineNetwork() {
        MobilityProfileDTO profile = MobilityProfileDTO.builder()
                .profileId("person_walk")
                .allowedNetworkKinds(List.of(NetworkKind.SITE))
                .layer(NetworkLayer.GROUND)
                .build();
        PathNetworkDTO network = PathNetworkDTO.builder()
                .networkRef("net_pipe")
                .networkKind(NetworkKind.PIPELINE)
                .build();

        ServiceException ex = assertThrows(ServiceException.class,
                () -> profileGate.assertAllowed(profile, network));
        assertEquals(ROUTE_PROFILE_NETWORK_FORBIDDEN.getCode(), ex.getCode());
    }

    @Test
    void allowedNetworkKindPasses() {
        MobilityProfileDTO profile = MobilityProfileDTO.builder()
                .profileId("person_walk")
                .allowedNetworkKinds(List.of(NetworkKind.SITE))
                .layer(NetworkLayer.GROUND)
                .build();
        PathNetworkDTO network = PathNetworkDTO.builder()
                .networkRef("net_site")
                .networkKind(NetworkKind.SITE)
                .build();

        assertDoesNotThrow(() -> profileGate.assertAllowed(profile, network));
    }

    @Test
    void groundRobotRejectsPortalHop() {
        MobilityProfileDTO profile = MobilityProfileDTO.builder()
                .profileId("ground_robot")
                .allowedNetworkKinds(List.of(NetworkKind.SITE))
                .layer(NetworkLayer.GROUND)
                .build();

        ServiceException ex = assertThrows(ServiceException.class,
                () -> profileGate.assertPortalAllowed(profile, true));
        assertEquals(ROUTE_PROFILE_PORTAL_FORBIDDEN.getCode(), ex.getCode());
    }
}
