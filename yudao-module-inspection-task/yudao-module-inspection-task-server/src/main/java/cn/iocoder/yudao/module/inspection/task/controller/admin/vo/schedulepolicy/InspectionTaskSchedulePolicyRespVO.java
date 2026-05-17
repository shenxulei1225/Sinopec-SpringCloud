package cn.iocoder.yudao.module.inspection.task.controller.admin.vo.schedulepolicy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理后台 - 排期策略 Response VO
 */
@Schema(description = "管理后台 - 排期策略 Response VO")
@Data
public class InspectionTaskSchedulePolicyRespVO {

    @Schema(description = "策略 ID", example = "1")
    private Long id;

    @Schema(description = "策略编码")
    private String policyCode;

    @Schema(description = "策略名称")
    private String policyName;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "策略说明")
    private String description;

    // ==================== 算法参数 ====================

    @Schema(description = "允许提前分钟数")
    private Integer allowAdvanceMinutes;

    @Schema(description = "允许延后分钟数")
    private Integer allowDelayMinutes;

    @Schema(description = "冲突策略")
    private Integer conflictStrategy;

    @Schema(description = "优先级")
    private Integer priority;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
