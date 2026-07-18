package cn.cheers.x.inspection.task.controller.admin.vo.schedulepolicy;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 管理后台 - 排期需求更新 Request VO
 */
@Schema(description = "管理后台 - 排期需求更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionTaskScheduleRequirementUpdateReqVO extends InspectionTaskScheduleRequirementBaseVO {

    @Schema(description = "排期需求ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "排期需求ID不能为空")
    private Long id;

    @Schema(description = "模板组合配置列表")
    private List<ScheduleTemplateConfigVO> scheduleTemplates;
}
