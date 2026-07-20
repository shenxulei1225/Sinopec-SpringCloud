package cn.cheers.x.module.platform.scheduling.engine;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.dto.work.ResourceRequirementDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SchedulingEngineImpl 冲突策略")
class SchedulingEngineImplConflictTest {

    private SchedulingEngineImpl engine;

    @BeforeEach
    void setUp() {
        engine = new SchedulingEngineImpl();
    }

    @Test
    void solve_missingDuration_throws() {
        WorkItemDTO work = WorkItemDTO.builder()
                .workId("w1")
                .entityTypeCode("patrol_task")
                .durationEstimateMinutes(null)
                .resourceRequirements(List.of(ResourceRequirementDTO.builder()
                        .resourceType("GROUND_ROBOT")
                        .fixedResourceId("robot-1")
                        .quantity(1)
                        .build()))
                .build();
        SchedulingSpecDTO spec = SchedulingSpecDTO.builder()
                .mode("once")
                .horizonStart("2026-07-21")
                .horizonEnd("2026-07-21")
                .conflictStrategy("defer_slot")
                .build();
        assertThrows(ServiceException.class,
                () -> engine.solve(List.of(work), spec, "job-1"));
    }

    @Test
    @DisplayName("defer_slot：同资源同日两项，第二项顺延到第一项之后")
    void solve_deferSlot_sameResource_defersSecond() {
        List<WorkItemDTO> works = List.of(
                work("w1", 60, null, "robot-1"),
                work("w2", 60, null, "robot-1"));
        SchedulingSpecDTO spec = onceSpec("defer_slot");

        List<ScheduleSlotDTO> slots = engine.solve(works, spec, "job-1");

        assertEquals(2, slots.size());
        Map<String, ScheduleSlotDTO> byWork = byWorkId(slots);
        OffsetDateTime end1 = OffsetDateTime.parse(byWork.get("w1").getPlannedEnd());
        OffsetDateTime start1 = OffsetDateTime.parse(byWork.get("w1").getPlannedStart());
        OffsetDateTime start2 = OffsetDateTime.parse(byWork.get("w2").getPlannedStart());
        assertEquals(start1.toLocalDate(), start2.toLocalDate(), "同日起始偏好");
        assertFalse(start2.isBefore(end1), "第二项 plannedStart >= 第一项 plannedEnd");
        assertAssigned(byWork.get("w1"), "robot-1", "GROUND_ROBOT");
        assertAssigned(byWork.get("w2"), "robot-1", "GROUND_ROBOT");
    }

    @Test
    @DisplayName("reject_batch：同资源重叠则整批失败")
    void solve_rejectBatch_overlap_throws() {
        List<WorkItemDTO> works = List.of(
                work("w1", 60, null, "robot-1"),
                work("w2", 60, null, "robot-1"));
        SchedulingSpecDTO spec = onceSpec("reject_batch");

        assertThrows(ServiceException.class,
                () -> engine.solve(works, spec, "job-1"));
    }

    @Test
    @DisplayName("priority_preempt：高优先级保留原窗，低优先级顺延")
    void solve_priorityPreempt_highKeepsWindow_lowDefers() {
        List<WorkItemDTO> works = List.of(
                work("w-low", 60, 1, "robot-1"),
                work("w-high", 60, 10, "robot-1"));
        SchedulingSpecDTO spec = onceSpec("priority_preempt");

        List<ScheduleSlotDTO> slots = engine.solve(works, spec, "job-1");

        Map<String, ScheduleSlotDTO> byWork = byWorkId(slots);
        OffsetDateTime highStart = OffsetDateTime.parse(byWork.get("w-high").getPlannedStart());
        OffsetDateTime highEnd = OffsetDateTime.parse(byWork.get("w-high").getPlannedEnd());
        OffsetDateTime lowStart = OffsetDateTime.parse(byWork.get("w-low").getPlannedStart());
        assertEquals(highStart.toLocalDate(), lowStart.toLocalDate());
        assertEquals(9, highStart.getHour(), "高优先级保留日起始窗");
        assertFalse(lowStart.isBefore(highEnd));
    }

    @Test
    @DisplayName("priority_preempt：低优先级超出 horizonEnd 则失败")
    void solve_priorityPreempt_beyondHorizon_throws() {
        List<WorkItemDTO> works = List.of(
                work("w-high", 600, 10, "robot-1"),
                work("w-low", 600, 1, "robot-1"));
        SchedulingSpecDTO spec = onceSpec("priority_preempt");

        assertThrows(ServiceException.class,
                () -> engine.solve(works, spec, "job-1"));
    }

    @Test
    @DisplayName("occupiedSlots 并入资源时间轴，defer 时避开已占窗")
    void solve_deferSlot_respectsOccupiedSlots() {
        WorkItemDTO work = work("w1", 60, null, "robot-1");
        SchedulingSpecDTO spec = onceSpec("defer_slot");
        OffsetDateTime occStart = LocalDate.of(2026, 7, 21)
                .atTime(LocalTime.of(9, 0))
                .atZone(ZoneId.systemDefault())
                .toOffsetDateTime();
        OffsetDateTime occEnd = occStart.plusMinutes(60);
        ScheduleSlotDTO occupied = ScheduleSlotDTO.builder()
                .slotId("occ-1")
                .workId("existing")
                .plannedStart(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(occStart))
                .plannedEnd(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(occEnd))
                .assignedResources(List.of(
                        cn.cheers.x.module.platform.contract.dto.slot.AssignedResourceDTO.builder()
                                .resourceId("robot-1")
                                .resourceType("GROUND_ROBOT")
                                .build()))
                .build();

        List<ScheduleSlotDTO> slots = engine.solve(List.of(work), spec, "job-1", List.of(occupied));

        assertEquals(1, slots.size());
        OffsetDateTime start = OffsetDateTime.parse(slots.get(0).getPlannedStart());
        assertFalse(start.isBefore(occEnd));
    }

    private static SchedulingSpecDTO onceSpec(String strategy) {
        return SchedulingSpecDTO.builder()
                .mode("once")
                .horizonStart("2026-07-21")
                .horizonEnd("2026-07-21")
                .conflictStrategy(strategy)
                .build();
    }

    private static WorkItemDTO work(String workId, int durationMinutes, Integer priority, String resourceId) {
        return WorkItemDTO.builder()
                .workId(workId)
                .entityTypeCode("patrol_task")
                .durationEstimateMinutes(durationMinutes)
                .priority(priority)
                .resourceRequirements(List.of(ResourceRequirementDTO.builder()
                        .resourceType("GROUND_ROBOT")
                        .fixedResourceId(resourceId)
                        .quantity(1)
                        .build()))
                .build();
    }

    private static Map<String, ScheduleSlotDTO> byWorkId(List<ScheduleSlotDTO> slots) {
        return slots.stream().collect(Collectors.toMap(ScheduleSlotDTO::getWorkId, Function.identity()));
    }

    private static void assertAssigned(ScheduleSlotDTO slot, String resourceId, String resourceType) {
        assertNotNull(slot.getAssignedResources());
        assertTrue(slot.getAssignedResources().stream().anyMatch(a ->
                resourceId.equals(a.getResourceId()) && resourceType.equals(a.getResourceType())));
    }
}
