package cn.cheers.x.device.protocolgateway.protocol.monitor;

import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorEventDTO;
import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorFieldDTO;
import cn.cheers.x.device.protocolgateway.datacollection.CatalogLookup;
import cn.cheers.x.device.protocolgateway.datacollection.DeviceProtocolVersionCatalog;
import cn.cheers.x.device.protocolgateway.datacollection.FieldDescriptionRow;
import cn.cheers.x.device.protocolgateway.datacollection.ProtocolInstructionCatalog;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 监控页按字段说明的中文名列值。返回码 200 走正常响应卷。整段对象不铺进表格。
 */
class ProtocolMonitorFieldReaderTest {

    @Test
    void successResponse_keepsBookOrderAndContainerRow() {
        ProtocolMonitorFieldReader reader = reader((opcode, version, volume) -> {
            assertEquals(ProtocolInstructionCatalog.VOLUME_INBOUND_SUCCESS, volume);
            assertNull(version);
            return CatalogLookup.found(List.of(
                    new FieldDescriptionRow("data", "object", "返回消息体", false),
                    new FieldDescriptionRow("data.electricity", "string", "设备电量或电压", false),
                    new FieldDescriptionRow("data.lat", "number", "纬度", false)
            ));
        });
        ProtocolMonitorEventDTO shown = reader.present(event(
                "{\"opcode\":500101,\"code\":200,\"data\":{\"electricity\":\"80\",\"lat\":31.2}}"
        ));
        assertEquals(3, shown.parsedFields().size());
        assertEquals("返回消息体", shown.parsedFields().get(0).label());
        assertEquals("", shown.parsedFields().get(0).value());
        assertEquals("设备电量或电压", shown.parsedFields().get(1).label());
        assertEquals("80", shown.parsedFields().get(1).value());
        assertEquals("string", shown.parsedFields().get(1).type());
        assertEquals(false, shown.parsedFields().get(1).required());
        assertNull(shown.parsedFields().get(1).issue());
        assertEquals("纬度", shown.parsedFields().get(2).label());
        assertEquals("31.2", shown.parsedFields().get(2).value());
        assertNull(shown.parsedNote());
    }

    @Test
    void wrongType_marksThatField() {
        ProtocolMonitorFieldReader reader = reader((opcode, version, volume) -> CatalogLookup.found(List.of(
                new FieldDescriptionRow("data.lat", "number", "纬度", true)
        )));
        ProtocolMonitorEventDTO shown = reader.present(event(
                "{\"opcode\":500101,\"code\":200,\"data\":{\"lat\":\"north\"}}"
        ));
        assertEquals("north", shown.parsedFields().get(0).value());
        assertTrue(shown.parsedFields().get(0).issue().contains("类型不对"));
    }

    @Test
    void commandPackage_eachInstructionIsOneAction() {
        ProtocolMonitorFieldReader reader = reader((opcode, version, volume) -> CatalogLookup.found(List.of(
                new FieldDescriptionRow("packages", "array", "有序动作集合", true),
                new FieldDescriptionRow("packages[].sequence", "integer", "动作序号", true),
                new FieldDescriptionRow("packages[].opcode", "integer", "内层动作码", true),
                new FieldDescriptionRow("packages[].request", "object", "动作请求参数", true),
                new FieldDescriptionRow("packages[].request.pointId", "string", "点位", true)
        )));
        ProtocolMonitorEventDTO shown = reader.present(event(
                "{\"opcode\":500104,\"packages\":["
                        + "{\"sequence\":1,\"opcode\":200102,\"request\":{\"pointId\":\"A\"}},"
                        + "{\"sequence\":2,\"opcode\":200301,\"request\":{\"pointId\":\"\"}}"
                        + "]}"
        ));
        assertEquals("有序动作集合", shown.parsedFields().get(0).label());
        assertEquals("动作序号 1 · 内层动作码 200102", shown.parsedFields().get(1).label());
        assertEquals("packages[0]", shown.parsedFields().get(1).path());
        assertEquals("1", shown.parsedFields().get(2).value());
        assertEquals("packages[0].sequence", shown.parsedFields().get(2).path());
        assertEquals("A", shown.parsedFields().get(5).value());
        assertEquals("动作序号 2 · 内层动作码 200301", shown.parsedFields().get(6).label());
        assertEquals("packages[1].request.pointId", shown.parsedFields().get(10).path());
        assertEquals("", shown.parsedFields().get(10).value());
        assertEquals("缺必填", shown.parsedFields().get(10).issue());
        assertNull(shown.parsedFields().get(5).issue());
    }

    @Test
    void commandPackage_hidesFieldsThisStepDoesNotHave() {
        ProtocolMonitorFieldReader reader = reader((opcode, version, volume) -> CatalogLookup.found(List.of(
                new FieldDescriptionRow("packages", "array", "有序动作集合", true),
                new FieldDescriptionRow("packages[].opcode", "integer", "内层动作码", true),
                new FieldDescriptionRow("packages[].request.height", "string", "地面高度", false),
                new FieldDescriptionRow("packages[].request.time", "string", "等待秒数", false)
        )));
        ProtocolMonitorEventDTO shown = reader.present(event(
                "{\"opcode\":500104,\"packages\":["
                        + "{\"opcode\":200102,\"request\":{\"height\":\"30.33\"}},"
                        + "{\"opcode\":200301,\"request\":{\"time\":\"100\"}}"
                        + "]}"
        ));
        assertEquals("30.33", valueOf(shown, "packages[0].request.height"));
        assertEquals(null, fieldOf(shown, "packages[0].request.time"));
        assertEquals("100", valueOf(shown, "packages[1].request.time"));
        assertEquals(null, fieldOf(shown, "packages[1].request.height"));
    }

    @Test
    void ambiguousBook_keepsPacketAndWritesReason() {
        ProtocolMonitorFieldReader reader = reader((opcode, version, volume) ->
                CatalogLookup.failed("同一操作码有多份不同说明书，设备未标明协议版本"));
        ProtocolMonitorEventDTO shown = reader.present(event("{\"opcode\":500104,\"taskId\":\"t1\"}"));
        assertTrue(shown.parsedFields().isEmpty());
        assertTrue(shown.parsedNote().contains("协议版本"));
        assertTrue(shown.payloadJson().contains("500104"));
    }

    private static String valueOf(ProtocolMonitorEventDTO shown, String path) {
        ProtocolMonitorFieldDTO field = fieldOf(shown, path);
        return field == null ? null : field.value();
    }

    private static ProtocolMonitorFieldDTO fieldOf(ProtocolMonitorEventDTO shown, String path) {
        for (ProtocolMonitorFieldDTO field : shown.parsedFields()) {
            if (path.equals(field.path())) {
                return field;
            }
        }
        return null;
    }

    private static ProtocolMonitorFieldReader reader(VolumeLookup lookup) {
        ProtocolInstructionCatalog catalog = new ProtocolInstructionCatalog() {
            @Override
            public CatalogLookup findMainPacket(int opcode, String protocolVersion) {
                return CatalogLookup.failed("本测试不读主包");
            }

            @Override
            public CatalogLookup findVolume(int opcode, String protocolVersion, String volume) {
                return lookup.find(opcode, protocolVersion, volume);
            }
        };
        DeviceProtocolVersionCatalog versions = deviceId -> Optional.empty();
        return new ProtocolMonitorFieldReader(catalog, versions, new ObjectMapper());
    }

    private static ProtocolMonitorEventDTO event(String payload) {
        return new ProtocolMonitorEventDTO(
                "e1",
                "sim-parse-001",
                "inbound",
                500101,
                "m1",
                "获取设备当前实时状态",
                "",
                payload,
                1L,
                List.of(),
                null
        );
    }

    @FunctionalInterface
    private interface VolumeLookup {
        CatalogLookup find(int opcode, String protocolVersion, String volume);
    }
}
