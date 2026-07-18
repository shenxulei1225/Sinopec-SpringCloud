package cn.cheers.x.module.platform.policy.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicySetRespDTO {

    private String policySetId;
    private String entityTypeCode;
    private String templateId;
    private String status;
    private Integer version;
}
