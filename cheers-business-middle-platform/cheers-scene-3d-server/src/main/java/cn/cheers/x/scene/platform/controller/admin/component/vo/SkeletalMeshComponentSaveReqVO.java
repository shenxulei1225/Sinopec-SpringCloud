package cn.cheers.x.scene.platform.controller.admin.component.vo;

import cn.cheers.x.scene.platform.dal.dataobject.component.SkeletalMeshComponentDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Skeletal Mesh 组件保存请求 VO")
@Data
public class SkeletalMeshComponentSaveReqVO {

    private String componentCode;

    private String displayName;

    private String skeletonCode;

    private String animationBlueprintCode;

    private String materialsJson;

    private Boolean castShadow;

    private Boolean receiveShadow;

    private Boolean useAnimationBlueprint;

    private String metadataJson;

    public SkeletalMeshComponentDO toDO() {
        SkeletalMeshComponentDO item = new SkeletalMeshComponentDO();
        item.setComponentCode(componentCode);
        item.setDisplayName(displayName);
        item.setSkeletonCode(skeletonCode);
        item.setAnimationBlueprintCode(animationBlueprintCode);
        item.setMaterialsJson(materialsJson);
        item.setCastShadow(castShadow);
        item.setReceiveShadow(receiveShadow);
        item.setUseAnimationBlueprint(useAnimationBlueprint);
        item.setMetadataJson(metadataJson);
        return item;
    }
}
