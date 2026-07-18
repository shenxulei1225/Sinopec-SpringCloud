package cn.cheers.x.alarm.controller.admin.vo.linkage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理后台 - 联动执行记录 Response VO
 */
@Schema(description = "管理后台 - 联动执行记录 Response VO")
@Data
public class LinkageExecutionRespVO {

    @Schema(description = "执行记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "告警ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long alarmId;

    @Schema(description = "联动规则ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long linkageRuleId;

    @Schema(description = "动作类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "DEVICE_CONTROL")
    private String actionType;

    @Schema(description = "动作配置（JSON）")
    private String actionConfig;

    @Schema(description = "目标设备ID", example = "1")
    private Long targetDeviceId;

    @Schema(description = "目标设备名称", example = "潜水泵-B区2号")
    private String targetDeviceName;

    @Schema(description = "执行状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "SUCCESS")
    private String executionStatus;

    @Schema(description = "重试次数", example = "0")
    private Integer retryCount;

    @Schema(description = "执行结果")
    private String executionResult;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "执行耗时（毫秒）", example = "1500")
    private Long durationMs;

    @Schema(description = "是否需要人工介入", example = "false")
    private Boolean manualIntervention;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
