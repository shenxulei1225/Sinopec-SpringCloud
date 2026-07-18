package cn.cheers.x.scene.platform.controller.admin.coordinate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 坐标转换配置响应 VO")
@Data
public class CoordinateTransformProfileRespVO {

    @Schema(description = "配置编码")
    private String profileCode;

    @Schema(description = "配置名称")
    private String profileName;

    @Schema(description = "地理 CRS 编码")
    private String geographicCrsCode;

    @Schema(description = "投影 CRS 编码")
    private String projectedCrsCode;

    @Schema(description = "元数据 JSON")
    private String metadataJson;
}
