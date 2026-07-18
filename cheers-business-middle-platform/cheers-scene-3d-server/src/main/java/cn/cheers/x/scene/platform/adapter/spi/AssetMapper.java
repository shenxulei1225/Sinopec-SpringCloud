package cn.cheers.x.scene.platform.adapter.spi;

public interface AssetMapper {

    EngineAssetRef toEngineAsset(AssetResourceAggregate asset);

    AssetImportResult fromEngineAsset(EngineAssetRef engineAsset);
}
