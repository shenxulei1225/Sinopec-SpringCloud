package cn.cheers.x.module.platform.orchestration.phase;

import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunRequest;
import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 阶段执行上下文：阶段间共享排程请求、求解中间结果与运行参数。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhaseContext {

    private ScheduleRunRequest request;
    private SchedulingSpecDTO schedulingSpec;
    private String orchestrationRef;
    private String runtimeJobId;
    private List<WorkItemDTO> workItems;
    private List<ScheduleSlotDTO> slots;
    private RoutePreviewDTO routePreview;
    private boolean dryRun;
    private OrchestrationPhase stopAfterPhase;
    private String policySnapshotId;
    private Long siteId;
}
