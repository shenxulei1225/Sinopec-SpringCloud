package cn.iocoder.yudao.module.inspection.task.controller.admin.vo.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "管理后台 - 单任务排期试算 Response VO")
@Data
public class SingleTaskSchedulePreviewRespVO {

    @Schema(description = "任务 ID", example = "1")
    private Long taskId;

    @Schema(description = "任务编码")
    private String taskCode;

    @Schema(description = "任务名称")
    private String taskName;

    @Schema(description = "最终生效排期配置 ID")
    private Long effectiveScheduleProfileId;

    @Schema(description = "最终生效排期配置名称")
    private String effectiveScheduleProfileName;

    @Schema(description = "试算开始日期")
    private LocalDate startDate;

    @Schema(description = "试算结束日期")
    private LocalDate endDate;

    @Schema(description = "候选计划点列表")
    private List<TaskScheduleCandidatePreviewRespVO> candidates = new ArrayList<>();

    @Schema(description = "告警信息")
    private List<String> warnings = new ArrayList<>();
}
