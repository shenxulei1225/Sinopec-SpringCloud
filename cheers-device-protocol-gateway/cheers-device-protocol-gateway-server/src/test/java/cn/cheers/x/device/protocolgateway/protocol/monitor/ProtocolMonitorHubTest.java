package cn.cheers.x.device.protocolgateway.protocol.monitor;

import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorDeviceDTO;
import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorEventDTO;
import cn.cheers.x.device.protocolgateway.config.DeviceProtocolGatewayProperties;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import cn.cheers.x.device.protocolgateway.transport.InMemoryDeviceSessionRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
                new InMemoryDeviceSessionRegistry(),
                event -> event);
    }

    @Test
    void syncReply_usesResponseName() {
        hub.copyOutbound("dev-1", "{\"opcode\":500104,\"msgId\":\"m1\"}");
        hub.copyInbound("dev-1", "{\"opcode\":500104,\"msgId\":\"m1\",\"code\":200,\"msg\":\"操作成功\"}");
        hub.copyInbound("dev-1", "{\"opcode\":500203,\"msgId\":\"m2\",\"status\":300101}");

        List<ProtocolMonitorEventDTO> rows = hub.snapshot("dev-1");
        assertEquals("巡检任务指令包下发", rows.get(0).identity());
        assertEquals("巡检任务指令包的响应", rows.get(1).identity());
        assertEquals("任务状态", rows.get(2).identity());
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
    void clearDropsOnlyTheSelectedDevice() {
        hub.copyInbound("dev-1", "{\"opcode\":500101,\"msgId\":\"a\"}");
        hub.copyInbound("dev-2", "{\"opcode\":500101,\"msgId\":\"b\"}");

        hub.clear("dev-1");

        assertEquals(0, hub.snapshot("dev-1").size());
        assertEquals(1, hub.snapshot("dev-2").size());
    }

    @Test
    void disconnectKeepsEachDeviceWindow() {
        hub.copyInbound("dev-1", "{\"opcode\":500101,\"msgId\":\"a\"}");
        hub.copyInbound("dev-2", "{\"opcode\":500103,\"msgId\":\"b\"}");
        hub.copySession("dev-2", "设备已断开");

        assertEquals(1, hub.snapshot("dev-1").size());
        assertEquals(2, hub.snapshot("dev-2").size());
        ProtocolMonitorDeviceDTO offline = hub.listDevices().stream()
                .filter(row -> "dev-2".equals(row.deviceId()))
                .findFirst()
                .orElseThrow();
        assertFalse(offline.online());
    }

    @Test
    void unreadablePayloadStillKept() {
        hub.copyInbound("dev-1", "not-json");
        ProtocolMonitorEventDTO row = hub.snapshot("dev-1").get(0);
        assertEquals("无法解析", row.identity());
        assertEquals("not-json", row.payloadJson());
    }
}
