package cn.cheers.x.module.platform.scheduling.engine;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 标准冲突判断：新任务计划窗相对同一设备已有占窗，是前面冲突、后面冲突，还是空闲不够。
 * <p>不管：是否允许挪已有任务、最多挪多久（那是策略）。
 */
final class SchedulingConflictDiagnosis {

    enum Side {
        /** 计划窗完全落在空闲区间，可直接插入 */
        NONE,
        /** 和新任务前面的已有任务重叠 */
        PREVIOUS,
        /** 和新任务后面的已有任务重叠 */
        NEXT,
        /** 前后都重叠 */
        BOTH,
        /** 未重叠但空闲区间短于本任务窗口 */
        INSUFFICIENT_GAP
    }

    private final TimelineBlock previous;
    private final TimelineBlock next;
    private final boolean overlapsPrevious;
    private final boolean overlapsNext;
    private final boolean preferredFits;
    private final long idleMinutes;
    private final Side side;

    private SchedulingConflictDiagnosis(TimelineBlock previous, TimelineBlock next,
                                        boolean overlapsPrevious, boolean overlapsNext,
                                        boolean preferredFits, long idleMinutes, Side side) {
        this.previous = previous;
        this.next = next;
        this.overlapsPrevious = overlapsPrevious;
        this.overlapsNext = overlapsNext;
        this.preferredFits = preferredFits;
        this.idleMinutes = idleMinutes;
        this.side = side;
    }

    /**
     * 在已按开始时刻排好的时间轴上，判断计划窗 [preferredStart, preferredEnd) 能否直接插入。
     */
    static SchedulingConflictDiagnosis diagnose(OffsetDateTime preferredStart, OffsetDateTime preferredEnd,
                                                List<TimelineBlock> busy,
                                                OffsetDateTime earliestStart, OffsetDateTime horizonLimit) {
        List<TimelineBlock> sorted = new ArrayList<>(busy == null ? List.of() : busy);
        sorted.sort(Comparator.comparing(TimelineBlock::start).thenComparing(TimelineBlock::end));
        TimelineBlock previous = null;
        TimelineBlock next = null;
        for (TimelineBlock block : sorted) {
            if (!block.start().isAfter(preferredStart)) {
                previous = block;
            } else {
                next = block;
                break;
            }
        }
        boolean overlapsPrevious = previous != null && preferredStart.isBefore(previous.end());
        boolean overlapsNext = next != null && next.start().isBefore(preferredEnd);
        OffsetDateTime idleStart = previous == null ? earliestStart : previous.blockedEnd();
        OffsetDateTime idleEnd = next == null ? horizonLimit : next.start();
        long idleMinutes = Math.max(0, Duration.between(idleStart, idleEnd).toMinutes());
        boolean preferredFits = !preferredStart.isBefore(idleStart) && !preferredEnd.isAfter(idleEnd);
        return new SchedulingConflictDiagnosis(previous, next, overlapsPrevious, overlapsNext,
                preferredFits, idleMinutes, resolveSide(preferredFits, overlapsPrevious, overlapsNext));
    }

    private static Side resolveSide(boolean preferredFits, boolean overlapsPrevious, boolean overlapsNext) {
        if (preferredFits) {
            return Side.NONE;
        }
        if (overlapsPrevious && overlapsNext) {
            return Side.BOTH;
        }
        if (overlapsPrevious) {
            return Side.PREVIOUS;
        }
        if (overlapsNext) {
            return Side.NEXT;
        }
        return Side.INSUFFICIENT_GAP;
    }

    TimelineBlock previous() {
        return previous;
    }

    TimelineBlock next() {
        return next;
    }

    boolean overlapsPrevious() {
        return overlapsPrevious;
    }

    boolean overlapsNext() {
        return overlapsNext;
    }

    boolean preferredFits() {
        return preferredFits;
    }

    long idleMinutes() {
        return idleMinutes;
    }

    Side side() {
        return side;
    }

    TimelineBlock previousOf(TimelineBlock target, List<TimelineBlock> busy) {
        TimelineBlock found = null;
        for (TimelineBlock block : busy) {
            if (block == target) {
                continue;
            }
            if (!block.start().isBefore(target.start())) {
                continue;
            }
            if (found == null || block.start().isAfter(found.start())) {
                found = block;
            }
        }
        return found;
    }

    TimelineBlock nextOf(TimelineBlock target, List<TimelineBlock> busy) {
        TimelineBlock found = null;
        for (TimelineBlock block : busy) {
            if (block == target) {
                continue;
            }
            if (!block.start().isAfter(target.start())) {
                continue;
            }
            if (found == null || block.start().isBefore(found.start())) {
                found = block;
            }
        }
        return found;
    }
}
