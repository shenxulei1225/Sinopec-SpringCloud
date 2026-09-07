package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "模型字段筛选条件")
@Data
public class ModelFieldFilterReqVO {

    @Schema(description = "字段编码（模型元数据字段）", requiredMode = Schema.RequiredMode.REQUIRED, example = "name")
    private String fieldCode;

    @Schema(description = "操作符（EQ/NE/IN/NOT_IN/GTE/LTE/GT/LT/BETWEEN/CONTAINS/LIKE）", example = "CONTAINS")
    private String op;

    @Schema(description = "筛选值")
    private Object value;

    @Schema(description = "是否按关系字段筛选；模型列表不支持该语义", example = "false")
    private Boolean relationField;
}
