package cn.iocoder.yudao.module.scene.platform.service.asset.impl;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.scene.platform.controller.admin.asset.vo.AssetResourceSaveReqVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.asset.vo.SceneAssetRespVO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.asset.AssetResourceDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.asset.AssetResourceMapper;
import cn.iocoder.yudao.module.scene.platform.service.asset.AssetResourceService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class AssetResourceServiceImpl implements AssetResourceService {

    @Resource
    private AssetResourceMapper assetResourceMapper;

    @Override
    public List<SceneAssetRespVO> getAssetList() {
        return BeanUtils.toBean(assetResourceMapper.selectList(), SceneAssetRespVO.class,
                item -> item.setMetadataJson(null));
    }

    @Override
    public SceneAssetRespVO getAsset(Long id) {
        return BeanUtils.toBean(getRequiredAsset(id), SceneAssetRespVO.class);
    }

    @Override
    public Long createAsset(AssetResourceSaveReqVO reqVO) {
        AssetResourceDO asset = BeanUtils.toBean(reqVO, AssetResourceDO.class);
        asset.setStatus(1);
        assetResourceMapper.insert(asset);
        return asset.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAsset(Long id, AssetResourceSaveReqVO reqVO) {
        AssetResourceDO asset = getRequiredAsset(id);
        BeanUtils.copyProperties(reqVO, asset);
        assetResourceMapper.updateById(asset);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAsset(Long id) {
        getRequiredAsset(id);
        assetResourceMapper.deleteById(id);
    }

    private AssetResourceDO getRequiredAsset(Long id) {
        AssetResourceDO asset = assetResourceMapper.selectById(id);
        if (asset == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "资源不存在");
        }
        return asset;
    }
}
