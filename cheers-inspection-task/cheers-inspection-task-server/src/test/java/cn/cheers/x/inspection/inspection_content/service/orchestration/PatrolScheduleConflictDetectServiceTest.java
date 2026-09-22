package cn.cheers.x.inspection.inspection_content.service.orchestration;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.inspection_content.service.orchestration.impl.PatrolScheduleConflictDetectService;
import cn.cheers.x.inspection.orchestration.dto.PatrolScheduleMapRespDTO;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleConflictReportDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.contract.dto.slot.AssignedResourceDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.dto.work.ResourceRequirementDTO;
import cn.cheers.x.module.platform.contract.dto.work.TimePreferencesDTO;
import cn.cheers.x.module.platform.contract.dto.work.TimeWindowDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import cn.cheers.x.module.platform.runtime.api.RuntimeQueryApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("排期冲突检测排除本任务残留占窗")
class PatrolScheduleConflictDetectServiceTest {

    private static final Long TASK_ID = 1L;
    private static final Long EQUIPMENT_ID = 88L;

    @Mock
    private PatrolScheduleMapService patrolScheduleMapService;

    @Mock
    private PatrolTaskEntityStore patrolTaskEntityStore;

    @Mock
    private RuntimeQueryApi runtimeQueryApi;

    private PatrolScheduleConflictDetectService detectService;

    @BeforeEach
    void setUp() {
        detectService = new PatrolScheduleConflictDetectService(
                patrolScheduleMapService, patrolTaskEntityStore, runtimeQueryApi);
    }

    @Test
    @DisplayName("本任务留下的占窗不报重叠")
    void detect_skipsOwnLeftoverOccupancy() {
        stubReadyDraftAndPlan();
        when(runtimeQueryApi.listSlots(any(), any(), eq(String.valueOf(EQUIPMENT_ID)), any(), any(), any()))
                .thenReturn(CommonResult.success(List.of(
                        leftover("patrol-reserve-1-0", "job-old"),
                        leftover("patrol-task-1-0", "job-failed"))));

        ScheduleConflictReportDTO report = detectService.detect(TASK_ID, horizon()).report();

        assertFalse(Boolean.TRUE.equals(report.getHasConflict()));
        assertEquals(0, report.getConflictCount() == null ? 0 : report.getConflictCount());
    }

    @Test
    @DisplayName("别人的占窗仍然报重叠")
    void detect_keepsForeignOccupancy() {
        stubReadyDraftAndPlan();
        ScheduleSlotDTO foreign = leftover("other-task-9-0", "job-other");
        when(runtimeQueryApi.listSlots(any(), any(), eq(String.valueOf(EQUIPMENT_ID)), any(), any(), any()))
                .thenReturn(CommonResult.success(List.of(foreign)));

        ScheduleConflictReportDTO report = detectService.detect(TASK_ID, horizon()).report();

        assertTrue(Boolean.TRUE.equals(report.getHasConflict()));
        assertTrue(report.getConflictCount() != null && report.getConflictCount() > 0);
    }

    private void stubReadyDraftAndPlan() {
        when(patrolTaskEntityStore.require(TASK_ID)).thenReturn(draft());
        when(patrolTaskEntityStore.readArrangePolicy(TASK_ID))
                .thenReturn(new PatrolTaskEntityStore.ArrangePolicy(null, 0, null, null));
        when(patrolScheduleMapService.expandPatrolWorkItems(any()))
                .thenReturn(PatrolScheduleMapRespDTO.builder()
                        .workItems(List.of(plannedItem()))
                        .build());
    }

    private static PatrolTaskDraft draft() {
        ExecutionDeviceBinding binding = new ExecutionDeviceBinding();
        binding.setEquipmentId(EQUIPMENT_ID);
        return new PatrolTaskDraft(
                TASK_ID, "草稿", "巡检", 7L, "ROBOT", null, binding, null,
                null, null, "draft", 3, null, null, null, Boolean.FALSE, null, null, null, null);
    }

    private static WorkItemDTO plannedItem() {
        return WorkItemDTO.builder()
                .workId("patrol-task-1-0")
                .estimatedDuration(1)
                .resourceRequirements(List.of(ResourceRequirementDTO.builder()
                        .resourceType("robot")
                        .fixedResourceId(String.valueOf(EQUIPMENT_ID))
                        .build()))
                .timePreferences(TimePreferencesDTO.builder()
                        .allowedWindows(List.of(TimeWindowDTO.builder()
                                .start("2026-09-01T09:00:00+08:00")
                                .end("2026-09-01T09:01:00+08:00")
                                .build()))
                        .build())
                .build();
    }

    private static ScheduleSlotDTO leftover(String workId, String jobId) {
        return ScheduleSlotDTO.builder()
                .slotId(workId)
                .runtimeJobId(jobId)
                .workId(workId)
                .plannedStart("2026-09-01T09:00:00+08:00")
                .plannedEnd("2026-09-01T09:01:00+08:00")
                .candidateStart("2026-09-01T09:00:00+08:00")
                .candidateEnd("2026-09-01T09:01:00+08:00")
                .slotStatus(SlotStatus.PLANNED)
                .assignedResources(List.of(AssignedResourceDTO.builder()
                        .resourceType("robot")
                        .resourceId(String.valueOf(EQUIPMENT_ID))
                        .build()))
                .build();
    }

    private static SchedulingSpecDTO horizon() {
        return SchedulingSpecDTO.builder()
                .horizonStart("2026-09-01")
                .horizonEnd("2026-09-30")
                .build();
    }
}
