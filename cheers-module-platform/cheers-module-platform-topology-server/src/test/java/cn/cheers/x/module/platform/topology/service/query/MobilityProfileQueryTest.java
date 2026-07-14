package cn.cheers.x.module.platform.topology.service.query;

import cn.cheers.x.module.platform.contract.dto.network.MobilityProfileDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.contract.enums.NetworkLayer;
import cn.cheers.x.module.platform.topology.dal.dataobject.MobilityProfileDO;
import cn.cheers.x.module.platform.topology.dal.mysql.MobilityProfileMapper;
import cn.cheers.x.module.platform.topology.service.query.impl.MobilityProfileQueryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MobilityProfileQueryTest {

    @InjectMocks
    private MobilityProfileQueryServiceImpl mobilityProfileQueryService;

    @Mock
    private MobilityProfileMapper mobilityProfileMapper;

    @Test
    void listProfilesReturnsFourBuiltInProfiles() {
        when(mobilityProfileMapper.selectAllActive()).thenReturn(List.of(
                profile("person_walk", "人员步行", "[\"SITE\"]", "GROUND", true, false),
                profile("ground_vehicle", "地面车辆", "[\"ROAD\",\"SITE\"]", "GROUND", true, true),
                profile("ground_robot", "地面机器人", "[\"SITE\"]", "GROUND", true, false),
                profile("uav_low", "低空UAV", "[\"SITE\",\"PERIMETER\",\"PIPELINE\"]", "AIR", false, true)
        ));

        List<MobilityProfileDTO> profiles = mobilityProfileQueryService.listProfiles();

        assertEquals(4, profiles.size());
        Set<String> profileIds = profiles.stream()
                .map(MobilityProfileDTO::getProfileId)
                .collect(Collectors.toSet());
        assertEquals(Set.of("person_walk", "ground_vehicle", "ground_robot", "uav_low"), profileIds);

        MobilityProfileDTO uavLow = profiles.stream()
                .filter(profile -> "uav_low".equals(profile.getProfileId()))
                .findFirst()
                .orElseThrow();
        assertEquals("低空UAV", uavLow.getDisplayName());
        assertEquals(NetworkLayer.AIR, uavLow.getLayer());
        assertEquals(List.of(NetworkKind.SITE, NetworkKind.PERIMETER, NetworkKind.PIPELINE),
                uavLow.getAllowedNetworkKinds());
        assertFalse(uavLow.getRespectDoors());
        assertTrue(uavLow.getAllowPortalHop());
    }

    private static MobilityProfileDO profile(String id, String displayName, String kindsJson, String layer,
                                             boolean respectDoors, boolean allowPortalHop) {
        return MobilityProfileDO.builder()
                .id(id)
                .displayName(displayName)
                .allowedNetworkKinds(kindsJson)
                .layer(layer)
                .respectDoors(respectDoors)
                .allowPortalHop(allowPortalHop)
                .build();
    }
}
