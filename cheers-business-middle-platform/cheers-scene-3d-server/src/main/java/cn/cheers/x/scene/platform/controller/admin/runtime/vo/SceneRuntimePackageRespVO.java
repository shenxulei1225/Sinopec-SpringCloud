package cn.cheers.x.scene.platform.controller.admin.runtime.vo;

import cn.cheers.x.scene.platform.model.Transform;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - 场景运行时包响应 VO")
@Data
public class SceneRuntimePackageRespVO {

    @Schema(description = "场景编码", example = "scene_001")
    private String sceneCode;

    @Schema(description = "场景名称", example = "示例场景")
    private String sceneName;

    @Schema(description = "ActorInstance 运行时配置列表")
    private List<ActorInstanceRuntimeConfigRespVO> actorInstances;

    @Schema(description = "场景级组件运行时配置列表")
    private List<SceneComponentRuntimeConfigRespVO> sceneComponents;

    @Schema(description = "环境变量")
    private Map<String, String> envVars;

    @Schema(description = "运行时配置")
    private RuntimeConfigRespVO runtimeConfig;

    @Schema(description = "场景资源包URL", example = "http://cdn.example.com/scene_001.zip")
    private String resourceUrl;

    @Schema(description = "编排资产清单（供前端按 assetCode 解析 runtimeUrl）")
    private List<CompositionAssetRespVO> assets;

    @Schema(description = "坐标参考（站心等，只读）")
    private Map<String, Object> coordinateReference;

    @Schema(description = "版本号", example = "1.0.0")
    private String version;


    @Schema(description = "创建时间")
    private Long createTime;

    @Schema(description = "更新时间")
    private Long updateTime;

    @Schema(description = "ActorInstance 运行时配置")
    @Data
    public static class ActorInstanceRuntimeConfigRespVO {

        @Schema(description = "Actor模板编码", example = "actor_001")
        private String actorCode;

        @Schema(description = "Actor名称", example = "示例Actor")
        private String actorName;

        @Schema(description = "实例编码", example = "instance_001")
        private String instanceCode;

        @Schema(description = "实例主键（便于前端拖动更新）")
        private Long id;

        @Schema(description = "实例显示名")
        private String instanceName;

        @Schema(description = "渲染资产编码（asset_resource.asset_code）")
        private String renderAssetCode;

        @Schema(description = "模型URL")
        private String modelUrl;

        @Schema(description = "完整位姿（位置/旋转度制 pitch-yaw-roll / 缩放）；画布优先用此字段")
        private Transform transform;

        @Schema(description = "初始位置X")
        private Double initialX;

        @Schema(description = "初始位置Y")
        private Double initialY;

        @Schema(description = "初始位置Z")
        private Double initialZ;

        @Schema(description = "初始旋转角度")
        private Double initialRotation;

        @Schema(description = "行为配置")
        private Map<String, Object> behaviorConfig;

        @Schema(description = "属性配置")
        private Map<String, Object> attributeConfig;

        @Schema(description = "是否启用", example = "true")
        private Boolean enabled;

        @Schema(description = "创建时间")
        private Long createTime;
    }

    @Schema(description = "场景级组件运行时配置")
    @Data
    public static class SceneComponentRuntimeConfigRespVO {

        @Schema(description = "组件编码", example = "comp_001")
        private String componentCode;

        @Schema(description = "组件名称", example = "示例组件")
        private String componentName;

        @Schema(description = "组件类型", example = "light")
        private String componentType;

        @Schema(description = "组件参数")
        private Map<String, Object> params;

        @Schema(description = "位置X")
        private Double posX;

        @Schema(description = "位置Y")
        private Double posY;

        @Schema(description = "位置Z")
        private Double posZ;

        @Schema(description = "是否启用", example = "true")
        private Boolean enabled;

        @Schema(description = "创建时间")
        private Long createTime;
    }

    @Schema(description = "运行时配置")
    @Data
    public static class RuntimeConfigRespVO {

        @Schema(description = "最大并发Actor数", example = "100")
        private Integer maxActors;

        @Schema(description = "物理引擎是否启用", example = "true")
        private Boolean physicsEnabled;

        @Schema(description = "网络同步间隔(ms)", example = "50")
        private Integer syncInterval;

        @Schema(description = "日志级别", example = "INFO")
        private String logLevel;

        @Schema(description = "自定义配置")
        private Map<String, Object> customConfig;
    }

    @Schema(description = "编排资产项")
    @Data
    public static class CompositionAssetRespVO {

        @Schema(description = "资产编码")
        private String assetCode;

        @Schema(description = "运行时 URL（通常为 GLB）")
        private String runtimeUrl;

        @Schema(description = "资产类型（scene_asset 分类节点 code）")
        private String assetType;

        @Schema(description = "源格式")
        private String format;

        @Schema(description = "转换状态 pending|ready|failed")
        private String convertStatus;
    }
}
