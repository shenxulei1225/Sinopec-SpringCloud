package cn.iocoder.yudao.module.emergency.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 响应时间统计响应 VO
 */
@Schema(description = "管理后台 - 响应时间统计响应 VO")
@Data
public class ResponseTimeStatisticsRespVO {

    @Schema(description = "平均启动时间（分钟）", example = "15.3")
    private Double avgStartTime;

    @Schema(description = "平均执行时间（分钟）", example = "120.5")
    private Double avgExecutionTime;

    @Schema(description = "平均总时间（分钟）", example = "135.8")
    private Double avgTotalTime;

    @Schema(description = "最短响应时间（分钟）", example = "5")
    private Double minResponseTime;

    @Schema(description = "最长响应时间（分钟）", example = "480")
    private Double maxResponseTime;

    @Schema(description = "按级别统计")
    private List<LevelTimeStatsVO> levelStats;

    @Schema(description = "按类型统计")
    private List<TypeTimeStatsVO> typeStats;

    @Schema(description = "时间分布")
    private List<TimeRangeCountVO> timeDistribution;

    @Data
    @Schema(description = "级别时间统计")
    public static class LevelTimeStatsVO {
        @Schema(description = "响应级别", example = "II")
        private String responseLevel;

        @Schema(description = "平均启动时间（分钟）", example = "12.5")
        private Double avgStartTime;

        @Schema(description = "平均执行时间（分钟）", example = "95.3")
        private Double avgExecutionTime;

        @Schema(description = "平均总时间（分钟）", example = "107.8")
        private Double avgTotalTime;

        @Schema(description = "数量", example = "45")
        private Long count;
    }

    @Data
    @Schema(description = "类型时间统计")
    public static class TypeTimeStatsVO {
        @Schema(description = "事件类型", example = "fire")
        private String eventType;

        @Schema(description = "类型名称", example = "火灾")
        private String typeName;

        @Schema(description = "平均启动时间（分钟）", example = "10.5")
        private Double avgStartTime;

        @Schema(description = "平均执行时间（分钟）", example = "88.3")
        private Double avgExecutionTime;

        @Schema(description = "平均总时间（分钟）", example = "98.8")
        private Double avgTotalTime;

        @Schema(description = "数量", example = "67")
        private Long count;
    }

    @Data
    @Schema(description = "时间范围统计")
    public static class TimeRangeCountVO {
        @Schema(description = "时间范围", example = "0-10分钟")
        private String timeRange;

        @Schema(description = "数量", example = "45")
        private Long count;

        @Schema(description = "百分比", example = "28.8")
        private Double percentage;
    }
}