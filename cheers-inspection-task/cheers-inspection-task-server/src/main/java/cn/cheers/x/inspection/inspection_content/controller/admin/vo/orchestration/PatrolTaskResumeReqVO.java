package cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration;

import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 让路式暂停后恢复：走 replan 重排剩余停靠点。
 */
@Data
public class PatrolTaskResumeReqVO {

    @NotNull(message = "taskId 不能为空")
    private Long taskId;

    @NotBlank(message = "sourceRuntimeJobId 不能为空")
    private String sourceRuntimeJobId;

    /** 优先：剩余停靠点 id */
    private List<String> remainingStopIds;

    /** 备选：已完成计划点 id，反推剩余 */
    private List<String> completedSlotIds;

    @NotNull(message = "schedulingSpec 不能为空")
    private SchedulingSpecDTO schedulingSpec;
}
