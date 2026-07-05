package cn.cheers.x.module.platform.scheduling.engine;

import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;

import java.util.List;

/**
 * 排程引擎：Work Item + 排程规格 → 计划点列表。
 */
public interface SchedulingEngine {

    List<ScheduleSlotDTO> solve(List<WorkItemDTO> workItems, SchedulingSpecDTO schedulingSpec, String runtimeJobId);
}
