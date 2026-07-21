package cn.cheers.x.inspection.inspection_content.service.orchestration;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolRouteRunReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolScheduleEnableReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolSlotWritebackReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolTaskPauseReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolTaskResumeReqVO;
import cn.cheers.x.inspection.inspection_content.service.orchestration.impl.PatrolRouteFacadeServiceImpl;
import cn.cheers.x.inspection.task.dal.dataobject.task.InspectionTaskDO;
import cn.cheers.x.inspection.task.dal.mysql.task.InspectionTaskMapper;
import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunRequest;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunResponse;
import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.contract.dto.slot.AssignedResourceDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import cn.cheers.x.module.platform.orchestration.api.ScheduleRunApi;
import cn.cheers.x.module.platform.orchestration.enums.OrchestrationRefs;
import cn.cheers.x.module.platform.orchestration.phase.OrchestrationPhase;
import cn.cheers.x.module.platform.runtime.api.RuntimeQueryApi;
import cn.cheers.x.module.platform.runtime.api.RuntimeSlotWriteApi;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimeSlotReleaseReqDTO;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimeSlotStatusUpdateReqDTO;
import cn.cheers.x.module.platform.runtime.enums.RuntimeSlotReleaseMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatrolRouteFacadeService 单元测试")
class PatrolRouteFacadeServiceTest {

    private static final Long FACILITY_ID = 7L;
    private static final Long TASK_ID = 99L;
    private static final List<Long> OBJECT_IDS = List.of(10L, 11L);
    private static final String RUNTIME_JOB_ID = "job-reserved";

    @Mock
    private ScheduleRunApi scheduleRunApi;

    @Mock
    private RuntimeQueryApi runtimeQueryApi;

    @Mock
    private RuntimeSlotWriteApi runtimeSlotWriteApi;

    @Mock
    private InspectionTaskMapper taskMapper;

    private PatrolRouteFacadeServiceImpl facadeService;

    @BeforeEach
    void setUp() {
        facadeService = new PatrolRouteFacadeServiceImpl();
        ReflectionTestUtils.setField(facadeService, "scheduleRunApi", scheduleRunApi);
        ReflectionTestUtils.setField(facadeService, "runtimeQueryApi", runtimeQueryApi);
        ReflectionTestUtils.setField(facadeService, "runtimeSlotWriteApi", runtimeSlotWriteApi);
        ReflectionTestUtils.setField(facadeService, "taskMapper", taskMapper);
    }

    @Test
    @DisplayName("preview：dryRun + stopAfter ROUTE + 种子 payload 键齐全")
    void previewRoute_buildsScheduleRunRequest() {
        PatrolRouteRunReqVO reqVO = routeReq("net-a", null);
        when(scheduleRunApi.run(any())).thenReturn(CommonResult.success(
                ScheduleRunResponse.builder()
                        .runtimeJobId("job-preview")
                        .routePreview(RoutePreviewDTO.builder().networkRef("net-a").build())
                        .workItems(List.of(WorkItemDTO.builder().workId("expanded-1").build()))
                        .build()));

        ScheduleRunResponse response = facadeService.previewRoute(reqVO);

        assertEquals("job-preview", response.getRuntimeJobId());
        assertEquals("net-a", response.getRoutePreview().getNetworkRef());

        ScheduleRunRequest captured = captureRunRequest();
        assertEquals(OrchestrationRefs.PATROL_ROUTE_PREVIEW_V1, captured.getOrchestrationRef());
        assertEquals(Boolean.TRUE, captured.getDryRun());
        assertEquals(OrchestrationPhase.ROUTE.name(), captured.getStopAfterPhase());
        assertSeedPayload(captured.getWorkItems().get(0).getPayload(), reqVO, false);
    }

    @Test
    @DisplayName("confirm：转发 taskId 到种子 payload")
    void confirmRoute_forwardsTaskId() {
        PatrolRouteRunReqVO reqVO = routeReq(null, TASK_ID);
        when(scheduleRunApi.run(any())).thenReturn(CommonResult.success(
                ScheduleRunResponse.builder().runtimeJobId("job-confirm").build()));

        facadeService.confirmRoute(reqVO);

        ScheduleRunRequest captured = captureRunRequest();
        assertEquals(OrchestrationRefs.PATROL_ROUTE_CONFIRM_V1, captured.getOrchestrationRef());
        assertEquals(Boolean.FALSE, captured.getDryRun());
        Map<String, Object> payload = captured.getWorkItems().get(0).getPayload();
        assertEquals(TASK_ID, payload.get("taskId"));
        assertSeedPayload(payload, reqVO, false);
    }

    @Test
    @DisplayName("confirm：缺 taskId 显式失败")
    void confirmRoute_missingTaskId_throws() {
        assertThrows(ServiceException.class, () -> facadeService.confirmRoute(routeReq(null, null)));
        verify(scheduleRunApi, never()).run(any());
    }

    @Test
    @DisplayName("reserve：taskEnabled=false，写入 runtimeJobId，任务 enabled=false")
    void reserveSchedule_buildsRequestAndUpdatesTask() {
        PatrolScheduleEnableReqVO reqVO = scheduleReq();
        when(taskMapper.selectById(TASK_ID)).thenReturn(taskWithRoute(false, null));
        when(scheduleRunApi.run(any())).thenReturn(CommonResult.success(
                ScheduleRunResponse.builder().runtimeJobId(RUNTIME_JOB_ID).build()));

        ScheduleRunResponse response = facadeService.reserveSchedule(reqVO);

        assertEquals(RUNTIME_JOB_ID, response.getRuntimeJobId());
        ScheduleRunRequest captured = captureRunRequest();
        assertEquals(OrchestrationRefs.PATROL_SCHEDULE_ENABLE_V1, captured.getOrchestrationRef());
        Map<String, Object> payload = captured.getWorkItems().get(0).getPayload();
        assertEquals(TASK_ID, payload.get("taskId"));
        assertEquals(Boolean.TRUE, payload.get("fromConfirmedSnapshot"));
        assertEquals(Boolean.FALSE, payload.get("taskEnabled"));

        ArgumentCaptor<InspectionTaskDO> taskCaptor = ArgumentCaptor.forClass(InspectionTaskDO.class);
        verify(taskMapper).updateById(taskCaptor.capture());
        assertFalse(taskCaptor.getValue().getEnabled());
        assertEquals(RUNTIME_JOB_ID, taskCaptor.getValue().getRuntimeJobId());
    }

    @Test
    @DisplayName("enable：验窗通过后 enabled=true，不再触发编排 solve")
    void enableSchedule_verifiesWindowAndEnablesTask() {
        PatrolScheduleEnableReqVO reqVO = scheduleReq();
        when(taskMapper.selectById(TASK_ID)).thenReturn(taskWithRoute(false, RUNTIME_JOB_ID));
        ScheduleSlotDTO ownSlot = slot("slot-a", RUNTIME_JOB_ID, "2026-07-21T08:00:00+08:00",
                "2026-07-21T09:00:00+08:00", "res-1");
        when(runtimeQueryApi.listSlotsByJobId(RUNTIME_JOB_ID))
                .thenReturn(CommonResult.success(List.of(ownSlot)))
                .thenReturn(CommonResult.success(List.of(ownSlot)));
        when(runtimeQueryApi.listSlots(any(), any(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(CommonResult.success(List.of(ownSlot)));

        ScheduleRunResponse response = facadeService.enableSchedule(reqVO);

        assertEquals(RUNTIME_JOB_ID, response.getRuntimeJobId());
        verify(scheduleRunApi, never()).run(any());
        ArgumentCaptor<InspectionTaskDO> taskCaptor = ArgumentCaptor.forClass(InspectionTaskDO.class);
        verify(taskMapper).updateById(taskCaptor.capture());
        assertTrue(taskCaptor.getValue().getEnabled());
    }

    @Test
    @DisplayName("enable：占用冲突显式失败")
    void enableSchedule_occupancyConflict_throws() {
        PatrolScheduleEnableReqVO reqVO = scheduleReq();
        when(taskMapper.selectById(TASK_ID)).thenReturn(taskWithRoute(false, RUNTIME_JOB_ID));
        ScheduleSlotDTO ownSlot = slot("slot-a", RUNTIME_JOB_ID, "2026-07-21T08:00:00+08:00",
                "2026-07-21T09:00:00+08:00", "res-1");
        ScheduleSlotDTO foreignSlot = slot("slot-b", "job-other", "2026-07-21T08:30:00+08:00",
                "2026-07-21T09:30:00+08:00", "res-1");
        when(runtimeQueryApi.listSlotsByJobId(RUNTIME_JOB_ID)).thenReturn(CommonResult.success(List.of(ownSlot)));
        when(runtimeQueryApi.listSlots(any(), any(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(CommonResult.success(List.of(ownSlot, foreignSlot)));

        assertThrows(ServiceException.class, () -> facadeService.enableSchedule(reqVO));
        verify(taskMapper, never()).updateById(any(InspectionTaskDO.class));
    }

    @Test
    @DisplayName("yield-pause：调用 releaseUnfinished YIELD_PAUSE")
    void yieldPause_callsReleaseUnfinished() {
        PatrolTaskPauseReqVO reqVO = pauseReq();
        when(taskMapper.selectById(TASK_ID)).thenReturn(taskWithRoute(false, RUNTIME_JOB_ID));
        when(runtimeSlotWriteApi.releaseUnfinished(any())).thenReturn(CommonResult.success(true));

        facadeService.yieldPause(reqVO);

        ArgumentCaptor<RuntimeSlotReleaseReqDTO> captor = ArgumentCaptor.forClass(RuntimeSlotReleaseReqDTO.class);
        verify(runtimeSlotWriteApi).releaseUnfinished(captor.capture());
        assertEquals(RuntimeSlotReleaseMode.YIELD_PAUSE, captor.getValue().getMode());
        assertEquals(RUNTIME_JOB_ID, captor.getValue().getRuntimeJobId());
    }

    @Test
    @DisplayName("resume：走 replan 模板并带 remainingStopIds")
    void resume_buildsReplanRequest() {
        PatrolTaskResumeReqVO reqVO = new PatrolTaskResumeReqVO();
        reqVO.setTaskId(TASK_ID);
        reqVO.setSourceRuntimeJobId(RUNTIME_JOB_ID);
        reqVO.setRemainingStopIds(List.of("stop-2", "stop-3"));
        reqVO.setSchedulingSpec(SchedulingSpecDTO.builder().mode("once").build());
        when(taskMapper.selectById(TASK_ID)).thenReturn(taskWithRoute(true, RUNTIME_JOB_ID));
        when(scheduleRunApi.run(any())).thenReturn(CommonResult.success(
                ScheduleRunResponse.builder().runtimeJobId("job-replan").build()));

        ScheduleRunResponse response = facadeService.resume(reqVO);

        assertEquals("job-replan", response.getRuntimeJobId());
        ScheduleRunRequest captured = captureRunRequest();
        assertEquals(OrchestrationRefs.PATROL_REPLAN_V1, captured.getOrchestrationRef());
        assertEquals(RUNTIME_JOB_ID, captured.getSourceRuntimeJobId());
        assertEquals(List.of("stop-2", "stop-3"), captured.getRemainingStopIds());
    }

    @Test
    @DisplayName("writeback：转发 updateSlotStatus")
    void writebackSlot_forwardsUpdate() {
        PatrolSlotWritebackReqVO reqVO = new PatrolSlotWritebackReqVO();
        reqVO.setSlotId("slot-1");
        reqVO.setSlotStatus(SlotStatus.COMPLETED);
        when(runtimeSlotWriteApi.updateSlotStatus(any())).thenReturn(CommonResult.success(true));

        facadeService.writebackSlot(reqVO);

        ArgumentCaptor<RuntimeSlotStatusUpdateReqDTO> captor =
                ArgumentCaptor.forClass(RuntimeSlotStatusUpdateReqDTO.class);
        verify(runtimeSlotWriteApi).updateSlotStatus(captor.capture());
        assertEquals("slot-1", captor.getValue().getSlotId());
        assertEquals(SlotStatus.COMPLETED, captor.getValue().getSlotStatus());
    }

    private ScheduleRunRequest captureRunRequest() {
        ArgumentCaptor<ScheduleRunRequest> captor = ArgumentCaptor.forClass(ScheduleRunRequest.class);
        verify(scheduleRunApi).run(captor.capture());
        return captor.getValue();
    }

    private static PatrolRouteRunReqVO routeReq(String preferredNetworkRef, Long taskId) {
        PatrolRouteRunReqVO reqVO = new PatrolRouteRunReqVO();
        reqVO.setFacilityId(FACILITY_ID);
        reqVO.setObjectIds(OBJECT_IDS);
        reqVO.setPreferredNetworkRef(preferredNetworkRef);
        reqVO.setTaskId(taskId);
        return reqVO;
    }

    private static PatrolScheduleEnableReqVO scheduleReq() {
        PatrolScheduleEnableReqVO reqVO = new PatrolScheduleEnableReqVO();
        reqVO.setTaskId(TASK_ID);
        reqVO.setSchedulingSpec(SchedulingSpecDTO.builder()
                .mode("weekly")
                .horizonStart("2026-07-21")
                .horizonEnd("2026-07-28")
                .conflictStrategy("priority_preempt")
                .build());
        return reqVO;
    }

    private static PatrolTaskPauseReqVO pauseReq() {
        PatrolTaskPauseReqVO reqVO = new PatrolTaskPauseReqVO();
        reqVO.setTaskId(TASK_ID);
        reqVO.setReason("yield for urgent task");
        return reqVO;
    }

    private static InspectionTaskDO taskWithRoute(boolean enabled, String runtimeJobId) {
        InspectionTaskDO task = new InspectionTaskDO();
        task.setId(TASK_ID);
        task.setRoutePlanId(1L);
        task.setEnabled(enabled);
        task.setRuntimeJobId(runtimeJobId);
        return task;
    }

    private static ScheduleSlotDTO slot(String slotId, String runtimeJobId, String start, String end,
                                         String resourceId) {
        return ScheduleSlotDTO.builder()
                .slotId(slotId)
                .runtimeJobId(runtimeJobId)
                .plannedStart(start)
                .plannedEnd(end)
                .slotStatus(SlotStatus.PLANNED)
                .assignedResources(List.of(AssignedResourceDTO.builder()
                        .resourceType("robot")
                        .resourceId(resourceId)
                        .build()))
                .build();
    }

    private static void assertSeedPayload(Map<String, Object> payload, PatrolRouteRunReqVO reqVO,
                                          boolean fromConfirmedSnapshot) {
        assertEquals(reqVO.getFacilityId(), payload.get("facilityId"));
        assertEquals(reqVO.getObjectIds(), payload.get("objectIds"));
        if (reqVO.getPreferredNetworkRef() != null) {
            assertEquals(reqVO.getPreferredNetworkRef(), payload.get("preferredNetworkRef"));
        }
        if (reqVO.getTaskId() != null) {
            assertEquals(reqVO.getTaskId(), payload.get("taskId"));
        }
        assertEquals(fromConfirmedSnapshot, payload.get("fromConfirmedSnapshot"));
    }
}
