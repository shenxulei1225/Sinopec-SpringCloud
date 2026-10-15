package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class EmergencyCommandDetailRespVO {

    private EmergencyCommandEventRespVO event;

    @Schema(description = "这起事件启动后的响应编号。尚未启动时为空")
    private String responseId;

    @Schema(description = "实际启动的预案编号。尚未启动、且该类型只有一份生效预案时，也填这一份，便于指挥页显示")
    private String planId;

    @Schema(description = "当前使用的预案名称")
    private String planName;

    @Schema(description = "该事件类型下生效的预案，供指挥页选择")
    private List<EmergencyCommandPlanRespVO> plans;

    @Schema(description = "实际启动的级别")
    private String responseLevel;

    @Schema(description = "响应启动时间")
    private String startedAt;

    @Schema(description = "响应上的步骤")
    private List<EmergencyCommandStepRespVO> steps;

    @Schema(description = "该类型预案每一级的步骤树。已启动的那一级带完成情况")
    private List<EmergencyCommandLevelRespVO> levels;

    @Schema(description = "预案上用户定义的阶段，按从先到后的顺序")
    private List<EmergencyCommandStageRespVO> stages;
}
