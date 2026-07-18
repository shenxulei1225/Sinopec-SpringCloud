package cn.cheers.x.scene.platform.controller.admin.coordinate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 坐标转换请求 VO")
@Data
public class CoordinateConvertReqVO {

    @Schema(description = "场景编码")
    private String sceneCode;

    @Schema(description = "配置编码")
    private String profileCode;

    @Schema(description = "源空间类型")
    private String sourceSpaceType;

    @Schema(description = "目标空间类型")
    private String targetSpaceType;

    @Schema(description = "X 坐标")
    private BigDecimal x;

    @Schema(description = "Y 坐标")
    private BigDecimal y;

    @Schema(description = "Z 坐标")
    private BigDecimal z;
}
