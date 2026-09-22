package cn.cheers.x.module.platform.scheduling.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("标准冲突判断：前面/后面/空闲不够")
class SchedulingConflictDiagnosisTest {

    @Test
    void diagnose_noBusy_fits() {
        OffsetDateTime start = at(9, 0);
        SchedulingConflictDiagnosis diagnosis = SchedulingConflictDiagnosis.diagnose(
                start, start.plusMinutes(60), List.of(), at(0, 0), at(23, 0));
        assertTrue(diagnosis.preferredFits());
        assertEquals(SchedulingConflictDiagnosis.Side.NONE, diagnosis.side());
        assertFalse(diagnosis.overlapsPrevious());
        assertFalse(diagnosis.overlapsNext());
    }

    @Test
    void diagnose_sameStartIsPrevious() {
        OffsetDateTime start = at(9, 0);
        TimelineBlock existing = new TimelineBlock(at(9, 0), at(10, 0), 0, null);
        SchedulingConflictDiagnosis diagnosis = SchedulingConflictDiagnosis.diagnose(
                start, start.plusMinutes(60), List.of(existing), at(0, 0), at(23, 0));
        assertFalse(diagnosis.preferredFits());
        assertTrue(diagnosis.overlapsPrevious());
        assertEquals(SchedulingConflictDiagnosis.Side.PREVIOUS, diagnosis.side());
    }

    @Test
    void diagnose_previousOverlap() {
        OffsetDateTime start = at(9, 30);
        TimelineBlock previous = new TimelineBlock(at(9, 0), at(10, 0), 0, null);
        SchedulingConflictDiagnosis diagnosis = SchedulingConflictDiagnosis.diagnose(
                start, start.plusMinutes(60), List.of(previous), at(0, 0), at(23, 0));
        assertFalse(diagnosis.preferredFits());
        assertTrue(diagnosis.overlapsPrevious());
        assertEquals(SchedulingConflictDiagnosis.Side.PREVIOUS, diagnosis.side());
    }

    @Test
    void diagnose_nextOverlap() {
        OffsetDateTime start = at(9, 30);
        TimelineBlock next = new TimelineBlock(at(10, 0), at(11, 0), 0, null);
        SchedulingConflictDiagnosis diagnosis = SchedulingConflictDiagnosis.diagnose(
                start, start.plusMinutes(60), List.of(next), at(0, 0), at(23, 0));
        assertFalse(diagnosis.preferredFits());
        assertTrue(diagnosis.overlapsNext());
        assertEquals(SchedulingConflictDiagnosis.Side.NEXT, diagnosis.side());
    }

    @Test
    void diagnose_gapTooSmall() {
        OffsetDateTime start = at(10, 0);
        TimelineBlock previous = new TimelineBlock(at(9, 0), at(10, 20), 0, null);
        TimelineBlock next = new TimelineBlock(at(10, 40), at(12, 0), 0, null);
        SchedulingConflictDiagnosis diagnosis = SchedulingConflictDiagnosis.diagnose(
                start, start.plusMinutes(60), List.of(previous, next), at(0, 0), at(23, 0));
        assertFalse(diagnosis.preferredFits());
        assertEquals(SchedulingConflictDiagnosis.Side.BOTH, diagnosis.side());
        assertEquals(20, diagnosis.idleMinutes());
    }

    private static OffsetDateTime at(int hour, int minute) {
        return LocalDate.of(2026, 7, 21)
                .atTime(LocalTime.of(hour, minute))
                .atZone(ZoneId.systemDefault())
                .toOffsetDateTime();
    }
}
