package cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.MeshComponentDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Mesh 组件保存请求 VO")
@Data
public class MeshComponentSaveReqVO {

    private String componentCode;

    private String meshDescription;

    private String materialsJson;

    private String metadataJson;

    public MeshComponentDO toDO() {
        MeshComponentDO item = new MeshComponentDO();
        item.setComponentCode(componentCode);
        item.setMeshDescription(meshDescription);
        item.setMaterialsJson(materialsJson);
        item.setMetadataJson(metadataJson);
        return item;
    }
}
