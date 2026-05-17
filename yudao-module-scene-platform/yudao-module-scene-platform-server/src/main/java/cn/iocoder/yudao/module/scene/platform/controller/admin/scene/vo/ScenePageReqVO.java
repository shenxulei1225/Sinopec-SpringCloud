package cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo;

import lombok.Data;

/**
 * 场景分页查询请求 VO
 */
@Data
public class ScenePageReqVO {

    private String sceneCode;

    private String sceneName;

    private String sceneType;

    private Integer status;

    private String publishStatus;

    private String projectCode;

    private Integer pageNo = 1;

    private Integer pageSize = 10;
}
