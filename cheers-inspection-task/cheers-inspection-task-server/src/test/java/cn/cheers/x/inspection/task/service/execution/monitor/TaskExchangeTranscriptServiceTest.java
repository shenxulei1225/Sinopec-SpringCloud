package cn.cheers.x.inspection.task.service.execution.monitor;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.api.dto.ReplayPoseReqDTO;
import cn.cheers.x.inspection.task.api.dto.TaskExchangeTranscriptReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.TaskExecutionSessionApi;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendProcessReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendProcessRespDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskExchangeTranscriptServiceTest {

    private PatrolGeneratedTaskLookup lookup;
    private TaskExecutionSessionApi sessionApi;
    private TaskExchangeTranscriptService service;

    @BeforeEach
    void setUp() {
        lookup = mock(PatrolGeneratedTaskLookup.class);
        sessionApi = mock(TaskExecutionSessionApi.class);
        service = new TaskExchangeTranscriptService(lookup, sessionApi, new ObjectMapper());
        when(sessionApi.appendProcess(any())).thenReturn(CommonResult.success(new TaskExecutionAppendProcessRespDTO()));
    }

    @Test
    void numericTaskIdWinsOverTheRunningRecord() {
        TaskExchangeTranscriptReqDTO req = inbound("{\"opcode\":500202,\"taskId\":\"99\"}");

        assertTrue(service.record(req).isRecorded());

        ArgumentCaptor<TaskExecutionAppendProcessReqDTO> captor = ArgumentCaptor.forClass(TaskExecutionAppendProcessReqDTO.class);
        verify(sessionApi).appendProcess(captor.capture());
        assertEquals(99L, captor.getValue().getExecutionRecordId());
        assertEquals(TaskExchangeTranscriptReqDTO.INBOUND, captor.getValue().getDirection());
        assertEquals(req.getPayloadJson(), captor.getValue().getPayloadJson());
        verify(lookup, never()).forExchange(any());
    }

    @Test
    void flightIdOnTheCloudPacketWins() {
        TaskExchangeTranscriptReqDTO req = inbound("{\"method\":\"flighttask_progress\",\"data\":{\"flight_id\":\"42\"}}");

        assertTrue(service.record(req).isRecorded());

        ArgumentCaptor<TaskExecutionAppendProcessReqDTO> captor = ArgumentCaptor.forClass(TaskExecutionAppendProcessReqDTO.class);
        verify(sessionApi).appendProcess(captor.capture());
        assertEquals(42L, captor.getValue().getExecutionRecordId());
        assertEquals("flighttask_progress", captor.getValue().getMessageKind());
        verify(lookup, never()).forExchange(any());
    }

    @Test
    void linkedRecordIdIsUsedWhenTheReplyHasNoFlightId() {
        TaskExchangeTranscriptReqDTO req = inbound("{\"method\":\"flighttask_execute\",\"tid\":\"t1\",\"data\":{\"result\":0}}");
        req.setExecutionRecordId(15L);

        assertTrue(service.record(req).isRecorded());

        ArgumentCaptor<TaskExecutionAppendProcessReqDTO> captor = ArgumentCaptor.forClass(TaskExecutionAppendProcessReqDTO.class);
        verify(sessionApi).appendProcess(captor.capture());
        assertEquals(15L, captor.getValue().getExecutionRecordId());
        verify(lookup, never()).forExchange(any());
    }

    @Test
    void singleRunningRecordReceivesAPacketWithoutTaskId() {
        when(lookup.forExchange("dev-1")).thenReturn(PatrolGeneratedTaskSelector.RunningMatch.found(7L));
        TaskExchangeTranscriptReqDTO req = inbound("{\"opcode\":500103,\"templateId\":\"99\",\"deviceId\":\"other\"}");

        assertTrue(service.record(req).isRecorded());

        ArgumentCaptor<TaskExecutionAppendProcessReqDTO> captor = ArgumentCaptor.forClass(TaskExecutionAppendProcessReqDTO.class);
        verify(sessionApi).appendProcess(captor.capture());
        assertEquals(7L, captor.getValue().getExecutionRecordId());
        assertEquals("500103", captor.getValue().getMessageKind());
    }

    @Test
    void noneOrManyRunningRecordsAreNotAttached() {
        when(lookup.forExchange("dev-1")).thenReturn(
                PatrolGeneratedTaskSelector.RunningMatch.skipped("这台设备没有正在执行或已经到点的任务，这次往来未记入任务"));

        TaskExchangeTranscriptRespView none = view(service.record(inbound("{\"opcode\":500101}")));
        when(lookup.forExchange("dev-1")).thenReturn(
                PatrolGeneratedTaskSelector.RunningMatch.skipped("这台设备有多条正在执行的任务，这次往来未记入任务"));
        TaskExchangeTranscriptRespView many = view(service.record(inbound("{\"opcode\":500101}")));

        assertFalse(none.recorded());
        assertTrue(none.reason().contains("没有正在执行或已经到点"));
        assertFalse(many.recorded());
        assertTrue(many.reason().contains("多条"));
        verify(sessionApi, never()).appendProcess(any());
    }

    @Test
    void poseWithoutAUniqueRecordReturnsFalse() {
        when(lookup.forExchange("dev-1")).thenReturn(
                PatrolGeneratedTaskSelector.RunningMatch.skipped("这台设备有多条已经到点的任务，还没确定执行哪一条，这次往来未记入任务"));
        ReplayPoseReqDTO req = pose();

        assertFalse(service.recordPose(req));

        verify(sessionApi, never()).appendPose(any());
    }

    @Test
    void poseWithRecordIdReturnsTrueOnlyAfterWrite() {
        when(sessionApi.appendPose(any())).thenReturn(CommonResult.success(Boolean.TRUE));
        ReplayPoseReqDTO req = pose();
        req.setExecutionRecordId(15L);

        assertTrue(service.recordPose(req));

        verify(lookup, never()).forExchange(any());
    }

    private static ReplayPoseReqDTO pose() {
        ReplayPoseReqDTO req = new ReplayPoseReqDTO();
        req.setDeviceId("dev-1");
        req.setT(1L);
        req.setX(121.5);
        req.setY(31.2);
        req.setZ(40.0);
        return req;
    }

    private static TaskExchangeTranscriptReqDTO inbound(String payloadJson) {
        TaskExchangeTranscriptReqDTO req = new TaskExchangeTranscriptReqDTO();
        req.setDeviceId("dev-1");
        req.setDirection(TaskExchangeTranscriptReqDTO.INBOUND);
        req.setPayloadJson(payloadJson);
        req.setReceivedAtEpochMs(1L);
        return req;
    }

    private record TaskExchangeTranscriptRespView(boolean recorded, String reason) {
    }

    private static TaskExchangeTranscriptRespView view(
            cn.cheers.x.inspection.task.api.dto.TaskExchangeTranscriptRespDTO resp) {
        return new TaskExchangeTranscriptRespView(resp.isRecorded(), resp.getReason());
    }
}
