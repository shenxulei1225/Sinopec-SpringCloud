package cn.cheers.x.alarm.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 给相关人员发通知。必须带告警编号；接收人可空，空则用该级别已配好的默认名单。
 */
@Data
public class AlarmNotifyReqDTO {

    @NotNull
    private Long alarmId;

    private List<Long> recipientUserIds = new ArrayList<>();
}
