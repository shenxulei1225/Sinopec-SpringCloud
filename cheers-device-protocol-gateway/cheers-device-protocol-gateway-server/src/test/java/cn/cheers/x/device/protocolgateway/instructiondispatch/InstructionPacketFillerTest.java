package cn.cheers.x.device.protocolgateway.instructiondispatch;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 对照只填空包已有路径；缺参数或路径对不上要失败。
 */
class InstructionPacketFillerTest {

    @Test
    void fillsExistingPath_andKeepsOtherKeys() {
        Map<String, Object> outbound = packet("p0", "30");
        Map<String, Object> filled = InstructionPacketFiller.fill(
                outbound,
                List.of(new SlotMapping("location_ref", "FLD-PNT-002", "request.pointId")),
                Map.of("location_ref", Map.of("id", 9L, "FLD-PNT-002", "pt-1"))
        );
        assertEquals("pt-1", request(filled).get("pointId"));
        assertEquals("30", request(filled).get("height"));
        assertEquals("p0", outbound.get("request") instanceof Map<?, ?> map ? map.get("pointId") : null);
    }

    @Test
    void missingSlot_fails() {
        assertThrows(IllegalArgumentException.class, () -> InstructionPacketFiller.fill(
                packet("", "0"),
                List.of(new SlotMapping("shot_count", "", "request.number")),
                Map.of()
        ));
    }

    @Test
    void missingSource_namesItsPlatformDefinedGroup() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> InstructionPacketFiller.fill(
                        packet("", "0"),
                        List.of(new SlotMapping(
                                "payloadPositionIndex",
                                "",
                                "request.number",
                                "EXECUTOR_MOUNT")),
                        Map.of()));

        assertEquals(
                "这一步缺少执行设备来源字段「payloadPositionIndex」",
                error.getMessage());
    }

    @Test
    void unknownPath_fails() {
        assertThrows(IllegalArgumentException.class, () -> InstructionPacketFiller.fill(
                packet("", "0"),
                List.of(new SlotMapping("shot_count", "", "request.angle")),
                Map.of("shot_count", 1)
        ));
    }

    @Test
    void numberAndBoolean_areStoredAsText() {
        Map<String, Object> filled = InstructionPacketFiller.fill(
                packet("", ""),
                List.of(
                        new SlotMapping("shot_count", "", "request.number"),
                        new SlotMapping("yaw", "", "request.height")
                ),
                Map.of("shot_count", 1, "yaw", true)
        );
        assertEquals("1", request(filled).get("number"));
        assertEquals("true", request(filled).get("height"));
    }

    @Test
    void nestedSourcePath_readsCoordinate() {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("lng", "");
        request.put("lat", "");
        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("request", request);
        Map<String, Object> loc = new LinkedHashMap<>();
        loc.put("longitude", "117.2");
        loc.put("latitude", "34.3");
        Map<String, Object> filled = InstructionPacketFiller.fill(
                outbound,
                List.of(
                        new SlotMapping("loc", "FLD-LOC-014.longitude", "request.lng"),
                        new SlotMapping("loc", "FLD-LOC-014.latitude", "request.lat")
                ),
                Map.of("loc", Map.of("FLD-LOC-014", loc))
        );
        assertEquals("117.2", request(filled).get("lng"));
        assertEquals("34.3", request(filled).get("lat"));
    }

    @Test
    void listItemPath_fillsFirstTargetPoint() {
        Map<String, Object> point = new LinkedHashMap<>();
        point.put("latitude", "");
        point.put("longitude", "");
        point.put("height", "");
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("points", List.of(point));
        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("data", data);
        Map<String, Object> loc = new LinkedHashMap<>();
        loc.put("latitude", "34.3");
        loc.put("longitude", "117.2");
        loc.put("height", "80");
        Map<String, Object> filled = InstructionPacketFiller.fill(
                outbound,
                List.of(
                        new SlotMapping("place", "FLD-LOC-014.latitude", "data.points.0.latitude"),
                        new SlotMapping("place", "FLD-LOC-014.longitude", "data.points.0.longitude"),
                        new SlotMapping("place", "FLD-LOC-014.height", "data.points.0.height")
                ),
                Map.of("place", Map.of("FLD-LOC-014", loc))
        );
        @SuppressWarnings("unchecked")
        Map<String, Object> filledPoint = (Map<String, Object>) ((List<?>) ((Map<?, ?>) filled.get("data")).get("points")).get(0);
        assertEquals("34.3", filledPoint.get("latitude"));
        assertEquals("117.2", filledPoint.get("longitude"));
        assertEquals("80", filledPoint.get("height"));
        assertEquals("", point.get("latitude"));
    }

    @Test
    void emptySlots_returnsCopy() {
        Map<String, Object> filled = InstructionPacketFiller.fill(packet("a", "1"), List.of(), Map.of());
        assertEquals("a", request(filled).get("pointId"));
    }

    private static Map<String, Object> packet(String pointId, String height) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("pointId", pointId);
        request.put("height", height);
        request.put("number", "");
        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("opcode", 200102);
        outbound.put("request", request);
        return outbound;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> request(Map<String, Object> packet) {
        return (Map<String, Object>) packet.get("request");
    }
}
