package cn.cheers.x.workorder.controller.admin.vo.standard;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 现场作业标准步骤
 */
@Schema(description = "管理后台 - 现场作业标准步骤")
@Data
public class FieldWorkStandardStepVO {

    @Schema(description = "步骤编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "s1")
    @NotBlank(message = "步骤编码不能为空")
    @Size(max = 64, message = "步骤编码长度不能超过64个字符")
    private String code;

    @Schema(description = "步骤标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "外观检查")
    @NotBlank(message = "步骤标题不能为空")
    @Size(max = 128, message = "步骤标题长度不能超过128个字符")
    private String title;

    @Schema(description = "是否必填", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "步骤必填标记不能为空")
    private Boolean required;

    @Schema(description = "控件类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "checkbox")
    @NotBlank(message = "步骤控件类型不能为空")
    @Size(max = 32, message = "步骤控件类型长度不能超过32个字符")
    private String controlType;

}
