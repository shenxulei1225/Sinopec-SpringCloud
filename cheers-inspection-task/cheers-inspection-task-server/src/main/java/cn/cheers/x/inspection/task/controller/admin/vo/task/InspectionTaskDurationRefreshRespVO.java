package cn.cheers.x.inspection.task.controller.admin.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 打开任务现算结果。未生成则直接收回后续步；已生成由页面确认是否重排。
 */
@Data
@Schema(description = "打开任务现算时长结果")
public class InspectionTaskDurationRefreshRespVO {

    @Schema(description = "检查项动作耗时（分钟）")
    private Integer itemActionDurationMinutes;

    @Schema(description = "路径耗时（分钟）")
    private Integer travelDurationMinutes;

    @Schema(description = "排期总时长（分钟）")
    private Integer totalDurationMinutes;

    @Schema(description = "检查项动作耗时是否相对任务上已写的数变了")
    private Boolean itemActionChanged;

    @Schema(description = "到达位置或路径耗时是否变了")
    private Boolean pathChanged;

    @Schema(description = "收回后放到哪一步")
    private Integer unlockedStep;

    @Schema(description = "任务是否已经生成并排期")
    private Boolean alreadyGenerated;

    @Schema(description = "给用户看的原因")
    private String message;
}
