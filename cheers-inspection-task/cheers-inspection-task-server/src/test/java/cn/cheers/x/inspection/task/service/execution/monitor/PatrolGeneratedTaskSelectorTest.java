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

    private static PatrolGeneratedTaskSelector.Candidate row(
            long id, String name, String status, String planned) {
        return new PatrolGeneratedTaskSelector.Candidate(
                id, name, status, DEVICE, OffsetDateTime.parse(planned));
    }
}
