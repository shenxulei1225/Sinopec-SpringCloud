package cn.iocoder.yudao.module.scene.platform.controller.admin.asset.vo;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.asset.AssetResourceDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 资源保存请求 VO")
@Data
public class AssetResourceSaveReqVO {

    private String assetCode;

    private String assetName;

    private String assetType;

    private String assetUrl;

    private String metadataJson;

    public AssetResourceDO toDO() {
        AssetResourceDO item = new AssetResourceDO();
        item.setAssetCode(assetCode);
        item.setAssetName(assetName);
        item.setAssetType(assetType);
        item.setAssetUrl(assetUrl);
        item.setMetadataJson(metadataJson);
        return item;
    }
}
