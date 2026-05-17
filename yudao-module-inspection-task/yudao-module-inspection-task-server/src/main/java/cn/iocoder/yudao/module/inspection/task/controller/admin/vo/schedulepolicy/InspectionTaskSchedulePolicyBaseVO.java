package cn.iocoder.yudao.module.inspection.task.controller.admin.vo.schedulepolicy;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理后台 - 排期策略基础 Request VO
 */
@Schema(description = "管理后台 - 排期策略基础 Request VO")
@Data
public class InspectionTaskSchedulePolicyBaseVO {

    @Schema(description = "策略编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "POLICY-001")
    @NotBlank(message = "策略编码不能为空")
    @Size(max = 64, message = "策略编码长度不能超过 64 位")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "策略编码只能包含字母、数字、下划线和中划线")
    private String policyCode;

    @Schema(description = "策略名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "灵活模式")
    @NotBlank(message = "策略名称不能为空")
    @Size(max = 128, message = "策略名称长度不能超过 128 位")
    private String policyName;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;

    @Schema(description = "策略说明", example = "允许适当提前或延后执行")
    @Size(max = 500, message = "策略说明长度不能超过 500 位")
    private String description;

    // ==================== 算法参数 ====================

    @Schema(description = "允许提前分钟数", example = "15")
    @Min(value = 0, message = "允许提前分钟数不能小于 0")
    private Integer allowAdvanceMinutes;

    @Schema(description = "允许延后分钟数", example = "30")
    @Min(value = 0, message = "允许延后分钟数不能小于 0")
    private Integer allowDelayMinutes;

    @Schema(description = "冲突策略：1-跳过 2-顺延 3-拆分", example = "2")
    @NotNull(message = "冲突策略不能为空")
    private Integer conflictStrategy;

    @Schema(description = "优先级", example = "10")
    @Min(value = 1, message = "优先级必须大于 0")
    private Integer priority;
}
