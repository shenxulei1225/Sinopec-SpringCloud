package cn.cheers.x.scene.platform.controller.admin.actor;

import cn.cheers.x.scene.platform.dal.dataobject.actor.ActorInstanceComponentDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Actor 实例组件保存请求 VO")
@Data
public class ActorInstanceComponentSaveReqVO {

    private Long actorInstanceId;

    private String actorCode;

    private String instanceCode;

    private String componentCode;

    private Boolean enabledFlag;

    private Integer sortNo;

    private String overrideJson;

    private String metadataJson;

    public ActorInstanceComponentDO toDO() {
        ActorInstanceComponentDO item = new ActorInstanceComponentDO();
        item.setActorInstanceId(actorInstanceId);
        item.setActorCode(actorCode);
        item.setInstanceCode(instanceCode);
        item.setComponentCode(componentCode);
        item.setEnabledFlag(enabledFlag);
        item.setSortNo(sortNo);
        item.setOverrideJson(overrideJson);
        item.setMetadataJson(metadataJson);
        return item;
    }
}
