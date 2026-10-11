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

/**
 * 日历插入：原定时刻能放下就按原定时刻放。
 * 放不下时先把这次这条贴进够长的空窗口；不够再推后面的已有任务，仍不够才拉前面的。
 * 单侧不超过可挪动分钟数，前后一起用就是两倍。这一窗口仍不够就换下一个空窗口。
 * 换设备时先在原定时刻找空闲候选；各台都占满，再回到第一台按上面挪时间。
 * <p>不管：排期模板几点执行；从哪张台账拉候选设备。
 * <p>禁止：没算出足够空档就改已有任务；拒绝本批时离开原定时刻。
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
                           Integer maxShiftMinutes,
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
            return placeOnResource(conflictStrategy, candidateResourceIds.get(0), maxShiftMinutes,
                    preferred, minutes, timelines, earliestStart, horizonLimit);
        }
        return placeOnResource(conflictStrategy, candidateResourceIds.get(0), maxShiftMinutes,
                preferred, minutes, timelines, earliestStart, horizonLimit);
    }

    /**
     * 先试原定时刻所在的空窗口，放不下再试它后面的空窗口。
     * 每个窗口：够长只挪这次这条；不够先推后面，仍不够再拉前面。
     */
    private static Placement placeOnResource(String conflictStrategy, String resourceId,
                                             Integer maxShiftMinutes,
                                             OffsetDateTime preferred, int minutes,
                                             Map<String, List<TimelineBlock>> timelines,
                                             OffsetDateTime earliestStart, OffsetDateTime horizonLimit) {
        List<TimelineBlock> busy = timelines.computeIfAbsent(resourceId, key -> new ArrayList<>());
        OffsetDateTime preferredEnd = preferred.plusMinutes(minutes);
        SchedulingConflictDiagnosis diagnosis = SchedulingConflictDiagnosis.diagnose(
                preferred, preferredEnd, busy, earliestStart, horizonLimit);
        if (diagnosis.preferredFits()) {
            return new Placement(resourceId, preferred, diagnosis, List.of());
        }
        if (STRATEGY_REJECT.equals(conflictStrategy)) {
            throw exception(SCHEDULING_BATCH_REJECTED);
        }
        int max = maxShiftMinutes == null ? 0 : Math.max(0, maxShiftMinutes);
        for (IdleGap gap : gapsFromPreferred(busy, preferred, earliestStart, horizonLimit)) {
            Placement placed = tryGap(resourceId, gap, preferred, minutes, max, busy, earliestStart, horizonLimit);
            if (placed != null) {
                return placed;
            }
        }
        throw exception(SCHEDULING_CANNOT_PLACE);
    }

    /** 这次这条放在这个时刻是否完全落进空窗口，不挪已有任务。 */
    static boolean selfFits(OffsetDateTime start, int minutes, List<TimelineBlock> busy,
                             OffsetDateTime earliestStart, OffsetDateTime horizonLimit) {
        if (start == null || start.isBefore(earliestStart)) {
            return false;
        }
        OffsetDateTime end = start.plusMinutes(minutes);
        if (!end.isBefore(horizonLimit)) {
            return false;
        }
        return SchedulingConflictDiagnosis.diagnose(start, end, busy, earliestStart, horizonLimit).preferredFits();
    }

    private static Placement tryGap(String resourceId, IdleGap gap, OffsetDateTime preferred, int minutes,
                                    int maxShift, List<TimelineBlock> busy,
                                    OffsetDateTime earliestStart, OffsetDateTime horizonLimit) {
        OffsetDateTime self = selfStartInGap(gap, preferred, minutes, horizonLimit);
        if (self != null) {
            return placedAt(resourceId, self, minutes, busy, earliestStart, horizonLimit);
        }
        if (maxShift <= 0) {
            return null;
        }
        int length = minutesBetween(gap.end(), gap.start());
        int needed = Math.max(0, minutes - length);
        int push = pushRoom(gap.after(), busy, horizonLimit);
        int pull = pullRoom(gap.before(), busy, earliestStart);
        int pushBy = gap.after() == null ? 0 : Math.min(needed, Math.min(maxShift, push));
        int remain = needed - pushBy;
        int pullBy = gap.before() == null ? 0 : Math.min(remain, Math.min(maxShift, pull));
        if (pushBy + pullBy < needed) {
            return null;
        }
        List<ResourceReservationDTO> shifted = new ArrayList<>();
        if (pullBy > 0) {
            gap.before().shiftEarlier(pullBy);
            if (gap.before().source() != null) {
                shifted.add(gap.before().source());
            }
        }
        if (pushBy > 0) {
            gap.after().shiftLater(pushBy);
            if (gap.after().source() != null) {
                shifted.add(gap.after().source());
            }
        }
        OffsetDateTime start = gap.start().minusMinutes(pullBy);
        if (!selfFits(start, minutes, busy, earliestStart, horizonLimit)) {
            throw exception(SCHEDULING_CANNOT_PLACE);
        }
        return placedAt(resourceId, start, minutes, busy, earliestStart, horizonLimit, shifted);
    }

    private static OffsetDateTime selfStartInGap(IdleGap gap, OffsetDateTime preferred, int minutes,
                                                  OffsetDateTime horizonLimit) {
        if (minutesBetween(gap.end(), gap.start()) < minutes) {
            return null;
        }
        OffsetDateTime latest = gap.end().minusMinutes(minutes);
        if (!latest.plusMinutes(minutes).isBefore(horizonLimit) && !gap.end().isBefore(horizonLimit)) {
            latest = horizonLimit.minusMinutes(minutes + 1L);
        }
        if (latest.isBefore(gap.start())) {
            return null;
        }
        OffsetDateTime start = preferred;
        if (start.isBefore(gap.start())) {
            start = gap.start();
        }
        if (start.isAfter(latest)) {
            start = latest;
        }
        OffsetDateTime end = start.plusMinutes(minutes);
        if (start.isBefore(gap.start()) || end.isAfter(gap.end()) || !end.isBefore(horizonLimit)) {
            return null;
        }
        return start;
    }

    private static int pushRoom(TimelineBlock after, List<TimelineBlock> busy, OffsetDateTime horizonLimit) {
        if (after == null) {
            return 0;
        }
        TimelineBlock later = SchedulingConflictDiagnosis.nextOf(after, busy);
        OffsetDateTime limit = later == null ? horizonLimit : later.start();
        int untilNext = minutesBetween(limit, after.blockedEnd());
        int untilHorizon = minutesBetween(horizonLimit, after.end());
        if (untilHorizon > 0) {
            untilHorizon = untilHorizon - 1;
        }
        return Math.max(0, Math.min(untilNext, untilHorizon));
    }

    private static int pullRoom(TimelineBlock before, List<TimelineBlock> busy, OffsetDateTime earliestStart) {
        if (before == null) {
            return 0;
        }
        TimelineBlock earlier = SchedulingConflictDiagnosis.previousOf(before, busy);
        OffsetDateTime limit = earlier == null ? earliestStart : earlier.blockedEnd();
        return minutesBetween(before.start(), limit);
    }

    private static List<IdleGap> gapsFromPreferred(List<TimelineBlock> busy, OffsetDateTime preferred,
                                                    OffsetDateTime earliestStart, OffsetDateTime horizonLimit) {
        List<TimelineBlock> sorted = new ArrayList<>(busy);
        sorted.sort(java.util.Comparator.comparing(TimelineBlock::start).thenComparing(TimelineBlock::end));
        List<IdleGap> gaps = new ArrayList<>();
        OffsetDateTime cursor = earliestStart;
        TimelineBlock previous = null;
        for (TimelineBlock block : sorted) {
            if (block.start().isAfter(cursor)) {
                gaps.add(new IdleGap(cursor, block.start(), previous, block));
            }
            OffsetDateTime blocked = block.blockedEnd();
            if (blocked.isAfter(cursor)) {
                cursor = blocked;
            }
            previous = block;
        }
        if (cursor.isBefore(horizonLimit)) {
            gaps.add(new IdleGap(cursor, horizonLimit, previous, null));
        }
        List<IdleGap> ordered = new ArrayList<>();
        IdleGap current = null;
        for (IdleGap gap : gaps) {
            if (!preferred.isBefore(gap.start()) && preferred.isBefore(gap.end())) {
                current = gap;
                break;
            }
        }
        if (current == null) {
            for (IdleGap gap : gaps) {
                if (!gap.start().isBefore(preferred)) {
                    current = gap;
                    break;
                }
            }
        }
        if (current != null) {
            ordered.add(current);
            for (IdleGap gap : gaps) {
                if (gap.start().isAfter(current.start())) {
                    ordered.add(gap);
                }
            }
            return ordered;
        }
        return gaps;
    }

    private static Placement placedAt(String resourceId, OffsetDateTime start, int minutes,
                                       List<TimelineBlock> busy,
                                       OffsetDateTime earliestStart, OffsetDateTime horizonLimit) {
        return placedAt(resourceId, start, minutes, busy, earliestStart, horizonLimit, List.of());
    }

    private static Placement placedAt(String resourceId, OffsetDateTime start, int minutes,
                                       List<TimelineBlock> busy,
                                       OffsetDateTime earliestStart, OffsetDateTime horizonLimit,
                                       List<ResourceReservationDTO> shifted) {
        SchedulingConflictDiagnosis diagnosis = SchedulingConflictDiagnosis.diagnose(
                start, start.plusMinutes(minutes), busy, earliestStart, horizonLimit);
        return new Placement(resourceId, start, diagnosis, shifted);
    }

    private record IdleGap(OffsetDateTime start, OffsetDateTime end, TimelineBlock before, TimelineBlock after) {
    }

    private static int minutesBetween(OffsetDateTime later, OffsetDateTime earlier) {
        return (int) Math.max(0, Duration.between(earlier, later).toMinutes());
    }

    record Placement(String resourceId, OffsetDateTime start,
                     SchedulingConflictDiagnosis diagnosis,
                     List<ResourceReservationDTO> shiftedExisting) {
    }
}
