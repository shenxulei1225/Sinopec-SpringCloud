package cn.iocoder.yudao.module.scene.platform.controller.admin.actor;

import cn.iocoder.yudao.module.scene.platform.model.ComponentTree;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Actor 组件树保存请求 VO")
@Data
public class ActorComponentTreeSaveReqVO {

    @Schema(description = "组件树")
    private ComponentTree componentTree;

    public String toJson() {
        return componentTree == null ? null : componentTree.toString();
    }
}
