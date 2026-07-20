package cn.iocoder.yudao.module.emergency.api.orchestration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyStartResponseExpandRespDTO {

    private Long eventId;
    private Long responseId;
    private String responseNo;
    private String responseLevel;
    private Long planId;
    private String orchestrationRef;
}
