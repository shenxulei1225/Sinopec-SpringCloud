package cn.cheers.x.scene.platform.controller.admin.actor.vo;

import cn.cheers.x.scene.platform.model.Transform;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "管理后台 - ActorInstance 组件树节点响应 VO")
@Data
public class ActorInstanceComponentTreeNodeRespVO implements Serializable {

    @Schema(description = "组件ID")
    private Long id;

    @Schema(description = "实例编码")
    private String instanceCode;

    @Schema(description = "组件编码")
    private String componentCode;

    @Schema(description = "组件类型名称")
    private String componentTypeName;

    @Schema(description = "是否启用")
    private Boolean enabledFlag;

    @Schema(description = "排序号")
    private Integer sortNo;

    @Schema(description = "父组件编码")
    private String parentComponentCode;

    @Schema(description = "相对变换")
    private Transform relativeTransform;

    @Schema(description = "模板预设属性 JSON")
    private String propertiesJson;

    @Schema(description = "运行时覆盖属性 JSON")
    private String overrideJson;

    @Schema(description = "构造参数 JSON")
    private String constructArgsJson;

    @Schema(description = "元数据 JSON")
    private String metadataJson;

    @Schema(description = "子组件节点列表")
    private List<ActorInstanceComponentTreeNodeRespVO> children = new ArrayList<>();
}
