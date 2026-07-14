package cn.cheers.x.module.platform.topology.service.convert;

import cn.cheers.x.module.platform.contract.dto.network.MobilityProfileDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.contract.enums.NetworkLayer;
import cn.cheers.x.module.platform.topology.dal.dataobject.MobilityProfileDO;
import com.alibaba.fastjson2.JSON;
import org.springframework.util.StringUtils;

import java.util.List;

public final class MobilityProfileConvert {

    private MobilityProfileConvert() {
    }

    public static MobilityProfileDTO toDto(MobilityProfileDO profile) {
        return MobilityProfileDTO.builder()
                .profileId(profile.getId())
                .displayName(profile.getDisplayName())
                .allowedNetworkKinds(parseNetworkKinds(profile.getAllowedNetworkKinds()))
                .layer(NetworkLayer.valueOf(profile.getLayer()))
                .respectDoors(profile.getRespectDoors())
                .allowPortalHop(profile.getAllowPortalHop())
                .build();
    }

    private static List<NetworkKind> parseNetworkKinds(String json) {
        if (!StringUtils.hasText(json)) {
            return List.of();
        }
        return JSON.parseArray(json, NetworkKind.class);
    }
}
