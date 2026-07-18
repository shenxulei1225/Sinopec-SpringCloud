package cn.cheers.x.module.platform.contract.dto.route;

import cn.cheers.x.module.platform.contract.dto.topology.TopologyPointDTO;
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
public class RoutePreviewSegmentDTO {

    private Integer fromStopIndex;
    private Integer toStopIndex;
    private NetworkKind networkKind;
    private String mobilityProfileId;
    private List<String> nodeIds;
    private List<String> edgeIds;
    private List<TopologyPointDTO> polyline;
    private Long distanceMeters;
}
