package cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Primitive 组件响应 VO")
@Data
public class PrimitiveComponentRespVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "组件编码")
    private String componentCode;

    @Schema(description = "显示名称")
    private String displayName;

    @Schema(description = "碰撞启用状态")
    private String collisionEnabled;

    @Schema(description = "对象类型")
    private String objectType;

    @Schema(description = "生成重叠事件")
    private Boolean generateOverlapEvents;

    @Schema(description = "模拟生成命中事件")
    private Boolean simulationGeneratesHitEvents;

    @Schema(description = "是否可被角色踏上")
    private Boolean canCharacterStepUpOn;

    @Schema(description = "使用默认碰撞")
    private Boolean useDefaultCollision;

    @Schema(description = "物理材质 JSON")
    private String physicsMaterialJson;

    @Schema(description = "边界 JSON")
    private String boundsJson;

    @Schema(description = "碰撞响应 JSON")
    private String collisionResponseJson;

    @Schema(description = "元数据 JSON")
    private String metadataJson;
}
