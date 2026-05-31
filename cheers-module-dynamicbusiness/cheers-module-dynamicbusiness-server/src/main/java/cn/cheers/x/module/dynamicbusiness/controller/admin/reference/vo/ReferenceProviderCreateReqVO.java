package cn.cheers.x.module.dynamicbusiness.controller.admin.reference.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理后台 - 引用 Provider 创建 Request VO")
@Data
public class ReferenceProviderCreateReqVO {

    @Schema(description = "Provider 编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "SYSTEM_USER")
    @NotBlank(message = "Provider 编码不能为空")
    private String providerCode;

    @Schema(description = "Provider 名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "系统用户")
    @NotBlank(message = "Provider 名称不能为空")
    private String providerName;

    @Schema(description = "Provider 类型", example = "INTERNAL")
    private String providerType;

    @Schema(description = "语义类型", example = "USER")
    private String semanticType;

    @Schema(description = "能力声明(JSON)")
    private String capabilityFlags;

    @Schema(description = "配置(JSON)")
    private String configJson;

    @Schema(description = "状态：1启用 0禁用", example = "1")
    private Integer status;

    @Schema(description = "租户作用域", example = "GLOBAL")
    private String tenantScope;

    @Schema(description = "优先级", example = "100")
    private Integer priority;
}
