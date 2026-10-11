package cn.cheers.x.device.protocolgateway.protocol.monitor;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ProtocolMonitorDownlinkFillerTest {

    @Test
    void fillsBlankIdentitySlotsOnly() {
        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("msgId", "");
        outbound.put("opcode", 500101);
        outbound.put("deviceId", "");
        outbound.put("templateId", "kept");

        Map<String, Object> filled = ProtocolMonitorDownlinkFiller.fillIdentity(outbound, "dev-1", "msg-1");

        assertEquals("dev-1", filled.get("deviceId"));
        assertEquals("msg-1", filled.get("msgId"));
        assertEquals("kept", filled.get("templateId"));
        assertEquals("", outbound.get("deviceId"));
        assertFalse(filled.containsKey("lat"));
    }

    @Test
    void fillsBlankTaskSlotsFromProtocolSample() {
        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("opcode", 500201);
        outbound.put("templateId", "");
        outbound.put("taskId", "");
        outbound.put("executionTime", 0);

        Map<String, Object> filled = ProtocolMonitorDownlinkFiller.fillBlankTaskSlots(outbound);

        assertEquals("100111", filled.get("templateId"));
        assertEquals("111122223301", filled.get("taskId"));
        assertEquals(true, ((Number) filled.get("executionTime")).longValue() > 0L);
        assertFalse(ProtocolMonitorDownlinkFiller.fillBlankTaskSlots(Map.of("opcode", 500303)).containsKey("templateId"));
    }

    @Test
    void innerRequestCannotSendAlone() {
        Map<String, Object> outbound = Map.of("opcode", 200102, "request", Map.of("lat", ""));
        assertEquals(true, ProtocolMonitorDownlinkFiller.isInnerStep(outbound));
    }
}
