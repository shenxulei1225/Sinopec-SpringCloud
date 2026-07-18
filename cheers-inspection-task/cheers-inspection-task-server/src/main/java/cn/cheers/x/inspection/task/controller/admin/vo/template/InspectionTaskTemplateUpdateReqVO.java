package cn.cheers.x.inspection.task.controller.admin.vo.template;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 巡检任务模板更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionTaskTemplateUpdateReqVO extends InspectionTaskTemplateBaseVO {

    @Schema(description = "模板 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "模板 ID 不能为空")
    private Long id;
}
