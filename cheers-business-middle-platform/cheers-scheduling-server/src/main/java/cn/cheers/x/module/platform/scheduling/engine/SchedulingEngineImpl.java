package cn.cheers.x.module.platform.scheduling.engine;

import cn.cheers.x.module.platform.contract.ContractVersions;
import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.contract.enums.SlotLockState;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * MVP 排程：weekly 模式按 horizon 每周展开；单次模式每个 Work Item 一个计划点。
 */
@Service
public class SchedulingEngineImpl implements SchedulingEngine {

    private static final LocalTime DEFAULT_DAY_START = LocalTime.of(9, 0);
    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();

    @Override
    public List<ScheduleSlotDTO> solve(List<WorkItemDTO> workItems, SchedulingSpecDTO schedulingSpec,
                                       String runtimeJobId) {
        if (workItems == null || workItems.isEmpty()) {
            return List.of();
        }
        String mode = schedulingSpec != null && StringUtils.hasText(schedulingSpec.getMode())
                ? schedulingSpec.getMode() : "once";
        LocalDate horizonStart = parseDate(schedulingSpec != null ? schedulingSpec.getHorizonStart() : null);
        LocalDate horizonEnd = parseDate(schedulingSpec != null ? schedulingSpec.getHorizonEnd() : null);
        if (horizonEnd == null) {
            horizonEnd = horizonStart != null ? horizonStart.plusWeeks(4) : LocalDate.now().plusWeeks(4);
        }
        if (horizonStart == null) {
            horizonStart = LocalDate.now();
        }

        List<ScheduleSlotDTO> slots = new ArrayList<>();
        int workIndex = 0;
        for (WorkItemDTO workItem : workItems) {
            if ("weekly".equalsIgnoreCase(mode)) {
                LocalDate cursor = horizonStart.plusDays(workIndex);
                while (!cursor.isAfter(horizonEnd)) {
                    slots.add(buildSlot(workItem, runtimeJobId, cursor, workItem.getDurationEstimateMinutes()));
                    cursor = cursor.plusWeeks(1);
                }
            } else {
                LocalDate day = horizonStart.plusDays(workIndex);
                slots.add(buildSlot(workItem, runtimeJobId, day, workItem.getDurationEstimateMinutes()));
            }
            workIndex++;
        }
        return applyConflictStrategy(slots, schedulingSpec);
    }

    private List<ScheduleSlotDTO> applyConflictStrategy(List<ScheduleSlotDTO> slots, SchedulingSpecDTO spec) {
        if (spec == null || !"defer_slot".equalsIgnoreCase(spec.getConflictStrategy())) {
            return slots;
        }
        List<ScheduleSlotDTO> resolved = new ArrayList<>();
        OffsetDateTime lastEnd = null;
        for (ScheduleSlotDTO slot : slots) {
            OffsetDateTime start = OffsetDateTime.parse(slot.getPlannedStart());
            OffsetDateTime end = OffsetDateTime.parse(slot.getPlannedEnd());
            if (lastEnd != null && !start.isAfter(lastEnd) && !start.isEqual(lastEnd)) {
                long minutes = java.time.Duration.between(start, end).toMinutes();
                start = lastEnd;
                end = start.plusMinutes(minutes);
                slot = copyWithTimes(slot, start, end);
            }
            resolved.add(slot);
            lastEnd = end;
        }
        return resolved;
    }

    private ScheduleSlotDTO copyWithTimes(ScheduleSlotDTO slot, OffsetDateTime start, OffsetDateTime end) {
        return ScheduleSlotDTO.builder()
                .contractVersion(slot.getContractVersion())
                .slotId(slot.getSlotId())
                .runtimeJobId(slot.getRuntimeJobId())
                .workId(slot.getWorkId())
                .entityTypeCode(slot.getEntityTypeCode())
                .plannedStart(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(start))
                .plannedEnd(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(end))
                .assignedResources(slot.getAssignedResources())
                .lockState(slot.getLockState())
                .slotStatus(slot.getSlotStatus())
                .policySnapshotId(slot.getPolicySnapshotId())
                .decisionTraceId(slot.getDecisionTraceId())
                .build();
    }

    private ScheduleSlotDTO buildSlot(WorkItemDTO workItem, String runtimeJobId, LocalDate day, Integer durationMinutes) {
        int minutes = durationMinutes != null && durationMinutes > 0 ? durationMinutes : 60;
        OffsetDateTime start = day.atTime(DEFAULT_DAY_START).atZone(DEFAULT_ZONE).toOffsetDateTime();
        OffsetDateTime end = start.plusMinutes(minutes);
        return ScheduleSlotDTO.builder()
                .contractVersion(ContractVersions.MVP)
                .slotId(UUID.randomUUID().toString())
                .runtimeJobId(runtimeJobId)
                .workId(workItem.getWorkId())
                .entityTypeCode(workItem.getEntityTypeCode())
                .plannedStart(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(start))
                .plannedEnd(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(end))
                .lockState(SlotLockState.NONE)
                .slotStatus(SlotStatus.PLANNED)
                .build();
    }

    private LocalDate parseDate(String text) {
        if (!StringUtils.hasText(text)) {
            return null;
        }
        return LocalDate.parse(text.substring(0, Math.min(text.length(), 10)));
    }
}
