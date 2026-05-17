package cn.iocoder.yudao.module.inspection.task.controller.admin.vo.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 任务执行记录 Response VO")
@Data
public class InspectionTaskExecutionRespVO {

    @Schema(description = "执行记录 ID", example = "1")
    private Long id;

    @Schema(description = "任务 ID", example = "1")
    private Long taskId;

    @Schema(description = "任务编码")
    private String taskCode;

    @Schema(description = "任务名称")
    private String taskName;

    @Schema(description = "计划点 ID", example = "1")
    private Long scheduleId;

    @Schema(description = "执行资源 ID", example = "1001")
    private Long resourceId;

    @Schema(description = "实际开始时间")
    private LocalDateTime actualStartTime;

    @Schema(description = "实际结束时间")
    private LocalDateTime actualEndTime;

    @Schema(description = "执行状态", example = "1")
    private Integer executionStatus;

    @Schema(description = "执行结果状态", example = "1")
    private Integer resultStatus;

    @Schema(description = "结果明细 JSON")
    private String resultJson;

    @Schema(description = "异常明细 JSON")
    private String exceptionJson;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
