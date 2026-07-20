package cn.cheers.x.scene.platform.controller.admin.coordinate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 仅更新坐标参考原点高程（及可选经纬度）")
@Data
public class CoordinateReferenceOriginHeightUpdateReqVO {

    @Schema(description = "原点高程（米，椭球高）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "原点高程不能为空")
    private BigDecimal originHeight;

    @Schema(description = "原点经度（WGS84）；不传则保留原值")
    private BigDecimal originLng;

    @Schema(description = "原点纬度（WGS84）；不传则保留原值")
    private BigDecimal originLat;

    @Schema(description = "高程来源：TERRAIN_SAMPLE / MANUAL 等", example = "TERRAIN_SAMPLE")
    private String originHeightSource;
}
