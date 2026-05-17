package cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.SplineMeshComponentDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Spline Mesh 组件保存请求 VO")
@Data
public class SplineMeshComponentSaveReqVO {

    private String componentCode;

    private String displayName;

    private String sourceSplineCode;

    private String meshCode;

    private Boolean castShadow;

    private Boolean receiveShadow;

    private String metadataJson;

    public SplineMeshComponentDO toDO() {
        SplineMeshComponentDO item = new SplineMeshComponentDO();
        item.setComponentCode(componentCode);
        item.setDisplayName(displayName);
        item.setSourceSplineCode(sourceSplineCode);
        item.setMeshCode(meshCode);
        item.setCastShadow(castShadow);
        item.setReceiveShadow(receiveShadow);
        item.setMetadataJson(metadataJson);
        return item;
    }
}
