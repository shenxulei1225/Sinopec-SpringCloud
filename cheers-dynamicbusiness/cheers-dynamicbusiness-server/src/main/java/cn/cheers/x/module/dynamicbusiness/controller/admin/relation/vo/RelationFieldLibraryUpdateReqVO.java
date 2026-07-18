package cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 关联字段库 更新请求 VO
 */
@Schema(description = "管理后台 - 关联字段库更新 Request VO")
@Data
public class RelationFieldLibraryUpdateReqVO {

    @Schema(description = "字段ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "字段ID不能为空")
    private Long id;

    @Schema(description = "字段名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "安全负责人")
    @NotBlank(message = "字段名称不能为空")
    @Size(max = 64, message = "字段名称长度不能超过64个字符")
    private String fieldName;

    @Schema(description = "字段编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "safety_manager")
    @NotBlank(message = "字段编码不能为空")
    @Size(max = 64, message = "字段编码长度不能超过64个字符")
    @Pattern(regexp = "^[a-z][a-z0-9_]*$", message = "字段编码格式不正确，必须以小写字母开头，只能包含小写字母、数字和下划线")
    private String fieldCode;

    @Schema(description = "关联业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "personnel")
    @NotBlank(message = "关联业务类型不能为空")
    @Size(max = 64, message = "关联业务类型编码长度不能超过64个字符")
    private String refEntityType;

    @Schema(description = "展示字段编码（可选，NULL 表示使用目标业务类型的默认名称字段）", example = "name")
    @Size(max = 64, message = "展示字段编码长度不能超过64个字符")
    private String displayFieldCode;

    @Schema(description = "是否启用约束器", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "是否启用约束器不能为空")
    private Boolean constraintEnabled;

    @Schema(description = "约束器类型", example = "ROLE_DEPT")
    @Size(max = 64, message = "约束器类型长度不能超过64个字符")
    private String constraintType;

    @Schema(description = "字段说明", example = "负责安全的人员")
    @Size(max = 512, message = "字段说明长度不能超过512个字符")
    private String description;
}
