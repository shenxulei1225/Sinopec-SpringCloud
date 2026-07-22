package cn.iocoder.yudao.module.emergency.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 处置效果统计响应 VO
 */
@Schema(description = "管理后台 - 处置效果统计响应 VO")
@Data
public class EffectivenessStatisticsRespVO {

    @Schema(description = "总处置事件数", example = "156")
    private Integer totalEvents;

    @Schema(description = "成功处置事件数", example = "148")
    private Integer successfulEvents;

    @Schema(description = "处置成功率", example = "94.9")
    private Double successRate;

    @Schema(description = "平均处置时间（分钟）", example = "95.6")
    private Double averageHandlingTime;

    @Schema(description = "最快处置时间（分钟）", example = "15")
    private Integer fastestHandlingTime;

    @Schema(description = "最慢处置时间（分钟）", example = "720")
    private Integer slowestHandlingTime;

    @Schema(description = "按事件类型效果分析")
    private List<TypeEffectivenessVO> typeEffectiveness;

    @Schema(description = "按响应级别效果分析")
    private List<LevelEffectivenessVO> levelEffectiveness;

    @Schema(description = "处置时间分布")
    private List<TimeDistributionVO> timeDistribution;

    @Data
    @Schema(description = "类型效果分析")
    public static class TypeEffectivenessVO {
        @Schema(description = "事件类型", example = "fire")
        private String eventType;

        @Schema(description = "事件总数", example = "89")
        private Integer totalEvents;

        @Schema(description = "成功处置数", example = "85")
        private Integer successfulEvents;

        @Schema(description = "成功率", example = "95.5")
        private Double successRate;

        @Schema(description = "平均处置时间（分钟）", example = "88.3")
        private Double avgHandlingTime;
    }

    @Data
    @Schema(description = "级别效果分析")
    public static class LevelEffectivenessVO {
        @Schema(description = "响应级别", example = "II")
        private String responseLevel;

        @Schema(description = "事件总数", example = "67")
        private Integer totalEvents;

        @Schema(description = "成功处置数", example = "64")
        private Integer successfulEvents;

        @Schema(description = "成功率", example = "95.5")
        private Double successRate;

        @Schema(description = "平均处置时间（分钟）", example = "102.4")
        private Double avgHandlingTime;
    }

    @Data
    @Schema(description = "时间分布")
    public static class TimeDistributionVO {
        @Schema(description = "时间范围", example = "0-30分钟")
        private String timeRange;

        @Schema(description = "事件数量", example = "45")
        private Integer eventCount;

        @Schema(description = "百分比", example = "28.8")
        private Double percentage;
    }
}




