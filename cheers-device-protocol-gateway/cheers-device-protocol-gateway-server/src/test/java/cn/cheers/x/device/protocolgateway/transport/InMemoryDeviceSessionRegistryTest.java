package cn.cheers.x.device.protocolgateway.transport;

import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;

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
}
