package cn.cheers.x.scene.platform.controller.admin.component.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 场景组件挂载响应 VO")
@Data
public class SceneComponentInstanceRespVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "场景 ID")
    private Long sceneId;

    @Schema(description = "场景编码")
    private String sceneCode;

    @Schema(description = "实例编码")
    private String instanceCode;

    @Schema(description = "组件编码")
    private String componentCode;

    @Schema(description = "启用状态")
    private Boolean enabledFlag;

    @Schema(description = "配置 JSON")
    private String configJson;

    @Schema(description = "元数据 JSON")
    private String metadataJson;

    @Schema(description = "排序")
    private Integer sortNo;
}
