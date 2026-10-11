package cn.cheers.x.device.protocolgateway.service;

import cn.cheers.x.device.protocolgateway.api.dto.MissionStartRespDTO;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.api.mission.DispatchAction;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import cn.cheers.x.device.protocolgateway.transport.DeviceTransport;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeviceProtocolMissionServiceTest {

    @Test
    void missingInstruction_returnsReason_withoutSending() {
        MissionTranslationService translation = mock(MissionTranslationService.class);
        DeviceProtocolDownlinkService downlink = mock(DeviceProtocolDownlinkService.class);
        DeviceTransport transport = mock(DeviceTransport.class);
        DeviceMissionPlan plan = new DeviceMissionPlan(
                "robot-ws", "robot-1", "88", "tpl", List.of(new DispatchAction(11L, Map.of())));
        when(translation.translate(plan, true)).thenThrow(new IllegalArgumentException(
                "动作 11 在机器人对接协议下没有指令对照，请到指令协议对接配置"));

        DeviceProtocolMissionService service = new DeviceProtocolMissionService(
                translation, downlink, new EnvelopeJsonCodec(new com.fasterxml.jackson.databind.ObjectMapper()), transport,
                new cn.cheers.x.device.protocolgateway.djicloud.DjiCloudOnlineRegistry(),
                org.mockito.Mockito.mock(cn.cheers.x.device.protocolgateway.djicloud.DjiCloudCommandSender.class));
        MissionStartRespDTO result = service.dispatchAndStartup(plan);

        assertFalse(result.success());
        assertTrue(result.failureReason().contains("指令协议对接"));
        verify(transport, never()).isOnline("robot-1");
        verify(downlink, never()).sendCommandPackage(org.mockito.ArgumentMatchers.any());
    }
}
