package cn.iocoder.yudao.module.alarm.controller.admin.vo.alarm;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 今日告警统计 VO
 *
 * @author 告警管理模块
 */
@Schema(description = "管理后台 - 今日告警统计 Response VO")
@Data
public class TodayAlarmStatisticsVO {

    @Schema(description = "统计日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-01-05")
    private LocalDate date;

    @Schema(description = "今日新增告警数", requiredMode = Schema.RequiredMode.REQUIRED, example = "15")
    private Integer newCount;

    @Schema(description = "今日已处理告警数", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer handledCount;

    @Schema(description = "今日已关闭告警数", requiredMode = Schema.RequiredMode.REQUIRED, example = "8")
    private Integer closedCount;

    @Schema(description = "今日待确认告警数", requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
    private Integer pendingCount;

    @Schema(description = "今日紧急告警数", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer criticalCount;

    @Schema(description = "今日重要告警数", requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
    private Integer majorCount;

    @Schema(description = "今日一般告警数", requiredMode = Schema.RequiredMode.REQUIRED, example = "8")
    private Integer minorCount;

    @Schema(description = "今日提示告警数", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer warningCount;

}
