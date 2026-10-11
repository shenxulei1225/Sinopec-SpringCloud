package cn.cheers.x.device.protocolgateway.protocol.processor;

import cn.cheers.x.device.protocolgateway.api.channel.AccessChannelCodes;
import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.device.protocolgateway.api.datacollection.ProtocolQualifyStatus;
import cn.cheers.x.device.protocolgateway.api.dto.DeviceUplinkEventDTO;
import cn.cheers.x.device.protocolgateway.datacollection.CatalogLookup;
import cn.cheers.x.device.protocolgateway.datacollection.DeviceProtocolVersionCatalog;
import cn.cheers.x.device.protocolgateway.datacollection.FieldDescriptionRow;
import cn.cheers.x.device.protocolgateway.datacollection.ProtocolInstructionCatalog;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.api.InspectionDeviceUplinkApi;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProtocolBookAdmissionTest {

    @Test
    void robotBook_rejectsRecordOpcode_beforeRobotProcessor() {
        ProtocolBookAdmission admission = admission(Set.of(500202, 200301), "robot-ws");
        DeviceProtocolVersionCatalog versions = versionCatalog("robot-ws");
        InspectionDeviceUplinkApi robotApi = mock(InspectionDeviceUplinkApi.class);
        DeviceTypeUplinkProcessor robot = new DeviceTypeUplinkProcessor(
                DeviceTypeUplinkProcessorConfiguration.ROBOT, versions, robotApi);
        RegisteredUplinkProcessorDispatcher dispatcher = new RegisteredUplinkProcessorDispatcher(
                List.of(robot), admission);

        dispatcher.consume(event("500202", Map.of(
                "packages", Map.of("opcode", 200201, "sequence", 1))));

        verify(robotApi, never()).notify(any());
    }

    @Test
    void robotBook_deliversPhotoResult_toRobotProcessor() {
        ProtocolBookAdmission admission = admission(Set.of(500202, 200301), "robot-ws");
        DeviceProtocolVersionCatalog versions = versionCatalog("robot-ws");
        InspectionDeviceUplinkApi robotApi = mock(InspectionDeviceUplinkApi.class);
        when(robotApi.notify(any())).thenReturn(CommonResult.success(true));
        DeviceTypeUplinkProcessor robot = new DeviceTypeUplinkProcessor(
                DeviceTypeUplinkProcessorConfiguration.ROBOT, versions, robotApi);
        RegisteredUplinkProcessorDispatcher dispatcher = new RegisteredUplinkProcessorDispatcher(
                List.of(robot), admission);

        dispatcher.consume(event("500202", Map.of(
                "packages", Map.of("opcode", 200301, "sequence", 1))));

        verify(robotApi).notify(any());
    }

    @Test
    void uavRecord_doesNotEnterRobotProcessor() {
        ProtocolBookAdmission admission = admission(Set.of(500202, 200201), "uav-ws");
        DeviceProtocolVersionCatalog versions = versionCatalog("uav-ws");
        InspectionDeviceUplinkApi robotApi = mock(InspectionDeviceUplinkApi.class);
        InspectionDeviceUplinkApi uavApi = mock(InspectionDeviceUplinkApi.class);
        when(uavApi.notify(any())).thenReturn(CommonResult.success(true));
        DeviceTypeUplinkProcessor robot = new DeviceTypeUplinkProcessor(
                DeviceTypeUplinkProcessorConfiguration.ROBOT, versions, robotApi);
        DeviceTypeUplinkProcessor uav = new DeviceTypeUplinkProcessor(
                DeviceTypeUplinkProcessorConfiguration.DONG_GU_UAV, versions, uavApi);
        RegisteredUplinkProcessorDispatcher dispatcher = new RegisteredUplinkProcessorDispatcher(
                List.of(robot, uav), admission);

        dispatcher.consume(event("500202", Map.of(
                "packages", Map.of("opcode", 200201, "sequence", 2))));

        verify(robotApi, never()).notify(any());
        verify(uavApi).notify(any());
        assertFalse(robot.supports(event("500202", Map.of()).identity()));
        assertTrue(uav.supports(event("500202", Map.of()).identity()));
    }

    private static ProtocolBookAdmission admission(Set<Integer> opcodes, String version) {
        DeviceProtocolVersionCatalog versions = versionCatalog(version);
        ProtocolInstructionCatalog catalog = mock(ProtocolInstructionCatalog.class);
        when(catalog.findMainPacket(anyInt(), any())).thenAnswer(invocation -> {
            int opcode = invocation.getArgument(0);
            String asked = invocation.getArgument(1);
            if (version.equals(asked) && opcodes.contains(opcode)) {
                return CatalogLookup.found(List.of(new FieldDescriptionRow("opcode", "integer", "操作码", true)));
            }
            return CatalogLookup.failed("找不到该协议版本的字段说明");
        });
        return new ProtocolBookAdmission(versions, catalog);
    }

    private static DeviceProtocolVersionCatalog versionCatalog(String version) {
        DeviceProtocolVersionCatalog versions = mock(DeviceProtocolVersionCatalog.class);
        when(versions.firstVersion("robot-1")).thenReturn(Optional.of(version));
        return versions;
    }

    private static DeviceUplinkEventDTO event(String messageKind, Map<String, Object> fields) {
        return DeviceUplinkEventDTO.of(new CollectionSample(
                AccessChannelCodes.INSPECTION,
                "robot-1",
                messageKind,
                ProtocolQualifyStatus.QUALIFIED,
                List.of(),
                fields,
                9L,
                "m1",
                1L));
    }
}
