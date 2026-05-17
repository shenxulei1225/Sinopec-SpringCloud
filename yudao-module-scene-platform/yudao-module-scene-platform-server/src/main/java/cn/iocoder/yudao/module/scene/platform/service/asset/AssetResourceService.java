package cn.iocoder.yudao.module.scene.platform.service.asset;

import cn.iocoder.yudao.module.scene.platform.controller.admin.asset.vo.AssetResourceSaveReqVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.asset.vo.SceneAssetRespVO;

import java.util.List;

public interface AssetResourceService {

    List<SceneAssetRespVO> getAssetList();

    SceneAssetRespVO getAsset(Long id);

    Long createAsset(AssetResourceSaveReqVO reqVO);

    void updateAsset(Long id, AssetResourceSaveReqVO reqVO);

    void deleteAsset(Long id);
}
