package cn.cheers.x.inspection.task.controller.admin.vo.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 调度计划批次 Response VO")
@Data
public class InspectionTaskSchedulePlanRespVO {

    @Schema(description = "批次 ID", example = "1")
    private Long id;

    @Schema(description = "批次编码", example = "PLAN-20260513-001")
    private String planCode;

    @Schema(description = "计划类型", example = "1")
    private Integer planType;

    @Schema(description = "计划状态", example = "1")
    private Integer planStatus;

    @Schema(description = "调度窗口开始时间")
    private LocalDateTime planHorizonStart;

    @Schema(description = "调度窗口结束时间")
    private LocalDateTime planHorizonEnd;

    @Schema(description = "触发来源")
    private String triggerSource;

    @Schema(description = "版本号", example = "1")
    private Integer version;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
