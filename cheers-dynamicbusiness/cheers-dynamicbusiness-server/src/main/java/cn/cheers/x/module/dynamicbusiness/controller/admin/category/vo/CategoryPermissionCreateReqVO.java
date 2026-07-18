package cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 分类级权限创建请求 VO
 */
@Schema(description = "管理后台 - 分类级权限创建 Request VO")
@Data
public class CategoryPermissionCreateReqVO {

    @Schema(description = "角色ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @Schema(description = "分类ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @Schema(description = "是否可查看", example = "true")
    private Boolean canView = true;

    @Schema(description = "是否可管理", example = "false")
    private Boolean canManage = false;
}
