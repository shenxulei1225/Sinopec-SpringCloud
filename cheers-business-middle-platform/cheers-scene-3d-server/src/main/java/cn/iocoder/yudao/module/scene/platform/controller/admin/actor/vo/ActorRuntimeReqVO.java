package cn.iocoder.yudao.module.scene.platform.controller.admin.actor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "管理后台 - Actor 运行时状态更新请求 VO")
@Data
public class ActorRuntimeReqVO {

    @Schema(description = "场景编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "scene_001")
    @NotBlank(message = "场景编码不能为空")
    private String sceneCode;

    @Schema(description = "Actor 实例编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "actor_001")
    @NotBlank(message = "Actor 实例编码不能为空")
    private String instanceCode;

    @Schema(description = "X 坐标", example = "100.5")
    private Double x;

    @Schema(description = "Y 坐标", example = "200.3")
    private Double y;

    @Schema(description = "Z 坐标", example = "50.0")
    private Double z;

    @Schema(description = "状态", example = "moving")
    private String status;
}
