package cn.iocoder.yudao.module.scene.platform.controller.admin.geo.vo;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.geo.GeoLayerConfigDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - GEO 图层保存请求 VO")
@Data
public class GeoLayerSaveReqVO {

    private String layerKey;

    private String layerName;

    private String layerType;

    private Boolean enabledFlag;

    private Integer sortNo;

    private String configJson;

    private String metadataJson;

    public GeoLayerConfigDO toDO() {
        GeoLayerConfigDO item = new GeoLayerConfigDO();
        item.setLayerKey(layerKey);
        item.setLayerName(layerName);
        item.setLayerType(layerType);
        item.setEnabledFlag(enabledFlag);
        item.setSortNo(sortNo);
        item.setConfigJson(configJson);
        item.setMetadataJson(metadataJson);
        return item;
    }
}
