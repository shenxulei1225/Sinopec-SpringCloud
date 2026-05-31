package cn.cheers.x.module.dynamicbusiness.controller.admin.reference.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Schema(description = "管理后台 - 引用 Provider 更新 Request VO")
@Data
public class ReferenceProviderUpdateReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "ID 不能为空")
    private Long id;

    @Schema(description = "Provider 编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "SYSTEM_USER")
    @NotNull(message = "Provider 编码不能为空")
    private String providerCode;

    @Schema(description = "Provider 名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "系统用户")
    @NotNull(message = "Provider 名称不能为空")
    private String providerName;

    @Schema(description = "Provider 类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "INTERNAL")
    @NotNull(message = "Provider 类型不能为空")
    private String providerType;

    @Schema(description = "语义类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "USER")
    @NotNull(message = "语义类型不能为空")
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

    @Schema(description = "健康状态", example = "UP")
    private String healthStatus;
}
