package cn.cheers.x.inspection.task.controller.admin.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "已生成任务打开时，已勾检查项的步骤和参数是否与生成时不一致")
@Data
public class InspectionTaskGeneratedInputReviewRespVO {

    @Schema(description = "任务已经生成。未生成时不比较、不提示")
    private Boolean alreadyGenerated;

    @Schema(description = "变过的已勾检查项。标准库多出来但没勾进任务的项不会出现")
    private List<Change> changes = new ArrayList<>();

    @Data
    public static class Change {

        private Long equipmentId;

        private String equipmentName;

        private Long inspectionItemId;

        private String inspectionItemName;

        @Schema(description = "步骤 或 参数")
        private String aspect;

        private String detail;

        @Schema(description = "这一处变的是要去的点。第 2 步须重新规划后才能下一步")
        private boolean routeInput;

        @Schema(description = "这一处变的是动作耗时。第 3 步须按新时长重新生成计划后才能进核对")
        private boolean durationInput;

        @Schema(description = "对不上的参数槽编码。页面按字段库判断是路网点还是动作耗时")
        private List<String> slots = new ArrayList<>();
    }
}
