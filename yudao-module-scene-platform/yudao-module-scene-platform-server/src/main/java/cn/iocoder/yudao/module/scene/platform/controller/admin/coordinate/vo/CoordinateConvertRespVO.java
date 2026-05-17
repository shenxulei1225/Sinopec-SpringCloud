package cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "管理后台 - 坐标转换响应 VO")
@Data
public class CoordinateConvertRespVO {

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

    @Schema(description = "转换管线")
    private List<String> pipeline;

    @Schema(description = "警告信息")
    private List<String> warnings;
}
