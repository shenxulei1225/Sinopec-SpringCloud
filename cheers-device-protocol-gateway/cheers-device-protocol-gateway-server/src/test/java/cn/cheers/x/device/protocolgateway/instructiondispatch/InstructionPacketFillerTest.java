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
    void unknownPath_fails() {
        assertThrows(IllegalArgumentException.class, () -> InstructionPacketFiller.fill(
                packet("", "0"),
                List.of(new SlotMapping("shot_count", "", "request.angle")),
                Map.of("shot_count", 1)
        ));
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
