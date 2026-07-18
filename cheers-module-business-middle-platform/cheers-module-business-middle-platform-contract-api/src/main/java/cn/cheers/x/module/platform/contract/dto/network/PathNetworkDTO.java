package cn.cheers.x.module.platform.contract.dto.network;

import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PathNetworkDTO {

    private String networkRef;
    private NetworkKind networkKind;
    private Long facilityId;
    private Long scopeId;
    private String status;
    private Integer version;
    private List<PathNodeDTO> nodes;
    private List<PathEdgeDTO> edges;
    private List<PortalDTO> portals;
}
