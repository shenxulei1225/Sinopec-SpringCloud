package cn.cheers.x.maintenance.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BindingResolveReqDTO {
    private Long assetId;
    private String assetTypeCode;
    private String frequencyCode;
    @NotBlank
    private String scope;
}
