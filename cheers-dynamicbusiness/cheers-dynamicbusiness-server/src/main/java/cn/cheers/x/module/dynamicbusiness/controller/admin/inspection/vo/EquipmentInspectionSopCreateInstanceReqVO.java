package cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "从 SOP 模板创建设备独占实例并绑定")
@Data
public class EquipmentInspectionSopCreateInstanceReqVO {

    @NotNull
    private Long sopTemplateId;

    @NotNull
    private Long equipmentId;

    @NotNull
    private Long inspectionItemId;

    @NotBlank
    private String executionMeans;

    @Schema(description = "步骤差量 JSON 对象或字符串")
    private Object stepOverride;

    @Schema(description = "参数差量 JSON 对象或字符串")
    private Object paramOverride;

    @Schema(description = "新实例名称；缺省则用模板名+设备后缀")
    private String name;
}
