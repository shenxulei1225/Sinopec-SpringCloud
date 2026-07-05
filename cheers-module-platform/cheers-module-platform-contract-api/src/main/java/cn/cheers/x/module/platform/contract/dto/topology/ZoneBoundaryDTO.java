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
public class ZoneBoundaryDTO {

    private String zoneId;
    private String zoneCode;
    private String displayName;
    private List<TopologyPointDTO> polygon;
}
