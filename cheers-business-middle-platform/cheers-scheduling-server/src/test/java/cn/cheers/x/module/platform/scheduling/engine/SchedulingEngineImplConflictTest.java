package cn.cheers.x.module.platform.scheduling.engine;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.platform.contract.dto.reservation.ResourceReservationDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.scheduling.enums.ErrorCodeConstants;
import cn.cheers.x.module.platform.contract.dto.work.ResourceRequirementDTO;
import cn.cheers.x.module.platform.contract.dto.work.TimePreferencesDTO;
import cn.cheers.x.module.platform.contract.dto.work.TimeWindowDTO;
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
                .estimatedDuration(null)
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
    @DisplayName("空闲不够且不允许挪已有：同资源两项计划窗重叠则失败")
    void solve_overlap_withoutAllowShift_throws() {
        List<WorkItemDTO> works = List.of(
                work("w1", 60, null, "robot-1"),
                work("w2", 60, null, "robot-1"));
        SchedulingSpecDTO spec = onceSpec("defer_slot");

        assertThrows(ServiceException.class, () -> engine.solve(works, spec, "job-1"));
    }

    @Test
    @DisplayName("与前面冲突且允许挪已有：前面任务提前，新任务仍在原计划窗")
    void solve_overlapPrevious_shiftsExistingEarlier() {
        List<WorkItemDTO> works = List.of(
                work("w1", 60, null, "robot-1"),
                work("w2", 60, null, "robot-1"));
        SchedulingSpecDTO spec = onceSpec("defer_slot");
        spec.setAllowShiftExisting(true);
        spec.setMaxShiftMinutes(60);

        List<ResourceReservationDTO> slots = engine.solve(works, spec, "job-1");

        Map<String, ResourceReservationDTO> byWork = byWorkId(slots);
        assertEquals(localDay(8, 0), OffsetDateTime.parse(byWork.get("w1").getCandidateStart()));
        assertEquals(localDay(9, 0), OffsetDateTime.parse(byWork.get("w2").getCandidateStart()));
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
    @DisplayName("priority_preempt：不允许挪已有时高优先级占原窗，低优先级插不进")
    void solve_priorityPreempt_highKeepsWindow_lowCannotInsert() {
        List<WorkItemDTO> works = List.of(
                work("w-low", 60, 1, "robot-1"),
                work("w-high", 60, 10, "robot-1"));
        SchedulingSpecDTO spec = onceSpec("priority_preempt");

        assertThrows(ServiceException.class, () -> engine.solve(works, spec, "job-1"));
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
    @DisplayName("unknown conflictStrategy 抛出无效策略错误")
    void solve_unknownConflictStrategy_throws() {
        List<WorkItemDTO> works = List.of(work("w1", 60, null, "robot-1"));
        SchedulingSpecDTO spec = onceSpec("bogus_strategy");

        ServiceException ex = assertThrows(ServiceException.class,
                () -> engine.solve(works, spec, "job-1"));
        assertEquals(ErrorCodeConstants.SCHEDULING_INVALID_CONFLICT_STRATEGY.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("blank conflictStrategy 仍走插入判断，空闲不够且未允许挪已有则失败")
    void solve_blankConflictStrategy_overlapWithoutAllow_throws() {
        List<WorkItemDTO> works = List.of(
                work("w1", 60, null, "robot-1"),
                work("w2", 60, null, "robot-1"));
        SchedulingSpecDTO spec = SchedulingSpecDTO.builder()
                .mode("once")
                .horizonStart("2026-07-21")
                .horizonEnd("2026-07-21")
                .conflictStrategy("  ")
                .build();

        assertThrows(ServiceException.class, () -> engine.solve(works, spec, "job-1"));
    }

    @Test
    @DisplayName("工作项已带计划时刻时按该时刻占窗，不再按 once 收成一条默认 9 点")
    void solve_usesPreferredStartFromTimePreferences() {
        WorkItemDTO morning = work("w-am", 60, null, "robot-1");
        morning.setTimePreferences(window("2026-09-01T09:00:00+08:00", "2026-09-01T10:00:00+08:00"));
        WorkItemDTO evening = work("w-pm", 60, null, "robot-1");
        evening.setTimePreferences(window("2026-09-01T18:00:00+08:00", "2026-09-01T19:00:00+08:00"));
        SchedulingSpecDTO spec = SchedulingSpecDTO.builder()
                .mode("once")
                .horizonStart("2026-09-01")
                .horizonEnd("2026-09-01")
                .conflictStrategy("defer_slot")
                .build();

        List<ResourceReservationDTO> slots = engine.solve(List.of(morning, evening), spec, "job-1");

        assertEquals(2, slots.size());
        Map<String, ResourceReservationDTO> byWork = byWorkId(slots);
        assertEquals(OffsetDateTime.parse("2026-09-01T09:00:00+08:00"),
                OffsetDateTime.parse(byWork.get("w-am").getCandidateStart()));
        assertEquals(OffsetDateTime.parse("2026-09-01T18:00:00+08:00"),
                OffsetDateTime.parse(byWork.get("w-pm").getCandidateStart()));
    }

    @Test
    @DisplayName("与后面冲突且允许挪已有：后面任务延后，新任务仍在原计划窗")
    void solve_overlapNext_shiftsExistingLater() {
        OffsetDateTime preferred = localDay(9, 30);
        WorkItemDTO work = work("w1", 60, null, "robot-1");
        work.setTimePreferences(window(
                DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(preferred),
                DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(preferred.plusMinutes(60))));
        SchedulingSpecDTO spec = onceSpec("defer_slot");
        spec.setAllowShiftExisting(true);
        spec.setMaxShiftMinutes(30);
        ResourceReservationDTO occupied = occupied("robot-1",
                DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(localDay(10, 0)),
                DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(localDay(11, 0)));

        List<ResourceReservationDTO> slots = engine.solve(List.of(work), spec, "job-1", List.of(occupied));

        ResourceReservationDTO inserted = slots.stream()
                .filter(item -> "w1".equals(item.getWorkId()))
                .findFirst()
                .orElseThrow();
        assertEquals(preferred, OffsetDateTime.parse(inserted.getCandidateStart()));
        assertEquals(localDay(10, 30), OffsetDateTime.parse(occupied.getCandidateStart()));
        assertAssigned(inserted, "robot-1", "GROUND_ROBOT");
    }

    @Test
    @DisplayName("前后都挡着且允许挪已有：前面提前、后面延后，新任务仍在原计划窗")
    void solve_overlapBoth_shiftsExistingBothSides() {
        OffsetDateTime preferred = localDay(9, 0);
        WorkItemDTO work = work("w1", 90, null, "robot-1");
        work.setTimePreferences(window(
                DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(preferred),
                DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(preferred.plusMinutes(90))));
        SchedulingSpecDTO spec = onceSpec("defer_slot");
        spec.setAllowShiftExisting(true);
        spec.setMaxShiftMinutes(30);
        ResourceReservationDTO previous = occupied("robot-1-prev",
                DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(localDay(8, 0)),
                DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(localDay(9, 30)));
        previous.setWorkId("existing-prev");
        previous.setCandidateId("occ-prev");
        previous.getAssignedResources().get(0).setResourceId("robot-1");
        ResourceReservationDTO next = occupied("robot-1-next",
                DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(localDay(10, 0)),
                DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(localDay(11, 0)));
        next.setWorkId("existing-next");
        next.setCandidateId("occ-next");
        next.getAssignedResources().get(0).setResourceId("robot-1");

        List<ResourceReservationDTO> slots = engine.solve(List.of(work), spec, "job-1", List.of(previous, next));

        ResourceReservationDTO inserted = slots.stream()
                .filter(item -> "w1".equals(item.getWorkId()))
                .findFirst()
                .orElseThrow();
        assertEquals(preferred, OffsetDateTime.parse(inserted.getCandidateStart()));
        assertEquals(localDay(7, 30), OffsetDateTime.parse(previous.getCandidateStart()));
        assertEquals(localDay(10, 30), OffsetDateTime.parse(next.getCandidateStart()));
    }

    @Test
    @DisplayName("需要挪动超过最大范围则失败")
    void solve_shiftExceedsMax_throws() {
        WorkItemDTO work = work("w1", 60, null, "robot-1");
        SchedulingSpecDTO spec = onceSpec("defer_slot");
        spec.setAllowShiftExisting(true);
        spec.setMaxShiftMinutes(15);
        ResourceReservationDTO occupied = occupied("robot-1",
                DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(localDay(9, 0)),
                DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(localDay(10, 0)));

        assertThrows(ServiceException.class,
                () -> engine.solve(List.of(work), spec, "job-1", List.of(occupied)));
    }

    @Test
    @DisplayName("允许挪已有但未填最大范围则报缺口")
    void solve_allowShiftWithoutMax_throws() {
        List<WorkItemDTO> works = List.of(
                work("w1", 60, null, "robot-1"),
                work("w2", 60, null, "robot-1"));
        SchedulingSpecDTO spec = onceSpec("defer_slot");
        spec.setAllowShiftExisting(true);

        ServiceException ex = assertThrows(ServiceException.class, () -> engine.solve(works, spec, "job-1"));
        assertEquals(ErrorCodeConstants.SCHEDULING_SHIFT_RANGE_REQUIRED.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("resource_first：计划时刻不动，改派同类空闲设备")
    void solve_resourceFirst_swapsPeerDevice() {
        OffsetDateTime preferred = localDay(9, 0);
        WorkItemDTO work = work("w1", 60, null, "robot-1");
        work.getResourceRequirements().get(0).setCandidateResourceIds(List.of("robot-1", "robot-2"));
        work.setTimePreferences(window(
                DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(preferred),
                DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(preferred.plusMinutes(60))));
        SchedulingSpecDTO spec = onceSpec("defer_slot");
        spec.setPlacementPreference("resource_first");
        ResourceReservationDTO occupied = occupied("robot-1",
                DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(preferred),
                DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(preferred.plusMinutes(60)));

        List<ResourceReservationDTO> slots = engine.solve(List.of(work), spec, "job-1", List.of(occupied));

        assertEquals(1, slots.size());
        assertEquals(preferred, OffsetDateTime.parse(slots.get(0).getCandidateStart()));
        assertAssigned(slots.get(0), "robot-2", "GROUND_ROBOT");
    }

    @Test
    @DisplayName("resource_first：候选设备计划窗都被占则失败，不改去挪时间")
    void solve_resourceFirst_allBusy_throws() {
        OffsetDateTime preferred = localDay(9, 0);
        WorkItemDTO work = work("w1", 60, null, "robot-1");
        work.getResourceRequirements().get(0).setCandidateResourceIds(List.of("robot-1", "robot-2"));
        work.setTimePreferences(window(
                DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(preferred),
                DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(preferred.plusMinutes(60))));
        SchedulingSpecDTO spec = onceSpec("defer_slot");
        spec.setPlacementPreference("resource_first");
        String start = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(preferred);
        String end = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(preferred.plusMinutes(60));
        List<ResourceReservationDTO> occupied = List.of(
                occupied("robot-1", start, end),
                occupied("robot-2", start, end));

        assertThrows(ServiceException.class,
                () -> engine.solve(List.of(work), spec, "job-1", occupied));
    }

    @Test
    @DisplayName("已占窗挡住计划窗且不允许挪已有：插入失败")
    void solve_occupiedOverlap_withoutAllow_throws() {
        WorkItemDTO work = work("w1", 60, null, "robot-1");
        SchedulingSpecDTO spec = onceSpec("defer_slot");
        ResourceReservationDTO occupied = occupied("robot-1",
                DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(localDay(9, 0)),
                DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(localDay(10, 0)));

        assertThrows(ServiceException.class,
                () -> engine.solve(List.of(work), spec, "job-1", List.of(occupied)));
    }

    private static SchedulingSpecDTO onceSpec(String strategy) {
        return SchedulingSpecDTO.builder()
                .mode("once")
                .horizonStart("2026-07-21")
                .horizonEnd("2026-07-21")
                .conflictStrategy(strategy)
                .build();
    }

    private static TimePreferencesDTO window(String start, String end) {
        return TimePreferencesDTO.builder()
                .allowedWindows(List.of(TimeWindowDTO.builder().start(start).end(end).build()))
                .build();
    }

    private static OffsetDateTime localDay(int hour, int minute) {
        return LocalDate.of(2026, 7, 21)
                .atTime(LocalTime.of(hour, minute))
                .atZone(ZoneId.systemDefault())
                .toOffsetDateTime();
    }

    private static ResourceReservationDTO occupied(String resourceId, String start, String end) {
        return ResourceReservationDTO.builder()
                .candidateId("occ-" + resourceId)
                .workId("existing-" + resourceId)
                .plannedStart(start)
                .plannedEnd(end)
                .candidateStart(start)
                .candidateEnd(end)
                .assignedResources(List.of(
                        cn.cheers.x.module.platform.contract.dto.slot.AssignedResourceDTO.builder()
                                .resourceId(resourceId)
                                .resourceType("GROUND_ROBOT")
                                .build()))
                .build();
    }

    private static WorkItemDTO work(String workId, int durationMinutes, Integer priority, String resourceId) {
        return WorkItemDTO.builder()
                .workId(workId)
                .entityTypeCode("patrol_task")
                .estimatedDuration(durationMinutes)
                .priority(priority)
                .resourceRequirements(List.of(ResourceRequirementDTO.builder()
                        .resourceType("GROUND_ROBOT")
                        .fixedResourceId(resourceId)
                        .quantity(1)
                        .build()))
                .build();
    }

    private static Map<String, ResourceReservationDTO> byWorkId(List<ResourceReservationDTO> slots) {
        return slots.stream().collect(Collectors.toMap(ResourceReservationDTO::getWorkId, Function.identity()));
    }

    private static void assertAssigned(ResourceReservationDTO slot, String resourceId, String resourceType) {
        assertNotNull(slot.getAssignedResources());
        assertTrue(slot.getAssignedResources().stream().anyMatch(a ->
                resourceId.equals(a.getResourceId()) && resourceType.equals(a.getResourceType())));
    }
}
