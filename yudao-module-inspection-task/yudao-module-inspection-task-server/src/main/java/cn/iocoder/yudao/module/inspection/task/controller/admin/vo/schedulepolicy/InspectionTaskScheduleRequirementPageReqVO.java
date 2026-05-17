package cn.iocoder.yudao.module.inspection.task.controller.admin.vo.schedulepolicy;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
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

    @Schema(description = "模板名称（模糊匹配）")
    private String templateName;

    @Schema(description = "关联排期策略 ID")
    private Long policyId;

    @Schema(description = "是否为模板：true-模板 false-任务")
    private Boolean isTemplate;

    @Schema(description = "排期模式：1-固定时间点 2-间隔执行 3-自动编排")
    private Integer scheduleMode;

    @Schema(description = "重复模式：1-一次性 2-每日 3-每周 4-每月")
    private Integer repeatMode;

    @Schema(description = "开始日期")
    private java.time.LocalDate startDate;

    @Schema(description = "结束日期")
    private java.time.LocalDate endDate;

    @Schema(description = "描述（模糊匹配）")
    private String description;
}
