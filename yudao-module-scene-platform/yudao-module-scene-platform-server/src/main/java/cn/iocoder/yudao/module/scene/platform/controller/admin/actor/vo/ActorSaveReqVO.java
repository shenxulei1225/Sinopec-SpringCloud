package cn.iocoder.yudao.module.scene.platform.controller.admin.actor.vo;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorDO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理后台 - Actor 创建/更新请求 VO")
@Data
public class ActorSaveReqVO {

    @Schema(description = "Actor 编码", required = true, example = "actor_001")
    @NotBlank(message = "Actor 编码不能为空")
    private String actorCode;

    @Schema(description = "Actor 名称", required = true, example = "建筑模型")
    @NotBlank(message = "Actor 名称不能为空")
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

    /**
     * 将 VO 转换为 DO
     */
    public ActorDO toDO() {
        ActorDO actorDO = new ActorDO();
        actorDO.setActorCode(this.actorCode);
        actorDO.setActorName(this.actorName);
        actorDO.setActorClass(this.actorClass);
        actorDO.setParentActorCode(this.parentActorCode);
        actorDO.setActorCategory(this.actorCategory);
        actorDO.setEngineProfile(this.engineProfile);
        actorDO.setAbstractFlag(this.abstractFlag);
        actorDO.setLifecycleStatus(this.lifecycleStatus);
        actorDO.setMetadataJson(this.metadataJson);
        return actorDO;
    }
}
