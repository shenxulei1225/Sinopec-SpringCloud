package cn.cheers.x.scene.platform.controller.admin.component.vo;

import cn.cheers.x.scene.platform.dal.dataobject.component.LightComponentDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Light 组件保存请求 VO")
@Data
public class LightComponentSaveReqVO {

    private String componentCode;

    private String displayName;

    private Float intensity;

    private String lightColor;

    private Boolean castShadows;

    private String mobility;

    private String metadataJson;

    public LightComponentDO toDO() {
        LightComponentDO item = new LightComponentDO();
        item.setComponentCode(componentCode);
        item.setDisplayName(displayName);
        item.setIntensity(intensity);
        item.setLightColor(lightColor);
        item.setCastShadows(castShadows);
        item.setMobility(mobility);
        item.setMetadataJson(metadataJson);
        return item;
    }
}
