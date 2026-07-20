package cn.cheers.x.module.platform.scheduling.engine;

import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;

import java.util.List;

/**
 * 排程引擎：Work Item + 排程规格 → 计划点列表。
 */
public interface SchedulingEngine {

    /**
     * 求解计划点；已占用计划点默认为空。
     */
    default List<ScheduleSlotDTO> solve(List<WorkItemDTO> workItems, SchedulingSpecDTO schedulingSpec,
                                        String runtimeJobId) {
        return solve(workItems, schedulingSpec, runtimeJobId, List.of());
    }

    /**
     * 求解计划点，并合并已占用计划点到资源时间轴。
     *
     * @param occupiedSlots 已占用计划点（可空）
     */
    List<ScheduleSlotDTO> solve(List<WorkItemDTO> workItems, SchedulingSpecDTO schedulingSpec,
                                String runtimeJobId, List<ScheduleSlotDTO> occupiedSlots);
}
