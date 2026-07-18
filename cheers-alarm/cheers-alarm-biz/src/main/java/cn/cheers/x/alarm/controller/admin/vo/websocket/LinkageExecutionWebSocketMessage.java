package cn.cheers.x.alarm.controller.admin.vo.websocket;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 联动执行结果 WebSocket 推送消息
 * 
 * 用于联动执行结果推送、联动需要人工介入推送
 */
@Schema(description = "联动执行结果 WebSocket 推送消息")
@Data
@Accessors(chain = true)
public class LinkageExecutionWebSocketMessage {

    @Schema(description = "执行记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "告警ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long alarmId;

    @Schema(description = "告警编码", example = "ALM-20260104-00001")
    private String alarmCode;

    @Schema(description = "联动规则ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long linkageRuleId;

    @Schema(description = "联动规则名称", example = "水位超标联动")
    private String linkageRuleName;

    @Schema(description = "动作类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "DEVICE_CONTROL")
    private String actionType;

    @Schema(description = "动作类型名称", example = "设备控制")
    private String actionTypeName;

    @Schema(description = "目标设备ID", example = "1")
    private Long targetDeviceId;

    @Schema(description = "目标设备名称", example = "潜水泵-B区2号")
    private String targetDeviceName;

    @Schema(description = "执行状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "SUCCESS")
    private String executionStatus;

    @Schema(description = "执行状态名称", example = "成功")
    private String executionStatusName;

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
