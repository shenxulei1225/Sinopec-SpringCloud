package cn.cheers.x.module.dynamicbusiness.service.linkage;

import cn.cheers.x.module.dynamicbusiness.service.event.PlatformEventKinds;
import cn.cheers.x.module.dynamicbusiness.service.event.PlatformEventMessage;
import cn.cheers.x.module.dynamicbusiness.service.event.PlatformEventMessageCodec;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LinkageEventChannelConsumerTest {

    @Mock
    private LinkageEventHandler linkageEventHandler;
    @Mock
    private LinkageActionStatusHandler linkageActionStatusHandler;

    private LinkageEventChannelConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new LinkageEventChannelConsumer(
                new ObjectMapper(), linkageEventHandler, linkageActionStatusHandler);
    }

    @Test
    void runtimeEventGoesToLinkageHandler() throws Exception {
        when(linkageEventHandler.supports(PlatformEventKinds.ALARM_LIFECYCLE)).thenReturn(true);
        when(linkageActionStatusHandler.supports(PlatformEventKinds.ALARM_LIFECYCLE)).thenReturn(false);
        PlatformEventMessage event = new PlatformEventMessage(
                "evt-1", 33L, "alarm-co2-high-raised", PlatformEventKinds.ALARM_LIFECYCLE,
                123L, 1L, "equipment", 13644L, "RAISED",
                Instant.parse("2026-11-05T01:00:00Z"), Map.of("reading", 1200));

        consumer.accept(new ObjectMapper().writeValueAsBytes(PlatformEventMessageCodec.toMap(event)));

        verify(linkageEventHandler).handle(any(PlatformEventMessage.class));
        verify(linkageActionStatusHandler, never()).handle(any());
    }

    @Test
    void pageFacilityStatsDoesNotEnterLinkage() throws Exception {
        consumer.accept(new ObjectMapper().writeValueAsBytes(Map.of(
                "messageKind", "alarm_facility",
                "facilityId", 1)));

        verify(linkageEventHandler, never()).supports(any());
        verify(linkageEventHandler, never()).handle(any());
    }
}
