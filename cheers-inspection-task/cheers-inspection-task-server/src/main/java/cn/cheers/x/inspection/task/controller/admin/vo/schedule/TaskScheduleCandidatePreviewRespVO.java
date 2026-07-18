package cn.cheers.x.inspection.task.controller.admin.vo.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 单任务候选计划点 Response VO")
@Data
public class TaskScheduleCandidatePreviewRespVO {

    @Schema(description = "规则 ID", example = "1")
    private Long ruleId;

    @Schema(description = "规则编码")
    private String ruleCode;

    @Schema(description = "规则名称")
    private String ruleName;

    @Schema(description = "计划日期")
    private LocalDate scheduledDate;

    @Schema(description = "计划开始时间")
    private LocalDateTime plannedStartTime;

    @Schema(description = "计划结束时间")
    private LocalDateTime plannedEndTime;

    @Schema(description = "优先级")
    private Integer priority;

    @Schema(description = "是否需要人工确认")
    private Boolean manualConfirmRequired;
}
