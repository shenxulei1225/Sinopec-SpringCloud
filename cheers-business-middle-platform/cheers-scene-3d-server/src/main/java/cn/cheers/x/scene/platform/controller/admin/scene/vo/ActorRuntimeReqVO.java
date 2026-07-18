package cn.cheers.x.scene.platform.controller.admin.scene.vo;

import cn.cheers.x.scene.platform.model.Transform;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Actor 运行时状态请求 VO")
@Data
public class ActorRuntimeReqVO {

    @Schema(description = "场景编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "SC001")
    private String sceneCode;

    @Schema(description = "Actor 实例 ID", example = "1001")
    private Long instanceId;

    @Schema(description = "Actor 实例编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "ACTOR_001")
    private String instanceCode;

    @Schema(description = "Transform（位置/旋转/缩放，对应 Three.js Object3D）", example = "{\"location\":{\"x\":100.5,\"y\":200.5,\"z\":0.0},\"rotation\":{\"pitch\":0.0,\"yaw\":0.0,\"roll\":0.0},\"scale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0}}")
    private Transform transform;

    @Schema(description = "状态码", example = "ONLINE")
    private String status;

    @Schema(description = "Actor 分类编码", example = "TYPE_A")
    private String actorCategory;
}
