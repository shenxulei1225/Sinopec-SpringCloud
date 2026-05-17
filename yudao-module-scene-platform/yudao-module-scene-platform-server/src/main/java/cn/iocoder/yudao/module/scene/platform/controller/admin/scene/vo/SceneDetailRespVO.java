package cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 场景详情响应 VO
 *
 * @author Sinopec
 */
@Schema(description = "管理后台 - 场景详情响应 VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SceneDetailRespVO {

    @Schema(description = "场景 ID", example = "1")
    private Long id;

    @Schema(description = "场景编码", example = "scene_001")
    private String sceneCode;

    @Schema(description = "场景名称", example = "测试场景")
    private String sceneName;

    @Schema(description = "场景类型: INDOOR/OUTDOOR/HYBRID", example = "INDOOR")
    private String sceneType;

    @Schema(description = "引擎配置 profile", example = "UE5.4")
    private String engineProfile;

    @Schema(description = "场景能力配置 JSON")
    private String capabilitiesJson;

    @Schema(description = "图层配置 JSON")
    private String layerConfigJson;

    @Schema(description = "默认视角配置 JSON")
    private String defaultViewpointJson;

    @Schema(description = "状态: 1-启用 0-停用", example = "1")
    private Integer status;

    @Schema(description = "发布状态: DRAFT/PUBLISHED", example = "PUBLISHED")
    private String publishStatus;

    @Schema(description = "发布时间")
    private LocalDateTime publishedAt;

    @Schema(description = "发布人")
    private String publishedBy;
}
