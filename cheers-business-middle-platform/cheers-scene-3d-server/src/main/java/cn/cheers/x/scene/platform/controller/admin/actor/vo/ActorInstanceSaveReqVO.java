package cn.cheers.x.scene.platform.controller.admin.actor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

import cn.cheers.x.scene.platform.dal.dataobject.actor.ActorInstanceDO;
import cn.cheers.x.scene.platform.model.Transform;

@Schema(description = "管理后台 - Actor 实例保存请求 VO")
@Data
public class ActorInstanceSaveReqVO implements Serializable {

    @Schema(description = "主键 ID（更新时传入）", example = "1")
    private Long id;

    @Schema(description = "场景ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "场景ID不能为空")
    private Long sceneId;

    @Schema(description = "Actor模板编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "tank_01")
    @NotBlank(message = "Actor模板编码不能为空")
    private String actorCode;

    @Schema(description = "实例编码（留空则自动生成）", example = "actor_001")
    private String instanceCode;

    @Schema(description = "实例名称（留空则默认使用Actor名称）", example = "油罐01")
    private String instanceName;

    @Schema(description = "父实例编码（留空表示挂载到场景根节点）", example = "root")
    private String parentInstanceCode;

    @Schema(description = "实例状态", example = "RUNNING")
    private String instanceStatus;

    @Schema(description = "可见标志", example = "true")
    private Boolean visibleFlag;

    @Schema(description = "变换（位置/旋转/缩放）")
    private Transform transform;

    @Schema(description = "层级Key（留空表示默认层级）", example = "main")
    private String layerKeys;

    @Schema(description = "业务元数据", example = "{\"facilityId\": 123}")
    private String metadataJson;

    /**
     * 转换为 ActorInstanceDO
     */
    public ActorInstanceDO toDO() {
        ActorInstanceDO dobj = new ActorInstanceDO();
        dobj.setId(id);
        dobj.setSceneId(sceneId);
        dobj.setActorCode(actorCode);
        dobj.setInstanceCode(instanceCode);
        dobj.setInstanceName(instanceName);
        dobj.setParentInstanceCode(parentInstanceCode);
        dobj.setInstanceStatus(instanceStatus);
        dobj.setVisibleFlag(visibleFlag);
        dobj.setTransform(transform);
        dobj.setLayerKeys(layerKeys);
        dobj.setMetadataJson(metadataJson);
        return dobj;
    }
}
