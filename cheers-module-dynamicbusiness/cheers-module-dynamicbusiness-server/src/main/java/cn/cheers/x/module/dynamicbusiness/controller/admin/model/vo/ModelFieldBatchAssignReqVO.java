package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 模型字段批量分配请求 VO
 */
@Schema(description = "管理后台 - 模型字段批量分配请求")
@Data
public class ModelFieldBatchAssignReqVO {

    @Schema(description = "模型ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "模型ID不能为空")
    private Long modelId;

    @Schema(description = "字段分配列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "字段分配列表不能为空")
    private List<FieldAssignmentItem> fieldAssignments;

    @Data
    @Schema(description = "字段分配项")
    public static class FieldAssignmentItem {
        @Schema(description = "字段ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "字段ID不能为空")
        private Long fieldId;

        @Schema(description = "是否必填", example = "true")
        private Boolean required;

        @Schema(description = "是否可查询（如果为 null，则使用模型字段智能默认值）", example = "true")
        private Boolean isSearchable;

        @Schema(description = "是否可筛选（如果为 null，则默认继承 isSearchable）", example = "true")
        private Boolean isFilterable;

        @Schema(description = "是否可排序（如果为 null，则使用模型字段智能默认值）", example = "true")
        private Boolean isSortable;

        @Schema(description = "默认值", example = "默认值")
        private String defaultValue;

        @Schema(description = "业务规则（JSON格式，包含范围、格式等验证规则）", example = "{\"min\": 0, \"max\": 100}")
        private String validationRules;

        @Schema(description = "排序值", example = "1")
        private Integer sort;

        @Schema(description = "关联字段库ID（关联字段使用，从字段库选用时必填）", example = "1")
        private Long refLibraryId;

        @Schema(description = "关联目标业务类型编码（ENTITY_REF/BATCH_ENTITY_REF 专用）", example = "dian_wei")
        private String targetEntityType;

        @Schema(description = "关联目标 Model 编码（ENTITY_REF/BATCH_ENTITY_REF 专用）", example = "MODEL-bc167ec7fa82422cb036804214c0c3ac")
        private String targetModelCode;
    }
}
