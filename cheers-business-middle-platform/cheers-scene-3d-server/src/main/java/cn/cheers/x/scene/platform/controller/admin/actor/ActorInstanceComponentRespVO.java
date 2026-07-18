package cn.cheers.x.scene.platform.controller.admin.actor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Actor 实例组件响应 VO")
@Data
public class ActorInstanceComponentRespVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "Actor 实例 ID")
    private Long actorInstanceId;

    @Schema(description = "Actor 编码")
    private String actorCode;

    @Schema(description = "实例编码")
    private String instanceCode;

    @Schema(description = "组件编码")
    private String componentCode;

    @Schema(description = "启用状态")
    private Boolean enabledFlag;

    @Schema(description = "排序")
    private Integer sortNo;

    @Schema(description = "覆盖 JSON")
    private String overrideJson;

    @Schema(description = "元数据 JSON")
    private String metadataJson;
}
