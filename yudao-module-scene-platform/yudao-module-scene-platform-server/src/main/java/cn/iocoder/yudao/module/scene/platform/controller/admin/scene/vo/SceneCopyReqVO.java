package cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 场景复制请求 VO
 */
@Data
public class SceneCopyReqVO {

    @NotBlank(message = "目标场景编码不能为空")
    private String targetSceneCode;

    @NotBlank(message = "目标场景名称不能为空")
    private String targetSceneName;
}
