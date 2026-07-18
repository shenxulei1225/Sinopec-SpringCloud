package cn.cheers.x.alarm.controller.admin.vo.statistics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 管理后台 - 告警趋势 Response VO
 */
@Schema(description = "管理后台 - 告警趋势 Response VO")
@Data
public class AlarmTrendRespVO {

    @Schema(description = "日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-01-04")
    private LocalDate date;

    @Schema(description = "新增告警数", requiredMode = Schema.RequiredMode.REQUIRED, example = "28")
    private Integer newCount;

    @Schema(description = "处理告警数", requiredMode = Schema.RequiredMode.REQUIRED, example = "25")
    private Integer handledCount;

    @Schema(description = "关闭告警数", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    private Integer closedCount;

    @Schema(description = "紧急告警数", example = "2")
    private Integer emergencyCount;

    @Schema(description = "严重告警数", example = "5")
    private Integer criticalCount;

    @Schema(description = "警告告警数", example = "15")
    private Integer warningCount;

    @Schema(description = "信息告警数", example = "6")
    private Integer infoCount;

}
