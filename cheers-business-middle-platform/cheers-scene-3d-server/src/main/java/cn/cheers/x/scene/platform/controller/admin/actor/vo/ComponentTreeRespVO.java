package cn.cheers.x.scene.platform.controller.admin.actor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 组件树响应 VO")
@Data
public class ComponentTreeRespVO {

    @Schema(description = "根组件节点")
    private ComponentTreeNodeRespVO root;
}
