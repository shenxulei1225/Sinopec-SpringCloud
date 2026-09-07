package cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 动态业务插件清单响应项。
 *
 * 用途：由后端明确下发“当前环境允许启用的插件”，前端只按该清单安装插件能力。
 */
@Data
@Schema(description = "管理后台 - 动态业务插件清单项 Response VO")
public class DynamicBusinessPluginManifestRespVO {

    @Schema(description = "插件唯一标识", requiredMode = Schema.RequiredMode.REQUIRED, example = "action-library")
    private String pluginId;

    @Schema(description = "插件版本", requiredMode = Schema.RequiredMode.REQUIRED, example = "1.0.0")
    private String version;

    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean enabled;

    @Schema(description = "平台版本范围", example = ">=1.0.0")
    private String platformRange;

    @Schema(description = "插件注册入口（审计用）", example = "features/action/plugin/manifest.ts")
    private String entry;

    @Schema(description = "依赖插件列表", example = "[\"dynamic-core\"]")
    private List<String> dependsOn;

    @Schema(description = "声明权限列表", example = "[\"work-blocks:detail\"]")
    private List<String> permissions;

    @Schema(description = "语义贡献列表", example = "[\"ACTION_TREE\",\"ACTION_PARAM_SCHEMA\"]")
    private List<String> semanticContributions;
}
