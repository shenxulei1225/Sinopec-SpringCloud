package cn.cheers.x.alarm.api.dto;

import lombok.Data;

/**
 * 新增告警后的账本编号。
 */
@Data
public class AlarmTriggerRespDTO {

    private Long alarmId;

    private String alarmCode;
}
