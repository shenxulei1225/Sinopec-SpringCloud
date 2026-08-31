package cn.cheers.x.inspection.task.service.execution.impl;

import cn.cheers.x.device.protocolgateway.api.dto.DeviceUplinkEventDTO;
import cn.cheers.x.device.protocolgateway.api.opcode.DeviceTaskStatusCode;
import cn.cheers.x.device.protocolgateway.api.opcode.TransportOpcode;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.execution.TaskExecutionSessionApi;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionWritebackReqDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InspectionDeviceUplinkServiceImplTest {

    private TaskExecutionSessionApi sessionApi;
    private InspectionDeviceUplinkServiceImpl service;

    @BeforeEach
    void setUp() {
        sessionApi = mock(TaskExecutionSessionApi.class);
        service = new InspectionDeviceUplinkServiceImpl(sessionApi, new ObjectMapper());
        when(sessionApi.writeback(any())).thenReturn(CommonResult.success(true));
    }

    @Test
    void taskStatus_startSuccess_writebackInProgress() {
        String payload = "{\"taskId\":\"9001\",\"status\":%d}"
                .formatted(DeviceTaskStatusCode.START_SUCCESS);
        service.applyUplink(new DeviceUplinkEventDTO(
                "SN-1", TransportOpcode.TASK_STATUS.code(), payload, 1L));

        ArgumentCaptor<TaskExecutionWritebackReqDTO> captor =
                ArgumentCaptor.forClass(TaskExecutionWritebackReqDTO.class);
        verify(sessionApi).writeback(captor.capture());
        assertEquals(9001L, captor.getValue().getExecutionRecordId());
        assertEquals("in_progress", captor.getValue().getExecutionStatus());
    }

    @Test
    void commandResult_updatesStepByPointId() {
        String payload = """
                {"taskId":"9001","packages":{"reportPoint":{"pointId":"s1"},"result":{}}}
                """;
        service.applyUplink(new DeviceUplinkEventDTO(
                "SN-1", TransportOpcode.COMMAND_RESULT.code(), payload, 2L));

        ArgumentCaptor<TaskExecutionWritebackReqDTO> captor =
                ArgumentCaptor.forClass(TaskExecutionWritebackReqDTO.class);
        verify(sessionApi).writeback(captor.capture());
        assertEquals("s1", captor.getValue().getStepUpdates().get(0).getStepCode());
        assertEquals("completed", captor.getValue().getStepUpdates().get(0).getStatus());
    }

    @Test
    void missingTaskId_skipsWriteback() {
        service.applyUplink(new DeviceUplinkEventDTO(
                "SN-1", TransportOpcode.TASK_STATUS.code(), "{\"status\":300101}", 1L));
        verify(sessionApi, never()).writeback(any());
    }
}
