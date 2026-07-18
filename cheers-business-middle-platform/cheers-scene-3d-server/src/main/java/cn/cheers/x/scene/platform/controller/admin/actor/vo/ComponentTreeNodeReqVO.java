package cn.cheers.x.scene.platform.controller.admin.actor.vo;

import cn.cheers.x.scene.platform.model.Transform;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 组件节点创建/更新 Request VO")
@Data
public class ComponentTreeNodeReqVO {

    @Schema(description = "组件名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "Transform")
    private String componentName;

    @Schema(description = "组件编码", example = "transform_root")
    private String componentCode;

    @Schema(description = "组件类型", example = "transform")
    private String componentType;

    @Schema(description = "组件参数", example = "{}")
    private String componentParam;

    @Schema(description = "子节点列表")
    private List<ComponentTreeNodeReqVO> children;

    @Schema(description = "变换（位置/旋转/缩放）")
    private Transform transform;

    @Schema(description = "父节点编码", example = "root")
    private String parentCode;

    @Schema(description = "是否可见", example = "true")
    private Boolean visible;

    @Schema(description = "层级", example = "0")
    private Integer layer;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "额外属性 JSON", example = "{}")
    private String extraJson;

    @Schema(description = "引用资源编码")
    private String assetCode;
}
