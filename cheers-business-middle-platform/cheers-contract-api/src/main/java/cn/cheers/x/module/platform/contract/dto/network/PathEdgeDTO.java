package cn.cheers.x.module.platform.contract.dto.network;

import cn.cheers.x.module.platform.contract.dto.topology.TopologyPointDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkLayer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PathEdgeDTO {

    private String edgeId;
    private String fromNodeId;
    private String toNodeId;
    private NetworkLayer layer;
    private String traversability;
    private Map<String, Double> impedanceByProfile;
    private List<String> allowedProfileIds;
    private List<TopologyPointDTO> waypoints;
}
