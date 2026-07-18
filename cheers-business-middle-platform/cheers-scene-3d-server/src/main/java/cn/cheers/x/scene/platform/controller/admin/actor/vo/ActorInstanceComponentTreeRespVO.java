package cn.cheers.x.scene.platform.controller.admin.actor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "管理后台 - ActorInstance 组件树响应 VO")
@Data
public class ActorInstanceComponentTreeRespVO implements Serializable {

    @Schema(description = "根组件节点")
    private ActorInstanceComponentTreeNodeRespVO root;
}
