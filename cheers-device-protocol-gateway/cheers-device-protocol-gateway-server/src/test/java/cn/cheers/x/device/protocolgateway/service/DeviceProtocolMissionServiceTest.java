package cn.cheers.x.device.protocolgateway.service;

import cn.cheers.x.device.protocolgateway.api.dto.MissionStartRespDTO;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.api.mission.MissionPointActionType;
import cn.cheers.x.device.protocolgateway.api.mission.MissionWaypoint;
import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolCodes;
import cn.cheers.x.device.protocolgateway.protocol.adapter.ProtocolAdapterRegistry;
import cn.cheers.x.device.protocolgateway.protocol.adapter.dongfang.DongfangUavWsAdapter;
import cn.cheers.x.device.protocolgateway.protocol.adapter.zhiren.ZhirenRobotWsAdapter;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import cn.cheers.x.device.protocolgateway.transport.DeviceTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 正式下行：离线不得假装成功；在线则 500104+500201。
 */
class DeviceProtocolMissionServiceTest {

    private DeviceProtocolMissionService missionService;
    private DeviceTransport deviceTransport;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        EnvelopeJsonCodec codec = new EnvelopeJsonCodec(objectMapper);
        ProtocolAdapterRegistry registry = new ProtocolAdapterRegistry(List.of(
                new ZhirenRobotWsAdapter(codec),
                new DongfangUavWsAdapter(codec)
        ));
        MissionTranslationService translationService = new MissionTranslationService(registry);
        deviceTransport = mock(DeviceTransport.class);
        DeviceProtocolDownlinkService downlinkService = new DeviceProtocolDownlinkService(codec, deviceTransport);
        missionService = new DeviceProtocolMissionService(
                translationService, downlinkService, codec, deviceTransport);
    }

    @Test
    void offline_returnsFailure() {
        when(deviceTransport.isOnline("dev-1")).thenReturn(false);
        DeviceMissionPlan plan = samplePlan();

        MissionStartRespDTO result = missionService.dispatchAndStartup(plan);

        assertFalse(result.success());
        assertFalse(result.online());
        assertFalse(result.commandSent());
        assertTrue(result.failureReason().contains("未连接"));
    }

    @Test
    void online_sendsCommandAndStartup() {
        when(deviceTransport.isOnline("dev-1")).thenReturn(true);
        when(deviceTransport.sendText(anyString(), anyString())).thenReturn(true);
        DeviceMissionPlan plan = samplePlan();

        MissionStartRespDTO result = missionService.dispatchAndStartup(plan);

        assertTrue(result.success());
        assertTrue(result.commandSent());
        assertTrue(result.startupSent());
    }

    private static DeviceMissionPlan samplePlan() {
        return new DeviceMissionPlan(
         ProtocolCodes.ZHIREN_ROBOT_WS,
         "dev-1",
         "tpl-1",
         "tpl-1",
         List.of(new MissionWaypoint("p1", "39.1", "117.0", 0.0, null, MissionPointActionType.NONE))
        );
    }
}
