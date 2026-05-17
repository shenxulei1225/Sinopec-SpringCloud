package cn.iocoder.yudao.module.scene.platform.controller.admin.actor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Actor 组件树更新请求 VO")
@Data
public class ActorComponentTreeSaveReqVO {

    @Schema(description = "组件树 JSON 字符串", example = "{\"root\":{\"componentCode\":\"RootComponent\",\"componentTypeName\":\"SceneComponent\"}}")
    private String componentTreeJson;
}
