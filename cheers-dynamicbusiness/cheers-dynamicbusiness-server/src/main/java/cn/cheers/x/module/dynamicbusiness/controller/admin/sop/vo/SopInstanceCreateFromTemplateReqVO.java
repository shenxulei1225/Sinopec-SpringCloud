package cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "从 SOP 模板新建独占实例并绑定")
@Data
public class SopInstanceCreateFromTemplateReqVO {

    @NotNull
    private Long sopTemplateId;

    @NotBlank
    private String hostType;

    @NotNull
    private Long hostId;

    @NotBlank
    private String subjectType;

    @NotNull
    private Long subjectId;

    @NotBlank
    private String dimensionKey;

    @NotBlank
    private String dimensionValue;

    @Schema(description = "动作树差量（整树替换）；JSON 对象或字符串")
    private Object treeOverride;

    @Schema(description = "按节点参数差量；JSON 对象或字符串")
    private Object paramOverride;

    @Schema(description = "新实例名称；缺省则用模板名+宿主后缀")
    private String name;
}
