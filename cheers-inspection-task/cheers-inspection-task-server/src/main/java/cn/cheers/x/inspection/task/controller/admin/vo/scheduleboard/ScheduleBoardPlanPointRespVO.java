package cn.cheers.x.inspection.task.controller.admin.vo.scheduleboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "排期看板 - 执行计划点")
public class ScheduleBoardPlanPointRespVO {

    @Schema(description = "计划点 id（L4 slotId）")
    private String id;

    @Schema(description = "计划点编码（同 slotId）")
    private String scheduleCode;

    @Schema(description = "来源总任务 id")
    private Long sourceTaskId;

    @Schema(description = "来源总任务名称")
    private String sourceTaskName;

    @Schema(description = "执行设备 id")
    private Long deviceId;

    @Schema(description = "执行设备展示名")
    private String deviceName;

    @Schema(description = "设备编号（站场内 A–Z 等）")
    private String deviceCode;

    @Schema(description = "设备类型：robot / drone / manual")
    private String deviceKind;

    @Schema(description = "计划日期 YYYY-MM-DD")
    private String scheduledDate;

    @Schema(description = "计划执行开始 ISO")
    private String plannedStart;

    @Schema(description = "计划执行结束 ISO")
    private String plannedEnd;

    @Schema(description = "实际执行开始 ISO")
    private String actualStart;

    @Schema(description = "实际执行结束 ISO")
    private String actualEnd;

    @Schema(description = "排期状态：PLANNED / CONFLICT / CANCELLED")
    private String scheduleStatus;

    @Schema(description = "执行状态：NOT_STARTED / EXECUTING / DONE / FAILED")
    private String executionStatus;

    @Schema(description = "失败原因：EXPIRED_NOT_STARTED / TIMEOUT_INCOMPLETE")
    private String failureReason;

    @Schema(description = "步骤完成比例 0–100")
    private Integer progressPercent;

    @Schema(description = "步骤全完但含失败项")
    private Boolean hasStepFailureAlert;
}
