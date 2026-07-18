package cn.cheers.x.inspection.task.controller.admin.vo.schedulepolicy;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 排期配置更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionTaskSchedulePolicyUpdateReqVO extends InspectionTaskSchedulePolicyBaseVO {

    @Schema(description = "配置 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "配置 ID 不能为空")
    private Long id;
}
