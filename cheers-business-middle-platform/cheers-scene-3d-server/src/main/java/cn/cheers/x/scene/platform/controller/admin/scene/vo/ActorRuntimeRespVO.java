package cn.cheers.x.scene.platform.controller.admin.scene.vo;

import cn.cheers.x.scene.platform.model.Rotator;
import cn.cheers.x.scene.platform.model.Transform;
import cn.cheers.x.scene.platform.model.Vector3;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Actor 运行时位置/状态响应 VO")
@Data
public class ActorRuntimeRespVO {

    @Schema(description = "Actor 实例 ID", example = "1001")
    private Long instanceId;

    @Schema(description = "Actor 实例编码", example = "ACTOR_001")
    private String instanceCode;

    @Schema(description = "Actor 类别（INSTRUMENT/MONITOR/INSPECTOR等）", example = "INSTRUMENT")
    private String actorCategory;

    @Schema(description = "位置（对应 Three.js Vector3.position）", example = "{\"x\":100.5,\"y\":200.5,\"z\":0.0}")
    private Vector3 location;

    @Schema(description = "旋转（对应 Three.js Rotator）", example = "{\"pitch\":0.0,\"yaw\":0.0,\"roll\":0.0}")
    private Rotator rotation;

    @Schema(description = "缩放（对应 Three.js Vector3.scale）", example = "{\"x\":1.0,\"y\":1.0,\"z\":1.0}")
    private Vector3 scale;

    @Schema(description = "状态码", example = "ONLINE")
    private String status;

    public static ActorRuntimeRespVO fromTransform(Long instanceId, String instanceCode, Transform transform, String status) {
        ActorRuntimeRespVO vo = new ActorRuntimeRespVO();
        vo.setInstanceId(instanceId);
        vo.setInstanceCode(instanceCode);
        vo.setLocation(transform != null ? transform.getLocation() : new Vector3());
        vo.setRotation(transform != null ? transform.getRotation() : new Rotator());
        vo.setScale(transform != null ? transform.getScale() : new Vector3());
        vo.setStatus(status != null ? status : "OFFLINE");
        return vo;
    }
}
