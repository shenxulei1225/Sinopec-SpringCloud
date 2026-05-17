package cn.iocoder.yudao.module.inspection.task.controller.admin.vo.schedulepolicy;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理后台 - 排期需求更新 Request VO
 */
@Schema(description = "管理后台 - 排期需求更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionTaskScheduleRequirementUpdateReqVO extends InspectionTaskScheduleRequirementBaseVO {

    @Schema(description = "需求 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "需求 ID 不能为空")
    private Long id;
}
