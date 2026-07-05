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
public class TopologyGraphDTO {

    private String topologyRef;
    private Long siteId;
    private String status;
    private Integer version;
    private List<TopologyNodeDTO> nodes;
    private List<TopologyEdgeDTO> edges;
    private List<ZoneBoundaryDTO> zoneBoundaries;
}
