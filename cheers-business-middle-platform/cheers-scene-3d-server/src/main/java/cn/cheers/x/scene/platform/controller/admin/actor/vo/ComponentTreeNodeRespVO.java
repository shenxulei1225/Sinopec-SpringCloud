package cn.cheers.x.scene.platform.controller.admin.actor.vo;

import cn.cheers.x.scene.platform.model.Transform;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 组件树节点响应 VO")
@Data
public class ComponentTreeNodeRespVO {

    @Schema(description = "组件编码", example = "RootComponent")
    private String componentCode;

    @Schema(description = "组件类型名称", example = "SceneComponent")
    private String componentTypeName;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabledFlag;

    @Schema(description = "排序号", example = "1")
    private Integer sortNo;

    @Schema(description = "相对变换矩阵")
    private Transform relativeTransform;

    @Schema(description = "扩展元数据 JSON", example = "{\"custom\":\"data\"}")
    private String metadataJson;

    @Schema(description = "子节点")
    private List<ComponentTreeNodeRespVO> children;
}
