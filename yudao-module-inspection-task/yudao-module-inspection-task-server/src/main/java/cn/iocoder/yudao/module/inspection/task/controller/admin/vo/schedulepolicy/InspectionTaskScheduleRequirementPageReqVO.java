package cn.iocoder.yudao.module.inspection.task.controller.admin.vo.schedulepolicy;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理后台 - 排期需求分页 Request VO
 */
@Schema(description = "管理后台 - 排期需求分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionTaskScheduleRequirementPageReqVO extends PageParam {

    @Schema(description = "需求名称（模糊匹配）")
    private String requirementName;

    @Schema(description = "关联排期策略 ID")
    private Long schedulePolicyId;

    @Schema(description = "描述（模糊匹配）")
    private String description;
}
