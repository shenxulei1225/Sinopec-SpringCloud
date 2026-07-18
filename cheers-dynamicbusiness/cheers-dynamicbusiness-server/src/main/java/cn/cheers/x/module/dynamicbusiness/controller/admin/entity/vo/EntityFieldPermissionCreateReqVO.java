package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 字段级权限创建请求 VO
 */
@Schema(description = "管理后台 - 字段级权限创建 Request VO")
@Data
public class EntityFieldPermissionCreateReqVO {

    @Schema(description = "角色ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @Schema(description = "模型ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "模型ID不能为空")
    private Long modelId;

    @Schema(description = "字段ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "字段ID不能为空")
    private Long fieldId;

    @Schema(description = "是否可查看", example = "true")
    private Boolean canView = true;

    @Schema(description = "是否可编辑", example = "false")
    private Boolean canEdit = false;
}
