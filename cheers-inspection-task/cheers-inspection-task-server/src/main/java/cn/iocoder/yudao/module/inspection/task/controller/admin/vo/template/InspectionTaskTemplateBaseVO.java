package cn.iocoder.yudao.module.inspection.task.controller.admin.vo.template;

import cn.iocoder.yudao.module.inspection.task.model.task.InspectionContent;
import cn.iocoder.yudao.module.inspection.task.model.task.ResourcePolicy;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 巡检任务模板基础 Request VO")
@Data
public class InspectionTaskTemplateBaseVO {

    @Schema(description = "父模板 ID", example = "100")
    private Long parentId;

    @Schema(description = "模板分类 ID", example = "200")
    private Long categoryId;

    @Schema(description = "模板编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "TPL-001")
    @NotBlank(message = "模板编码不能为空")
    @Size(max = 64, message = "模板编码长度不能超过 64 位")
    private String templateCode;

    @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "罐区A日常巡检模板")
    @NotBlank(message = "模板名称不能为空")
    @Size(max = 128, message = "模板名称长度不能超过 128 位")
    private String templateName;

    @Schema(description = "模板说明")
    @Size(max = 500, message = "模板说明长度不能超过 500 位")
    private String description;

    @Schema(description = "默认排期策略 ID", example = "100")
    private Long defaultSchedulePolicyId;

    @Schema(description = "默认资源策略")
    private ResourcePolicy resourcePolicy;

    @Schema(description = "模板巡检内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "模板巡检内容不能为空")
    private InspectionContent inspectionContent;
}
