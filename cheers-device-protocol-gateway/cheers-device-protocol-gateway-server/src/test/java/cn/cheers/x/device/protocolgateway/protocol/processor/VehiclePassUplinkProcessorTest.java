package cn.cheers.x.device.protocolgateway.protocol.processor;

import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.device.protocolgateway.api.datacollection.ProtocolQualifyStatus;
import cn.cheers.x.device.protocolgateway.api.dto.DeviceUplinkEventDTO;
import cn.cheers.x.device.protocolgateway.vehicle.VehicleHttpsCommandService;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.security.VehiclePassEventCodes;
import cn.cheers.x.module.dynamicbusiness.api.security.VehiclePassUplinkApi;
import cn.cheers.x.module.dynamicbusiness.api.security.dto.VehiclePassNotifyResp;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VehiclePassUplinkProcessorTest {

    @Test
    void seenEvent_isHandedToVehiclePass() {
        VehiclePassUplinkApi api = mock(VehiclePassUplinkApi.class);
        when(api.notify(org.mockito.ArgumentMatchers.any())).thenReturn(CommonResult.success(
                new VehiclePassNotifyResp(4L, false, "", "鄂A1", 1L, "进场", true)));
        VehiclePassUplinkProcessor processor = new VehiclePassUplinkProcessor(api, mock(VehicleHttpsCommandService.class));
        DeviceUplinkEventDTO event = event(VehiclePassEventCodes.SEEN);
        assertTrue(processor.supports(event.identity()));
        processor.process(event.identity(), event);
        verify(api).notify(event.sample());
    }

    @Test
    void inspectionEvent_isNotVehiclePass() {
        VehiclePassUplinkProcessor processor = new VehiclePassUplinkProcessor(
                mock(VehiclePassUplinkApi.class), mock(VehicleHttpsCommandService.class));
        DeviceUplinkEventDTO event = event("inspection_step");
        assertFalse(processor.supports(event.identity()));
    }

    @Test
    void unqualified_isNotWritten() {
        VehiclePassUplinkApi api = mock(VehiclePassUplinkApi.class);
        VehiclePassUplinkProcessor processor = new VehiclePassUplinkProcessor(api, mock(VehicleHttpsCommandService.class));
        DeviceUplinkEventDTO event = new DeviceUplinkEventDTO(new CollectionSample(
                "parking", "gate-1", VehiclePassEventCodes.DONE,
                ProtocolQualifyStatus.UNQUALIFIED, List.of("缺车牌"),
                Map.of(), null, "m1", 1L));
        assertThrows(IllegalStateException.class, () -> processor.process(event.identity(), event));
        verify(api, never()).notify(org.mockito.ArgumentMatchers.any());
    }

    private static DeviceUplinkEventDTO event(String messageKind) {
        return DeviceUplinkEventDTO.of(new CollectionSample(
                "parking",
                "gate-1",
                messageKind,
                ProtocolQualifyStatus.QUALIFIED,
                List.of(),
                Map.of("车牌", "鄂A1"),
                null,
                "m1",
                1L));
    }
}
