package cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 场景全局组件分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SceneComponentPageReqVO extends PageParam {

    @Schema(description = "场景 ID", example = "1")
    private Long sceneId;

    @Schema(description = "组件类型", example = "POST_PROCESS_VOLUME")
    private String componentType;

    @Schema(description = "组件名称", example = "环境雾效")
    private String componentName;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;
}
