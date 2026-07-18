package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 实体操作权限创建请求 VO
 */
@Schema(description = "管理后台 - 实体操作权限创建 Request VO")
@Data
public class EntityOperationPermissionCreateReqVO {

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "业务类型编码不能为空")
    private String entityTypeCode;

    @Schema(description = "角色ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @Schema(description = "实体ID（为空时表示对模型下所有实体的权限）", example = "1")
    private Long entityId;

    @Schema(description = "模型ID（用于模型级别的权限控制）", example = "1")
    private Long modelId;

    @Schema(description = "是否可创建", example = "true")
    private Boolean canCreate = false;

    @Schema(description = "是否可更新", example = "true")
    private Boolean canUpdate = false;

    @Schema(description = "是否可删除", example = "true")
    private Boolean canDelete = false;
}
