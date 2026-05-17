package cn.iocoder.yudao.module.inspection.task.controller.admin.vo.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 管理后台 - 任务计划点 Response VO
 */
@Schema(description = "管理后台 - 任务计划点 Response VO")
@Data
public class InspectionTaskScheduleRespVO {

    // ==================== 身份信息 ====================
    @Schema(description = "计划点 ID", example = "1")
    private Long id;
    @Schema(description = "批次 ID", example = "1")
    private Long planId;
    @Schema(description = "批次编码")
    private String planCode;
    @Schema(description = "任务 ID", example = "1")
    private Long taskId;
    @Schema(description = "任务编码")
    private String taskCode;
    @Schema(description = "任务名称")
    private String taskName;

    // ==================== 编排来源 ====================
    @Schema(description = "排期需求 ID")
    private Long scheduleRequirementId;
    @Schema(description = "排期策略 ID")
    private Long schedulePolicyId;

    // ==================== 计划时间 ====================
    @Schema(description = "计划日期")
    private LocalDate scheduledDate;
    @Schema(description = "计划开始时间")
    private LocalDateTime scheduledStartTime;
    @Schema(description = "计划结束时间")
    private LocalDateTime scheduledEndTime;

    // ==================== 状态 ====================
    @Schema(description = "计划点状态", example = "1")
    private Integer scheduleStatus;

    // ==================== 手动调整信息 ====================
    @Schema(description = "调整类型")
    private Integer adjustType;
    @Schema(description = "调整描述")
    private String adjustDesc;
    @Schema(description = "调整人 ID")
    private Long adjustedBy;
    @Schema(description = "调整时间")
    private LocalDateTime adjustedAt;

    // ==================== 替代关系 ====================
    @Schema(description = "是否已被替代")
    private Boolean superseded;
    @Schema(description = "替代当前计划点的新计划点 ID")
    private Long supersededById;

    // ==================== 时间戳 ====================
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
