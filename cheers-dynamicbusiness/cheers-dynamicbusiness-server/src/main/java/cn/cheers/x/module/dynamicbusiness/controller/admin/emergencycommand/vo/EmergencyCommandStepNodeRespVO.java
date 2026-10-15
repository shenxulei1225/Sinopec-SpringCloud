package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class EmergencyCommandStepNodeRespVO {

    @Schema(description = "步骤编号")
    private String nodeKey;

    @Schema(description = "父步骤编号。空表示这一步在最外层")
    private String parentNodeKey;

    @Schema(description = "步骤说明")
    private String description;

    @Schema(description = "同级顺序")
    private Integer order;

    @Schema(description = "未完成或已完成。这一级还没启动时为空")
    private String runStatus;

    @Schema(description = "这一步完成的时间。还没完成时为空")
    private String completedAt;

    @Schema(description = "这一步开始执行的时间。还没开始时为空")
    private String startedAt;

    @Schema(description = "这一步所属阶段。空表示预案还没把这一步分到阶段")
    private String stageKey;
}
