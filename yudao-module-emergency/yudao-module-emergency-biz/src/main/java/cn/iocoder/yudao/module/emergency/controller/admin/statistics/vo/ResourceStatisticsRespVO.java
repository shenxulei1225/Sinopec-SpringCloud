package cn.iocoder.yudao.module.emergency.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 资源统计响应 VO
 */
@Schema(description = "管理后台 - 资源统计响应 VO")
@Data
public class ResourceStatisticsRespVO {

    @Schema(description = "总调度次数", example = "234")
    private Integer totalDispatches;

    @Schema(description = "成功完成调度次数", example = "228")
    private Integer completedDispatches;

    @Schema(description = "调度成功率", example = "97.4")
    private Double dispatchSuccessRate;

    @Schema(description = "按类型分布")
    private List<TypeUsageVO> typeUsage;

    @Schema(description = "平均使用时长（分钟）", example = "85.6")
    private Double averageUsageTime;

    @Schema(description = "最长使用时长（分钟）", example = "360")
    private Integer maxUsageTime;

    @Schema(description = "资源利用率趋势")
    private List<UtilizationTrendVO> utilizationTrend;

    @Schema(description = "当前资源状态分布")
    private List<StatusDistributionVO> statusDistribution;

    @Data
    @Schema(description = "类型使用情况")
    public static class TypeUsageVO {
        @Schema(description = "资源类型", example = "vehicle")
        private String resourceType;

        @Schema(description = "调度次数", example = "45")
        private Integer dispatchCount;

        @Schema(description = "平均使用时长（分钟）", example = "92.3")
        private Double avgUsageTime;

        @Schema(description = "使用率", example = "78.5")
        private Double utilizationRate;
    }

    @Data
    @Schema(description = "利用率趋势")
    public static class UtilizationTrendVO {
        @Schema(description = "时间段", example = "2023-01")
        private String timePeriod;

        @Schema(description = "调度次数", example = "18")
        private Integer dispatchCount;

        @Schema(description = "平均利用率", example = "72.3")
        private Double avgUtilizationRate;
    }

    @Data
    @Schema(description = "状态分布")
    public static class StatusDistributionVO {
        @Schema(description = "状态", example = "available")
        private String status;

        @Schema(description = "数量", example = "25")
        private Integer count;

        @Schema(description = "百分比", example = "62.5")
        private Double percentage;
    }
}




