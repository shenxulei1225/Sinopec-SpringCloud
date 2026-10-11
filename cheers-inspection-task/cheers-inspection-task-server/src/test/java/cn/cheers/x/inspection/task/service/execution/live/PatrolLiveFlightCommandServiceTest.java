package cn.cheers.x.inspection.task.service.execution.live;

import cn.cheers.x.device.protocolgateway.api.DeviceProtocolMissionApi;
import cn.cheers.x.device.protocolgateway.api.dto.MissionStartRespDTO;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatrolLiveFlightCommandServiceTest {

    @Mock
    private EntityRpcApi entityRpcApi;
    @Mock
    private DeviceProtocolMissionApi missionApi;

    private PatrolLiveFlightCommandService service;

    @BeforeEach
    void setUp() {
        service = new PatrolLiveFlightCommandService(entityRpcApi, missionApi);
    }

    @Test
    void sendsTheTakeoffAlreadyWrittenOnThisRun() {
        Map<String, Object> params = takeoffParams();
        when(entityRpcApi.getEntity(9L, PatrolLiveFlightCommandService.RECORD_TYPE))
                .thenReturn(CommonResult.success(record(List.of(action(
                        PatrolLiveFlightCommandService.TAKEOFF_COMMAND, 17L, params)))));
        when(missionApi.dispatchAndStartup(any())).thenReturn(CommonResult.success(
                new MissionStartRespDTO(true, true, true, false, null, "{}")));

        service.send(9L, "takeoff");

        ArgumentCaptor<DeviceMissionPlan> plan = ArgumentCaptor.forClass(DeviceMissionPlan.class);
        verify(missionApi).dispatchAndStartup(plan.capture());
        assertEquals(17L, plan.getValue().actions().get(0).actionId());
        assertEquals(PatrolLiveFlightCommandService.TAKEOFF_COMMAND, plan.getValue().actions().get(0).actionCode());
        assertEquals(80, ((Number) plan.getValue().actions().get(0).params().get("security_takeoff_height")).intValue());
        assertEquals("flight-9", plan.getValue().actions().get(0).params().get("flight_id"));
    }

    @Test
    void doesNotSendTakeoffWhenTheHeightWasNotWritten() {
        Map<String, Object> params = takeoffParams();
        params.remove("security_takeoff_height");
        when(entityRpcApi.getEntity(9L, PatrolLiveFlightCommandService.RECORD_TYPE))
                .thenReturn(CommonResult.success(record(List.of(action(
                        PatrolLiveFlightCommandService.TAKEOFF_ON_TASK, 18L, params)))));

        RuntimeException error = assertThrows(RuntimeException.class, () -> service.send(9L, "takeoff"));

        assertTrue(error.getMessage().contains("安全起飞高度"));
        verify(missionApi, never()).dispatchAndStartup(any());
    }

    @Test
    void doesNotSendTakeoffWhenThisRunHasNoTakeoffAction() {
        when(entityRpcApi.getEntity(9L, PatrolLiveFlightCommandService.RECORD_TYPE))
                .thenReturn(CommonResult.success(record(List.of())));

        RuntimeException error = assertThrows(RuntimeException.class, () -> service.send(9L, "takeoff"));

        assertTrue(error.getMessage().contains("没有记下起飞动作"));
        verify(missionApi, never()).dispatchAndStartup(any());
    }

    @Test
    void sendsReturnHomeWithoutExtraNumbers() {
        when(entityRpcApi.getEntity(9L, PatrolLiveFlightCommandService.RECORD_TYPE))
                .thenReturn(CommonResult.success(record(List.of())));
        EntityRespDTO action = new EntityRespDTO();
        action.setId(22L);
        when(entityRpcApi.getEntityByCode(
                PatrolLiveFlightCommandService.RETURN_HOME, PatrolLiveFlightCommandService.ACTION_TYPE))
                .thenReturn(CommonResult.success(action));
        when(missionApi.dispatchAndStartup(any())).thenReturn(CommonResult.success(
                new MissionStartRespDTO(true, true, true, false, null, "{}")));

        service.send(9L, "returnHome");

        ArgumentCaptor<DeviceMissionPlan> plan = ArgumentCaptor.forClass(DeviceMissionPlan.class);
        verify(missionApi).dispatchAndStartup(plan.capture());
        assertEquals(22L, plan.getValue().actions().get(0).actionId());
        assertEquals(PatrolLiveFlightCommandService.RETURN_HOME, plan.getValue().actions().get(0).actionCode());
        assertTrue(plan.getValue().actions().get(0).params().isEmpty());
    }

    private static EntityRespDTO record(List<Map<String, Object>> actions) {
        EntityRespDTO record = new EntityRespDTO();
        record.setId(9L);
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("protocolVersion", "dji-dock-1.16.1");
        snapshot.put("logicalDeviceId", "dock-sn");
        snapshot.put("dispatchActions", actions);
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("execution_status", "in_progress");
        fields.put("standard_snapshot", snapshot);
        record.setCustomFields(fields);
        return record;
    }

    private static Map<String, Object> action(String code, long actionId, Map<String, Object> params) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("actionId", actionId);
        item.put("actionCode", code);
        item.put("params", params);
        return item;
    }

    private static Map<String, Object> takeoffParams() {
        Map<String, Object> target = new LinkedHashMap<>();
        target.put("latitude", 39.12);
        target.put("longitude", 117.01);
        target.put("height", 12);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("FLD-LOC-014", target);
        params.put("security_takeoff_height", 80);
        params.put("rth_mode", 1);
        params.put("rth_altitude", 100);
        params.put("rc_lost_action", 2);
        params.put("commander_mode_lost_action", 1);
        params.put("commander_flight_mode", 1);
        params.put("commander_flight_height", 60);
        params.put("max_speed", 10);
        params.put("flight_id", "flight-9");
        return params;
    }
}
