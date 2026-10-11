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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.platform.scheduling.enums.ErrorCodeConstants.SCHEDULING_BATCH_REJECTED;
import static cn.cheers.x.module.platform.scheduling.enums.ErrorCodeConstants.SCHEDULING_CANNOT_PLACE;
import static cn.cheers.x.module.platform.scheduling.enums.ErrorCodeConstants.SCHEDULING_DURATION_REQUIRED;
import static cn.cheers.x.module.platform.scheduling.enums.ErrorCodeConstants.SCHEDULING_INVALID_CONFLICT_STRATEGY;

/**
 * 排程引擎：工作项已带计划时刻则只做资源冲突求解；否则按 mode 展开后再求解。
 * <p>同一原定时刻先试一次平移，只挪这次这条。剩下的再逐个贴进空窗口，窗口不够才推后面、拉前面。
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

        if (!STRATEGY_REJECT.equals(strategy)
                && SchedulingConflictPlacer.PREFERENCE_WINDOW.equals(placementPreference)) {
            candidates = alignSameClock(candidates, timelines, earliestStart, horizonLimit);
        }

        List<ResourceReservationDTO> resolved = new ArrayList<>();
        for (Candidate candidate : candidates) {
            List<String> resourceIds = SchedulingConflictPlacer.candidateIds(candidate.workItem().getResourceRequirements());
            OffsetDateTime preferred = candidate.preferredStart();
            int minutes = candidate.workItem().getEstimatedDuration();
            SchedulingConflictPlacer.Placement placement = SchedulingConflictPlacer.place(
                    strategy, placementPreference, maxShiftMinutes,
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

    /**
     * 同一台设备、同一个原定时刻的多天：试一个平移分钟数。
     * 一次平移加上剩下要单独处理的次数更少，才改这些天的原定时刻。本来能放下的不挪。
     */
    private List<Candidate> alignSameClock(List<Candidate> candidates,
                                            Map<String, List<TimelineBlock>> timelines,
                                            OffsetDateTime earliestStart, OffsetDateTime horizonLimit) {
        Map<String, List<Integer>> groups = new LinkedHashMap<>();
        for (int index = 0; index < candidates.size(); index++) {
            Candidate candidate = candidates.get(index);
            String resourceId = SchedulingConflictPlacer.candidateIds(
                    candidate.workItem().getResourceRequirements()).stream().findFirst().orElse("__unassigned__");
            String clock = candidate.preferredStart().atZoneSameInstant(DEFAULT_ZONE).toLocalTime().toString();
            groups.computeIfAbsent(resourceId + "|" + clock, key -> new ArrayList<>()).add(index);
        }
        List<Candidate> aligned = new ArrayList<>(candidates);
        for (List<Integer> indexes : groups.values()) {
            if (indexes.size() < 2) {
                continue;
            }
            int conflictCount = 0;
            for (int index : indexes) {
                if (!fitsExternal(aligned.get(index), 0, timelines, earliestStart, horizonLimit)) {
                    conflictCount++;
                }
            }
            if (conflictCount < 2) {
                continue;
            }
            Set<Integer> deltas = new LinkedHashSet<>();
            for (int index : indexes) {
                collectSelfDeltas(aligned.get(index), timelines, earliestStart, horizonLimit, deltas);
            }
            int bestCost = conflictCount;
            int bestDelta = 0;
            for (int delta : deltas) {
                int cost = bulkCost(aligned, indexes, delta, timelines, earliestStart, horizonLimit, conflictCount);
                if (cost < bestCost) {
                    bestCost = cost;
                    bestDelta = delta;
                }
            }
            if (bestDelta == 0) {
                continue;
            }
            applyDelta(aligned, indexes, bestDelta, timelines, earliestStart, horizonLimit);
        }
        return aligned;
    }

    private int bulkCost(List<Candidate> candidates, List<Integer> indexes, int delta,
                          Map<String, List<TimelineBlock>> timelines,
                          OffsetDateTime earliestStart, OffsetDateTime horizonLimit, int conflictCount) {
        int moved = 0;
        int leftovers = 0;
        List<Span> taken = new ArrayList<>();
        for (int index : indexes) {
            Candidate candidate = candidates.get(index);
            int minutes = candidate.workItem().getEstimatedDuration();
            if (fitsExternal(candidate, 0, timelines, earliestStart, horizonLimit)) {
                taken.add(new Span(candidate.preferredStart(), minutes));
                continue;
            }
            OffsetDateTime start = candidate.preferredStart().plusMinutes(delta);
            if (fitsExternal(candidate, delta, timelines, earliestStart, horizonLimit)
                    && !overlaps(start, minutes, taken)) {
                moved++;
                taken.add(new Span(start, minutes));
            } else {
                leftovers++;
            }
        }
        return moved == 0 ? conflictCount : 1 + leftovers;
    }

    private void applyDelta(List<Candidate> candidates, List<Integer> indexes, int delta,
                             Map<String, List<TimelineBlock>> timelines,
                             OffsetDateTime earliestStart, OffsetDateTime horizonLimit) {
        List<Span> taken = new ArrayList<>();
        for (int index : indexes) {
            Candidate candidate = candidates.get(index);
            int minutes = candidate.workItem().getEstimatedDuration();
            if (fitsExternal(candidate, 0, timelines, earliestStart, horizonLimit)) {
                taken.add(new Span(candidate.preferredStart(), minutes));
                continue;
            }
            OffsetDateTime start = candidate.preferredStart().plusMinutes(delta);
            if (!fitsExternal(candidate, delta, timelines, earliestStart, horizonLimit)
                    || overlaps(start, minutes, taken)) {
                continue;
            }
            candidates.set(index, new Candidate(candidate.workItem(), candidate.runtimeJobId(), start));
            taken.add(new Span(start, minutes));
        }
    }

    private void collectSelfDeltas(Candidate candidate, Map<String, List<TimelineBlock>> timelines,
                                    OffsetDateTime earliestStart, OffsetDateTime horizonLimit,
                                    Set<Integer> deltas) {
        int minutes = candidate.workItem().getEstimatedDuration();
        String resourceId = SchedulingConflictPlacer.candidateIds(
                candidate.workItem().getResourceRequirements()).stream().findFirst().orElse("__unassigned__");
        List<TimelineBlock> busy = timelines.getOrDefault(resourceId, List.of());
        OffsetDateTime preferred = candidate.preferredStart();
        SchedulingConflictDiagnosis diagnosis = SchedulingConflictDiagnosis.diagnose(
                preferred, preferred.plusMinutes(minutes), busy, earliestStart, horizonLimit);
        OffsetDateTime idleStart = diagnosis.previous() == null ? earliestStart : diagnosis.previous().blockedEnd();
        OffsetDateTime idleEnd = diagnosis.next() == null ? horizonLimit : diagnosis.next().start();
        addGapDelta(deltas, preferred, minutes, idleStart, idleEnd, horizonLimit);
        if (diagnosis.next() != null) {
            TimelineBlock later = SchedulingConflictDiagnosis.nextOf(diagnosis.next(), busy);
            OffsetDateTime nextStart = diagnosis.next().blockedEnd();
            OffsetDateTime nextEnd = later == null ? horizonLimit : later.start();
            addGapDelta(deltas, preferred, minutes, nextStart, nextEnd, horizonLimit);
        }
    }

    private static void addGapDelta(Set<Integer> deltas, OffsetDateTime preferred, int minutes,
                                     OffsetDateTime gapStart, OffsetDateTime gapEnd, OffsetDateTime horizonLimit) {
        if (gapEnd.isBefore(gapStart) || java.time.Duration.between(gapStart, gapEnd).toMinutes() < minutes) {
            return;
        }
        OffsetDateTime latest = gapEnd.minusMinutes(minutes);
        if (!latest.plusMinutes(minutes).isBefore(horizonLimit)) {
            return;
        }
        OffsetDateTime start = preferred;
        if (start.isBefore(gapStart)) {
            start = gapStart;
        }
        if (start.isAfter(latest)) {
            start = latest;
        }
        deltas.add((int) java.time.Duration.between(preferred, start).toMinutes());
    }

    private boolean fitsExternal(Candidate candidate, int deltaMinutes,
                                  Map<String, List<TimelineBlock>> timelines,
                                  OffsetDateTime earliestStart, OffsetDateTime horizonLimit) {
        int minutes = candidate.workItem().getEstimatedDuration();
        String resourceId = SchedulingConflictPlacer.candidateIds(
                candidate.workItem().getResourceRequirements()).stream().findFirst().orElse("__unassigned__");
        List<TimelineBlock> busy = timelines.getOrDefault(resourceId, List.of());
        return SchedulingConflictPlacer.selfFits(
                candidate.preferredStart().plusMinutes(deltaMinutes), minutes, busy, earliestStart, horizonLimit);
    }

    private static boolean overlaps(OffsetDateTime start, int minutes, List<Span> taken) {
        OffsetDateTime end = start.plusMinutes(minutes);
        for (Span span : taken) {
            if (start.isBefore(span.end()) && span.start().isBefore(end)) {
                return true;
            }
        }
        return false;
    }

    private record Span(OffsetDateTime start, int minutes) {
        OffsetDateTime end() {
            return start.plusMinutes(minutes);
        }
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
