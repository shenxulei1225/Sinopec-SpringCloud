package cn.cheers.x.scene.platform.api.dto;

import lombok.Data;

@Data
public class ActorInstanceRuntimeRespDTO {

    private Long id;
    private Long sceneId;
    private String sceneCode;
    private String actorCode;
    private String instanceCode;
    private String instanceName;
    private String instanceStatus;
    private Boolean visibleFlag;
    private String layerKeys;
    private String metadataJson;

    private String renderType;
    private String modelUrl;

    private Double positionX;
    private Double positionY;
    private Double positionZ;

    private Double rotationX;
    private Double rotationY;
    private Double rotationZ;

    private Double scaleX;
    private Double scaleY;
    private Double scaleZ;
}
