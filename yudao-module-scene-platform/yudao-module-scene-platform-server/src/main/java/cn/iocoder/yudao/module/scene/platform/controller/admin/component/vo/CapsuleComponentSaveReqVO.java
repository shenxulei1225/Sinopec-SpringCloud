package cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.CapsuleComponentDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Capsule 组件保存请求 VO")
@Data
public class CapsuleComponentSaveReqVO {

    private String componentCode;

    private String displayName;

    private Float capsuleRadius;

    private Float capsuleHalfHeight;

    private String collisionEnabled;

    private Boolean generateOverlapEvents;

    private String metadataJson;

    public CapsuleComponentDO toDO() {
        CapsuleComponentDO item = new CapsuleComponentDO();
        item.setComponentCode(componentCode);
        item.setDisplayName(displayName);
        item.setCapsuleRadius(capsuleRadius);
        item.setCapsuleHalfHeight(capsuleHalfHeight);
        item.setCollisionEnabled(collisionEnabled);
        item.setGenerateOverlapEvents(generateOverlapEvents);
        item.setMetadataJson(metadataJson);
        return item;
    }
}
