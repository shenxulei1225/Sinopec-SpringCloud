package cn.cheers.x.scene.platform.adapter.spi;

public interface LayerMapper {

    EngineLayerGroup toEngineLayer(SceneLayerAggregate layer);

    SceneLayerAggregate fromEngineLayer(EngineLayerGroup engineLayer);
}
