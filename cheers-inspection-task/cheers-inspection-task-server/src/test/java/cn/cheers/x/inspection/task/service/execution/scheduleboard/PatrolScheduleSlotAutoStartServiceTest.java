package cn.cheers.x.inspection.task.service.execution.scheduleboard;

import cn.cheers.x.module.platform.contract.dto.reservation.ResourceReservationDTO;
import cn.cheers.x.module.platform.contract.enums.CandidateType;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PatrolScheduleSlotAutoStartServiceTest {

    @Test
    void triggerDue_atPlannedStart() {
        OffsetDateTime plannedStart = OffsetDateTime.parse("2026-09-18T10:00:00+08:00");
        OffsetDateTime now = plannedStart;
        ResourceReservationDTO reservation = reservation(
                SlotStatus.PLANNED, plannedStart, plannedStart.plusMinutes(30));
        assertTrue(PatrolScheduleSlotAutoStartService.isTriggerDue(reservation, now));
    }

    @Test
    void triggerDue_beforePlannedStart_false() {
        OffsetDateTime plannedStart = OffsetDateTime.parse("2026-09-18T10:00:00+08:00");
        OffsetDateTime now = plannedStart.minusMinutes(1);
        ResourceReservationDTO reservation = reservation(
                SlotStatus.PLANNED, plannedStart, plannedStart.plusMinutes(30));
        assertFalse(PatrolScheduleSlotAutoStartService.isTriggerDue(reservation, now));
    }

    @Test
    void triggerDue_afterPlannedEnd_false() {
        OffsetDateTime plannedStart = OffsetDateTime.parse("2026-09-18T10:00:00+08:00");
        OffsetDateTime plannedEnd = plannedStart.plusMinutes(30);
        OffsetDateTime now = plannedEnd.plusMinutes(1);
        ResourceReservationDTO reservation = reservation(SlotStatus.PLANNED, plannedStart, plannedEnd);
        assertFalse(PatrolScheduleSlotAutoStartService.isTriggerDue(reservation, now));
    }

    @Test
    void triggerDue_inProgress_false() {
        OffsetDateTime plannedStart = OffsetDateTime.parse("2026-09-18T10:00:00+08:00");
        ResourceReservationDTO reservation = reservation(
                SlotStatus.IN_PROGRESS, plannedStart, plannedStart.plusMinutes(30));
        assertFalse(PatrolScheduleSlotAutoStartService.isTriggerDue(reservation, plannedStart));
    }

    private static ResourceReservationDTO reservation(
            SlotStatus status, OffsetDateTime start, OffsetDateTime end) {
        return ResourceReservationDTO.builder()
                .candidateId("slot-1")
                .candidateType(CandidateType.TASK_EXECUTION)
                .candidateStatus(status)
                .plannedStart(start.toString())
                .plannedEnd(end.toString())
                .build();
    }
}
