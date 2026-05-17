package cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 场景列表响应 VO")
@Data
public class SceneListRespVO {

    @Schema(description = "场景编号", example = "1")
    private Long id;

    @Schema(description = "场景编码", example = "SCENE_001")
    private String sceneCode;

    @Schema(description = "场景名称", example = "测试场景")
    private String sceneName;

    @Schema(description = "场景类型: INDOOR / OUTDOOR / HYBRID", example = "INDOOR")
    private String sceneType;

    @Schema(description = "引擎配置", example = "threejs")
    private String engineProfile;

    @Schema(description = "状态: 1-启用 0-停用", example = "1")
    private Integer status;

    @Schema(description = "发布状态: DRAFT / PUBLISHED", example = "PUBLISHED")
    private String publishStatus;

    @Schema(description = "发布时间", example = "2025-01-01 00:00:00")
    private LocalDateTime publishedAt;

    @Schema(description = "所属项目编码", example = "PROJECT_001")
    private String projectCode;
}
