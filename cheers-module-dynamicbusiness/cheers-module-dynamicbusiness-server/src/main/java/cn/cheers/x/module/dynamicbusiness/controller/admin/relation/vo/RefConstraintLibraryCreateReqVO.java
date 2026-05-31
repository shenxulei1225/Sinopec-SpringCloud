package cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - Ref 约束器库创建 Request VO")
@Data
public class RefConstraintLibraryCreateReqVO {

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "personnel")
    @NotBlank(message = "业务类型编码不能为空")
    @Size(max = 64, message = "业务类型编码长度不能超过64个字符")
    private String businessTypeCode;

    @Schema(description = "Ref 目标类型", example = "personnel")
    @Size(max = 64, message = "Ref 目标类型长度不能超过64个字符")
    private String refTargetType;

    @Schema(description = "约束器类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "ROLE_DEPT")
    @NotBlank(message = "约束器类型不能为空")
    @Size(max = 64, message = "约束器类型长度不能超过64个字符")
    private String constraintType;

    @Schema(description = "约束器名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "角色+部门")
    @NotBlank(message = "约束器名称不能为空")
    @Size(max = 64, message = "约束器名称长度不能超过64个字符")
    private String constraintName;

    @Schema(description = "状态：0-开启，1-关闭", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "排序值", example = "0")
    private Integer sort;
}
