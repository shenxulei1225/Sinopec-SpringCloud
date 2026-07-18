package cn.iocoder.yudao.module.scene.platform.adapter.spi;

public interface AssetMapper {

    EngineAssetRef toEngineAsset(AssetResourceAggregate asset);

    AssetImportResult fromEngineAsset(EngineAssetRef engineAsset);
}
