package cn.iocoder.yudao.module.scene.platform.controller.admin.actor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - Actor 响应 VO")
@Data
public class ActorRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "Actor 编码", example = "actor_001")
    private String actorCode;

    @Schema(description = "Actor 名称", example = "建筑模型")
    private String actorName;

    @Schema(description = "Actor 类名", example = "StaticMeshActor")
    private String actorClass;

    @Schema(description = "父 Actor 编码", example = "actor_parent")
    private String parentActorCode;

    @Schema(description = "Actor 分类", example = "Building")
    private String actorCategory;

    @Schema(description = "引擎配置标识", example = "UE5")
    private String engineProfile;

    @Schema(description = "是否为抽象类型", example = "false")
    private Boolean abstractFlag;

    @Schema(description = "生命周期状态", example = "active")
    private String lifecycleStatus;

    @Schema(description = "扩展元数据 JSON", example = "{\"custom\":\"data\"}")
    private String metadataJson;

    @Schema(description = "默认组件树")
    private ComponentTreeRespVO componentTree;
}
