package cn.iocoder.yudao.module.emergency.controller.admin.command.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理后台 - 指令步骤 Response VO
 */
@Schema(description = "管理后台 - 指令步骤 Response VO")
@Data
public class CommandStepRespVO {

    @Schema(description = "步骤编号", example = "1")
    private Long id;

    @Schema(description = "指令ID", example = "1")
    private Long commandId;

    @Schema(description = "步骤内容", example = "立即组织人员撤离现场")
    private String stepContent;

    @Schema(description = "执行时限（分钟）", example = "30")
    private Integer timeLimitMinutes;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "完成时间")
    private LocalDateTime completeTime;

    @Schema(description = "状态", example = "pending")
    private String status;

    @Schema(description = "超时标记", example = "false")
    private Boolean timeoutFlag;

    @Schema(description = "实际执行时长（分钟）", example = "25")
    private Integer actualDurationMinutes;

    @Schema(description = "关联预案步骤ID", example = "1")
    private Long planStepId;

    @Schema(description = "执行人ID", example = "1")
    private Long executorId;

    @Schema(description = "执行人姓名", example = "张三")
    private String executorName;

    @Schema(description = "超时原因")
    private String timeoutReason;

    @Schema(description = "处理措施")
    private String handlingMeasures;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

