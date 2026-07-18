package cn.cheers.x.module.platform.contract.dto.topology;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopologyEdgeDTO {

    private String edgeId;
    private String fromNodeId;
    private String toNodeId;
    private Long distanceMeters;
    private Integer weight;
    private List<TopologyPointDTO> waypoints;
    private String scope;
}
