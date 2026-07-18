package cn.cheers.x.inspection.task.controller.admin.vo.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "管理后台 - 单任务排期试算 Request VO")
@Data
public class InspectionTaskSingleSchedulePreviewReqVO {

    @Schema(description = "任务 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "任务 ID 不能为空")
    private Long taskId;

    @Schema(description = "开始日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-01-01")
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @Schema(description = "结束日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-01-07")
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;
}
