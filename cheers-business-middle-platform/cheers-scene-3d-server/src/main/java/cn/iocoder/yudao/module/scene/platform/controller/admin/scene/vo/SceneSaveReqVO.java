package cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 场景创建/更新请求 VO
 */
@Data
public class SceneSaveReqVO {

    private Long id;

    private String sceneCode;

    @NotBlank(message = "场景名称不能为空")
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
}
