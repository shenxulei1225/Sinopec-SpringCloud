package cn.cheers.x.device.protocolgateway.djicloud;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DjiCloudInboundTopicsTest {

    @Test
    void subscribesEveryDevicePushedMqttTopic() {
        List<String> filters = DjiCloudInboundTopics.subscriptions().stream()
                .map(DjiCloudInboundTopics.Subscription::filter)
                .toList();

        assertTrue(filters.contains("sys/product/+/status"));
        assertTrue(filters.contains("thing/product/+/osd"));
        assertTrue(filters.contains("thing/product/+/state"));
        assertTrue(filters.contains("thing/product/+/events"));
        assertTrue(filters.contains("thing/product/+/requests"));
        assertTrue(filters.contains("thing/product/+/services_reply"));
        assertTrue(filters.contains("thing/product/+/property/set_reply"));
        assertTrue(filters.contains("thing/product/+/drc/up"));
        assertEquals(8, filters.size());
        for (String cloudPublished : DjiCloudInboundTopics.cloudPublishedFilters()) {
            assertTrue(!filters.contains(cloudPublished));
        }
    }

    @Test
    void stateIsKeptAndEventsThatAskForReplyAreAcked() {
        DjiCloudTopicFrameRegistry registry = new DjiCloudTopicFrameRegistry(new ObjectMapper());
        byte[] state = "{\"data\":{}}".getBytes(StandardCharsets.UTF_8);

        assertNull(registry.accept("thing/product/dock-1/state", state));
        assertNotNull(registry.latest("thing/product/dock-1/state"));

        DjiCloudTopicFrameRegistry.Reply reply = registry.accept(
                "thing/product/dock-1/events",
                "{\"method\":\"flighttask_progress\",\"tid\":\"t1\",\"bid\":\"b1\",\"need_reply\":1,\"data\":{}}"
                        .getBytes(StandardCharsets.UTF_8));

        assertEquals("thing/product/dock-1/events_reply", reply.topic());
        assertTrue(reply.json().contains("\"result\":0"));
        assertTrue(reply.json().contains("\"gateway\":\"dock-1\""));
        assertNull(registry.accept("thing/product/dock-1/osd", state));
    }
}
