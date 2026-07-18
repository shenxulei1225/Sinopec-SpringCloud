package cn.cheers.x.scene.platform.controller.admin.component.vo;

import cn.cheers.x.framework.common.core.ArrayValuable;
import cn.cheers.x.scene.platform.dal.dataobject.component.SceneComponentDO;
import cn.cheers.x.scene.platform.model.Transform;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "管理后台 - 场景级全局组件响应 VO")
@Data
public class SceneComponentRespVO implements ArrayValuable<SceneComponentRespVO> {

    @Schema(description = "组件 ID", example = "1")
    private Long id;

    @Schema(description = "组件类型", example = "POST_PROCESS_VOLUME")
    private String componentType;

    @Schema(description = "所属场景 ID", example = "1001")
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

    @Schema(description = "组件配置 JSON", example = "{\"fogEnabled\":true}")
    private String configJson;

    @Schema(description = "关联的 Actor ID 列表（JSON 数组字符串）", example = "[1001,1002]")
    private String associatedActorIds;

    @Schema(description = "扩展元数据", example = "{\"description\":\"主场景雾效\"}")
    private String metadataJson;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    public static final SceneComponentRespVO[] array = new SceneComponentRespVO[0];

    @Override
    public SceneComponentRespVO[] array() {
        return array;
    }

    public static SceneComponentRespVO convert(SceneComponentDO dobj) {
        if (dobj == null) {
            return null;
        }
        SceneComponentRespVO resp = new SceneComponentRespVO();
        resp.setId(dobj.getId());
        resp.setComponentType(dobj.getComponentType());
        resp.setSceneId(dobj.getSceneId());
        resp.setComponentName(dobj.getComponentName());
        resp.setDisplayName(dobj.getDisplayName());
        resp.setRelativeTransform(dobj.getRelativeTransform());
        resp.setEnabled(dobj.getEnabled());
        resp.setVisible(dobj.getVisible());
        resp.setRenderOrder(dobj.getRenderOrder());
        resp.setConfigJson(dobj.getConfigJson());
        resp.setAssociatedActorIds(dobj.getAssociatedActorIds());
        resp.setMetadataJson(dobj.getMetadataJson());
        resp.setCreateTime(dobj.getCreateTime());
        return resp;
    }

    public static List<SceneComponentRespVO> convert(List<SceneComponentDO> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        return list.stream().map(SceneComponentRespVO::convert).collect(Collectors.toList());
    }

}
