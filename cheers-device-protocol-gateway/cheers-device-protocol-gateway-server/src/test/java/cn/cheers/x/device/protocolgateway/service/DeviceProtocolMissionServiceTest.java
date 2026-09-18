package cn.cheers.x.device.protocolgateway.service;

import cn.cheers.x.device.protocolgateway.api.dto.MissionStartRespDTO;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.api.mission.DispatchAction;
import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolCodes;
import cn.cheers.x.device.protocolgateway.config.DeviceProtocolGatewayProperties;
import cn.cheers.x.device.protocolgateway.instructiondispatch.InstructionDispatchService;
import cn.cheers.x.device.protocolgateway.instructiondispatch.InstructionTemplate;
import cn.cheers.x.device.protocolgateway.instructiondispatch.MappingRow;
import cn.cheers.x.device.protocolgateway.instructiondispatch.ProtocolMappingCatalog;
import cn.cheers.x.device.protocolgateway.instructiondispatch.SlotMapping;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import cn.cheers.x.device.protocolgateway.protocol.uplink.SyncReplyWaiter;
import cn.cheers.x.device.protocolgateway.transport.DeviceTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 正式下行：离线不得假装成功；在线须等到 500104/500201 同号成功答卷。
 */
class DeviceProtocolMissionServiceTest {

    private DeviceProtocolMissionService missionService;
    private DeviceTransport deviceTransport;
    private EnvelopeJsonCodec codec;
    private SyncReplyWaiter waiter;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        codec = new EnvelopeJsonCodec(objectMapper);
        MissionTranslationService translationService = new MissionTranslationService(
                new InstructionDispatchService(new FakeCatalog(), codec));
        deviceTransport = mock(DeviceTransport.class);
        waiter = new SyncReplyWaiter();
        DeviceProtocolGatewayProperties properties = new DeviceProtocolGatewayProperties();
        properties.setSyncReplyTimeoutMs(200L);
        DeviceProtocolDownlinkService downlinkService = new DeviceProtocolDownlinkService(
                codec, deviceTransport, waiter, properties);
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
    void online_writeOnly_isNotSuccess() {
        when(deviceTransport.isOnline("dev-1")).thenReturn(true);
        when(deviceTransport.sendText(eq("dev-1"), anyString())).thenReturn(true);

        MissionStartRespDTO result = missionService.dispatchAndStartup(samplePlan());

        assertFalse(result.success());
        assertTrue(result.commandSent());
        assertTrue(result.failureReason().contains("超时"));
    }

    @Test
    void online_sameOpcodeReply_isSuccess() {
        when(deviceTransport.isOnline("dev-1")).thenReturn(true);
        when(deviceTransport.sendText(eq("dev-1"), anyString())).thenAnswer(invocation -> {
            String json = invocation.getArgument(1);
            int opcode = codec.readOpcode(json);
            if (opcode == 500106 || opcode == 500105) {
                return true;
            }
            String msgId = codec.readMsgId(json);
            String reply = "{\"opcode\":" + opcode + ",\"msgId\":\"" + msgId
                    + "\",\"code\":200,\"msg\":\"ok\",\"data\":{}}";
            waiter.completeIfPending("dev-1", msgId, opcode, reply);
            return true;
        });

        MissionStartRespDTO result = missionService.dispatchAndStartup(samplePlan());

        assertTrue(result.success());
        assertTrue(result.commandSent());
        assertTrue(result.startupSent());
    }

    private static DeviceMissionPlan samplePlan() {
        return new DeviceMissionPlan(
                ProtocolCodes.ROBOT_WS,
                "dev-1",
                "tpl-1",
                "tpl-1",
                List.of(new DispatchAction(11L, Map.of(
                        "location_ref", Map.of("FLD-PNT-002", "p1")
                )))
        );
    }

    private static final class FakeCatalog implements ProtocolMappingCatalog {
        @Override
        public Optional<MappingRow> find(long actionId, String protocolVersion) {
            if (actionId == 11L && ProtocolCodes.ROBOT_WS.equals(protocolVersion)) {
                return Optional.of(new MappingRow(
                        11L,
                        ProtocolCodes.ROBOT_WS,
                        501L,
                        List.of(new SlotMapping("location_ref", "FLD-PNT-002", "request.pointId"))
                ));
            }
            return Optional.empty();
        }

        @Override
        public InstructionTemplate loadInstruction(long instructionId) {
            Map<String, Object> request = new LinkedHashMap<>();
            request.put("pointId", "");
            Map<String, Object> outbound = new LinkedHashMap<>();
            outbound.put("opcode", 200102);
            outbound.put("request", request);
            return new InstructionTemplate(200102, outbound);
        }
    }
}
