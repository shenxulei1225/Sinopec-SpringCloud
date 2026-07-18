package cn.iocoder.yudao.module.scene.platform.service.asset;

/**
 * 校验 {@code assetType} 是否为 {@code scene_asset} 分类树下的有效节点 code。
 */
public interface SceneAssetCategoryValidator {

    /**
     * @param assetType 分类节点 code（通常大写，如 BUILDING）
     * @throws cn.cheers.x.framework.common.exception.ServiceException 无效或不存在时
     */
    void validateAssetTypeCode(String assetType);
}
