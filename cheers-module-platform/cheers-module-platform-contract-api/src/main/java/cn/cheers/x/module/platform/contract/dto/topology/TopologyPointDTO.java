package cn.cheers.x.module.platform.contract.dto.topology;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopologyPointDTO {

    private Double x;
    private Double y;
    private Double z;
}
