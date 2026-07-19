package cn.cheers.x.maintenance.controller.admin.vo.binding;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class BindingRuleCreateReqVO {
    @NotBlank @Size(max=64) private String code;
    @NotBlank @Size(max=128) private String name;
    @NotBlank @Size(max=32) private String scope;
    private Long assetId;
    @Size(max=64) private String assetTypeCode;
    @Size(max=32) private String frequencyCode;
    private Long handbookId;
    @NotNull private Long fieldStandardId;
    @Size(max=64) private String orchestrationTemplateCode;
    private Integer priority;
}
