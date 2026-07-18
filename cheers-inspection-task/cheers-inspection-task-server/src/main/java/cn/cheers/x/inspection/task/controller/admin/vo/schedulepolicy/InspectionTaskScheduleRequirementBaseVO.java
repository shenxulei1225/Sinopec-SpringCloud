package cn.cheers.x.inspection.task.controller.admin.vo.schedulepolicy;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理后台 - 排期需求基础 Request VO
 *
 * <p>排期需求是一个配置容器，包含多个模板组合。</p>
 */
@Schema(description = "管理后台 - 排期需求基础 Request VO")
@Data
public class InspectionTaskScheduleRequirementBaseVO {

    @Schema(description = "需求名称", example = "罐区巡检配置")
    @NotNull(message = "需求名称不能为空")
    @Size(max = 128, message = "需求名称长度不能超过128位")
    private String requirementName;

    @Schema(description = "关联的排期策略ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "请选择排期策略")
    private Long schedulePolicyId;

    @Schema(description = "排期需求说明", example = "罐区日常巡检配置")
    @Size(max = 500, message = "需求说明长度不能超过500位")
    private String description;
}
