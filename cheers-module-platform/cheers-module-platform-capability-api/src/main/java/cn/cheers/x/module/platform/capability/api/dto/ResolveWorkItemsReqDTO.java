package cn.cheers.x.module.platform.capability.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResolveWorkItemsReqDTO {

    @NotBlank(message = "businessTypeCode 不能为空")
    private String businessTypeCode;

    @NotEmpty(message = "instances 不能为空")
    private List<SourceInstanceInputDTO> instances;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SourceInstanceInputDTO {
        @NotBlank(message = "sourceInstanceId 不能为空")
        private String sourceInstanceId;
        private Map<String, Object> customFields;
    }
}
