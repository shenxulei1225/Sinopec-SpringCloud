package cn.iocoder.yudao.module.emergency.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 事件统计响应 VO
 */
@Schema(description = "管理后台 - 事件统计响应 VO")
@Data
public class EventStatisticsRespVO {

    @Schema(description = "总事件数量", example = "156")
    private Integer totalEvents;

    @Schema(description = "已处理事件数量", example = "142")
    private Integer handledEvents;

    @Schema(description = "处理率", example = "91.0")
    private Double handlingRate;

    @Schema(description = "按类型分布")
    private List<TypeDistributionVO> typeDistribution;

    @Schema(description = "按级别分布")
    private List<LevelDistributionVO> levelDistribution;

    @Schema(description = "按月份趋势")
    private List<MonthlyTrendVO> monthlyTrend;

    @Schema(description = "平均响应时间（分钟）", example = "45.5")
    private Double averageResponseTime;

    @Schema(description = "最长响应时间（分钟）", example = "180")
    private Integer maxResponseTime;

    @Schema(description = "最短响应时间（分钟）", example = "5")
    private Integer minResponseTime;

    @Data
    @Schema(description = "类型分布")
    public static class TypeDistributionVO {
        @Schema(description = "事件类型", example = "fire")
        private String eventType;

        @Schema(description = "数量", example = "89")
        private Integer count;

        @Schema(description = "百分比", example = "57.1")
        private Double percentage;
    }

    @Data
    @Schema(description = "级别分布")
    public static class LevelDistributionVO {
        @Schema(description = "事件级别", example = "I")
        private String eventLevel;

        @Schema(description = "数量", example = "23")
        private Integer count;

        @Schema(description = "百分比", example = "14.7")
        private Double percentage;
    }

    @Data
    @Schema(description = "月份趋势")
    public static class MonthlyTrendVO {
        @Schema(description = "年月", example = "2023-01")
        private String month;

        @Schema(description = "事件数量", example = "12")
        private Integer eventCount;

        @Schema(description = "已处理数量", example = "11")
        private Integer handledCount;
    }
}