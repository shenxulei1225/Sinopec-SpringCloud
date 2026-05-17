package cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.BoxComponentDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Box 组件保存请求 VO")
@Data
public class BoxComponentSaveReqVO {

    private String componentCode;

    private String displayName;

    private Float boxExtentX;

    private Float boxExtentY;

    private Float boxExtentZ;

    private String collisionEnabled;

    private Boolean generateOverlapEvents;

    private String metadataJson;

    public BoxComponentDO toDO() {
        BoxComponentDO item = new BoxComponentDO();
        item.setComponentCode(componentCode);
        item.setDisplayName(displayName);
        item.setBoxExtentX(boxExtentX);
        item.setBoxExtentY(boxExtentY);
        item.setBoxExtentZ(boxExtentZ);
        item.setCollisionEnabled(collisionEnabled);
        item.setGenerateOverlapEvents(generateOverlapEvents);
        item.setMetadataJson(metadataJson);
        return item;
    }
}
