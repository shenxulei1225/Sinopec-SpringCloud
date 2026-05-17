package cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.ChildActorComponentDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Child Actor 组件保存请求 VO")
@Data
public class ChildActorComponentSaveReqVO {

    private String componentCode;

    private String displayName;

    private String childActorCode;

    private Boolean inheritTransform;

    private Boolean childActorEditable;

    private String metadataJson;

    public ChildActorComponentDO toDO() {
        ChildActorComponentDO item = new ChildActorComponentDO();
        item.setComponentCode(componentCode);
        item.setDisplayName(displayName);
        item.setChildActorCode(childActorCode);
        item.setInheritTransform(inheritTransform);
        item.setChildActorEditable(childActorEditable);
        item.setMetadataJson(metadataJson);
        return item;
    }
}
