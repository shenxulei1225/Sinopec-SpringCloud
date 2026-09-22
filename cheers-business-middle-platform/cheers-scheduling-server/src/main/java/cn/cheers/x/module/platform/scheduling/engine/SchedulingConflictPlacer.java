package cn.cheers.x.module.platform.scheduling.engine;

import cn.cheers.x.module.platform.contract.dto.reservation.ResourceReservationDTO;
import cn.cheers.x.module.platform.contract.dto.work.ResourceRequirementDTO;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.platform.scheduling.enums.ErrorCodeConstants.SCHEDULING_BATCH_REJECTED;
import static cn.cheers.x.module.platform.scheduling.enums.ErrorCodeConstants.SCHEDULING_CANNOT_PLACE;
import static cn.cheers.x.module.platform.scheduling.enums.ErrorCodeConstants.SCHEDULING_INVALID_PLACEMENT_PREFERENCE;
import static cn.cheers.x.module.platform.scheduling.enums.ErrorCodeConstants.SCHEDULING_SHIFT_RANGE_REQUIRED;

/**
 * 日历插入：计划窗能放进空闲区间就插入；空闲不够时，按策略决定能否挪已有任务腾位置。
 * <p>怎么挪由冲突方向决定：与前面冲突则把前面的已有任务提前，与后面冲突则把后面的延后。
 * <p>不管：排期模板几点执行；从哪张台账拉候选设备。
 * <p>禁止：空闲不够时去挪新任务冒充插入；策略未允许时挪已有任务。
 */
final class SchedulingConflictPlacer {

    static final String PREFERENCE_WINDOW = "window_first";
    static final String PREFERENCE_RESOURCE = "resource_first";
    static final String STRATEGY_REJECT = "reject_batch";

    private SchedulingConflictPlacer() {
    }

    static String normalizePreference(String raw) {
        if (!StringUtils.hasText(raw)) {
            return PREFERENCE_WINDOW;
        }
        String normalized = raw.trim().toLowerCase();
        if (PREFERENCE_WINDOW.equals(normalized) || PREFERENCE_RESOURCE.equals(normalized)) {
            return normalized;
        }
        throw exception(SCHEDULING_INVALID_PLACEMENT_PREFERENCE);
    }

    static List<String> candidateIds(List<ResourceRequirementDTO> requirements) {
        LinkedHashSet<String> ids = new LinkedHashSet<>();
        if (requirements == null) {
            return List.of();
        }
        for (ResourceRequirementDTO req : requirements) {
            if (req == null) {
                continue;
            }
            if (StringUtils.hasText(req.getFixedResourceId())) {
                ids.add(req.getFixedResourceId().trim());
            }
            if (req.getCandidateResourceIds() != null) {
                for (String id : req.getCandidateResourceIds()) {
                    if (StringUtils.hasText(id)) {
                        ids.add(id.trim());
                    }
                }
            }
        }
        return new ArrayList<>(ids);
    }

    static Placement place(String conflictStrategy, String preference,
                           boolean allowShiftExisting, Integer maxShiftMinutes,
                           OffsetDateTime preferred, int minutes,
                           List<String> candidateResourceIds,
                           Map<String, List<TimelineBlock>> timelines,
                           OffsetDateTime earliestStart, OffsetDateTime horizonLimit) {
        if (candidateResourceIds.isEmpty()) {
            candidateResourceIds = List.of("__unassigned__");
        }
        OffsetDateTime preferredEnd = preferred.plusMinutes(minutes);
        if (PREFERENCE_RESOURCE.equals(preference)) {
            for (String resourceId : candidateResourceIds) {
                List<TimelineBlock> busy = timelines.computeIfAbsent(resourceId, key -> new ArrayList<>());
                SchedulingConflictDiagnosis diagnosis = SchedulingConflictDiagnosis.diagnose(
                        preferred, preferredEnd, busy, earliestStart, horizonLimit);
                if (diagnosis.preferredFits()) {
                    return new Placement(resourceId, preferred, diagnosis, List.of());
                }
            }
            throw exception(STRATEGY_REJECT.equals(conflictStrategy)
                    ? SCHEDULING_BATCH_REJECTED
                    : SCHEDULING_CANNOT_PLACE);
        }
        String resourceId = candidateResourceIds.get(0);
        List<TimelineBlock> busy = timelines.computeIfAbsent(resourceId, key -> new ArrayList<>());
        SchedulingConflictDiagnosis diagnosis = SchedulingConflictDiagnosis.diagnose(
                preferred, preferredEnd, busy, earliestStart, horizonLimit);
        if (diagnosis.preferredFits()) {
            return new Placement(resourceId, preferred, diagnosis, List.of());
        }
        if (STRATEGY_REJECT.equals(conflictStrategy) || !allowShiftExisting) {
            throw exception(STRATEGY_REJECT.equals(conflictStrategy)
                    ? SCHEDULING_BATCH_REJECTED
                    : SCHEDULING_CANNOT_PLACE);
        }
        if (maxShiftMinutes == null) {
            throw exception(SCHEDULING_SHIFT_RANGE_REQUIRED);
        }
        List<ResourceReservationDTO> shifted = shiftExisting(
                diagnosis, busy, preferred, preferredEnd, Math.max(0, maxShiftMinutes),
                earliestStart, horizonLimit);
        return new Placement(resourceId, preferred, diagnosis, shifted);
    }

    /**
     * 与前面冲突 → 前面的已有任务提前；与后面冲突 → 后面的已有任务延后。
     * 两边都挡着或空闲不够时两边都挪，各自不超过最大挪动范围。
     */
    private static List<ResourceReservationDTO> shiftExisting(
            SchedulingConflictDiagnosis diagnosis, List<TimelineBlock> busy,
            OffsetDateTime preferredStart, OffsetDateTime preferredEnd, int maxShiftMinutes,
            OffsetDateTime earliestStart, OffsetDateTime horizonLimit) {
        int shiftPrev = 0;
        int shiftNext = 0;
        if (diagnosis.previous() != null && preferredStart.isBefore(diagnosis.previous().blockedEnd())) {
            shiftPrev = minutesBetween(diagnosis.previous().blockedEnd(), preferredStart);
        }
        if (diagnosis.next() != null && preferredEnd.isAfter(diagnosis.next().start())) {
            shiftNext = minutesBetween(preferredEnd, diagnosis.next().start());
        }
        if (shiftPrev > maxShiftMinutes || shiftNext > maxShiftMinutes) {
            throw exception(SCHEDULING_CANNOT_PLACE);
        }
        if (shiftPrev == 0 && shiftNext == 0) {
            throw exception(SCHEDULING_CANNOT_PLACE);
        }
        List<ResourceReservationDTO> shifted = new ArrayList<>();
        if (shiftPrev > 0) {
            TimelineBlock previous = diagnosis.previous();
            OffsetDateTime newStart = previous.start().minusMinutes(shiftPrev);
            if (newStart.isBefore(earliestStart)) {
                throw exception(SCHEDULING_CANNOT_PLACE);
            }
            TimelineBlock before = diagnosis.previousOf(previous, busy);
            if (before != null && newStart.isBefore(before.blockedEnd())) {
                throw exception(SCHEDULING_CANNOT_PLACE);
            }
            previous.shiftEarlier(shiftPrev);
            if (previous.source() != null) {
                shifted.add(previous.source());
            }
        }
        if (shiftNext > 0) {
            TimelineBlock next = diagnosis.next();
            OffsetDateTime newEnd = next.end().plusMinutes(shiftNext);
            if (!newEnd.isBefore(horizonLimit)) {
                throw exception(SCHEDULING_CANNOT_PLACE);
            }
            TimelineBlock after = diagnosis.nextOf(next, busy);
            if (after != null && next.start().plusMinutes(shiftNext).isAfter(after.start())) {
                throw exception(SCHEDULING_CANNOT_PLACE);
            }
            next.shiftLater(shiftNext);
            if (next.source() != null) {
                shifted.add(next.source());
            }
        }
        SchedulingConflictDiagnosis after = SchedulingConflictDiagnosis.diagnose(
                preferredStart, preferredEnd, busy, earliestStart, horizonLimit);
        if (!after.preferredFits()) {
            throw exception(SCHEDULING_CANNOT_PLACE);
        }
        return shifted;
    }

    private static int minutesBetween(OffsetDateTime later, OffsetDateTime earlier) {
        return (int) Math.max(0, Duration.between(earlier, later).toMinutes());
    }

    record Placement(String resourceId, OffsetDateTime start,
                     SchedulingConflictDiagnosis diagnosis,
                     List<ResourceReservationDTO> shiftedExisting) {
    }
}
