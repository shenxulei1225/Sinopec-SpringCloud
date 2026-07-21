package cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 任务让路/挂起/中止请求。
 */
@Data
public class PatrolTaskPauseReqVO {

    @NotNull(message = "taskId 不能为空")
    private Long taskId;

    /** 可选；缺省时从任务 runtimeJobId 解析 */
    private String runtimeJobId;

    private String reason;
}
