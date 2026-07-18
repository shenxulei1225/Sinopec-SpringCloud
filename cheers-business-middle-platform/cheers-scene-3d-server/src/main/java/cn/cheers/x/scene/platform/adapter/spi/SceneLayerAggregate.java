package cn.cheers.x.scene.platform.adapter.spi;

import lombok.Data;

@Data
public class SceneLayerAggregate {

    private String sceneCode;

    private String layerKey;

    private String layerType;

    private String parentLayerKey;
}
