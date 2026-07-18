package cn.cheers.x.inspection.task.controller.admin.vo.template;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 从任务另存为模板 Request VO")
@Data
public class InspectionTaskTemplateSaveFromTaskReqVO {

    @Schema(description = "任务 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "任务 ID 不能为空")
    private Long taskId;

    @Schema(description = "模板编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "TPL-001")
    @NotBlank(message = "模板编码不能为空")
    @Size(max = 64, message = "模板编码长度不能超过 64 位")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "模板编码只能包含字母、数字、下划线和中划线")
    private String templateCode;

    @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "罐区A日常巡检模板")
    @NotBlank(message = "模板名称不能为空")
    @Size(max = 128, message = "模板名称长度不能超过 128 位")
    private String templateName;

    @Schema(description = "是否包含子任务", example = "true")
    private Boolean includeChildren;
}
