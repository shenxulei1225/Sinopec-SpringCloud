package cn.cheers.x.inspection.task.controller.admin.vo.scheduleboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "排期看板 - 计划点执行详情")
public class ScheduleBoardExecutionDetailRespVO {

    @Schema(description = "计划点 id")
    private String pointId;

    @Schema(description = "步骤全完但含失败项")
    private Boolean hasStepFailureAlert;

    @Schema(description = "告警文案")
    private String alertMessage;

    @Schema(description = "执行过程时间轴")
    private List<ProcessEventVO> processTimeline = new ArrayList<>();

    @Schema(description = "步骤树")
    private List<StepNodeVO> stepTree = new ArrayList<>();

    @Data
    @Schema(description = "过程事件")
    public static class ProcessEventVO {
        private String id;
        private String at;
        private String label;
        private String kind;
        private String detail;
    }

    @Data
    @Schema(description = "步骤树节点")
    public static class StepNodeVO {
        private String id;
        private String name;
        private String status;
        private List<StepNodeVO> children = new ArrayList<>();
    }
}
