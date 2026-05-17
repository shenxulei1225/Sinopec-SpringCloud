package cn.iocoder.yudao.module.scene.platform.convert;

import cn.iocoder.yudao.module.scene.platform.controller.admin.asset.vo.SceneAssetRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.geo.vo.GeoLayerDetailRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.geo.vo.GeoLayerRespVO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.asset.AssetResourceDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.geo.GeoLayerConfigDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.scene.SceneAssetBindingDO;

import java.util.List;

public class SceneWorkspaceConvert {

    private SceneWorkspaceConvert() {
    }

    public static GeoLayerDetailRespVO convert(GeoLayerConfigDO layer, List<SceneAssetRespVO> assets) {
        GeoLayerDetailRespVO respVO = new GeoLayerDetailRespVO();
        GeoLayerRespVO layerVO = new GeoLayerRespVO();
        layerVO.setId(layer.getId());
        layerVO.setLayerKey(layer.getLayerKey());
        layerVO.setLayerName(layer.getLayerName());
        layerVO.setLayerType(layer.getLayerType());
        layerVO.setVisibleFlag(layer.getEnabledFlag());
        layerVO.setSortNo(layer.getSortNo());
        layerVO.setMetadataJson(layer.getMetadataJson());
        respVO.setLayer(layerVO);
        respVO.setAssets(assets);
        return respVO;
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
