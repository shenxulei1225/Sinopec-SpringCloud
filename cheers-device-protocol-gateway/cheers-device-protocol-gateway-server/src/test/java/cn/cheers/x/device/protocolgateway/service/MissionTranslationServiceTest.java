package cn.cheers.x.device.protocolgateway.service;

import cn.cheers.x.device.protocolgateway.api.message.CommandSendEnvelope;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.api.mission.MissionPointActionType;
import cn.cheers.x.device.protocolgateway.api.mission.MissionWaypoint;
import cn.cheers.x.device.protocolgateway.api.opcode.RobotMissionOpcode;
import cn.cheers.x.device.protocolgateway.api.opcode.TransportOpcode;
import cn.cheers.x.device.protocolgateway.api.opcode.UavMissionOpcode;
import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolCodes;
import cn.cheers.x.device.protocolgateway.protocol.adapter.ProtocolAdapterRegistry;
import cn.cheers.x.device.protocolgateway.protocol.adapter.dongfang.DongfangUavWsAdapter;
import cn.cheers.x.device.protocolgateway.protocol.adapter.zhiren.ZhirenRobotWsAdapter;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 动态执行意图 → 按对接协议编码翻译；禁止依赖写死设备业务类型。
 */
class MissionTranslationServiceTest {

    private MissionTranslationService translationService;
    private EnvelopeJsonCodec codec;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        codec = new EnvelopeJsonCodec(objectMapper);
        ProtocolAdapterRegistry registry = new ProtocolAdapterRegistry(List.of(
                new ZhirenRobotWsAdapter(codec),
                new DongfangUavWsAdapter(codec)
        ));
        translationService = new MissionTranslationService(registry);
    }

    @Test
    void zhirenRobot_dynamicWaypoints_includeMoveAndPhoto() throws Exception {
        DeviceMissionPlan plan = new DeviceMissionPlan(
         ProtocolCodes.ZHIREN_ROBOT_WS,
         "device-1",
         "1001234",
         "1001234",
         List.of(
                        new MissionWaypoint("p1", "39.1", "117.0", 10.0, null, MissionPointActionType.NONE),
                        new MissionWaypoint("p2", "39.2", "117.1", 20.0, null, MissionPointActionType.PHOTO)
                )
        );

        CommandSendEnvelope envelope = translationService.translate(plan);
        List<Map<String, Object>> packages = codec.decodePackagesJson(envelope.packagesJson());
        assertEquals(3, packages.size());
        assertEquals(RobotMissionOpcode.MOVE.code(), ((Number) packages.get(0).get("opcode")).intValue());
        assertEquals(RobotMissionOpcode.MOVE.code(), ((Number) packages.get(1).get("opcode")).intValue());
        assertEquals(RobotMissionOpcode.PHOTO.code(), ((Number) packages.get(2).get("opcode")).intValue());

        String wire = codec.encodeCommandSend(envelope);
        JsonNode root = objectMapper.readTree(wire);
        assertEquals(TransportOpcode.COMMAND_SEND.code(), root.get("opcode").asInt());
        assertTrue(root.get("packages").isTextual());
    }

    @Test
    void dongfangUav_usesTakeoffMoveLandOpcodes() {
        DeviceMissionPlan plan = new DeviceMissionPlan(
         ProtocolCodes.DONGFANG_UAV_WS,
         "uav-1",
         "tpl-1",
         "tpl-1",
         List.of(new MissionWaypoint("a1", "31.0", "121.0", null, "20", MissionPointActionType.NONE))
        );
        CommandSendEnvelope envelope = translationService.translate(plan);
        List<Map<String, Object>> packages = codec.decodePackagesJson(envelope.packagesJson());
        assertEquals(UavMissionOpcode.TAKEOFF.code(), ((Number) packages.get(0).get("opcode")).intValue());
        assertTrue(packages.stream().anyMatch(p ->
                ((Number) p.get("opcode")).intValue() == UavMissionOpcode.LAND.code()));
    }

    @Test
    void unknownProtocolCode_failsExplicitly() {
        DeviceMissionPlan plan = new DeviceMissionPlan(
         "unknown-protocol",
         "d1",
         "t1",
         "t1",
         List.of(new MissionWaypoint("p", "1", "2", null, null, MissionPointActionType.NONE))
        );
        assertThrows(IllegalArgumentException.class, () -> translationService.translate(plan));
    }

    @Test
    void emptyWaypoints_failsExplicitly() {
        DeviceMissionPlan plan = new DeviceMissionPlan(
         ProtocolCodes.ZHIREN_ROBOT_WS,
         "d1",
         "t1",
         "t1",
         List.of()
        );
        assertThrows(IllegalArgumentException.class, () -> translationService.translate(plan));
    }
}
