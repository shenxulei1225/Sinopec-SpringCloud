package cn.cheers.x.device.protocolgateway.protocol.monitor;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void fillsHeartbeatTimestampWhenPlaceholderIsZero() {
        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("opcode", 500105);
        outbound.put("msgId", "");
        outbound.put("time", 0);

        Map<String, Object> filled = ProtocolMonitorDownlinkFiller.fillIdentity(outbound, "dev-1", "hb-1");

        assertEquals("hb-1", filled.get("msgId"));
        assertEquals(true, ((Number) filled.get("time")).longValue() > 1_000_000_000L);
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
    void reportSwitchStart_writesPageFrequencyAndTotal() {
        Map<String, Object> packet = new LinkedHashMap<>();
        packet.put("opcode", 500102);
        packet.put("enable", false);
        packet.put("frequency", 0);
        packet.put("total", 0);

        ProtocolMonitorDownlinkFiller.applyReportSwitch(packet, true, 30, 100);

        assertEquals(true, packet.get("enable"));
        assertEquals(30, packet.get("frequency"));
        assertEquals(100, packet.get("total"));
    }

    @Test
    void reportSwitchStop_writesEnableFalseAndPageCounts() {
        Map<String, Object> packet = new LinkedHashMap<>();
        packet.put("enable", false);
        packet.put("frequency", 0);
        packet.put("total", 0);

        ProtocolMonitorDownlinkFiller.applyReportSwitch(packet, false, 30, 100);

        assertEquals(false, packet.get("enable"));
        assertEquals(30, packet.get("frequency"));
        assertEquals(100, packet.get("total"));
    }

    @Test
    void reportSwitchStart_rejectsZeroFrequency() {
        Map<String, Object> packet = new LinkedHashMap<>();
        packet.put("enable", false);
        packet.put("frequency", 0);
        packet.put("total", 0);

        assertThrows(
                IllegalArgumentException.class,
                () -> ProtocolMonitorDownlinkFiller.applyReportSwitch(packet, true, 0, 100));
    }

    @Test
    void streamAddress_writesRtmp() {
        Map<String, Object> packet = new LinkedHashMap<>();
        packet.put("opcode", 500401);
        packet.put("streamUrl", "");

        ProtocolMonitorDownlinkFiller.applyStreamAddress(packet, " rtsp://127.0.0.1:8554/patrol-1 ");

        assertEquals("rtsp://127.0.0.1:8554/patrol-1", packet.get("streamUrl"));
    }

    @Test
    void streamAddress_writesRecordAndLiveSlots() {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("streamUrl", "");
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("url", "");
        Map<String, Object> packet = new LinkedHashMap<>();
        packet.put("request", request);
        packet.put("data", data);

        ProtocolMonitorDownlinkFiller.applyStreamAddress(packet, "rtsp://host/patrol-9");

        assertEquals("rtsp://host/patrol-9", request.get("streamUrl"));
        assertEquals("rtsp://host/patrol-9", data.get("url"));
    }

    @Test
    void streamAddress_rejectsBlank() {
        Map<String, Object> packet = new LinkedHashMap<>();
        packet.put("streamUrl", "");

        assertThrows(
                IllegalArgumentException.class,
                () -> ProtocolMonitorDownlinkFiller.applyStreamAddress(packet, "  "));
    }

    @Test
    void innerRequestCannotSendAlone() {
        Map<String, Object> outbound = Map.of("opcode", 200102, "request", Map.of("lat", ""));
        assertEquals(true, ProtocolMonitorDownlinkFiller.isInnerStep(outbound));
    }
}
