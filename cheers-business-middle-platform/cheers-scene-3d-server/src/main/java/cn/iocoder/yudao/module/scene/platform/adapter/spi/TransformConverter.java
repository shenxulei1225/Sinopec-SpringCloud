package cn.iocoder.yudao.module.scene.platform.adapter.spi;

public interface TransformConverter {

    EngineTransform toEngineTransform(SceneTransform transform);

    SceneTransform fromEngineTransform(EngineTransform engineTransform);
}
