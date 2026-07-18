package cn.cheers.x.module.dynamicbusiness.controller.admin.dynamictable.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 动态表字段响应 VO
 */
@Schema(description = "管理后台 - 动态表字段响应")
@Data
public class DynamicTableColumnRespVO {

    @Schema(description = "字段配置ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "动态表ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long dynamicTableId;

    @Schema(description = "字段ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Long fieldId;

    @Schema(description = "列名", requiredMode = Schema.RequiredMode.REQUIRED, example = "f_task_name")
    private String columnName;

    @Schema(description = "数据类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "VARCHAR(255)")
    private String dataType;

    @Schema(description = "是否可空", example = "true")
    private Boolean nullable;

    @Schema(description = "列注释", example = "任务名称")
    private String columnComment;

    @Schema(description = "排序顺序", example = "1")
    private Integer sortOrder;

    @Schema(description = "状态（1-启用，0-禁用）", example = "1")
    private Integer status;
}
