package cn.cheers.x.module.platform.scheduling.conflict;

import cn.cheers.x.module.platform.contract.dto.reservation.ResourceReservationDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleConflictReportDTO;
import cn.cheers.x.module.platform.contract.dto.slot.AssignedResourceDTO;
import cn.cheers.x.module.platform.contract.dto.work.ResourceRequirementDTO;
import cn.cheers.x.module.platform.contract.dto.work.TimePreferencesDTO;
import cn.cheers.x.module.platform.contract.dto.work.TimeWindowDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduleConflictReporterTest {

    @Test
    void report_noOverlap_hasNoConflict() {
        WorkItemDTO planned = work("w1", "2026-09-21T17:00:00+08:00", "2026-09-21T18:00:00+08:00");
        ResourceReservationDTO occupied = occupied("2026-09-21T08:00:00+08:00", "2026-09-21T09:00:00+08:00");
        ScheduleConflictReportDTO report = ScheduleConflictReporter.report(List.of(planned), List.of(occupied), 0);
        assertFalse(Boolean.TRUE.equals(report.getHasConflict()));
        assertEquals(1, report.getPlannedCount());
        assertTrue(report.getDevices() == null || report.getDevices().isEmpty());
    }

    @Test
    void report_overlap_listsDeviceDay() {
        WorkItemDTO planned = work("w1", "2026-09-21T09:00:00+08:00", "2026-09-21T10:00:00+08:00");
        ResourceReservationDTO occupied = occupied("2026-09-21T08:00:00+08:00", "2026-09-21T09:30:00+08:00");
        ScheduleConflictReportDTO report = ScheduleConflictReporter.report(List.of(planned), List.of(occupied), 0);
        assertTrue(report.getHasConflict());
        assertEquals(1, report.getDevices().size());
        assertEquals("21", report.getDevices().get(0).getDays().get(0).getDate().substring(8));
        assertEquals(1, report.getDevices().get(0).getDays().get(0).getOverlaps().size());
    }

    private static WorkItemDTO work(String workId, String start, String end) {
        return WorkItemDTO.builder()
                .workId(workId)
                .estimatedDuration(60)
                .resourceRequirements(List.of(ResourceRequirementDTO.builder()
                        .resourceType("GROUND_ROBOT")
                        .fixedResourceId("101")
                        .build()))
                .timePreferences(TimePreferencesDTO.builder()
                        .allowedWindows(List.of(TimeWindowDTO.builder().start(start).end(end).build()))
                        .build())
                .build();
    }

    private static ResourceReservationDTO occupied(String start, String end) {
        return ResourceReservationDTO.builder()
                .workId("existing-morning")
                .candidateStart(start)
                .candidateEnd(end)
                .assignedResources(List.of(AssignedResourceDTO.builder()
                        .resourceId("101")
                        .resourceType("GROUND_ROBOT")
                        .build()))
                .build();
    }
}
