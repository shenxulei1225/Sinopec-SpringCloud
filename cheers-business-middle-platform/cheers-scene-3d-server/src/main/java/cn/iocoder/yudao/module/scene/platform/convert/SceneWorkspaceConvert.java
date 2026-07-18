package cn.iocoder.yudao.module.scene.platform.convert;

import cn.iocoder.yudao.module.scene.platform.controller.admin.asset.vo.SceneAssetRespVO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.asset.AssetResourceDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.scene.SceneAssetBindingDO;

public class SceneWorkspaceConvert {

    private SceneWorkspaceConvert() {
    }

    public static SceneAssetRespVO convert(AssetResourceDO asset, SceneAssetBindingDO binding) {
        SceneAssetRespVO respVO = new SceneAssetRespVO();
        respVO.setId(asset.getId());
        respVO.setAssetCode(asset.getAssetCode());
        respVO.setAssetName(asset.getAssetName());
        respVO.setAssetType(asset.getAssetType());
        respVO.setAssetUrl(asset.getAssetUrl());
        respVO.setMetadataJson(asset.getMetadataJson());
        if (binding != null) {
            respVO.setBindingMetadataJson(binding.getMetadataJson());
        }
        return respVO;
    }
}
