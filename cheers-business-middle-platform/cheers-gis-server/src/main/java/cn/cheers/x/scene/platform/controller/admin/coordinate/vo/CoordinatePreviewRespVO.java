package cn.cheers.x.scene.platform.controller.admin.coordinate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 坐标转换预览响应 VO")
@Data
public class CoordinatePreviewRespVO {

    @Schema(description = "地理坐标预览")
    private CoordinateConvertRespVO geographic;

    @Schema(description = "投影坐标预览")
    private CoordinateConvertRespVO projected;

    @Schema(description = "ECEF 坐标预览")
    private CoordinateConvertRespVO ecef;

    @Schema(description = "局部切平面坐标预览")
    private CoordinateConvertRespVO localTangent;

    @Schema(description = "引擎坐标预览")
    private CoordinateConvertRespVO engine;

    @Schema(description = "警告信息")
    private List<String> warnings;

    @Schema(description = "地理 CRS 目录")
    private CoordinateCrsCatalogRespVO geographicCrsCatalog;

    @Schema(description = "投影 CRS 目录")
    private CoordinateCrsCatalogRespVO projectedCrsCatalog;
}
