package cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.SphereComponentDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Sphere 组件保存请求 VO")
@Data
public class SphereComponentSaveReqVO {

    private String componentCode;

    private String displayName;

    private Float sphereRadius;

    private String collisionEnabled;

    private Boolean generateOverlapEvents;

    private String metadataJson;

    public SphereComponentDO toDO() {
        SphereComponentDO item = new SphereComponentDO();
        item.setComponentCode(componentCode);
        item.setDisplayName(displayName);
        item.setSphereRadius(sphereRadius);
        item.setCollisionEnabled(collisionEnabled);
        item.setGenerateOverlapEvents(generateOverlapEvents);
        item.setMetadataJson(metadataJson);
        return item;
    }
}
