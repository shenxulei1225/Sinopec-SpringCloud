package cn.cheers.x.scene.platform.controller.admin.coordinate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 坐标参考响应 VO")
@Data
public class CoordinateReferenceRespVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "场景编码")
    private String sceneCode;

    @Schema(description = "原点经度（WGS84）")
    private java.math.BigDecimal originLng;

    @Schema(description = "原点纬度（WGS84）")
    private java.math.BigDecimal originLat;

    @Schema(description = "原点高度（米）")
    private java.math.BigDecimal originHeight;

    @Schema(description = "地理坐标系")
    private CoordinateCrsCatalogRespVO geographicCrsCatalog;

    @Schema(description = "投影坐标系")
    private CoordinateCrsCatalogRespVO projectedCrsCatalog;

    @Schema(description = "转换模板")
    private CoordinateTransformProfileRespVO transformProfile;

    @Schema(description = "坐标系代码")
    private String crsCode;

    @Schema(description = "坐标系名称")
    private String crsName;

    @Schema(description = "坐标系类型")
    private String crsType;

    @Schema(description = "元数据 JSON")
    private String metadataJson;
}
