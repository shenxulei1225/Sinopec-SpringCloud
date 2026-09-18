package cn.cheers.x.inspection.task.service.execution.impl;

import cn.cheers.x.device.protocolgateway.api.dto.MissionStartRespDTO;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.execution.steptree.EntityRpcTaskStepTreeCatalog;
import cn.cheers.x.inspection.task.service.execution.steptree.HostSopParamPack;
import cn.cheers.x.inspection.task.service.execution.steptree.TaskStepNode;
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
import java.util.Optional;

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
    private EntityRpcTaskStepTreeCatalog catalog;
    private InspectionTaskStartExecutionServiceImpl service;

    @BeforeEach
    void setUp() {
        store = mock(PatrolTaskEntityStore.class);
        strategyRuntimeApi = mock(StrategyRuntimeApi.class);
        catalog = mock(EntityRpcTaskStepTreeCatalog.class);
        service = new InspectionTaskStartExecutionServiceImpl(store, strategyRuntimeApi, catalog);
    }

    @Test
    void missingBinding_throws() {
        when(store.require(1L)).thenReturn(new PatrolTaskDraft(
                1L, "样例", "巡检", 1L, "ROBOT", null, null, null, null, null, "draft", 0, null, null));

        ServiceException ex = assertThrows(ServiceException.class, () -> service.startExecution(1L));
        assertTrue(ex.getMessage().contains("未绑定执行设备"));
    }

    @Test
    void missingStepTree_throws() {
        when(store.require(1L)).thenReturn(sampleDraft());
        when(catalog.requireStepTree(1L)).thenThrow(
                new ServiceException(400, "任务没有执行步骤图，请先在路径规划时生成步骤"));

        ServiceException ex = assertThrows(ServiceException.class, () -> service.startExecution(1L));
        assertTrue(ex.getMessage().contains("没有执行步骤图"));
        verify(strategyRuntimeApi, never()).handle(any());
    }

    @Test
    void startExecution_publishesPreparedStartEvent() {
        when(store.require(1L)).thenReturn(sampleDraft());
        when(catalog.requireStepTree(1L)).thenReturn(List.of(
                new TaskStepNode("n-move", 1, null, "action", "act-arrive", 11L, "到达指定位置",
                        Map.of("location_ref", "SHOULD_NOT_USE")),
                new TaskStepNode("n-item", 2, null, "inspection_item", null, 7L, "检查阀", Map.of()),
                new TaskStepNode("n-human", 3, "n-item", "action", "act-human", 33L, "人工确认", Map.of())
        ));
        when(catalog.loadHostPack(eq(200L))).thenReturn(HostSopParamPack.empty());
        when(catalog.resolveActionId(any())).thenReturn(Optional.empty());
        when(strategyRuntimeApi.handle(any())).thenReturn(CommonResult.success(startedOk(88L)));

        MissionStartRespDTO result = service.startExecution(1L);

        assertTrue(result.success());
        ArgumentCaptor<StrategyTriggerEventDTO> captor = ArgumentCaptor.forClass(StrategyTriggerEventDTO.class);
        verify(strategyRuntimeApi).handle(captor.capture());
        StrategyTriggerEventDTO event = captor.getValue();
        assertEquals(StrategyTriggerEventDTO.EVENT_EXECUTION_START, event.getEventType());
        assertEquals("robot-ws", event.getProtocolVersion());
        assertEquals("SN-001", event.getLogicalDeviceId());
        assertEquals("任务准备", event.getSteps().get(0).get("name"));
        assertEquals(11L, ((Number) event.getDispatchActions().get(0).get("actionId")).longValue());
        verify(catalog).loadHostPack(200L);
        verify(catalog, never()).loadHostPack(99L);
    }

    @Test
    void dispatchFailed_returnsFailureWithoutPretendingStarted() {
        when(store.require(1L)).thenReturn(sampleDraft());
        when(catalog.requireStepTree(1L)).thenReturn(List.of(
                new TaskStepNode("n-move", 1, null, "action", "act-arrive", 11L, "到达指定位置", Map.of())
        ));
        when(catalog.loadHostPack(eq(200L))).thenReturn(HostSopParamPack.empty());
        StrategyHandleRespDTO handled = startedOk(88L);
        handled.setDispatchSuccess(false);
        handled.setDispatchFailureReason("设备当前未连接：SN-001");
        when(strategyRuntimeApi.handle(any())).thenReturn(CommonResult.success(handled));

        MissionStartRespDTO result = service.startExecution(1L);

        assertFalse(result.success());
        assertEquals("设备当前未连接：SN-001", result.failureReason());
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
                1L, "样例", "巡检", 8L, "ROBOT", content, binding, null, null, null, "draft", 2, null, null);
    }
}
