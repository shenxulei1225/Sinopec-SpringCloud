package cn.cheers.x.scene.platform.controller.admin.actor;

import cn.cheers.x.scene.platform.dal.dataobject.actor.ActorDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Actor 保存请求 VO")
@Data
public class ActorSaveReqVO {

    private String actorCode;

    private String actorName;

    private String actorClass;

    private String parentActorCode;

    private String actorCategory;

    private String engineProfile;

    private Boolean abstractFlag;

    private String lifecycleStatus;

    private String metadataJson;

    public ActorDO toDO() {
        ActorDO item = new ActorDO();
        item.setActorCode(actorCode);
        item.setActorName(actorName);
        item.setActorClass(actorClass);
        item.setParentActorCode(parentActorCode);
        item.setActorCategory(actorCategory);
        item.setEngineProfile(engineProfile);
        item.setAbstractFlag(abstractFlag);
        item.setLifecycleStatus(lifecycleStatus);
        item.setMetadataJson(metadataJson);
        return item;
    }
}
