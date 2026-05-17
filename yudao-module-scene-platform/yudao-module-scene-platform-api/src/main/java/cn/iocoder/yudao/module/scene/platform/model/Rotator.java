package cn.iocoder.yudao.module.scene.platform.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "旋转（欧拉角）")
@Data
public class Rotator {

    @Schema(description = "俯仰角（绕 X 轴旋转）")
    private Double pitch;

    @Schema(description = "偏航角（绕 Y 轴旋转）")
    private Double yaw;

    @Schema(description = "翻滚角（绕 Z 轴旋转）")
    private Double roll;
}
