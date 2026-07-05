package cn.cheers.x.module.platform.policy.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyResolveForRunReqDTO {

    @NotBlank(message = "policySetId 不能为空")
    private String policySetId;

    private String businessTypeCode;
}
