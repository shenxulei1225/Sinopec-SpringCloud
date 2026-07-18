package cn.cheers.x.scene.platform.adapter.spi;

import lombok.Data;

@Data
public class SceneActorConfigAggregate {

    private String sceneCode;

    private String configType;

    private String payloadJson;
}
