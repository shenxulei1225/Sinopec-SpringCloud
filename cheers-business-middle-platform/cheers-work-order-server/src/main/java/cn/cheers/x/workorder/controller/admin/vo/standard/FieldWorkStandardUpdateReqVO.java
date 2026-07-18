package cn.cheers.x.workorder.controller.admin.vo.standard;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 管理后台 - 现场作业标准更新 Request VO
 */
@Schema(description = "管理后台 - 现场作业标准更新 Request VO")
@Data
public class FieldWorkStandardUpdateReqVO {

    @Schema(description = "标准名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "离心泵月检")
    @NotBlank(message = "标准名称不能为空")
    @Size(max = 128, message = "标准名称长度不能超过128个字符")
    private String name;

    @Schema(description = "业务域范围", requiredMode = Schema.RequiredMode.REQUIRED, example = "inspection")
    @NotBlank(message = "业务域范围不能为空")
    @Size(max = 32, message = "业务域范围长度不能超过32个字符")
    private String scope;

    @Schema(description = "步骤列表（有序）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "步骤列表不能为空")
    @Valid
    private List<FieldWorkStandardStepVO> steps;

}
