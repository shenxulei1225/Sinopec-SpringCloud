package cn.cheers.x.module.platform.exchange.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoJsonImportResultDTO {

    private String networkRef;
    private int nodeCount;
    private int edgeCount;
}
