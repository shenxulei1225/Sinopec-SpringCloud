package cn.cheers.x.module.platform.topology.api.dto;

import cn.cheers.x.module.platform.contract.dto.topology.TopologyEdgeDTO;
import cn.cheers.x.module.platform.contract.dto.topology.TopologyNodeDTO;
import cn.cheers.x.module.platform.contract.dto.topology.ZoneBoundaryDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopologyGraphSaveReqDTO {

    @Valid
    private List<TopologyNodeDTO> nodes;
    @Valid
    private List<TopologyEdgeDTO> edges;
    @Valid
    private List<ZoneBoundaryDTO> zoneBoundaries;
}
