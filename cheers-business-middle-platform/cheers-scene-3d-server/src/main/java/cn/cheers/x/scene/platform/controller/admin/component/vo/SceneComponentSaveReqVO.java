package cn.cheers.x.scene.platform.controller.admin.component.vo;

import cn.cheers.x.scene.platform.dal.dataobject.component.SceneComponentDO;
import cn.cheers.x.scene.platform.model.Transform;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "管理后台 - 场景级全局组件保存请求 VO")
@Data
public class SceneComponentSaveReqVO {

    @Schema(description = "组件 ID（更新时必填）", example = "1")
    private Long id;

    @Schema(description = "组件类型", required = true, example = "POST_PROCESS_VOLUME")
    @NotBlank(message = "组件类型不能为空")
    private String componentType;

    @Schema(description = "所属场景 ID", required = true, example = "1001")
    @NotNull(message = "场景 ID 不能为空")
    private Long sceneId;

    @Schema(description = "组件名称（UE 组件实例名）", example = "PostProcessVolume_01")
    private String componentName;

    @Schema(description = "显示名称", example = "环境雾效/后处理")
    private String displayName;

    @Schema(description = "相对变换（位置、旋转、缩放）")
    private Transform relativeTransform;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;

    @Schema(description = "可见性", example = "true")
    private Boolean visible;

    @Schema(description = "渲染顺序", example = "1")
    private Integer renderOrder;

    @Schema(description = "组件配置 JSON", example = "{\"fogEnabled\":true,\"fogDensity\":0.02}")
    private String configJson;

    @Schema(description = "关联的 Actor ID 列表（JSON 数组字符串）", example = "[1001,1002]")
    private String associatedActorIds;

    @Schema(description = "扩展元数据", example = "{\"description\":\"主场景雾效\"}")
    private String metadataJson;

    public static SceneComponentDO convert(SceneComponentSaveReqVO from) {
        return from.toDO();
    }

    public SceneComponentDO toDO() {
        SceneComponentDO dobj = new SceneComponentDO();
        dobj.setId(id);
        dobj.setComponentType(componentType);
        dobj.setSceneId(sceneId);
        dobj.setComponentName(componentName);
        dobj.setDisplayName(displayName);
        dobj.setRelativeTransform(relativeTransform);
        dobj.setEnabled(enabled);
        dobj.setVisible(visible);
        dobj.setRenderOrder(renderOrder);
        dobj.setConfigJson(configJson);
        dobj.setAssociatedActorIds(associatedActorIds);
        dobj.setMetadataJson(metadataJson);
        return dobj;
    }
}