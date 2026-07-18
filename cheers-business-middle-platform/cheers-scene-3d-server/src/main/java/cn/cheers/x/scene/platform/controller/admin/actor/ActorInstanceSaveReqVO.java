package cn.cheers.x.scene.platform.controller.admin.actor;

import cn.cheers.x.framework.common.util.json.JsonUtils;
import cn.cheers.x.scene.platform.dal.dataobject.actor.ActorInstanceDO;
import cn.cheers.x.scene.platform.model.Transform;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Actor 实例保存请求 VO")
@Data
public class ActorInstanceSaveReqVO {

    private Long sceneId;
    private String actorCode;
    private String instanceCode;
    private String instanceName;
    private String parentInstanceCode;
    private String instanceStatus;
    private Boolean visibleFlag;
    private Integer versionNo;
    private String transformJson;
    private String metadataJson;
    private String path;
    /** 所属图层编码列表（JSON 数组），如 ["main", "sub"] */
    private String layerKeys;

    public ActorInstanceDO toDO() {
        ActorInstanceDO item = new ActorInstanceDO();
        item.setSceneId(sceneId);
        item.setActorCode(actorCode);
        item.setInstanceCode(instanceCode);
        item.setInstanceName(instanceName);
        item.setParentInstanceCode(parentInstanceCode);
        item.setInstanceStatus(instanceStatus);
        item.setVisibleFlag(visibleFlag);
        item.setVersionNo(versionNo);
        if (transformJson != null) {
            item.setTransform(JsonUtils.parseObject(transformJson, Transform.class));
        }
        item.setMetadataJson(metadataJson);
        item.setPath(path);
        item.setLayerKeys(layerKeys);
        return item;
    }
}
