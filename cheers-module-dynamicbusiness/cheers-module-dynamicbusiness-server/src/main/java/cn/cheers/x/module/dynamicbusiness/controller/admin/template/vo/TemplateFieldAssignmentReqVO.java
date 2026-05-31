package cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 模板字段分配请求 VO
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 模板字段分配请求")
@Data
public class TemplateFieldAssignmentReqVO {

    @Schema(description = "字段ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "字段ID不能为空")
    private Long fieldId;

    @Schema(description = "排序值", example = "0")
    private Integer sortOrder;

    @Schema(description = "是否必填", example = "false")
    private Boolean required;

    @Schema(description = "默认值", example = "")
    private String defaultValue;
}
