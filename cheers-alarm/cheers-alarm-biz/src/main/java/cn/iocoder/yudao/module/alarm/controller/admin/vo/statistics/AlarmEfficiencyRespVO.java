package cn.iocoder.yudao.module.alarm.controller.admin.vo.statistics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 告警处理效率 Response VO
 */
@Schema(description = "管理后台 - 告警处理效率 Response VO")
@Data
public class AlarmEfficiencyRespVO {

    @Schema(description = "平均响应时间（分钟）", requiredMode = Schema.RequiredMode.REQUIRED, example = "3.5")
    private Double avgResponseTimeMinutes;

    @Schema(description = "平均处理时间（分钟）", requiredMode = Schema.RequiredMode.REQUIRED, example = "15.2")
    private Double avgHandleTimeMinutes;

    @Schema(description = "平均关闭时间（分钟）", requiredMode = Schema.RequiredMode.REQUIRED, example = "25.8")
    private Double avgCloseTimeMinutes;

    @Schema(description = "告警处理率（百分比）", requiredMode = Schema.RequiredMode.REQUIRED, example = "95.5")
    private Double handleRate;

    @Schema(description = "告警关闭率（百分比）", requiredMode = Schema.RequiredMode.REQUIRED, example = "92.3")
    private Double closeRate;

    @Schema(description = "超时未确认告警数", example = "3")
    private Integer timeoutPendingCount;

    @Schema(description = "超时未处理告警数", example = "2")
    private Integer timeoutHandlingCount;

    @Schema(description = "需人工介入联动数", example = "1")
    private Integer manualInterventionCount;

}
