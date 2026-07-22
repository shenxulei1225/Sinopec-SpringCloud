package cn.iocoder.yudao.module.emergency.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 资源使用统计响应 VO
 */
@Schema(description = "管理后台 - 资源使用统计响应 VO")
@Data
public class ResourceUsageStatisticsRespVO {

    @Schema(description = "总调度次数", example = "234")
    private Long totalDispatches;

    @Schema(description = "总调度资源数", example = "456")
    private Long totalResourcesDispatched;

    @Schema(description = "按资源类型统计")
    private List<ResourceTypeStatsVO> resourceTypeStats;

    @Schema(description = "按响应级别统计")
    private List<LevelResourceStatsVO> levelStats;

    @Schema(description = "最常使用的资源")
    private List<TopResourceVO> topResources;

    @Schema(description = "资源利用率统计")
    private List<ResourceUtilizationVO> utilizationStats;

    @Data
    @Schema(description = "资源类型统计")
    public static class ResourceTypeStatsVO {
        @Schema(description = "资源类型", example = "vehicle")
        private String resourceType;

        @Schema(description = "类型名称", example = "车辆")
        private String typeName;

        @Schema(description = "调度次数", example = "89")
        private Integer dispatchCount;

        @Schema(description = "总资源数", example = "178")
        private Long totalResources;

        @Schema(description = "平均每次调度资源数", example = "2.0")
        private Double avgResourcesPerDispatch;
    }

    @Data
    @Schema(description = "级别资源统计")
    public static class LevelResourceStatsVO {
        @Schema(description = "响应级别", example = "II")
        private String responseLevel;

        @Schema(description = "调度次数", example = "45")
        private Integer dispatchCount;

        @Schema(description = "平均资源数", example = "3.5")
        private Double avgResources;

        @Schema(description = "利用率", example = "78.5")
        private Double utilizationRate;
    }

    @Data
    @Schema(description = "最常使用资源")
    public static class TopResourceVO {
        @Schema(description = "资源ID", example = "123")
        private Long resourceId;

        @Schema(description = "资源名称", example = "消防车001")
        private String resourceName;

        @Schema(description = "资源类型", example = "vehicle")
        private String resourceType;

        @Schema(description = "使用次数", example = "25")
        private Integer usageCount;

        @Schema(description = "总使用时长（分钟）", example = "1250")
        private Double totalUsageTime;
    }

    @Data
    @Schema(description = "资源利用率")
    public static class ResourceUtilizationVO {
        @Schema(description = "资源类型", example = "vehicle")
        private String resourceType;

        @Schema(description = "类型名称", example = "车辆")
        private String typeName;

        @Schema(description = "总资源数", example = "50")
        private Integer totalResources;

        @Schema(description = "使用中资源数", example = "12")
        private Integer inUseResources;

        @Schema(description = "利用率", example = "24.0")
        private Double utilizationRate;
    }
}
