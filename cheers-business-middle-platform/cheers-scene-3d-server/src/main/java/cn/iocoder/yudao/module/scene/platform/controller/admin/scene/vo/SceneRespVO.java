package cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 场景响应 VO
 */
@Data
public class SceneRespVO {

    private Long id;
    private String sceneCode;
    private String sceneName;
    private String sceneType;
    private String engineProfile;
    private String capabilitiesJson;
    private String layerConfigJson;
    private String defaultViewpointJson;
    private String projectCode;
    private String businessKey;
    private Integer status;
    private String publishStatus;
    private LocalDateTime publishedAt;
    private String publishedBy;
    private String actorInstanceCodesJson;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
