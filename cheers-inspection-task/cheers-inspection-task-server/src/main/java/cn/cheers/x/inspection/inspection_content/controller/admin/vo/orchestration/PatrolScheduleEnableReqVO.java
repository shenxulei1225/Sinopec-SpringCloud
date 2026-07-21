package cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration;

import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 启用排程请求：从已确认任务快照展开并 solve/persist。
 */
@Data
public class PatrolScheduleEnableReqVO {

    @NotNull(message = "taskId 不能为空")
    private Long taskId;

    @NotNull(message = "schedulingSpec 不能为空")
    private SchedulingSpecDTO schedulingSpec;
}
