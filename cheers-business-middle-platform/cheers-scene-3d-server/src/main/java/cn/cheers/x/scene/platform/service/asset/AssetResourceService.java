package cn.cheers.x.scene.platform.service.asset;

import cn.cheers.x.scene.platform.controller.admin.asset.vo.AssetResourceSaveReqVO;
import cn.cheers.x.scene.platform.controller.admin.asset.vo.SceneAssetRespVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AssetResourceService {

    List<SceneAssetRespVO> getAssetList();

    SceneAssetRespVO getAsset(Long id);

    Long createAsset(AssetResourceSaveReqVO reqVO);

    void updateAsset(Long id, AssetResourceSaveReqVO reqVO);

    void deleteAsset(Long id);

    SceneAssetRespVO uploadAsset(MultipartFile file, String assetCode, String assetName, String assetType);
}
