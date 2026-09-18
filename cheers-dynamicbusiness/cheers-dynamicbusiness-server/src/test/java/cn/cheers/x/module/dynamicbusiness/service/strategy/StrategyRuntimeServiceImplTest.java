package cn.cheers.x.module.dynamicbusiness.service.strategy;

import cn.cheers.x.alarm.api.AlarmNotifyApi;
import cn.cheers.x.alarm.api.AlarmTriggerApi;
import cn.cheers.x.alarm.api.dto.AlarmTriggerRespDTO;
import cn.cheers.x.device.protocolgateway.api.DeviceProtocolMissionApi;
import cn.cheers.x.device.protocolgateway.api.dto.MissionStartRespDTO;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendProcessRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyHandleRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyTriggerEventDTO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.strategy.ConditionStrategyDO;
import cn.cheers.x.module.dynamicbusiness.service.execution.TaskExecutionSessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StrategyRuntimeServiceImplTest {

    private ConditionStrategyService conditionStrategyService;
    private TaskExecutionSessionService sessionService;
    private AlarmTriggerApi alarmTriggerApi;
    private AlarmNotifyApi alarmNotifyApi;
    private DeviceProtocolMissionApi missionApi;
    private StrategyRuntimeServiceImpl service;

    @BeforeEach
    void setUp() {
        conditionStrategyService = mock(ConditionStrategyService.class);
        sessionService = mock(TaskExecutionSessionService.class);
        alarmTriggerApi = mock(AlarmTriggerApi.class);
        alarmNotifyApi = mock(AlarmNotifyApi.class);
        missionApi = mock(DeviceProtocolMissionApi.class);
        service = new StrategyRuntimeServiceImpl();
        ReflectionTestUtils.setField(service, "conditionStrategyService", conditionStrategyService);
        ReflectionTestUtils.setField(service, "taskExecutionSessionService", sessionService);
        ReflectionTestUtils.setField(service, "alarmTriggerApi", alarmTriggerApi);
        ReflectionTestUtils.setField(service, "alarmNotifyApi", alarmNotifyApi);
        ReflectionTestUtils.setField(service, "deviceProtocolMissionApi", missionApi);
    }

    @Test
    void handle_collectionWithRecord_appendsProcess() {
        when(conditionStrategyService.listVisibleEnabled(StrategyTriggerEventDTO.EVENT_COLLECTION_RECEIVED))
                .thenReturn(List.of(appendProcessStrategy()));
        TaskExecutionAppendProcessRespDTO written = new TaskExecutionAppendProcessRespDTO();
        written.setExecutionRecordId(9L);
        written.setProcessEntryCount(1);
        when(sessionService.appendProcess(any())).thenReturn(written);

        StrategyHandleRespDTO resp = service.handle(collectionEvent(9L));
        assertTrue(resp.isMatched());
        assertEquals("往执行账里记一条过程", resp.getActionName());
        assertEquals(1, resp.getProcessEntryCount());
        verify(sessionService).appendProcess(any());
        verify(alarmTriggerApi, never()).trigger(any());
    }

    @Test
    void handle_missingRecordId_skipsWithoutWrite() {
        when(conditionStrategyService.listVisibleEnabled(StrategyTriggerEventDTO.EVENT_COLLECTION_RECEIVED))
                .thenReturn(List.of(appendProcessStrategy()));
        StrategyHandleRespDTO resp = service.handle(collectionEvent(null));
        assertFalse(resp.isMatched());
        assertEquals("没有一条策略的条件成立", resp.getSkipReason());
        verify(sessionService, never()).appendProcess(any());
    }

    @Test
    void handle_emptyCatalog_skipsWithoutInventing() {
        when(conditionStrategyService.listVisibleEnabled(StrategyTriggerEventDTO.EVENT_COLLECTION_RECEIVED))
                .thenReturn(List.of());
        StrategyHandleRespDTO resp = service.handle(collectionEvent(9L));
        assertFalse(resp.isMatched());
        assertEquals("没有已启用的策略", resp.getSkipReason());
        verify(sessionService, never()).appendProcess(any());
    }

    @Test
    void handle_missingQualify_failsVisible() {
        when(conditionStrategyService.listVisibleEnabled(StrategyTriggerEventDTO.EVENT_COLLECTION_RECEIVED))
                .thenReturn(List.of(appendProcessStrategy()));
        StrategyTriggerEventDTO event = collectionEvent(9L);
        event.setProtocolQualify(null);
        assertThrows(ServiceException.class, () -> service.handle(event));
        verify(sessionService, never()).appendProcess(any());
    }

    @Test
    void handle_fieldCompareAlarm_raisesWithoutGuessingDevice() {
        when(conditionStrategyService.listVisibleEnabled(StrategyTriggerEventDTO.EVENT_COLLECTION_RECEIVED))
                .thenReturn(List.of(alarmStrategy()));
        AlarmTriggerRespDTO alarm = new AlarmTriggerRespDTO();
        alarm.setAlarmId(77L);
        alarm.setAlarmCode("ALM-1");
        when(alarmTriggerApi.trigger(any())).thenReturn(CommonResult.success(alarm));

        StrategyTriggerEventDTO event = collectionEvent(null);
        event.setFields(Map.of("pressure", 80));
        StrategyHandleRespDTO resp = service.handle(event);
        assertTrue(resp.isMatched());
        assertEquals("新增一条告警", resp.getActionName());
        assertEquals(77L, resp.getAlarmId());
        verify(alarmTriggerApi).trigger(any());
        verify(sessionService, never()).appendProcess(any());
    }

    @Test
    void handle_fieldCompareNotReached_skips() {
        when(conditionStrategyService.listVisibleEnabled(StrategyTriggerEventDTO.EVENT_COLLECTION_RECEIVED))
                .thenReturn(List.of(alarmStrategy()));
        StrategyTriggerEventDTO event = collectionEvent(null);
        event.setFields(Map.of("pressure", 10));
        StrategyHandleRespDTO resp = service.handle(event);
        assertFalse(resp.isMatched());
        verify(alarmTriggerApi, never()).trigger(any());
    }

    @Test
    void handle_alarmMissingConfiguredIds_failsVisible() {
        ConditionStrategyDO row = alarmStrategy();
        row.setActionParamsJson("{}");
        when(conditionStrategyService.listVisibleEnabled(StrategyTriggerEventDTO.EVENT_COLLECTION_RECEIVED))
                .thenReturn(List.of(row));
        StrategyTriggerEventDTO event = collectionEvent(null);
        event.setFields(Map.of("pressure", 80));
        assertThrows(ServiceException.class, () -> service.handle(event));
        verify(alarmTriggerApi, never()).trigger(any());
    }

    @Test
    void handle_startChain_createsThenDispatchesThenMarksInProgress() {
        when(conditionStrategyService.listVisibleEnabled(StrategyTriggerEventDTO.EVENT_EXECUTION_START))
                .thenReturn(List.of(createStrategy(), dispatchStrategy(), markInProgressStrategy()));
        TaskExecutionStartRespDTO started = new TaskExecutionStartRespDTO();
        started.setExecutionRecordId(88L);
        when(sessionService.start(any())).thenReturn(started);
        when(missionApi.dispatchAndStartup(any())).thenReturn(CommonResult.success(
                new MissionStartRespDTO(true, true, true, true, null, "{}")));

        StrategyHandleRespDTO resp = service.handle(startEvent());
        assertTrue(resp.isMatched());
        assertEquals(88L, resp.getExecutionRecordId());
        assertEquals(Boolean.TRUE, resp.getDispatchSuccess());
        verify(sessionService).start(any());
        verify(missionApi).dispatchAndStartup(any());
        verify(sessionService).writeback(any());
    }

    @Test
    void handle_dispatchFailed_stopsBeforeMarkInProgress() {
        when(conditionStrategyService.listVisibleEnabled(StrategyTriggerEventDTO.EVENT_EXECUTION_START))
                .thenReturn(List.of(createStrategy(), dispatchStrategy(), markInProgressStrategy()));
        TaskExecutionStartRespDTO started = new TaskExecutionStartRespDTO();
        started.setExecutionRecordId(88L);
        when(sessionService.start(any())).thenReturn(started);
        when(missionApi.dispatchAndStartup(any())).thenReturn(CommonResult.success(
                new MissionStartRespDTO(false, false, false, false, "设备当前未连接：SN-001", null)));

        StrategyHandleRespDTO resp = service.handle(startEvent());
        assertTrue(resp.isMatched());
        assertEquals(Boolean.FALSE, resp.getDispatchSuccess());
        assertEquals("设备当前未连接：SN-001", resp.getDispatchFailureReason());
        verify(sessionService, never()).writeback(any());
    }

    @Test
    void handle_stepUpdates_writeStepOnly() {
        when(conditionStrategyService.listVisibleEnabled(StrategyTriggerEventDTO.EVENT_COLLECTION_RECEIVED))
                .thenReturn(List.of(updateStepStrategy()));
        StrategyTriggerEventDTO event = collectionEvent(9L);
        event.setStepUpdates(List.of(Map.of("stepCode", "seq-3", "status", "completed")));
        StrategyHandleRespDTO resp = service.handle(event);
        assertTrue(resp.isMatched());
        assertEquals("更新某一步的状态", resp.getActionName());
        verify(sessionService).writeback(any());
        verify(sessionService, never()).appendProcess(any());
    }

    private static ConditionStrategyDO appendProcessStrategy() {
        ConditionStrategyDO row = new ConditionStrategyDO();
        row.setId(1L);
        row.setName("采集结果到了就往执行账里记一条过程");
        row.setEnabled(true);
        row.setEventType(StrategyTriggerEventDTO.EVENT_COLLECTION_RECEIVED);
        row.setConditionJson("{\"all\":[{\"type\":\"HAS_EXECUTION_RECORD\"}]}");
        row.setActionCode(RegisteredActions.APPEND_PROCESS);
        row.setPriority(10);
        return row;
    }

    private static ConditionStrategyDO alarmStrategy() {
        ConditionStrategyDO row = new ConditionStrategyDO();
        row.setId(2L);
        row.setName("压力到了就新增一条告警");
        row.setEnabled(true);
        row.setEventType(StrategyTriggerEventDTO.EVENT_COLLECTION_RECEIVED);
        row.setConditionJson("{\"all\":[{\"type\":\"FIELD_COMPARE\",\"field\":\"pressure\",\"op\":\"GT\",\"value\":\"50\"}]}");
        row.setActionCode(RegisteredActions.RAISE_ALARM);
        row.setActionParamsJson("{\"alarmTypeId\":3,\"alarmLevel\":\"WARNING\",\"deviceId\":11,\"locationId\":22}");
        row.setPriority(20);
        return row;
    }

    private static ConditionStrategyDO createStrategy() {
        ConditionStrategyDO row = new ConditionStrategyDO();
        row.setId(3L);
        row.setName("人点了开始就新建这次执行的账");
        row.setEnabled(true);
        row.setEventType(StrategyTriggerEventDTO.EVENT_EXECUTION_START);
        row.setConditionJson("{\"all\":[{\"type\":\"ALWAYS\"}]}");
        row.setActionCode(RegisteredActions.CREATE_EXECUTION);
        row.setPriority(10);
        return row;
    }

    private static ConditionStrategyDO dispatchStrategy() {
        ConditionStrategyDO row = new ConditionStrategyDO();
        row.setId(4L);
        row.setName("人点了开始就把任务发给设备");
        row.setEnabled(true);
        row.setEventType(StrategyTriggerEventDTO.EVENT_EXECUTION_START);
        row.setConditionJson("{\"all\":[{\"type\":\"ALWAYS\"}]}");
        row.setActionCode(RegisteredActions.DISPATCH_TO_DEVICE);
        row.setPriority(20);
        return row;
    }

    private static ConditionStrategyDO markInProgressStrategy() {
        ConditionStrategyDO row = new ConditionStrategyDO();
        row.setId(5L);
        row.setName("发给设备后把这次执行标成进行中");
        row.setEnabled(true);
        row.setEventType(StrategyTriggerEventDTO.EVENT_EXECUTION_START);
        row.setConditionJson("{\"all\":[{\"type\":\"ALWAYS\"}]}");
        row.setActionCode(RegisteredActions.UPDATE_EXECUTION_STATUS);
        row.setActionParamsJson("{\"executionStatus\":\"in_progress\"}");
        row.setPriority(30);
        return row;
    }

    private static ConditionStrategyDO updateStepStrategy() {
        ConditionStrategyDO row = new ConditionStrategyDO();
        row.setId(6L);
        row.setName("采集到某一步结果就更新这一步");
        row.setEnabled(true);
        row.setEventType(StrategyTriggerEventDTO.EVENT_COLLECTION_RECEIVED);
        row.setConditionJson("{\"all\":[{\"type\":\"HAS_EXECUTION_RECORD\"},{\"type\":\"HAS_STEP_UPDATES\"}]}");
        row.setActionCode(RegisteredActions.UPDATE_STEP_STATUS);
        row.setPriority(20);
        return row;
    }

    private static StrategyTriggerEventDTO collectionEvent(Long recordId) {
        StrategyTriggerEventDTO event = new StrategyTriggerEventDTO();
        event.setEventType(StrategyTriggerEventDTO.EVENT_COLLECTION_RECEIVED);
        event.setExecutionRecordId(recordId);
        event.setEntityTypeCode("task_record_patrol");
        event.setReceivedAtEpochMs(1L);
        event.setMessageKind("500202");
        event.setProtocolQualify("QUALIFIED");
        return event;
    }

    private static StrategyTriggerEventDTO startEvent() {
        StrategyTriggerEventDTO event = new StrategyTriggerEventDTO();
        event.setEventType(StrategyTriggerEventDTO.EVENT_EXECUTION_START);
        event.setEntityTypeCode("task_record_patrol");
        event.setTaskDefinitionId(1L);
        event.setModelCode("exec_patrol_round");
        event.setExecutionName("样例-执行");
        event.setStandardSnapshot(Map.of("source", "test"));
        event.setSteps(List.of(Map.of("name", "任务准备", "stepCode", "prepare", "stepOrder", 0)));
        event.setProtocolVersion("robot-ws");
        event.setLogicalDeviceId("SN-001");
        event.setDispatchActions(List.of(Map.of("actionId", 11L, "params", Map.of())));
        return event;
    }
}
