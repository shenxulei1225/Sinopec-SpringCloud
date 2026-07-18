package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "字段筛选条件")
public class FieldFilterReqVO {

    @Schema(description = "字段编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "status")
    @NotBlank(message = "fieldCode 不能为空")
    private String fieldCode;

    @Schema(description = "操作符（EQ/IN/GTE/LTE/BETWEEN/CONTAINS 等）", requiredMode = Schema.RequiredMode.REQUIRED, example = "EQ")
    @NotBlank(message = "op 不能为空")
    private String op;

    @Schema(description = "筛选值（支持单值/数组）")
    private Object value;

    @Schema(description = "是否关联字段筛选", example = "false")
    private Boolean relationField;
}
