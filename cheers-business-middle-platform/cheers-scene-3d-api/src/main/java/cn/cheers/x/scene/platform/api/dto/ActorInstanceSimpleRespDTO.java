package cn.cheers.x.scene.platform.api.dto;

import lombok.Data;

@Data
public class ActorInstanceSimpleRespDTO {

    private Long id;
    private Long sceneId;
    private String actorCode;
    private String instanceCode;
    private String instanceName;
    private String parentInstanceCode;
    private String instanceStatus;
    private Boolean visibleFlag;
    private String path;
    private String layerKeys;
}
