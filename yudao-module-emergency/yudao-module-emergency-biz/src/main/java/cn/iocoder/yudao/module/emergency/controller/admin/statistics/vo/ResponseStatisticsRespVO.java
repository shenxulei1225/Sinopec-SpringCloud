package cn.iocoder.yudao.module.emergency.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 响应统计响应 VO
 */
@Schema(description = "管理后台 - 响应统计响应 VO")
@Data
public class ResponseStatisticsRespVO {

    @Schema(description = "总响应数量", example = "89")
    private Integer totalResponses;

    @Schema(description = "已结束响应数量", example = "85")
    private Integer endedResponses;

    @Schema(description = "完成率", example = "95.5")
    private Double completionRate;

    @Schema(description = "按级别分布")
    private List<LevelDistributionVO> levelDistribution;

    @Schema(description = "平均响应启动时间（分钟）", example = "15.3")
    private Double averageStartTime;

    @Schema(description = "平均响应持续时间（分钟）", example = "120.5")
    private Double averageDuration;

    @Schema(description = "最长响应持续时间（分钟）", example = "480")
    private Integer maxDuration;

    @Schema(description = "响应时间趋势")
    private List<TimeTrendVO> timeTrend;

    @Data
    @Schema(description = "级别分布")
    public static class LevelDistributionVO {
        @Schema(description = "响应级别", example = "II")
        private String responseLevel;

        @Schema(description = "数量", example = "45")
        private Integer count;

        @Schema(description = "百分比", example = "50.6")
        private Double percentage;
    }

    @Data
    @Schema(description = "时间趋势")
    public static class TimeTrendVO {
        @Schema(description = "时间段", example = "2023-01")
        private String timePeriod;

        @Schema(description = "响应数量", example = "8")
        private Integer responseCount;

        @Schema(description = "平均启动时间（分钟）", example = "12.5")
        private Double avgStartTime;

        @Schema(description = "平均持续时间（分钟）", example = "95.3")
        private Double avgDuration;
    }
}




