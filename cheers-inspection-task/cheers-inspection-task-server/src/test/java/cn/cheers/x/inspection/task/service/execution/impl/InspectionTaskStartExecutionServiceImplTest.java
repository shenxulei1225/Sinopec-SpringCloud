package cn.cheers.x.inspection.task.service.execution.impl;

import cn.cheers.x.device.protocolgateway.api.dto.MissionStartRespDTO;
import cn.cheers.x.device.protocolgateway.api.mission.DispatchAction;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.execution.openrun.PatrolOpenRunMaterializeService;
import cn.cheers.x.inspection.task.service.execution.openrun.PatrolOpenRunMaterializeService.FrozenOpenRun;
import cn.cheers.x.inspection.task.service.execution.scheduleboard.PatrolScheduleSlotExecutionWritebackService;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.dynamicbusiness.api.strategy.StrategyRuntimeApi;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyHandleRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyTriggerEventDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InspectionTaskStartExecutionServiceImplTest {

    private PatrolTaskEntityStore store;
    private StrategyRuntimeApi strategyRuntimeApi;
    private PatrolOpenRunMaterializeService openRunMaterializeService;
    private PatrolScheduleSlotExecutionWritebackService scheduleSlotExecutionWritebackService;
    private InspectionTaskStartExecutionServiceImpl service;

    @BeforeEach
    void setUp() {
        store = mock(PatrolTaskEntityStore.class);
        strategyRuntimeApi = mock(StrategyRuntimeApi.class);
        openRunMaterializeService = mock(PatrolOpenRunMaterializeService.class);
        scheduleSlotExecutionWritebackService = mock(PatrolScheduleSlotExecutionWritebackService.class);
        service = new InspectionTaskStartExecutionServiceImpl(
                store, strategyRuntimeApi, openRunMaterializeService, scheduleSlotExecutionWritebackService);
        when(scheduleSlotExecutionWritebackService.resolveScheduleSlotId(any(), any())).thenReturn("slot-1");
    }

    @Test
    void missingBinding_throws() {
        when(store.require(1L)).thenReturn(new PatrolTaskDraft(
                1L, "样例", "巡检", 1L, "ROBOT", null, null, null, null, null, "draft", 0, null, null, null, null, null, null, null, null));
        when(openRunMaterializeService.requireFrozen(any(), eq("slot-1")))
                .thenThrow(new ServiceException(400, "任务未绑定执行设备（须含 equipmentId、protocolCode、logicalDeviceId）"));

        ServiceException ex = assertThrows(ServiceException.class, () -> service.startExecution(1L));
        assertTrue(ex.getMessage().contains("未绑定执行设备") || ex.getMessage().contains("待执行"));
    }

    @Test
    void missingFrozenSnapshot_throws() {
        when(store.require(1L)).thenReturn(sampleDraft());
        when(openRunMaterializeService.requireFrozen(any(), eq("slot-1")))
                .thenThrow(new ServiceException(400, "该计划点尚未准备好待执行记录，请先生成任务"));

        ServiceException ex = assertThrows(ServiceException.class, () -> service.startExecution(1L));
        assertTrue(ex.getMessage().contains("待执行") || ex.getMessage().contains("生成任务"));
        verify(strategyRuntimeApi, never()).handle(any());
    }

    @Test
    void startExecution_readsFrozenSnapshotAndDispatches() {
        PatrolTaskDraft draft = sampleDraft();
        when(store.require(1L)).thenReturn(draft);
        FrozenOpenRun frozen = new FrozenOpenRun(
                88L,
                draft.executionDeviceBinding(),
                List.of(new DispatchAction(11L, Map.of("angle", 10))),
                Map.of("dispatchActions", List.of(Map.of("actionId", 11L, "params", Map.of("angle", 10)))));
        when(openRunMaterializeService.requireFrozen(draft, "slot-1")).thenReturn(frozen);
        when(strategyRuntimeApi.handle(any())).thenReturn(CommonResult.success(startedOk(88L)));

        MissionStartRespDTO result = service.startExecution(1L);

        assertTrue(result.success());
        ArgumentCaptor<StrategyTriggerEventDTO> captor = ArgumentCaptor.forClass(StrategyTriggerEventDTO.class);
        verify(strategyRuntimeApi).handle(captor.capture());
        StrategyTriggerEventDTO event = captor.getValue();
        assertEquals(StrategyTriggerEventDTO.EVENT_EXECUTION_START, event.getEventType());
        assertEquals(88L, event.getExecutionRecordId());
        assertEquals("robot-ws", event.getProtocolVersion());
        assertEquals("SN-001", event.getLogicalDeviceId());
        assertEquals(11L, ((Number) event.getDispatchActions().get(0).get("actionId")).longValue());
        verify(openRunMaterializeService).requireFrozen(draft, "slot-1");
        verify(scheduleSlotExecutionWritebackService).markStarted("slot-1", 1L, 88L, 8L);
    }

    @Test
    void dispatchFailed_returnsFailureWithoutPretendingStarted() {
        PatrolTaskDraft draft = sampleDraft();
        when(store.require(1L)).thenReturn(draft);
        when(openRunMaterializeService.requireFrozen(any(), eq("slot-1"))).thenReturn(new FrozenOpenRun(
                88L, draft.executionDeviceBinding(), List.of(new DispatchAction(11L, Map.of())), Map.of()));
        StrategyHandleRespDTO handled = startedOk(88L);
        handled.setDispatchSuccess(false);
        handled.setDispatchFailureReason("设备当前未连接：SN-001");
        when(strategyRuntimeApi.handle(any())).thenReturn(CommonResult.success(handled));

        MissionStartRespDTO result = service.startExecution(1L);

        assertFalse(result.success());
        assertEquals("设备当前未连接：SN-001", result.failureReason());
        verify(scheduleSlotExecutionWritebackService, never()).markStarted(any(), any(), any(), any());
    }

    private static StrategyHandleRespDTO startedOk(Long recordId) {
        StrategyHandleRespDTO resp = new StrategyHandleRespDTO();
        resp.setMatched(true);
        resp.setExecutionRecordId(recordId);
        resp.setDispatchSuccess(true);
        resp.setDispatchOnline(true);
        resp.setDispatchCommandSent(true);
        resp.setDispatchStartupSent(true);
        resp.setActionName("更新这次执行的状态");
        return resp;
    }

    private static PatrolTaskDraft sampleDraft() {
        ExecutionDeviceBinding binding = new ExecutionDeviceBinding();
        binding.setEquipmentId(99L);
        binding.setProtocolCode("robot-ws");
        binding.setLogicalDeviceId("SN-001");
        InspectionContent content = new InspectionContent();
        InspectionContent.ObjectContent object = new InspectionContent.ObjectContent();
        object.setObjectId(200L);
        InspectionContent.ItemContent item = new InspectionContent.ItemContent();
        item.setItemId(7L);
        object.setItems(List.of(item));
        content.setCustomObjects(List.of(object));
        return new PatrolTaskDraft(
                1L, "样例", "巡检", 8L, "ROBOT", content, binding, null, null, null, "draft", 2, null, null, null, null, null, null, null, null);
    }
}
