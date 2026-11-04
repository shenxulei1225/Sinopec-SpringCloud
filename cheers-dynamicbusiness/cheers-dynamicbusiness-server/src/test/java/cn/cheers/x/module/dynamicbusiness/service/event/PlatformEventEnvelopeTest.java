package cn.cheers.x.module.dynamicbusiness.service.event;

import cn.cheers.x.framework.common.exception.ServiceException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlatformEventEnvelopeTest {

    @Test
    void requiresStableTopicIdentityAndCopiesAttributes() {
        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("reading", 12.3);

        PlatformEventEnvelope event = PlatformEventEnvelope.require(
                "evt-1", 41L, "alarm.water.high", 123L, 9L,
                "equipment", 77L, "RAISED", Instant.parse("2026-11-02T01:00:00Z"),
                attributes);
        attributes.put("reading", 99);

        assertEquals(12.3, event.attributes().get("reading"));
        assertThrows(UnsupportedOperationException.class,
                () -> event.attributes().put("x", 1));
    }

    @Test
    void missingTopicReferenceIsRejected() {
        assertThrows(ServiceException.class, () -> PlatformEventEnvelope.require(
                "evt-1", null, "alarm.water.high", 123L, 9L,
                "equipment", 77L, "RAISED", Instant.now(), Map.of()));
    }
}
