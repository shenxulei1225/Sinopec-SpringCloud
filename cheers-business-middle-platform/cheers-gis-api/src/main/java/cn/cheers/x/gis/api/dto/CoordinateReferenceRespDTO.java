package cn.cheers.x.gis.api.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 坐标参考 RPC 响应（供 scene-3d 组装 runtime package）。
 */
@Data
public class CoordinateReferenceRespDTO {

    private Long id;
    private String sceneCode;
    private BigDecimal originLng;
    private BigDecimal originLat;
    private BigDecimal originHeight;
    private String crsCode;
    private String crsName;
    private String crsType;
    private String metadataJson;
}
