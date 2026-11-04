package cn.cheers.x.alarm.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 报警标识灭掉后，把还亮着的那条告警改成已消除。
 */
@Data
public class AlarmEliminateReqDTO {

    @NotNull
    private Long deviceId;

    @NotNull
    private Long ruleId;
}
