package cn.cheers.x.alarm.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 跨模块新增一条告警。缺类型/级别/设备/位置不得猜。
 */
@Data
public class AlarmTriggerReqDTO {

    @NotNull
    private Long alarmTypeId;

    @NotBlank
    private String alarmLevel;

    @NotBlank
    private String alarmContent;

    @NotNull
    private Long deviceId;

    @NotNull
    private Long locationId;

    private String triggerValue;

    private String thresholdValue;

    private Long ruleId;
}
