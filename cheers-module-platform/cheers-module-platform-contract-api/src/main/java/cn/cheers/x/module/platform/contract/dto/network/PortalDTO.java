package cn.cheers.x.module.platform.contract.dto.network;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortalDTO {

    private String portalId;
    private String fromNetworkRef;
    private String fromNodeId;
    private String toNetworkRef;
    private String toNodeId;
    private List<String> allowedProfileIds;
}
