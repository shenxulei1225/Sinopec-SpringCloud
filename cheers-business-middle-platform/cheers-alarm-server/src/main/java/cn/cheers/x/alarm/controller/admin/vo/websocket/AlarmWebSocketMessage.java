package cn.cheers.x.alarm.controller.admin.vo.websocket;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 告警 WebSocket 推送消息
 * 
 * 用于新告警推送、告警状态更新推送、告警升级推送
 */
@Schema(description = "告警 WebSocket 推送消息")
@Data
@Accessors(chain = true)
public class AlarmWebSocketMessage {

    @Schema(description = "告警ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "告警编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "ALM-20260104-00001")
    private String alarmCode;

    @Schema(description = "告警类型路径", example = "环境告警 > 水位告警 > 水位超标")
    private String alarmTypePath;

    @Schema(description = "告警级别", requiredMode = Schema.RequiredMode.REQUIRED, example = "WARNING")
    private String alarmLevel;

    @Schema(description = "告警状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "PENDING")
    private String alarmStatus;

    @Schema(description = "告警来源", requiredMode = Schema.RequiredMode.REQUIRED, example = "SYSTEM")
    private String alarmSource;

    @Schema(description = "告警内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "水位超标，当前水位55cm，阈值50cm")
    private String alarmContent;

    @Schema(description = "设备名称", example = "水位传感器-B区2号")
    private String deviceName;

    @Schema(description = "位置名称", example = "B区 > 2号防火分区 > 集水坑")
    private String locationName;

    @Schema(description = "触发值", example = "55cm")
    private String triggerValue;

    @Schema(description = "阈值", example = "50cm")
    private String thresholdValue;

    @Schema(description = "触发次数", example = "1")
    private Integer triggerCount;

    @Schema(description = "升级级别", example = "0")
    private Integer escalationLevel;

    @Schema(description = "持续时长（秒）", example = "300")
    private Long durationSeconds;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    // ======================= 状态更新相关字段 =======================

    @Schema(description = "操作类型", example = "ACKNOWLEDGE")
    private String operationType;

    @Schema(description = "操作人姓名", example = "张三")
    private String operatorName;

    @Schema(description = "操作时间")
    private LocalDateTime operationTime;

    @Schema(description = "操作备注")
    private String operationRemark;

}
