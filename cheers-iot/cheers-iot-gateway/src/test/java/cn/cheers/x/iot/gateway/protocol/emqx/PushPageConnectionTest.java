package cn.cheers.x.iot.gateway.protocol.emqx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PushPageConnectionTest {

    @Test
    void parsesPageUsername() {
        PushPageConnection.Identity identity = PushPageConnection.parseUsername("page:1:100");
        assertEquals(1L, identity.tenantId());
        assertEquals(100L, identity.userId());
    }

    @Test
    void rejectsDeviceUsernameAndBadPageUsername() {
        assertNull(PushPageConnection.parseUsername("device-user"));
        assertNull(PushPageConnection.parseUsername("page:0:1"));
        assertFalse(PushPageConnection.isPageClient("iot-gateway-mqtt"));
        assertTrue(PushPageConnection.isPageClient("page-tab-1"));
    }

    @Test
    void allowsPageTopics() {
        assertTrue(PushPageConnection.topicAllowed("alarm/facility/12"));
        assertTrue(PushPageConnection.topicAllowed("device/88/position"));
        assertTrue(PushPageConnection.topicAllowed("device/88/status"));
        assertTrue(PushPageConnection.topicAllowed("inspection/execution/15/#"));
        assertTrue(PushPageConnection.topicAllowed("inspection/device/dock-1/#"));
        assertFalse(PushPageConnection.topicAllowed("device/88/alarm"));
        assertFalse(PushPageConnection.topicAllowed("inspection/execution/0/#"));
        assertFalse(PushPageConnection.topicAllowed("inspection/execution/15/photo"));
    }
}
