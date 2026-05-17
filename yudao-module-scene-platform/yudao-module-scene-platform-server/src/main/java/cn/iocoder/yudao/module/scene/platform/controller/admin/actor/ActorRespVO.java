package cn.iocoder.yudao.module.scene.platform.controller.admin.actor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Actor 响应 VO")
@Data
public class ActorRespVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "Actor 编码")
    private String actorCode;

    @Schema(description = "Actor 名称")
    private String actorName;

    @Schema(description = "Actor 类")
    private String actorClass;

    @Schema(description = "父 Actor 编码")
    private String parentActorCode;

    @Schema(description = "分类")
    private String actorCategory;

    @Schema(description = "引擎配置")
    private String engineProfile;

    @Schema(description = "是否抽象")
    private Boolean abstractFlag;

    @Schema(description = "生命周期状态")
    private String lifecycleStatus;

    @Schema(description = "元数据 JSON")
    private String metadataJson;
}
