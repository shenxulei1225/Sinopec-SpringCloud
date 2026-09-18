package cn.cheers.x.device.protocolgateway.protocol.monitor;

import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorEventDTO;
import cn.cheers.x.device.protocolgateway.config.DeviceProtocolGatewayProperties;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import cn.cheers.x.device.protocolgateway.transport.InMemoryDeviceSessionRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 监控窗口：先选设备才订阅；收发都进窗口；超限丢掉最旧的。
 */
class ProtocolMonitorHubTest {

    private ProtocolMonitorHub hub;

    @BeforeEach
    void setUp() {
        DeviceProtocolGatewayProperties properties = new DeviceProtocolGatewayProperties();
        properties.setMonitorWindowSize(3);
        hub = new ProtocolMonitorHub(
                properties,
                new EnvelopeJsonCodec(new ObjectMapper()),
                new InMemoryDeviceSessionRegistry());
    }

    @Test
    void subscribeWithoutDevice_rejected() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> hub.subscribe(" "));
        assertTrue(error.getMessage().contains("必须先选一台设备"));
    }

    @Test
    void inboundAndOutboundStayInWindow() {
        hub.copyInbound("dev-1", "{\"opcode\":500103,\"msgId\":\"u1\"}");
        hub.copyOutbound("dev-1", "{\"opcode\":500106,\"msgId\":\"u1\",\"data\":{\"opcode\":500103}}");

        List<ProtocolMonitorEventDTO> rows = hub.snapshot("dev-1");
        assertEquals(2, rows.size());
        assertEquals(ProtocolMonitorHub.DIRECTION_INBOUND, rows.get(0).direction());
        assertEquals("定时上报设备状态", rows.get(0).identity());
        assertEquals(ProtocolMonitorHub.DIRECTION_OUTBOUND, rows.get(1).direction());
        assertEquals("回执", rows.get(1).identity());
    }

    @Test
    void windowDropsOldest() {
        hub.copyInbound("dev-1", "{\"opcode\":500105,\"msgId\":\"1\"}");
        hub.copyInbound("dev-1", "{\"opcode\":500105,\"msgId\":\"2\"}");
        hub.copyInbound("dev-1", "{\"opcode\":500105,\"msgId\":\"3\"}");
        hub.copyInbound("dev-1", "{\"opcode\":500105,\"msgId\":\"4\"}");

        List<ProtocolMonitorEventDTO> rows = hub.snapshot("dev-1");
        assertEquals(3, rows.size());
        assertEquals("2", rows.get(0).msgId());
        assertEquals("4", rows.get(2).msgId());
    }

    @Test
    void unreadablePayloadStillKept() {
        hub.copyInbound("dev-1", "not-json");
        ProtocolMonitorEventDTO row = hub.snapshot("dev-1").get(0);
        assertEquals("无法解析", row.identity());
        assertEquals("not-json", row.payloadJson());
    }
}
