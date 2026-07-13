package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 模型字段规则更新请求 VO（包含模型ID和字段ID）
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 模型字段规则更新请求")
@Data
public class ModelFieldRulesUpdateReqVO {

    @Schema(description = "模型ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "模型ID不能为空")
    private Long modelId;

    @Schema(description = "字段ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "字段ID不能为空")
    private Long fieldId;

    @Schema(description = "是否必填", example = "true")
    private Boolean required;

    @Schema(description = "默认值", example = "默认值")
    private String defaultValue;

    @Schema(description = "业务规则（JSON格式，包含范围、格式等验证规则）", example = "{\"min\": 0, \"max\": 100}")
    private String validationRules;

    @Schema(description = "是否可查询（如果为 null，则使用字段定义中的默认值）", example = "true")
    private Boolean isSearchable;

    @Schema(description = "是否可排序（如果为 null，则使用字段定义中的默认值）", example = "true")
    private Boolean isSortable;

    @Schema(description = "是否可筛选", example = "true")
    private Boolean isFilterable;

    // 注意：fieldGroupId 已移除，分组关联应通过 ModelFieldGroupAssignmentController 管理
}

