package cn.cheers.x.scene.platform.controller.admin.actor.vo;

import cn.cheers.x.scene.platform.model.Transform;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "管理后台 - Actor 实例 Spawn 请求 VO")
@Data
public class ActorInstanceSpawnReqVO implements Serializable {

    @Schema(description = "场景ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "场景ID不能为空")
    private Long sceneId;

    @Schema(description = "Actor模板编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "tank_01")
    @NotBlank(message = "Actor模板编码不能为空")
    private String actorCode;

    @Schema(description = "实例编码（留空则自动生成）", example = "actor_001")
    private String instanceCode;

    @Schema(description = "实例名称（留空则默认使用Actor名称）", example = "油罐01")
    private String instanceName;

    @Schema(description = "父实例编码（留空表示挂载到场景根节点）", example = "root")
    private String parentInstanceCode;

    @Schema(description = "变换（位置/旋转/缩放）")
    private Transform transform;

    @Schema(description = "层级Key（留空表示默认层级）", example = "main")
    private String layerKeys;

    @Schema(description = "可见标志", example = "true")
    private Boolean visibleFlag;

    @Schema(description = "业务元数据（如 {\"renderAssetCode\":\"LEGACY-HOUSE-1\"}）",
            example = "{\"renderAssetCode\":\"LEGACY-HOUSE-1\"}")
    private String metadataJson;

    /**
     * 实例级组件树
     *
     * 前端从 Actor 模板获取 componentTree 后，自行构建实例时可直接回传完整组件树。
     * 包含 propertiesJson（模板预设属性）和 overrideJson（运行时覆盖属性）。
     * 后端接收到后直接持久化，不再从模板重新构建。
     *
     * 如果前端未传此字段，后端会自动从 Actor 模板加载 componentTree。
     */
    @Schema(description = "实例级组件树（可选，不传则从模板加载）")
    private ComponentTreeVO componentTree;
}
