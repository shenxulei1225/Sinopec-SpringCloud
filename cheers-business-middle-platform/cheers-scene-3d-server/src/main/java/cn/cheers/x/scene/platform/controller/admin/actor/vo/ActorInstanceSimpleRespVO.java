package cn.cheers.x.scene.platform.controller.admin.actor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - ActorInstance 精简响应 VO")
@Data
public class ActorInstanceSimpleRespVO {

    @Schema(description = "主键 ID", example = "1")
    private Long id;

    @Schema(description = "场景ID", example = "1")
    private Long sceneId;

    @Schema(description = "Actor模板编码", example = "tank_01")
    private String actorCode;

    @Schema(description = "实例编码", example = "actor_001")
    private String instanceCode;

    @Schema(description = "实例名称", example = "油罐01")
    private String instanceName;

    @Schema(description = "父实例编码")
    private String parentInstanceCode;

    @Schema(description = "实例状态")
    private String instanceStatus;

    @Schema(description = "可见标志")
    private Boolean visibleFlag;

    @Schema(description = "实例路径")
    private String path;

    @Schema(description = "图层编码列表 JSON")
    private String layerKeys;
}
