package cn.cheers.x.scene.platform.controller.admin.actor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 更新 Actor 实例 GPS 高程（及可选经纬度）")
@Data
public class ActorInstanceGpsUpdateReqVO implements Serializable {

    @Schema(description = "GPS 高程（米，椭球高）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "GPS 高程不能为空")
    private BigDecimal gpsHeight;

    @Schema(description = "GPS 经度（WGS84）；可由站心+局部换算")
    private BigDecimal gpsLng;

    @Schema(description = "GPS 纬度（WGS84）；可由站心+局部换算")
    private BigDecimal gpsLat;

    @Schema(description = "高程来源：TERRAIN_SAMPLE / MANUAL 等", example = "TERRAIN_SAMPLE")
    private String gpsHeightSource;
}
