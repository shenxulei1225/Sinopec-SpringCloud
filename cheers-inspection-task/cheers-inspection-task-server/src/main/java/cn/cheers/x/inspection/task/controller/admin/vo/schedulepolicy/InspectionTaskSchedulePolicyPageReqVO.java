package cn.cheers.x.inspection.task.controller.admin.vo.schedulepolicy;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理后台 - 排期策略分页 Request VO
 */
@Schema(description = "管理后台 - 排期策略分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionTaskSchedulePolicyPageReqVO extends PageParam {

    @Schema(description = "策略编码（模糊匹配）")
    private String policyCode;

    @Schema(description = "策略名称（模糊匹配）")
    private String policyName;

    @Schema(description = "是否启用：true-启用 false-禁用")
    private Boolean enabled;

    @Schema(description = "冲突策略：1-跳过 2-顺延 3-拆分")
    private Integer conflictStrategy;

    @Schema(description = "优先级")
    private Integer priority;

    @Schema(description = "允许提前分钟数（最小值）")
    private Integer minAllowAdvanceMinutes;

    @Schema(description = "允许延后分钟数（最小值）")
    private Integer minAllowDelayMinutes;
}
