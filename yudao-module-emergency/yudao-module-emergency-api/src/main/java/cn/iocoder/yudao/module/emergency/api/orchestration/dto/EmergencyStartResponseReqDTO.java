package cn.iocoder.yudao.module.emergency.api.orchestration.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyStartResponseReqDTO {

    @NotNull
    private Long eventId;

    @NotNull
    private Long planId;

    @NotBlank
    private String responseLevel;

    private String commandOrg;

    private String reason;

    /** 编排模板引用，写入 startInfo 便于追溯 */
    private String orchestrationRef;
}
