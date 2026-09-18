package cn.cheers.x.device.protocolgateway.service;

import cn.cheers.x.device.protocolgateway.api.message.CommandSendEnvelope;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.api.mission.DispatchAction;
import cn.cheers.x.device.protocolgateway.api.opcode.TransportOpcode;
import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolCodes;
import cn.cheers.x.device.protocolgateway.instructiondispatch.InstructionDispatchService;
import cn.cheers.x.device.protocolgateway.instructiondispatch.InstructionTemplate;
import cn.cheers.x.device.protocolgateway.instructiondispatch.MappingRow;
import cn.cheers.x.device.protocolgateway.instructiondispatch.ProtocolMappingCatalog;
import cn.cheers.x.device.protocolgateway.instructiondispatch.SlotMapping;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 按下发动作列表 + 对照表填包；厂商名和空动作列表必须失败。
 */
class MissionTranslationServiceTest {

    private MissionTranslationService translationService;
    private EnvelopeJsonCodec codec;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        codec = new EnvelopeJsonCodec(objectMapper);
        translationService = new MissionTranslationService(new InstructionDispatchService(new FakeCatalog(), codec));
    }

    @Test
    void mappingFill_writesSlotAndKeepsOpcode() throws Exception {
        DeviceMissionPlan plan = new DeviceMissionPlan(
                ProtocolCodes.ROBOT_WS,
                "device-1",
                "1001234",
                "1001234",
                List.of(new DispatchAction(11L, Map.of(
                        "location_ref", Map.of("FLD-PNT-002", "p2")
                )))
        );

        CommandSendEnvelope envelope = translationService.translate(plan);
        List<Map<String, Object>> packages = codec.decodePackagesJson(envelope.packagesJson());
        assertEquals(1, packages.size());
        assertEquals(200102, ((Number) packages.get(0).get("opcode")).intValue());
        assertEquals("p2", ((Map<?, ?>) packages.get(0).get("request")).get("pointId"));

        JsonNode root = objectMapper.readTree(codec.encodeCommandSend(envelope));
        assertEquals(TransportOpcode.COMMAND_SEND.code(), root.get("opcode").asInt());
    }

    @Test
    void vendorProtocolName_fails() {
        DeviceMissionPlan plan = new DeviceMissionPlan(
                "zhiren-robot-ws",
                "d1",
                "t1",
                "t1",
                List.of(new DispatchAction(11L, Map.of("location_ref", Map.of("FLD-PNT-002", "p"))))
        );
        assertThrows(IllegalArgumentException.class, () -> translationService.translate(plan));
    }

    @Test
    void emptyActions_fails() {
        DeviceMissionPlan plan = new DeviceMissionPlan(
                ProtocolCodes.ROBOT_WS, "d1", "t1", "t1", List.of());
        assertThrows(IllegalArgumentException.class, () -> translationService.translate(plan));
    }

    @Test
    void missingMapping_fails() {
        DeviceMissionPlan plan = new DeviceMissionPlan(
                ProtocolCodes.ROBOT_WS,
                "d1",
                "t1",
                "t1",
                List.of(new DispatchAction(99L, Map.of()))
        );
        assertThrows(IllegalArgumentException.class, () -> translationService.translate(plan));
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
            request.put("lat", "");
            Map<String, Object> outbound = new LinkedHashMap<>();
            outbound.put("opcode", 200102);
            outbound.put("request", request);
            return new InstructionTemplate(200102, outbound);
        }
    }
}
