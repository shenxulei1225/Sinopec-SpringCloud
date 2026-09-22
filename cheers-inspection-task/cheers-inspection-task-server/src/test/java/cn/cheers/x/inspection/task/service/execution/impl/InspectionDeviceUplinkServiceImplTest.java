package cn.cheers.x.inspection.task.service.execution.impl;

import cn.cheers.x.device.protocolgateway.api.channel.AccessChannelCodes;
import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.device.protocolgateway.api.datacollection.ProtocolQualifyStatus;
import cn.cheers.x.device.protocolgateway.api.opcode.DeviceTaskStatusCode;
import cn.cheers.x.device.protocolgateway.api.opcode.TransportOpcode;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.service.execution.scheduleboard.PatrolScheduleSlotExecutionWritebackService;
import cn.cheers.x.module.dynamicbusiness.api.strategy.StrategyRuntimeApi;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyHandleRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyTriggerEventDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InspectionDeviceUplinkServiceImplTest {

    private StrategyRuntimeApi strategyRuntimeApi;
    private PatrolScheduleSlotExecutionWritebackService scheduleSlotExecutionWritebackService;
    private InspectionDeviceUplinkServiceImpl service;

    @BeforeEach
    void setUp() {
        strategyRuntimeApi = mock(StrategyRuntimeApi.class);
        scheduleSlotExecutionWritebackService = mock(PatrolScheduleSlotExecutionWritebackService.class);
        service = new InspectionDeviceUplinkServiceImpl(
                strategyRuntimeApi, new ObjectMapper(), scheduleSlotExecutionWritebackService);
        when(strategyRuntimeApi.handle(any())).thenReturn(CommonResult.success(matchedHandle()));
    }

    @Test
    void taskStatus_startSuccess_putsExecutionStatusOnEvent() {
        service.applyCollection(sample(
                AccessChannelCodes.INSPECTION,
                TransportOpcode.TASK_STATUS.code(),
                9001L,
                Map.of("status", DeviceTaskStatusCode.START_SUCCESS),
                ProtocolQualifyStatus.UNCHECKED));

        StrategyTriggerEventDTO event = captureEvent();
        assertEquals(StrategyTriggerEventDTO.EVENT_COLLECTION_RECEIVED, event.getEventType());
        assertEquals(9001L, event.getExecutionRecordId());
        assertEquals("in_progress", event.getExecutionStatus());
    }

    @Test
    void commandResult_putsStepBySequenceOnEvent() {
        service.applyCollection(sample(
                AccessChannelCodes.INSPECTION,
                TransportOpcode.COMMAND_RESULT.code(),
                9001L,
                Map.of("packages", Map.of("sequence", 3, "opcode", 200301, "result", Map.of())),
                ProtocolQualifyStatus.UNCHECKED));

        StrategyTriggerEventDTO event = captureEvent();
        assertEquals("seq-3", event.getStepUpdates().get(0).get("stepCode"));
        assertEquals("completed", event.getStepUpdates().get(0).get("status"));
    }

    @Test
    void commandResult_missingSequence_doesNotInventStep() {
        service.applyCollection(sample(
                AccessChannelCodes.INSPECTION,
                TransportOpcode.COMMAND_RESULT.code(),
                9001L,
                Map.of("packages", Map.of("opcode", 200301, "result", Map.of())),
                ProtocolQualifyStatus.UNCHECKED));
        StrategyTriggerEventDTO event = captureEvent();
        assertTrue(event.getStepUpdates() == null || event.getStepUpdates().isEmpty());
    }

    @Test
    void missingExecutionRecordId_stillAsksStrategy() {
        when(strategyRuntimeApi.handle(any())).thenReturn(CommonResult.success(skippedHandle()));
        service.applyCollection(sample(
                AccessChannelCodes.INSPECTION,
                TransportOpcode.TASK_STATUS.code(),
                null,
                Map.of("status", DeviceTaskStatusCode.START_SUCCESS),
                ProtocolQualifyStatus.UNCHECKED));
        StrategyTriggerEventDTO event = captureEvent();
        assertEquals(StrategyTriggerEventDTO.EVENT_COLLECTION_RECEIVED, event.getEventType());
        assertEquals(null, event.getExecutionRecordId());
    }

    @Test
    void industrialChannel_skipsStrategy() {
        service.applyCollection(sample(
                AccessChannelCodes.INDUSTRIAL,
                TransportOpcode.TASK_STATUS.code(),
                9001L,
                Map.of("status", DeviceTaskStatusCode.START_SUCCESS),
                ProtocolQualifyStatus.UNCHECKED));
        verify(strategyRuntimeApi, never()).handle(any());
    }

    @Test
    void unqualified_asksStrategy_withoutStepOrStatus() {
        service.applyCollection(sample(
                AccessChannelCodes.INSPECTION,
                TransportOpcode.TASK_STATUS.code(),
                9001L,
                Map.of("status", DeviceTaskStatusCode.START_SUCCESS),
                ProtocolQualifyStatus.UNQUALIFIED));
        StrategyTriggerEventDTO event = captureEvent();
        assertEquals(null, event.getExecutionStatus());
        assertTrue(event.getStepUpdates() == null || event.getStepUpdates().isEmpty());
    }

    @Test
    void strategyFailure_isVisible() {
        when(strategyRuntimeApi.handle(any())).thenReturn(CommonResult.error(500, "采集结果交给策略失败"));
        assertThrows(ServiceException.class, () -> service.applyCollection(sample(
                AccessChannelCodes.INSPECTION,
                TransportOpcode.TASK_STATUS.code(),
                9001L,
                Map.of("status", DeviceTaskStatusCode.START_SUCCESS),
                ProtocolQualifyStatus.UNCHECKED)));
    }

    private StrategyTriggerEventDTO captureEvent() {
        ArgumentCaptor<StrategyTriggerEventDTO> captor =
                ArgumentCaptor.forClass(StrategyTriggerEventDTO.class);
        verify(strategyRuntimeApi).handle(captor.capture());
        return captor.getValue();
    }

    private static StrategyHandleRespDTO matchedHandle() {
        StrategyHandleRespDTO resp = new StrategyHandleRespDTO();
        resp.setMatched(true);
        resp.setActionName("往执行账里记一条过程");
        resp.setProcessEntryCount(1);
        return resp;
    }

    private static StrategyHandleRespDTO skippedHandle() {
        StrategyHandleRespDTO resp = new StrategyHandleRespDTO();
        resp.setMatched(false);
        resp.setSkipReason("没有这次执行的账本编号，不记过程");
        return resp;
    }

    private static CollectionSample sample(
            String channel,
            int opcode,
            Long executionRecordId,
            Map<String, Object> fields,
            ProtocolQualifyStatus qualify
    ) {
        return new CollectionSample(
                channel,
                "SN-1",
                String.valueOf(opcode),
                qualify,
                qualify == ProtocolQualifyStatus.UNQUALIFIED ? List.of("缺必填") : List.of(),
                fields,
                executionRecordId,
                "m1",
                1L);
    }
}
