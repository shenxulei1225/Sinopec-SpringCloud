package cn.cheers.x.scene.platform.controller.admin.coordinate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 坐标转换校验响应 VO")
@Data
public class CoordinateValidateRespVO {

    @Schema(description = "场景编码")
    private String sceneCode;

    @Schema(description = "校验结果")
    private Boolean passed;

    @Schema(description = "校验项")
    private List<String> checks;

    @Schema(description = "警告信息")
    private List<String> warnings;

    @Schema(description = "地理 CRS 目录")
    private CoordinateCrsCatalogRespVO geographicCrsCatalog;

    @Schema(description = "投影 CRS 目录")
    private CoordinateCrsCatalogRespVO projectedCrsCatalog;
}
