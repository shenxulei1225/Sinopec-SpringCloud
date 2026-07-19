package cn.cheers.x.maintenance.controller.admin.vo.binding;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class BindingResolvePreviewReqVO {
    private Long assetId; private String assetTypeCode; private String frequencyCode;
    @NotBlank private String scope;
}
