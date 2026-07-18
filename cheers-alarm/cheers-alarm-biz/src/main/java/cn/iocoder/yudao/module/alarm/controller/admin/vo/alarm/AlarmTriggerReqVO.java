package cn.iocoder.yudao.module.alarm.controller.admin.vo.alarm;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理后台 - 系统自动触发告警 Request VO
 * 
 * <p>用于系统自动触发告警时的请求参数</p>
 */
@Schema(description = "管理后台 - 系统自动触发告警 Request VO")
@Data
public class AlarmTriggerReqVO {

    @Schema(description = "告警类型ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "告警类型不能为空")
    private Long alarmTypeId;

    @Schema(description = "告警级别", requiredMode = Schema.RequiredMode.REQUIRED, example = "WARNING")
    @NotBlank(message = "告警级别不能为空")
    private String alarmLevel;

    @Schema(description = "告警内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "水位超标，当前水位55cm，阈值50cm")
    @NotBlank(message = "告警内容不能为空")
    private String alarmContent;

    @Schema(description = "关联设备ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "关联设备不能为空")
    private Long deviceId;

    @Schema(description = "设备名称", example = "水位传感器-B区2号")
    private String deviceName;

    @Schema(description = "位置ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "位置信息不能为空")
    private Long locationId;

    @Schema(description = "位置名称", example = "B区 > 2号防火分区 > 集水坑")
    private String locationName;

    @Schema(description = "触发值", example = "55cm")
    private String triggerValue;

    @Schema(description = "阈值", example = "50cm")
    private String thresholdValue;

    @Schema(description = "触发的告警规则ID", example = "1")
    private Long ruleId;

}
