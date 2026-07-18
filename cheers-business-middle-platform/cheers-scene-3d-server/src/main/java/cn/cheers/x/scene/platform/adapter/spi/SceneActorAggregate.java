package cn.cheers.x.scene.platform.adapter.spi;

import lombok.Data;

@Data
public class SceneActorAggregate {

    private String sceneCode;

    private String actorType;

    private String actorName;

    private String payloadJson;
}
