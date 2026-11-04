package cn.cheers.x.module.dynamicbusiness.service.event;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PlatformEventMessageCodecTest {

    @Test
    void roundTripKeepsTopicAndKind() {
        PlatformEventMessage source = new PlatformEventMessage(
                "evt-1", 33L, "alarm-co2-high-raised", PlatformEventKinds.ALARM_LIFECYCLE,
                123L, 1L, "equipment", 13644L, "RAISED",
                Instant.parse("2026-11-05T01:00:00Z"), Map.of("reading", 1200));

        PlatformEventMessage read = PlatformEventMessageCodec.fromMap(PlatformEventMessageCodec.toMap(source));

        assertEquals("evt-1", read.eventId());
        assertEquals(33L, read.eventTopicId());
        assertEquals(PlatformEventKinds.ALARM_LIFECYCLE, read.eventKind());
        assertEquals(13644L, read.sourceEntityId());
        assertEquals(1200, read.payload().get("reading"));
    }

    @Test
    void pageFacilityStatsIsNotARuntimeEvent() {
        assertNull(PlatformEventMessageCodec.fromMap(Map.of(
                "messageKind", "alarm_facility",
                "facilityId", 1)));
    }
}
