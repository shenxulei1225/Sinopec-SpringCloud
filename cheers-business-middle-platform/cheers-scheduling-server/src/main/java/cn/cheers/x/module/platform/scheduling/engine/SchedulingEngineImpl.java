package cn.cheers.x.module.platform.scheduling.engine;

import cn.cheers.x.module.platform.contract.ContractVersions;
import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.contract.dto.slot.AssignedResourceDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.dto.work.ResourceRequirementDTO;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.platform.scheduling.enums.ErrorCodeConstants.SCHEDULING_BATCH_REJECTED;
import static cn.cheers.x.module.platform.scheduling.enums.ErrorCodeConstants.SCHEDULING_CANNOT_PLACE;
import static cn.cheers.x.module.platform.scheduling.enums.ErrorCodeConstants.SCHEDULING_DURATION_REQUIRED;

/**
 * 排程引擎：展开候选计划点后按资源时间轴解析冲突。
 */
@Service
public class SchedulingEngineImpl implements SchedulingEngine {

    private static final LocalTime DEFAULT_DAY_START = LocalTime.of(9, 0);
    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();
    private static final String STRATEGY_DEFER = "defer_slot";
    private static final String STRATEGY_REJECT = "reject_batch";
    private static final String STRATEGY_PRIORITY = "priority_preempt";

    @Override
    public List<ScheduleSlotDTO> solve(List<WorkItemDTO> workItems, SchedulingSpecDTO schedulingSpec,
                                       String runtimeJobId) {
        return solve(workItems, schedulingSpec, runtimeJobId, List.of());
    }

    @Override
    public List<ScheduleSlotDTO> solve(List<WorkItemDTO> workItems, SchedulingSpecDTO schedulingSpec,
                                       String runtimeJobId, List<ScheduleSlotDTO> occupiedSlots) {
        if (workItems == null || workItems.isEmpty()) {
            return List.of();
        }
        for (WorkItemDTO workItem : workItems) {
            requirePositiveDuration(workItem);
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
        OffsetDateTime horizonLimit = horizonEnd.plusDays(1).atStartOfDay(DEFAULT_ZONE).toOffsetDateTime();

        List<Candidate> candidates = expandCandidates(workItems, mode, horizonStart, horizonEnd, runtimeJobId);
        String strategy = normalizeStrategy(schedulingSpec != null ? schedulingSpec.getConflictStrategy() : null);
        Map<String, List<BusyInterval>> timelines = seedTimelines(occupiedSlots);

        if (STRATEGY_PRIORITY.equals(strategy)) {
            candidates = new ArrayList<>(candidates);
            candidates.sort(Comparator
                    .comparingInt((Candidate c) -> priorityOf(c.workItem())).reversed()
                    .thenComparing(Candidate::workId)
                    .thenComparing(Candidate::preferredStart));
        }

        List<ScheduleSlotDTO> resolved = new ArrayList<>();
        for (Candidate candidate : candidates) {
            String resourceKey = resourceKey(candidate.workItem());
            List<BusyInterval> busy = timelines.computeIfAbsent(resourceKey, k -> new ArrayList<>());
            OffsetDateTime preferred = candidate.preferredStart();
            int minutes = candidate.workItem().getDurationEstimateMinutes();

            OffsetDateTime placedStart;
            if (STRATEGY_REJECT.equals(strategy)) {
                if (overlaps(preferred, preferred.plusMinutes(minutes), busy)) {
                    throw exception(SCHEDULING_BATCH_REJECTED);
                }
                placedStart = preferred;
            } else {
                placedStart = earliestFit(preferred, minutes, busy, horizonLimit);
            }

            OffsetDateTime placedEnd = placedStart.plusMinutes(minutes);
            if (!placedEnd.isBefore(horizonLimit)) {
                throw exception(SCHEDULING_CANNOT_PLACE);
            }

            ScheduleSlotDTO slot = buildSlot(candidate, placedStart, placedEnd);
            resolved.add(slot);
            busy.add(new BusyInterval(placedStart, placedEnd));
            busy.sort(Comparator.comparing(BusyInterval::start));
        }

        if (STRATEGY_PRIORITY.equals(strategy)) {
            resolved.sort(Comparator.comparing(ScheduleSlotDTO::getPlannedStart)
                    .thenComparing(ScheduleSlotDTO::getWorkId));
        }
        return resolved;
    }

    private List<Candidate> expandCandidates(List<WorkItemDTO> workItems, String mode,
                                             LocalDate horizonStart, LocalDate horizonEnd,
                                             String runtimeJobId) {
        List<Candidate> candidates = new ArrayList<>();
        int workIndex = 0;
        for (WorkItemDTO workItem : workItems) {
            if ("weekly".equalsIgnoreCase(mode)) {
                LocalDate cursor = horizonStart.plusDays(workIndex);
                while (!cursor.isAfter(horizonEnd)) {
                    candidates.add(new Candidate(workItem, runtimeJobId,
                            cursor.atTime(DEFAULT_DAY_START).atZone(DEFAULT_ZONE).toOffsetDateTime()));
                    cursor = cursor.plusWeeks(1);
                }
            } else {
                // once：同批共享 horizon 日起始偏好，冲突由资源时间轴策略解析
                candidates.add(new Candidate(workItem, runtimeJobId,
                        horizonStart.atTime(DEFAULT_DAY_START).atZone(DEFAULT_ZONE).toOffsetDateTime()));
            }
            workIndex++;
        }
        return candidates;
    }

    private Map<String, List<BusyInterval>> seedTimelines(List<ScheduleSlotDTO> occupiedSlots) {
        Map<String, List<BusyInterval>> timelines = new HashMap<>();
        if (occupiedSlots == null) {
            return timelines;
        }
        for (ScheduleSlotDTO occupied : occupiedSlots) {
            if (occupied == null || !StringUtils.hasText(occupied.getPlannedStart())
                    || !StringUtils.hasText(occupied.getPlannedEnd())) {
                continue;
            }
            OffsetDateTime start = OffsetDateTime.parse(occupied.getPlannedStart());
            OffsetDateTime end = OffsetDateTime.parse(occupied.getPlannedEnd());
            for (String key : occupiedResourceKeys(occupied)) {
                timelines.computeIfAbsent(key, k -> new ArrayList<>()).add(new BusyInterval(start, end));
            }
        }
        for (List<BusyInterval> busy : timelines.values()) {
            busy.sort(Comparator.comparing(BusyInterval::start));
        }
        return timelines;
    }

    private List<String> occupiedResourceKeys(ScheduleSlotDTO occupied) {
        List<String> keys = new ArrayList<>();
        if (occupied.getAssignedResources() != null) {
            for (AssignedResourceDTO assigned : occupied.getAssignedResources()) {
                if (assigned == null) {
                    continue;
                }
                if (StringUtils.hasText(assigned.getResourceId())) {
                    keys.add(assigned.getResourceId());
                } else if (StringUtils.hasText(assigned.getResourceType())) {
                    keys.add("__type:" + assigned.getResourceType());
                }
            }
        }
        if (keys.isEmpty()) {
            keys.add("__unassigned__");
        }
        return keys;
    }

    private OffsetDateTime earliestFit(OffsetDateTime preferred, int minutes,
                                       List<BusyInterval> busy, OffsetDateTime horizonLimit) {
        OffsetDateTime cursor = preferred;
        for (BusyInterval interval : busy) {
            OffsetDateTime tentativeEnd = cursor.plusMinutes(minutes);
            if (!tentativeEnd.isAfter(interval.start())) {
                break;
            }
            if (cursor.isBefore(interval.end())) {
                cursor = interval.end();
            }
        }
        OffsetDateTime end = cursor.plusMinutes(minutes);
        if (!end.isBefore(horizonLimit)) {
            throw exception(SCHEDULING_CANNOT_PLACE);
        }
        return cursor;
    }

    private boolean overlaps(OffsetDateTime start, OffsetDateTime end, List<BusyInterval> busy) {
        for (BusyInterval interval : busy) {
            // overlap if start < interval.end && end > interval.start
            if (start.isBefore(interval.end()) && end.isAfter(interval.start())) {
                return true;
            }
        }
        return false;
    }

    private ScheduleSlotDTO buildSlot(Candidate candidate, OffsetDateTime start, OffsetDateTime end) {
        WorkItemDTO workItem = candidate.workItem();
        return ScheduleSlotDTO.builder()
                .contractVersion(ContractVersions.MVP)
                .slotId(UUID.randomUUID().toString())
                .runtimeJobId(candidate.runtimeJobId())
                .workId(workItem.getWorkId())
                .entityTypeCode(workItem.getEntityTypeCode())
                .plannedStart(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(start))
                .plannedEnd(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(end))
                .assignedResources(toAssignedResources(workItem))
                .lockState(SlotLockState.NONE)
                .slotStatus(SlotStatus.PLANNED)
                .build();
    }

    private List<AssignedResourceDTO> toAssignedResources(WorkItemDTO workItem) {
        if (workItem.getResourceRequirements() == null || workItem.getResourceRequirements().isEmpty()) {
            return List.of();
        }
        List<AssignedResourceDTO> assigned = new ArrayList<>();
        for (ResourceRequirementDTO req : workItem.getResourceRequirements()) {
            if (req == null) {
                continue;
            }
            assigned.add(AssignedResourceDTO.builder()
                    .resourceType(req.getResourceType())
                    .resourceId(req.getFixedResourceId())
                    .build());
        }
        return assigned;
    }

    private String resourceKey(WorkItemDTO workItem) {
        if (workItem.getResourceRequirements() != null) {
            for (ResourceRequirementDTO req : workItem.getResourceRequirements()) {
                if (req == null) {
                    continue;
                }
                if (StringUtils.hasText(req.getFixedResourceId())) {
                    return req.getFixedResourceId();
                }
                if (StringUtils.hasText(req.getResourceType())) {
                    return "__type:" + req.getResourceType();
                }
            }
        }
        return "__unassigned__";
    }

    private void requirePositiveDuration(WorkItemDTO workItem) {
        Integer minutes = workItem.getDurationEstimateMinutes();
        if (minutes == null || minutes <= 0) {
            throw exception(SCHEDULING_DURATION_REQUIRED);
        }
    }

    private String normalizeStrategy(String strategy) {
        if (!StringUtils.hasText(strategy)) {
            return STRATEGY_DEFER;
        }
        String normalized = strategy.trim().toLowerCase();
        if (STRATEGY_REJECT.equals(normalized) || STRATEGY_PRIORITY.equals(normalized)
                || STRATEGY_DEFER.equals(normalized)) {
            return normalized;
        }
        return STRATEGY_DEFER;
    }

    private int priorityOf(WorkItemDTO workItem) {
        return workItem.getPriority() != null ? workItem.getPriority() : 0;
    }

    private LocalDate parseDate(String text) {
        if (!StringUtils.hasText(text)) {
            return null;
        }
        return LocalDate.parse(text.substring(0, Math.min(text.length(), 10)));
    }

    private record Candidate(WorkItemDTO workItem, String runtimeJobId, OffsetDateTime preferredStart) {
        String workId() {
            return workItem.getWorkId();
        }
    }

    private record BusyInterval(OffsetDateTime start, OffsetDateTime end) {
    }
}
