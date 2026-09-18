package cn.cheers.x.inspection.task.controller.admin.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 生成总任务步骤图：路径停靠对应的被巡检设备顺序。
 * <p>摄像机或不传则按勾选顺序。
 */
@Schema(description = "生成任务步骤图")
@Data
public class GenerateStepTreeReqVO {

    @Schema(description = "路径停靠对应的被巡检设备 id 顺序")
    private List<Long> equipmentOrder;

    @Schema(description = "任务创建选定的起点（无人机起飞点）")
    private String startStopId;

    @Schema(description = "任务创建选定的终点（无人机降落点）")
    private String endStopId;
}
