package cn.cheers.x.scene.platform.controller.admin.component.vo;

import cn.cheers.x.scene.platform.dal.dataobject.component.PrimitiveComponentDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Primitive 组件保存请求 VO")
@Data
public class PrimitiveComponentSaveReqVO {

    private String componentCode;

    private String displayName;

    private String collisionEnabled;

    private String objectType;

    private Boolean generateOverlapEvents;

    private Boolean simulationGeneratesHitEvents;

    private Boolean canCharacterStepUpOn;

    private Boolean useDefaultCollision;

    private String physicsMaterialJson;

    private String boundsJson;

    private String collisionResponseJson;

    private String metadataJson;

    public PrimitiveComponentDO toDO() {
        PrimitiveComponentDO item = new PrimitiveComponentDO();
        item.setComponentCode(componentCode);
        item.setDisplayName(displayName);
        item.setCollisionEnabled(collisionEnabled);
        item.setObjectType(objectType);
        item.setGenerateOverlapEvents(generateOverlapEvents);
        item.setSimulationGeneratesHitEvents(simulationGeneratesHitEvents);
        item.setCanCharacterStepUpOn(canCharacterStepUpOn);
        item.setUseDefaultCollision(useDefaultCollision);
        item.setPhysicsMaterialJson(physicsMaterialJson);
        item.setBoundsJson(boundsJson);
        item.setCollisionResponseJson(collisionResponseJson);
        item.setMetadataJson(metadataJson);
        return item;
    }
}
