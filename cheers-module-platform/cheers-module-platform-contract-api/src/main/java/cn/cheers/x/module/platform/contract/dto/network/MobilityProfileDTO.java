package cn.cheers.x.module.platform.contract.dto.network;

import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.contract.enums.NetworkLayer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MobilityProfileDTO {

    private String profileId;
    private String displayName;
    private List<NetworkKind> allowedNetworkKinds;
    private NetworkLayer layer;
    private Boolean respectDoors;
    private Boolean allowPortalHop;
}
