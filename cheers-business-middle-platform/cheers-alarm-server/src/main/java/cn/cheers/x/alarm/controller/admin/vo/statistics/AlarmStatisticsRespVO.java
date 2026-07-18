package cn.cheers.x.alarm.controller.admin.vo.statistics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 告警统计 Response VO
 */
@Schema(description = "管理后台 - 告警统计 Response VO")
@Data
public class AlarmStatisticsRespVO {

    @Schema(description = "当前活跃告警数", requiredMode = Schema.RequiredMode.REQUIRED, example = "12")
    private Integer activeCount;

    @Schema(description = "今日新增告警数", requiredMode = Schema.RequiredMode.REQUIRED, example = "28")
    private Integer todayNewCount;

    @Schema(description = "今日处理告警数", requiredMode = Schema.RequiredMode.REQUIRED, example = "25")
    private Integer todayHandledCount;

    @Schema(description = "今日关闭告警数", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    private Integer todayClosedCount;

    @Schema(description = "待确认告警数", requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
    private Integer pendingCount;

    @Schema(description = "已确认告警数", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    private Integer acknowledgedCount;

    @Schema(description = "处理中告警数", requiredMode = Schema.RequiredMode.REQUIRED, example = "4")
    private Integer handlingCount;

    @Schema(description = "紧急告警数", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer emergencyCount;

    @Schema(description = "严重告警数", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    private Integer criticalCount;

    @Schema(description = "警告告警数", requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
    private Integer warningCount;

    @Schema(description = "信息告警数", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer infoCount;

    @Schema(description = "平均响应时间（分钟）", example = "3.5")
    private Double avgResponseTimeMinutes;

    @Schema(description = "平均处理时间（分钟）", example = "15.2")
    private Double avgHandleTimeMinutes;

}
