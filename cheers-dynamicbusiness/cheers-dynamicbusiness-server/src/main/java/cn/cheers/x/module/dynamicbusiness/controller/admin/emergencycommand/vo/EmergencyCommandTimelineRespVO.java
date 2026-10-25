package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class EmergencyCommandTimelineRespVO {

    @Schema(description = "这条记录的编号")
    private String recordId;

    @Schema(description = "intake、assess、start_plan、level_change、step_started、step_completed、submit_report、enter_aftermath")
    private String kind;

    @Schema(description = "发生时间")
    private String at;

    @Schema(description = "步骤所属级别。预警为 warning")
    private String level;

    @Schema(description = "预案步骤编号")
    private String nodeKey;

    @Schema(description = "从预案读出的步骤说明。脉络本身不存这句话")
    private String description;

    @Schema(description = "级别变化前")
    private String fromLevel;

    @Schema(description = "级别变化后")
    private String toLevel;

    @Schema(description = "这次上报用的信息表")
    private String reportFormId;

    @Schema(description = "这条记录当时的预案")
    private String planId;

    @Schema(description = "接报来源。alert 为告警转入，manual 为人工接报")
    private String source;

    @Schema(description = "接报时的事件名称")
    private String summary;

    @Schema(description = "这次研判写下的意见")
    private String comment;
}
