package cn.cheers.x.bpm.api.task;

import cn.cheers.x.bpm.api.task.dto.BpmActivityNodeRespDTO;
import cn.cheers.x.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.cheers.x.bpm.controller.admin.task.vo.task.BpmTaskApproveReqVO;
import cn.cheers.x.bpm.service.task.BpmProcessInstanceService;
import cn.cheers.x.bpm.service.task.BpmTaskService;
import cn.cheers.x.framework.common.pojo.CommonResult;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.task.api.Task;
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
class BpmProcessInstanceApiImplTest {

    @InjectMocks
    private BpmProcessInstanceApiImpl api;

    @Mock
    private BpmProcessInstanceService processInstanceService;
    @Mock
    private BpmTaskService bpmTaskService;
    @Mock
    private RuntimeService runtimeService;
    @Mock
    private ProcessInstanceQuery processInstanceQuery;
    @Mock
    private ProcessInstance processInstance;
    @Mock
    private Task task;

    @Test
    void getRunningTasksByBusinessKey_noInstance_returnsEmpty() {
        when(runtimeService.createProcessInstanceQuery()).thenReturn(processInstanceQuery);
        when(processInstanceQuery.processInstanceBusinessKey("emergency-event:1")).thenReturn(processInstanceQuery);
        when(processInstanceQuery.singleResult()).thenReturn(null);

        CommonResult<List<BpmActivityNodeRespDTO>> result =
                api.getRunningTasksByBusinessKey("emergency-event:1");

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertTrue(result.getData().isEmpty());
        verifyNoInteractions(bpmTaskService);
    }

    @Test
    void getRunningTasksByBusinessKey_mapsActiveTasks() {
        when(runtimeService.createProcessInstanceQuery()).thenReturn(processInstanceQuery);
        when(processInstanceQuery.processInstanceBusinessKey("emergency-event:9")).thenReturn(processInstanceQuery);
        when(processInstanceQuery.singleResult()).thenReturn(processInstance);
        when(processInstance.getId()).thenReturn("pi-9");
        when(task.getId()).thenReturn("task-1");
        when(task.getTaskDefinitionKey()).thenReturn("event_confirm");
        when(task.getName()).thenReturn("确认");
        when(task.getProcessInstanceId()).thenReturn("pi-9");
        when(bpmTaskService.getRunningTaskListByProcessInstanceId("pi-9", null, null))
                .thenReturn(Collections.singletonList(task));

        CommonResult<List<BpmActivityNodeRespDTO>> result =
                api.getRunningTasksByBusinessKey("emergency-event:9");

        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
        BpmActivityNodeRespDTO node = result.getData().get(0);
        assertEquals("task-1", node.getTaskId());
        assertEquals("event_confirm", node.getTaskDefinitionKey());
        assertEquals("确认", node.getName());
        assertEquals("pi-9", node.getProcessInstanceId());
    }

    @Test
    void approveTask_delegatesToBpmTaskService() {
        BpmTaskApproveReqDTO reqDTO = new BpmTaskApproveReqDTO();
        reqDTO.setId("task-2");
        reqDTO.setReason("ok");
        reqDTO.setVariables(Map.of("eventId", 2L));

        CommonResult<Boolean> result = api.approveTask(100L, reqDTO);

        assertTrue(result.isSuccess());
        assertEquals(Boolean.TRUE, result.getData());
        ArgumentCaptor<BpmTaskApproveReqVO> captor = ArgumentCaptor.forClass(BpmTaskApproveReqVO.class);
        verify(bpmTaskService).approveTask(eq(100L), captor.capture());
        assertEquals("task-2", captor.getValue().getId());
        assertEquals("ok", captor.getValue().getReason());
    }

}
