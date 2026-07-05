package cn.cheers.x.module.platform.policy.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstantiatePolicySetReqDTO {

    @NotBlank(message = "templateId 不能为空")
    private String templateId;

    @NotBlank(message = "businessTypeCode 不能为空")
    private String businessTypeCode;

    private Map<String, Object> params;
}
