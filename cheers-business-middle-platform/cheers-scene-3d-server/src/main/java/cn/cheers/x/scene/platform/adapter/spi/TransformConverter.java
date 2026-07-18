package cn.cheers.x.scene.platform.adapter.spi;

public interface TransformConverter {

    EngineTransform toEngineTransform(SceneTransform transform);

    SceneTransform fromEngineTransform(EngineTransform engineTransform);
}
