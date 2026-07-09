package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 实体访问权限创建请求 VO
 */
@Schema(description = "管理后台 - 实体访问权限创建 Request VO")
@Data
public class EntityAccessPermissionCreateReqVO {

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "业务类型编码不能为空")
    private String entityTypeCode;

    @Schema(description = "角色ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "实体ID不能为空")
    private Long entityId;

    @Schema(description = "是否可查看", example = "true")
    private Boolean canView = true;
}
