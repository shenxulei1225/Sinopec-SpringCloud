package cn.iocoder.yudao.module.scene.platform.controller.admin.actor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

import cn.iocoder.yudao.module.scene.platform.model.Transform;

/**
 * 组件树节点 VO
 *
 * 用途：表示 Actor 模板中的单个组件节点，以及其在组件树中的层级关系。
 * 前端在 spawn 时回传此结构，包含：
 * - propertiesJson：模板预设属性（继承自模板）
 * - overrideJson：运行时覆盖属性（用户修改的值）
 *
 * 与模板 ComponentTreeNode 的区别：
 * - 模板 ComponentTreeNode 只有 propertiesJson（预设值）
 * - 实例 ComponentTreeNodeVO 同时包含 propertiesJson 和 overrideJson
 * - 后端接收到后直接持久化到 ActorInstanceComponentDO
 */
@Schema(description = "管理后台 - 组件树节点 VO")
@Data
public class ComponentTreeNodeVO implements Serializable {

    @Schema(description = "组件唯一标识代码", example = "mainLight")
    private String componentCode;

    @Schema(description = "组件类型名称", example = "DirectionalLight")
    private String componentTypeName;

    @Schema(description = "组件启用标志", example = "true")
    private Boolean enabledFlag = Boolean.TRUE;

    @Schema(description = "组件在 siblings 中的排序号", example = "0")
    private Integer sortNo = 0;

    @Schema(description = "组件的相对变换（相对于父组件）")
    private Transform transform;

    @Schema(description = "父组件的代码", example = "RootComponent")
    private String parentComponentCode;

    @Schema(description = "元数据（JSON格式）")
    private String metadataJson;

    @Schema(description = "模板预设属性（JSON格式）", example = "{\"opacity\": 0.8, \"color\": \"red\"}")
    private String propertiesJson;

    @Schema(description = "运行时覆盖属性（JSON格式）", example = "{\"brightness\": 0.9}")
    private String overrideJson = "{}";

    @Schema(description = "构造参数（JSON格式）", example = "{\"lightType\": \"directional\"}")
    private String constructArgsJson;

    @Schema(description = "子组件节点列表")
    private List<ComponentTreeNodeVO> children = new java.util.ArrayList<>();
}
