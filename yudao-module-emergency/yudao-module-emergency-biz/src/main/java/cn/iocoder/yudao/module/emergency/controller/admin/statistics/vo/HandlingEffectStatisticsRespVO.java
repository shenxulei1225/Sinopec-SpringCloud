package cn.iocoder.yudao.module.emergency.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 处置效果统计响应 VO
 */
@Schema(description = "管理后台 - 处置效果统计响应 VO")
@Data
public class HandlingEffectStatisticsRespVO {

    @Schema(description = "总体成功率", example = "94.9")
    private Double overallSuccessRate;

    @Schema(description = "按级别成功率")
    private List<LevelSuccessRateVO> levelSuccessRates;

    @Schema(description = "按类型成功率")
    private List<TypeSuccessRateVO> typeSuccessRates;

    @Schema(description = "升级统计")
    private List<UpgradeStatsVO> upgradeStats;

    @Schema(description = "取消统计")
    private List<CancelStatsVO> cancelStats;

    @Schema(description = "时间效率分析")
    private List<TimeEfficiencyVO> timeEfficiency;

    @Data
    @Schema(description = "级别成功率")
    public static class LevelSuccessRateVO {
        @Schema(description = "响应级别", example = "II")
        private String responseLevel;

        @Schema(description = "成功数量", example = "64")
        private Long successCount;

        @Schema(description = "总数量", example = "67")
        private Long totalCount;

        @Schema(description = "成功率", example = "95.5")
        private Double successRate;
    }

    @Data
    @Schema(description = "类型成功率")
    public static class TypeSuccessRateVO {
        @Schema(description = "事件类型", example = "fire")
        private String eventType;

        @Schema(description = "类型名称", example = "火灾")
        private String typeName;

        @Schema(description = "成功数量", example = "85")
        private Long successCount;

        @Schema(description = "总数量", example = "89")
        private Long totalCount;

        @Schema(description = "成功率", example = "95.5")
        private Double successRate;
    }

    @Data
    @Schema(description = "升级统计")
    public static class UpgradeStatsVO {
        @Schema(description = "原级别", example = "III")
        private String fromLevel;

        @Schema(description = "目标级别", example = "II")
        private String toLevel;

        @Schema(description = "升级次数", example = "12")
        private Long upgradeCount;

        @Schema(description = "平均升级时间（分钟）", example = "45.3")
        private Double avgUpgradeTime;
    }

    @Data
    @Schema(description = "取消统计")
    public static class CancelStatsVO {
        @Schema(description = "响应级别", example = "III")
        private String responseLevel;

        @Schema(description = "取消数量", example = "5")
        private Long cancelCount;
    }

    @Data
    @Schema(description = "时间效率分析")
    public static class TimeEfficiencyVO {
        @Schema(description = "响应级别", example = "II")
        private String responseLevel;

        @Schema(description = "平均响应时间（分钟）", example = "102.4")
        private Double avgResponseTime;

        @Schema(description = "标准时间（分钟）", example = "15")
        private Integer standardTime;

        @Schema(description = "效率得分", example = "85.6")
        private Double efficiencyScore;

        @Schema(description = "超时率", example = "12.5")
        private Double timeoutRate;
    }
}