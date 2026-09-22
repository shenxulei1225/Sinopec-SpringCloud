package cn.cheers.x.module.platform.scheduling.engine;

import cn.cheers.x.module.platform.contract.ContractVersions;
import cn.cheers.x.module.platform.contract.dto.reservation.ResourceReservationDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleConflictReportDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.scheduling.conflict.ScheduleConflictReporter;
import cn.cheers.x.module.platform.contract.dto.slot.AssignedResourceDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.enums.CandidateType;
import cn.cheers.x.module.platform.contract.dto.work.ResourceRequirementDTO;
import cn.cheers.x.module.platform.contract.dto.work.TimePreferencesDTO;
import cn.cheers.x.module.platform.contract.dto.work.TimeWindowDTO;
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
import static cn.cheers.x.module.platform.scheduling.enums.ErrorCodeConstants.SCHEDULING_INVALID_CONFLICT_STRATEGY;

/**
 * 排程引擎：工作项已带计划时刻则只做资源冲突求解；否则按 mode 展开后再求解。
 * <p>先判断空闲够不够插入、冲突在前面还是后面；策略只决定能否挪已有任务、最多挪多久。
 * <p>不负责：从业务排期模板发明执行点；从设备台账拉候选名单。
 */
@Service
public class SchedulingEngineImpl implements SchedulingEngine {

    private static final LocalTime DEFAULT_DAY_START = LocalTime.of(9, 0);
    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();
    private static final String STRATEGY_DEFER = "defer_slot";
    private static final String STRATEGY_REJECT = "reject_batch";
    private static final String STRATEGY_PRIORITY = "priority_preempt";

    @Override
    public List<ResourceReservationDTO> solve(List<WorkItemDTO> workItems, SchedulingSpecDTO schedulingSpec,
                                              String runtimeJobId) {
        return solve(workItems, schedulingSpec, runtimeJobId, List.of());
    }

    @Override
    public List<ResourceReservationDTO> solve(List<WorkItemDTO> workItems, SchedulingSpecDTO schedulingSpec,
                                              String runtimeJobId, List<ResourceReservationDTO> occupiedReservations) {
        if (workItems == null || workItems.isEmpty()) {
            return List.of();
        }
        for (WorkItemDTO workItem : workItems) {
            requirePositiveDuration(workItem);
        }

        int taskGapMinutes = resolveTaskGapMinutes(schedulingSpec);

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
        String placementPreference = SchedulingConflictPlacer.normalizePreference(
                schedulingSpec != null ? schedulingSpec.getPlacementPreference() : null);
        boolean allowShiftExisting = schedulingSpec != null && Boolean.TRUE.equals(schedulingSpec.getAllowShiftExisting());
        Integer maxShiftMinutes = schedulingSpec != null ? schedulingSpec.getMaxShiftMinutes() : null;
        Map<String, List<TimelineBlock>> timelines = seedTimelines(occupiedReservations, taskGapMinutes);
        OffsetDateTime earliestStart = horizonStart.atStartOfDay(DEFAULT_ZONE).toOffsetDateTime();

        if (STRATEGY_PRIORITY.equals(strategy)) {
            candidates = new ArrayList<>(candidates);
            candidates.sort(Comparator
                    .comparingInt((Candidate c) -> priorityOf(c.workItem())).reversed()
                    .thenComparing(Candidate::workId)
                    .thenComparing(Candidate::preferredStart));
        }

        List<ResourceReservationDTO> resolved = new ArrayList<>();
        for (Candidate candidate : candidates) {
            List<String> resourceIds = SchedulingConflictPlacer.candidateIds(candidate.workItem().getResourceRequirements());
            OffsetDateTime preferred = candidate.preferredStart();
            int minutes = candidate.workItem().getEstimatedDuration();
            SchedulingConflictPlacer.Placement placement = SchedulingConflictPlacer.place(
                    strategy, placementPreference, allowShiftExisting, maxShiftMinutes,
                    preferred, minutes, resourceIds, timelines, earliestStart, horizonLimit);
            OffsetDateTime placedStart = placement.start();
            OffsetDateTime placedEnd = placedStart.plusMinutes(minutes);
            if (!placedEnd.isBefore(horizonLimit)) {
                throw exception(SCHEDULING_CANNOT_PLACE);
            }

            ResourceReservationDTO reservation = buildReservation(
                    candidate, placement.resourceId(), placedStart, placedEnd);
            resolved.add(reservation);
            for (ResourceReservationDTO shifted : placement.shiftedExisting()) {
                if (shifted != null && resolved.stream().noneMatch(item -> item == shifted)) {
                    resolved.add(shifted);
                }
            }
            List<TimelineBlock> busy = timelines.computeIfAbsent(placement.resourceId(), key -> new ArrayList<>());
            busy.add(new TimelineBlock(placedStart, placedEnd, taskGapMinutes, reservation));
            busy.sort(Comparator.comparing(TimelineBlock::start));
        }

        if (STRATEGY_PRIORITY.equals(strategy)) {
            resolved.sort(Comparator.comparing(ResourceReservationDTO::getCandidateStart)
                    .thenComparing(ResourceReservationDTO::getWorkId));
        }
        return resolved;
    }

    @Override
    public ScheduleConflictReportDTO detectConflicts(List<WorkItemDTO> workItems, SchedulingSpecDTO schedulingSpec,
                                                     List<ResourceReservationDTO> occupiedReservations) {
        return ScheduleConflictReporter.report(workItems, occupiedReservations, resolveTaskGapMinutes(schedulingSpec));
    }

    private static int resolveTaskGapMinutes(SchedulingSpecDTO schedulingSpec) {
        if (schedulingSpec == null || schedulingSpec.getTaskGapMinutes() == null) {
            return 0;
        }
        return Math.max(0, schedulingSpec.getTaskGapMinutes());
    }

    /** @deprecated 兼容旧调用 */
    @Deprecated
    public List<ScheduleSlotDTO> solveLegacy(List<WorkItemDTO> workItems, SchedulingSpecDTO schedulingSpec,
                                             String runtimeJobId, List<ScheduleSlotDTO> occupiedSlots) {
        return solve(workItems, schedulingSpec, runtimeJobId,
                occupiedSlots == null ? List.of() : occupiedSlots.stream().map(ScheduleSlotDTO::legacyToReservation).toList())
                .stream().map(ScheduleSlotDTO::from).toList();
    }

    private List<Candidate> expandCandidates(List<WorkItemDTO> workItems, String mode,
                                             LocalDate horizonStart, LocalDate horizonEnd,
                                             String runtimeJobId) {
        List<Candidate> candidates = new ArrayList<>();
        int workIndex = 0;
        for (WorkItemDTO workItem : workItems) {
            OffsetDateTime preferred = preferredStartOf(workItem);
            if (preferred != null) {
                // 业务侧已给出计划时刻：本引擎只占窗，不再按 mode 复制
                candidates.add(new Candidate(workItem, runtimeJobId, preferred));
            } else if ("weekly".equalsIgnoreCase(mode)) {
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

    /**
     * 工作项已写入允许窗时，认第一条窗的开始为计划时刻。
     */
    private static OffsetDateTime preferredStartOf(WorkItemDTO workItem) {
        TimePreferencesDTO prefs = workItem.getTimePreferences();
        if (prefs == null || prefs.getAllowedWindows() == null || prefs.getAllowedWindows().isEmpty()) {
            return null;
        }
        TimeWindowDTO window = prefs.getAllowedWindows().get(0);
        if (window == null || !StringUtils.hasText(window.getStart())) {
            return null;
        }
        return OffsetDateTime.parse(window.getStart(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private Map<String, List<TimelineBlock>> seedTimelines(List<ResourceReservationDTO> occupiedReservations,
                                                           int taskGapMinutes) {
        Map<String, List<TimelineBlock>> timelines = new HashMap<>();
        if (occupiedReservations == null) {
            return timelines;
        }
        for (ResourceReservationDTO occupied : occupiedReservations) {
            if (occupied == null) {
                continue;
            }
            OffsetDateTime start = parseOccupiedStart(occupied);
            OffsetDateTime end = parseOccupiedEnd(occupied);
            if (start == null || end == null) {
                continue;
            }
            for (String key : occupiedResourceKeys(occupied)) {
                timelines.computeIfAbsent(key, k -> new ArrayList<>())
                        .add(new TimelineBlock(start, end, taskGapMinutes, occupied));
            }
        }
        for (List<TimelineBlock> busy : timelines.values()) {
            busy.sort(Comparator.comparing(TimelineBlock::start));
        }
        return timelines;
    }

    private static OffsetDateTime parseOccupiedStart(ResourceReservationDTO occupied) {
        if (StringUtils.hasText(occupied.getCandidateStart())) {
            return OffsetDateTime.parse(occupied.getCandidateStart());
        }
        if (StringUtils.hasText(occupied.getPlannedStart())) {
            return OffsetDateTime.parse(occupied.getPlannedStart());
        }
        return null;
    }

    private static OffsetDateTime parseOccupiedEnd(ResourceReservationDTO occupied) {
        if (StringUtils.hasText(occupied.getCandidateEnd())) {
            return OffsetDateTime.parse(occupied.getCandidateEnd());
        }
        if (StringUtils.hasText(occupied.getPlannedEnd())) {
            return OffsetDateTime.parse(occupied.getPlannedEnd());
        }
        return null;
    }

    private List<String> occupiedResourceKeys(ResourceReservationDTO occupied) {
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

    private ResourceReservationDTO buildReservation(Candidate candidate, String assignedResourceId,
                                                    OffsetDateTime start, OffsetDateTime end) {
        WorkItemDTO workItem = candidate.workItem();
        String startText = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(start);
        String endText = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(end);
        return ResourceReservationDTO.builder()
                .contractVersion(ContractVersions.MVP)
                .candidateId(UUID.randomUUID().toString())
                .runtimeJobId(candidate.runtimeJobId())
                .candidateType(CandidateType.TASK_EXECUTION)
                .workId(workItem.getWorkId())
                .entityTypeCode(workItem.getEntityTypeCode())
                .candidateStart(startText)
                .candidateEnd(endText)
                .assignedResources(toAssignedResources(workItem, assignedResourceId))
                .lockState(SlotLockState.NONE)
                .candidateStatus(SlotStatus.PLANNED)
                .build();
    }

    private List<AssignedResourceDTO> toAssignedResources(WorkItemDTO workItem, String assignedResourceId) {
        String resourceType = firstResourceType(workItem);
        if (!StringUtils.hasText(assignedResourceId) || "__unassigned__".equals(assignedResourceId)) {
            return List.of();
        }
        return List.of(AssignedResourceDTO.builder()
                .resourceType(resourceType)
                .resourceId(assignedResourceId)
                .build());
    }

    private static String firstResourceType(WorkItemDTO workItem) {
        if (workItem.getResourceRequirements() == null) {
            return null;
        }
        for (ResourceRequirementDTO req : workItem.getResourceRequirements()) {
            if (req != null && StringUtils.hasText(req.getResourceType())) {
                return req.getResourceType();
            }
        }
        return null;
    }

    private void requirePositiveDuration(WorkItemDTO workItem) {
        Integer minutes = workItem.getEstimatedDuration();
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
        throw exception(SCHEDULING_INVALID_CONFLICT_STRATEGY);
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

}
