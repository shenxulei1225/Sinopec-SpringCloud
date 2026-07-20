package cn.cheers.x.scene.platform.controller.admin.actor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 批量更新实例 GPS 的单条项")
@Data
public class ActorInstanceGpsBatchItemReqVO implements Serializable {

    @Schema(description = "实例主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "实例 ID 不能为空")
    private Long id;

    @Schema(description = "GPS 高程（米，椭球高）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "GPS 高程不能为空")
    private BigDecimal gpsHeight;

    @Schema(description = "GPS 经度（WGS84）")
    private BigDecimal gpsLng;

    @Schema(description = "GPS 纬度（WGS84）")
    private BigDecimal gpsLat;

    @Schema(description = "高程来源", example = "TERRAIN_SAMPLE")
    private String gpsHeightSource;
}
