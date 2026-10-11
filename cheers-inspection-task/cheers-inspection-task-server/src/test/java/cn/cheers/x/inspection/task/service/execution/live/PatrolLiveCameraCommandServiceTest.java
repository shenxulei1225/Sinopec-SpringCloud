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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatrolLiveCameraCommandServiceTest {

    @Mock
    private EntityRpcApi entityRpcApi;
    @Mock
    private DeviceProtocolMissionApi missionApi;

    private PatrolLiveCameraCommandService service;

    @BeforeEach
    void setUp() {
        service = new PatrolLiveCameraCommandService(entityRpcApi, missionApi);
    }

    @Test
    void sendsZoomWithTheNumbersFromThePicture() {
        when(entityRpcApi.getEntity(9L, PatrolLiveCameraCommandService.RECORD_TYPE))
                .thenReturn(CommonResult.success(record("in_progress", "dji-dock-1.16.1", "dock-sn")));
        EntityRespDTO action = new EntityRespDTO();
        action.setId(41L);
        when(entityRpcApi.getEntityByCode("zoom", PatrolLiveCameraCommandService.ACTION_TYPE))
                .thenReturn(CommonResult.success(action));
        when(missionApi.dispatchAndStartup(any())).thenReturn(CommonResult.success(
                new MissionStartRespDTO(true, true, true, false, null, "{}")));

        MissionStartRespDTO result = service.send(9L, "zoom", Map.of(
                "selected_camera", "43-0-0",
                "camera_type", "zoom",
                "zoom_factor", 5));

        assertEquals(true, result.success());
        ArgumentCaptor<DeviceMissionPlan> plan = ArgumentCaptor.forClass(DeviceMissionPlan.class);
        verify(missionApi).dispatchAndStartup(plan.capture());
        assertEquals("dji-dock-1.16.1", plan.getValue().protocolVersion());
        assertEquals("dock-sn", plan.getValue().deviceId());
        assertEquals("zoom", plan.getValue().actions().get(0).actionCode());
        assertEquals(5, ((Number) plan.getValue().actions().get(0).params().get("zoom_factor")).intValue());
    }

    @Test
    void sendsStartRecordOnTheSameActionAsTheTaskOpening() {
        when(entityRpcApi.getEntity(9L, PatrolLiveCameraCommandService.RECORD_TYPE))
                .thenReturn(CommonResult.success(record("in_progress", "dji-dock-1.16.1", "dock-sn")));
        EntityRespDTO action = new EntityRespDTO();
        action.setId(75L);
        when(entityRpcApi.getEntityByCode("startRecord", PatrolLiveCameraCommandService.ACTION_TYPE))
                .thenReturn(CommonResult.success(action));
        when(missionApi.dispatchAndStartup(any())).thenReturn(CommonResult.success(
                new MissionStartRespDTO(true, true, true, false, null, "{}")));

        service.send(9L, "startRecord", Map.of("selected_camera", "43-0-0"));

        ArgumentCaptor<DeviceMissionPlan> plan = ArgumentCaptor.forClass(DeviceMissionPlan.class);
        verify(missionApi).dispatchAndStartup(plan.capture());
        assertEquals("startRecord", plan.getValue().actions().get(0).actionCode());
        assertEquals(75L, plan.getValue().actions().get(0).actionId());
        assertEquals("43-0-0", plan.getValue().actions().get(0).params().get("payload_position_index"));
    }

    @Test
    void refusesFrameZoomOnPilotWithoutCallingTheGateway() {
        when(entityRpcApi.getEntity(9L, PatrolLiveCameraCommandService.RECORD_TYPE))
                .thenReturn(CommonResult.success(record("in_progress", "dji-pilot-1.16.1", "pilot-sn")));

        assertThrows(RuntimeException.class, () -> service.send(9L, "frameZoom", Map.of(
                "selected_camera", "43-0-0",
                "camera_type", "zoom",
                "locked", false,
                "frame_x", 0.1,
                "frame_y", 0.2,
                "frame_width", 0.3,
                "frame_height", 0.4)));
        verify(missionApi, never()).dispatchAndStartup(any());
    }

    @Test
    void refusesWhenTheCameraWasNotChosen() {
        when(entityRpcApi.getEntity(eq(9L), eq(PatrolLiveCameraCommandService.RECORD_TYPE)))
                .thenReturn(CommonResult.success(record("in_progress", "dji-dock-1.16.1", "dock-sn")));

        assertThrows(RuntimeException.class, () -> service.send(9L, "zoom", Map.of(
                "camera_type", "zoom",
                "zoom_factor", 5)));
        verify(entityRpcApi, never()).getEntityByCode(any(), any());
    }

    private static EntityRespDTO record(String status, String protocol, String deviceId) {
        EntityRespDTO record = new EntityRespDTO();
        record.setId(9L);
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("protocolVersion", protocol);
        snapshot.put("logicalDeviceId", deviceId);
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("execution_status", status);
        fields.put("standard_snapshot", snapshot);
        record.setCustomFields(fields);
        return record;
    }
}
