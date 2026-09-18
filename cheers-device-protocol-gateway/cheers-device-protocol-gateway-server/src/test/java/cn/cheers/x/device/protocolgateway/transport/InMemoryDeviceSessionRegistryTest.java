package cn.cheers.x.device.protocolgateway.transport;

import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InMemoryDeviceSessionRegistryTest {

    @Test
    void bindAndFindByDeviceId() {
        InMemoryDeviceSessionRegistry registry = new InMemoryDeviceSessionRegistry();
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getUri()).thenReturn(URI.create("ws://localhost:8095/device-001"));

        registry.bind("device-001", session);

        assertTrue(registry.find("device-001").isPresent());
        assertEquals(session, registry.find("device-001").orElseThrow());

        registry.unbind("device-001");
        assertTrue(registry.find("device-001").isEmpty());
    }

    @Test
    void listOnlineOnlyIncludesOpenSessions() {
        InMemoryDeviceSessionRegistry registry = new InMemoryDeviceSessionRegistry();
        WebSocketSession open = mock(WebSocketSession.class);
        WebSocketSession closed = mock(WebSocketSession.class);
        when(open.isOpen()).thenReturn(true);
        when(closed.isOpen()).thenReturn(false);

        registry.bind("online-1", open);
        registry.bind("offline-1", closed);

        assertEquals(List.of("online-1"), registry.listOnlineDeviceIds());
        assertTrue(registry.isOnline("online-1"));
        assertTrue(!registry.isOnline("offline-1"));
    }
}
