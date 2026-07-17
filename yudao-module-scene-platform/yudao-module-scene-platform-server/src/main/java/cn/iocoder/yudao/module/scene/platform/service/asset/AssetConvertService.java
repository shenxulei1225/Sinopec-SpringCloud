package cn.iocoder.yudao.module.scene.platform.service.asset;

/**
 * 资产格式转换服务：生成 Web 运行时 GLB URL，并写入 {@code metadataJson}。
 */
public interface AssetConvertService {

    /**
     * 将指定资产转换为运行时 GLB（或标记 pending/failed）。
     *
     * @param assetId 资产编号
     */
    void convertToGlb(Long assetId);
}
