package cn.cheers.x.module.dynamicbusiness.controller.admin.reference.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 引用 Provider Response VO")
@Data
public class ReferenceProviderRespVO {

    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "Provider 编码", example = "SYSTEM_USER")
    private String providerCode;

    @Schema(description = "Provider 名称", example = "系统用户")
    private String providerName;

    @Schema(description = "Provider 类型", example = "INTERNAL")
    private String providerType;

    @Schema(description = "语义类型", example = "USER")
    private String semanticType;

    @Schema(description = "功能声明(JSON)")
    private String featureFlags;

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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
