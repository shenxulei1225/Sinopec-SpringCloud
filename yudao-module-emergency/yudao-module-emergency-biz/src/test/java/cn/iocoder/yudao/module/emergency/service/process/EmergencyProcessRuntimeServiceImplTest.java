package cn.iocoder.yudao.module.emergency.service.process;

import cn.cheers.x.bpm.api.task.BpmProcessInstanceApi;
import cn.cheers.x.bpm.api.task.dto.BpmActivityNodeRespDTO;
import cn.cheers.x.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.cheers.x.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventMapper;
import cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmergencyProcessRuntimeServiceImplTest {

    @InjectMocks
    private EmergencyProcessRuntimeServiceImpl runtimeService;

    @Mock
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    @Mock
    private EmergencyEventMapper emergencyEventMapper;

    @Test
    void listCurrentNodes_withoutBinding_failsExplicitly() {
        EmergencyEventDO event = new EmergencyEventDO();
        event.setId(1L);
        event.setProcessInstanceId(null);
        when(emergencyEventMapper.selectById(1L)).thenReturn(event);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> runtimeService.listCurrentNodes(1L));
        assertEquals(ErrorCodeConstants.EVENT_PROCESS_NOT_STARTED.getCode(), ex.getCode());
        verifyNoInteractions(bpmProcessInstanceApi);
    }

    @Test
    void startOnEventCreated_persistsProcessInstanceId() {
        EmergencyEventDO event = new EmergencyEventDO();
        event.setId(9L);
        when(emergencyEventMapper.selectById(9L)).thenReturn(event);
        when(bpmProcessInstanceApi.createProcessInstance(eq(100L), any(BpmProcessInstanceCreateReqDTO.class)))
                .thenReturn(CommonResult.success("pi-9"));

        String piId = runtimeService.startOnEventCreated(9L, 100L);

        assertEquals("pi-9", piId);
        ArgumentCaptor<EmergencyEventDO> updateCaptor = ArgumentCaptor.forClass(EmergencyEventDO.class);
        verify(emergencyEventMapper).updateById(updateCaptor.capture());
        assertEquals(9L, updateCaptor.getValue().getId());
        assertEquals("pi-9", updateCaptor.getValue().getProcessInstanceId());

        ArgumentCaptor<BpmProcessInstanceCreateReqDTO> reqCaptor =
                ArgumentCaptor.forClass(BpmProcessInstanceCreateReqDTO.class);
        verify(bpmProcessInstanceApi).createProcessInstance(eq(100L), reqCaptor.capture());
        assertEquals(EmergencyProcessKeys.DEFINITION_KEY, reqCaptor.getValue().getProcessDefinitionKey());
        assertEquals(EmergencyProcessKeys.businessKey(9L), reqCaptor.getValue().getBusinessKey());
    }

    @Test
    void completeUserTask_whenTaskMissing_failsExplicitly() {
        EmergencyEventDO event = new EmergencyEventDO();
        event.setId(2L);
        event.setProcessInstanceId("pi-2");
        when(emergencyEventMapper.selectById(2L)).thenReturn(event);
        when(bpmProcessInstanceApi.getRunningTasksByBusinessKey(EmergencyProcessKeys.businessKey(2L)))
                .thenReturn(CommonResult.success(Collections.emptyList()));

        ServiceException ex = assertThrows(ServiceException.class,
                () -> runtimeService.completeUserTask(2L, 100L, EmergencyProcessKeys.TASK_CONFIRM, Map.of()));
        assertEquals(ErrorCodeConstants.EVENT_PROCESS_TASK_NOT_FOUND.getCode(), ex.getCode());
        verify(bpmProcessInstanceApi, never()).approveTask(anyLong(), any());
    }

    @Test
    void completeUserTask_approvesMatchedTask() {
        EmergencyEventDO event = new EmergencyEventDO();
        event.setId(3L);
        event.setProcessInstanceId("pi-3");
        when(emergencyEventMapper.selectById(3L)).thenReturn(event);

        BpmActivityNodeRespDTO node = new BpmActivityNodeRespDTO();
        node.setTaskId("task-confirm-1");
        node.setTaskDefinitionKey(EmergencyProcessKeys.TASK_CONFIRM);
        when(bpmProcessInstanceApi.getRunningTasksByBusinessKey(EmergencyProcessKeys.businessKey(3L)))
                .thenReturn(CommonResult.success(List.of(node)));
        when(bpmProcessInstanceApi.approveTask(eq(100L), any(BpmTaskApproveReqDTO.class)))
                .thenReturn(CommonResult.success(Boolean.TRUE));

        runtimeService.completeUserTask(3L, 100L, EmergencyProcessKeys.TASK_CONFIRM, Map.of("ok", true));

        ArgumentCaptor<BpmTaskApproveReqDTO> approveCaptor = ArgumentCaptor.forClass(BpmTaskApproveReqDTO.class);
        verify(bpmProcessInstanceApi).approveTask(eq(100L), approveCaptor.capture());
        assertEquals("task-confirm-1", approveCaptor.getValue().getId());
    }

}
