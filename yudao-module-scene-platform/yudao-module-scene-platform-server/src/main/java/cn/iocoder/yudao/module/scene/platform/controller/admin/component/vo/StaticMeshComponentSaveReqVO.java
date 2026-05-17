package cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.StaticMeshComponentDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Static Mesh 组件保存请求 VO")
@Data
public class StaticMeshComponentSaveReqVO {

    private String componentCode;

    private String displayName;

    private String meshCode;

    private String materialsJson;

    private Boolean castShadow;

    private Boolean receiveShadow;

    private Boolean generateOverlapEvents;

    private String mobility;

    private String metadataJson;

    public StaticMeshComponentDO toDO() {
        StaticMeshComponentDO item = new StaticMeshComponentDO();
        item.setComponentCode(componentCode);
        item.setDisplayName(displayName);
        item.setMeshCode(meshCode);
        item.setMaterialsJson(materialsJson);
        item.setCastShadow(castShadow);
        item.setReceiveShadow(receiveShadow);
        item.setGenerateOverlapEvents(generateOverlapEvents);
        item.setMobility(mobility);
        item.setMetadataJson(metadataJson);
        return item;
    }
}
