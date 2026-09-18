package cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "管理后台 - 数据目录能力项响应 VO")
public record EntityTypeCapabilityItemRespVO(
        @Schema(description = "能力编码", example = "version-management")
        String code,
        @Schema(description = "能力名称", example = "版本管理")
        String name,
        @Schema(description = "能力说明")
        String description,
        @Schema(description = "是否启用")
        boolean enabled
) {
}
