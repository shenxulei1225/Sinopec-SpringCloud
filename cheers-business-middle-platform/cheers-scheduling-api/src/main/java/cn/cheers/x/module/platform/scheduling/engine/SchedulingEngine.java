package cn.cheers.x.module.platform.scheduling.engine;

import cn.cheers.x.module.platform.contract.dto.reservation.ResourceReservationDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleConflictReportDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;

import java.util.List;

/**
 * 排程引擎：先检测冲突报告，再按策略求解占窗。
 */
public interface SchedulingEngine {

    default List<ResourceReservationDTO> solve(List<WorkItemDTO> workItems, SchedulingSpecDTO schedulingSpec,
                                               String runtimeJobId) {
        return solve(workItems, schedulingSpec, runtimeJobId, List.of());
    }

    List<ResourceReservationDTO> solve(List<WorkItemDTO> workItems, SchedulingSpecDTO schedulingSpec,
                                       String runtimeJobId, List<ResourceReservationDTO> occupiedReservations);

    /**
     * 只出冲突报告，不挪窗、不换设备。
     */
    ScheduleConflictReportDTO detectConflicts(List<WorkItemDTO> workItems, SchedulingSpecDTO schedulingSpec,
                                              List<ResourceReservationDTO> occupiedReservations);

    /**
     * @deprecated 使用 {@link #solve(List, SchedulingSpecDTO, String, List)} 且 occupied 为 {@link ResourceReservationDTO}
     */
    @Deprecated
    default List<ScheduleSlotDTO> solveLegacy(List<WorkItemDTO> workItems, SchedulingSpecDTO schedulingSpec,
                                              String runtimeJobId, List<ScheduleSlotDTO> occupiedSlots) {
        List<ResourceReservationDTO> occupied = occupiedSlots == null ? List.of()
                : occupiedSlots.stream().map(ScheduleSlotDTO::legacyToReservation).toList();
        return solve(workItems, schedulingSpec, runtimeJobId, occupied).stream()
                .map(ScheduleSlotDTO::from)
                .toList();
    }
}
