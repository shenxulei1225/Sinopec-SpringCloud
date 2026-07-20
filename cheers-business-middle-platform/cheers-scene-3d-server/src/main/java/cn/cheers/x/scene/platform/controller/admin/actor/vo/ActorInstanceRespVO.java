package cn.cheers.x.scene.platform.controller.admin.actor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

import cn.cheers.x.scene.platform.model.Transform;

@Schema(description = "管理后台 - Actor 实例响应 VO")
@Data
public class ActorInstanceRespVO implements Serializable {

    @Schema(description = "主键 ID", example = "1")
    private Long id;

    @Schema(description = "场景ID", example = "1")
    private Long sceneId;

    @Schema(description = "Actor模板编码", example = "tank_01")
    private String actorCode;

    @Schema(description = "实例编码", example = "actor_001")
    private String instanceCode;

    @Schema(description = "实例名称", example = "油罐01")
    private String instanceName;

    @Schema(description = "父实例编码", example = "root")
    private String parentInstanceCode;

    @Schema(description = "实例状态", example = "RUNNING")
    private String instanceStatus;

    @Schema(description = "可见标志", example = "true")
    private Boolean visibleFlag;

    @Schema(description = "版本号", example = "1")
    private Integer versionNo;

    @Schema(description = "变换信息（位置/旋转/缩放）")
    private Transform transform;

    @Schema(description = "业务元数据")
    private String metadataJson;

    @Schema(description = "实例路径（层级路径）", example = "root/child/grandchild")
    private String path;

    @Schema(description = "层级Key", example = "main")
    /** 所属图层编码列表（JSON 数组），如 ["main", "sub"] */
    private String layerKeys;

    @Schema(description = "GPS 经度（WGS84）")
    private java.math.BigDecimal gpsLng;

    @Schema(description = "GPS 纬度（WGS84）")
    private java.math.BigDecimal gpsLat;

    @Schema(description = "GPS 椭球高（米）")
    private java.math.BigDecimal gpsHeight;

    @Schema(description = "GPS 高程来源：TERRAIN_SAMPLE / MANUAL 等")
    private String gpsHeightSource;

    @Schema(description = "实例组件树")
    private ActorInstanceComponentTreeRespVO componentTree;

    @Schema(description = "关联的组件列表")
    private List<ActorInstanceComponentRespVO> components;

    @Schema(description = "创建时间")
    private java.time.LocalDateTime createTime;

    @Schema(description = "更新时间")
    private java.time.LocalDateTime updateTime;

    /**
     * 实例组件 VO
     */
    @Data
    public static class ActorInstanceComponentRespVO implements Serializable {
        @Schema(description = "组件ID")
        private Long id;

        @Schema(description = "实例编码")
        private String instanceCode;

        @Schema(description = "组件编码（UE中SceneComponent的名字，如'StaticMeshComponent0'）")
        private String componentCode;

        @Schema(description = "组件类型（SceneComponent/StaticMeshComponent/LightComponent等）")
        private String componentTypeName;

        @Schema(description = "相对变换")
        private Transform relativeTransform;

        @Schema(description = "是否启用")
        private Boolean enabledFlag;

        @Schema(description = "排序号")
        private Integer sortNo;

        @Schema(description = "覆盖配置JSON")
        private String overrideJson;

        @Schema(description = "构造参数JSON（用于引擎实例化）")
        private String constructArgsJson;
    }
}
