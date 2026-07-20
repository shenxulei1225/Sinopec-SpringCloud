package cn.cheers.x.module.platform.orchestration.phase;

import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunRequest;
import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.orchestration.api.dto.OrchestrationRunRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 阶段执行上下文：排程运行与通用编排运行共用；阶段间用 attributes 传中间结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhaseContext {

    /** 排程型运行请求（schedule/run） */
    private ScheduleRunRequest request;

    /** 通用编排运行请求（orchestration/run） */
    private OrchestrationRunRequest orchestrationRunRequest;

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

    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>();

    public void putAttr(String key, Object value) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttr(String key) {
        if (attributes == null) {
            return null;
        }
        return (T) attributes.get(key);
    }
}
