package cn.cheers.x.scene.platform.controller.admin.actor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Actor 组件树响应 VO")
@Data
public class ActorComponentTreeRespVO {

    @Schema(description = "组件树 JSON")
    private String componentTreeJson;
}
