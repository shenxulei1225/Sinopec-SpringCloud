package cn.cheers.x.scene.platform.controller.admin.component.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Skeletal Mesh 组件响应 VO")
@Data
public class SkeletalMeshComponentRespVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "组件编码")
    private String componentCode;

    @Schema(description = "显示名称")
    private String displayName;

    @Schema(description = "骨架编码")
    private String skeletonCode;

    @Schema(description = "动画蓝图编码")
    private String animationBlueprintCode;

    @Schema(description = "材质 JSON")
    private String materialsJson;

    @Schema(description = "投影阴影")
    private Boolean castShadow;

    @Schema(description = "接收阴影")
    private Boolean receiveShadow;

    @Schema(description = "使用动画蓝图")
    private Boolean useAnimationBlueprint;

    @Schema(description = "元数据 JSON")
    private String metadataJson;
}
