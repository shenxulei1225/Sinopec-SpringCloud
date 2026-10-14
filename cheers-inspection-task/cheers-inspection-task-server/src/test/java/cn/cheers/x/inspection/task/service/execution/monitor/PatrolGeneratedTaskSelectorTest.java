package cn.cheers.x.inspection.task.service.execution.monitor;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PatrolGeneratedTaskSelectorTest {

    private static final OffsetDateTime NOW = OffsetDateTime.parse("2026-10-10T20:00:00+08:00");
    private static final String DEVICE = "equipment-1";

    @Test
    void usesTheOnlyRunningRecord() {
        PatrolGeneratedTaskSelector.Candidate chosen = PatrolGeneratedTaskSelector.choose(List.of(
                row(1, "早班", "pending", "2026-10-10T09:00:00+08:00"),
                row(2, "正在做", "in_progress", "2026-10-10T18:00:00+08:00")
        ), DEVICE, NOW);

        assertEquals(2L, chosen.id());
    }

    @Test
    void usesLatestDuePendingWhenNoneIsRunning() {
        PatrolGeneratedTaskSelector.Candidate chosen = PatrolGeneratedTaskSelector.choose(List.of(
                row(10, "上午", "pending", "2026-10-10T09:00:00+08:00"),
                row(11, "傍晚", "pending", "2026-10-10T18:00:00+08:00"),
                row(12, "明天", "pending", "2026-10-11T09:00:00+08:00")
        ), DEVICE, NOW);

        assertEquals(11L, chosen.id());
    }

    @Test
    void refusesWhenNothingIsDue() {
        assertThrows(IllegalArgumentException.class, () -> PatrolGeneratedTaskSelector.choose(List.of(
                row(12, "明天", "pending", "2026-10-11T09:00:00+08:00")
        ), DEVICE, NOW));
    }

    @Test
    void runningOnlyUsesTheSingleInProgressAndIgnoresPending() {
        PatrolGeneratedTaskSelector.RunningMatch match = PatrolGeneratedTaskSelector.runningOnly(List.of(
                row(1, "待执行", "pending", "2026-10-10T09:00:00+08:00"),
                row(2, "正在做", "in_progress", "2026-10-10T18:00:00+08:00")
        ), DEVICE);

        assertEquals(2L, match.recordId());
    }

    @Test
    void runningOnlySkipsWhenNoneOrManyAreInProgress() {
        PatrolGeneratedTaskSelector.RunningMatch none = PatrolGeneratedTaskSelector.runningOnly(List.of(
                row(1, "待执行", "pending", "2026-10-10T09:00:00+08:00")
        ), DEVICE);
        PatrolGeneratedTaskSelector.RunningMatch many = PatrolGeneratedTaskSelector.runningOnly(List.of(
                row(2, "甲", "in_progress", "2026-10-10T18:00:00+08:00"),
                row(3, "乙", "in_progress", "2026-10-10T19:00:00+08:00")
        ), DEVICE);

        assertEquals(false, none.found());
        assertEquals(false, many.found());
        assertEquals(true, many.skipReason().contains("多条"));
    }

    @Test
    void exchangeUsesTheRunningRecordAheadOfADueOne() {
        PatrolGeneratedTaskSelector.RunningMatch match = PatrolGeneratedTaskSelector.forExchange(List.of(
                row(1, "到点", "pending", "2026-10-10T18:00:00+08:00"),
                row(2, "正在做", "in_progress", "2026-10-10T09:00:00+08:00")
        ), DEVICE, NOW);

        assertEquals(2L, match.recordId());
    }

    @Test
    void exchangeUsesTheOnlyDueRecordAndSkipsWhenSeveralAreDue() {
        PatrolGeneratedTaskSelector.RunningMatch one = PatrolGeneratedTaskSelector.forExchange(List.of(
                row(1, "到点", "pending", "2026-10-10T18:00:00+08:00"),
                row(4, "明天", "pending", "2026-10-11T09:00:00+08:00")
        ), DEVICE, NOW);
        PatrolGeneratedTaskSelector.RunningMatch many = PatrolGeneratedTaskSelector.forExchange(List.of(
                row(1, "上午", "pending", "2026-10-10T09:00:00+08:00"),
                row(2, "傍晚", "pending", "2026-10-10T18:00:00+08:00")
        ), DEVICE, NOW);

        assertEquals(1L, one.recordId());
        assertEquals(false, many.found());
        assertEquals(true, many.skipReason().contains("已经到点"));
    }

    private static PatrolGeneratedTaskSelector.Candidate row(
            long id, String name, String status, String planned) {
        return new PatrolGeneratedTaskSelector.Candidate(
                id, name, status, DEVICE, OffsetDateTime.parse(planned));
    }
}
