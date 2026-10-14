package cn.cheers.x.device.protocolgateway.protocol.monitor;

import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorDownlinkResultDTO;
import cn.cheers.x.device.protocolgateway.datacollection.DeviceRegistrationLookup;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import cn.cheers.x.device.protocolgateway.protocol.exchange.RunningTaskExchangeRecorder;
import cn.cheers.x.device.protocolgateway.service.MissionTranslationService;
import cn.cheers.x.device.protocolgateway.transport.DeviceTransport;
import cn.cheers.x.inspection.task.api.InspectionDeviceUplinkApi;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 监控页下发写出后就返回，不等设备同号答卷。
 */
class ProtocolMonitorDownlinkServiceTest {

    @Test
    void syncOpcode_returnsAfterWrite() {
        DeviceRegistrationLookup registration = mock(DeviceRegistrationLookup.class);
        ProtocolMonitorDownlinkCatalog catalog = mock(ProtocolMonitorDownlinkCatalog.class);
        EnvelopeJsonCodec codec = mock(EnvelopeJsonCodec.class);
        DeviceTransport transport = mock(DeviceTransport.class);
        RunningTaskExchangeRecorder recorder = mock(RunningTaskExchangeRecorder.class);
        ProtocolMonitorDownlinkService service = new ProtocolMonitorDownlinkService(
                registration,
                catalog,
                codec,
                transport,
                recorder,
                mock(InspectionDeviceUplinkApi.class),
                mock(MissionTranslationService.class));
        when(registration.registrationOf("dev-1"))
                .thenReturn(new DeviceRegistrationLookup.Registration("robot-ws", ""));
        when(catalog.require("robot-ws", 500101)).thenReturn(new ProtocolMonitorDownlinkCatalog.Prepared(
                "获取设备实时状态",
                500101,
                Map.of("opcode", 500101, "msgId", "", "deviceId", ""),
                true,
                null,
                "",
                "500101",
                "downlink"));
        when(codec.encodeObject(any())).thenReturn("{\"opcode\":500101}");
        when(transport.sendText(eq("dev-1"), anyString())).thenReturn(true);

        ProtocolMonitorDownlinkResultDTO result = service.send("dev-1", 500101);

        assertTrue(result.written());
        assertFalse(result.businessSuccess());
        assertTrue(result.note().contains("已提交下发"));
        verify(transport).sendText(eq("dev-1"), eq("{\"opcode\":500101}"));
        verify(recorder).enqueue(eq("dev-1"), anyString(), eq("{\"opcode\":500101}"), isNull());
    }
}
