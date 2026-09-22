package cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration;

import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 智能编排试排 / 生成任务（commitOrchestration）共用请求：taskId + 排程规格（horizon、冲突策略等）。
 * <p>试排（previewOrchestration）与生成任务都带同一 schedulingSpec；与「排期模板配置」不同层。
 */
@Data
public class PatrolOrchestrationRunReqVO {

    @NotNull(message = "taskId 不能为空")
    private Long taskId;

    @NotNull(message = "schedulingSpec 不能为空")
    private SchedulingSpecDTO schedulingSpec;
}
