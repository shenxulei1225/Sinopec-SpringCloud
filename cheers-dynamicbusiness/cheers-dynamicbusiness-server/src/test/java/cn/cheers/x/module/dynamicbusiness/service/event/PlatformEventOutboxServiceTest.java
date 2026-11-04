package cn.cheers.x.module.dynamicbusiness.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlatformEventOutboxServiceTest {

    @Mock
    private PlatformEventOutboxMapper mapper;
    @Mock
    private ObjectProvider<PlatformEventOutboxDeliveryService> deliveryProvider;
    @InjectMocks
    private PlatformEventOutboxService service;

    @Test
    void enqueueKeepsStableTopicAndStartsPending() {
        PlatformEventEnvelope event = PlatformEventEnvelope.require(
                "evt-1", 41L, "alarm.water.high", 123L, 9L,
                "equipment", 77L, "RAISED", Instant.parse("2026-11-02T01:00:00Z"),
                Map.of("reading", 12.3));

        when(deliveryProvider.getIfAvailable()).thenReturn(null);

        service.enqueue(PlatformEventKinds.ALARM_LIFECYCLE, event);

        ArgumentCaptor<PlatformEventOutboxDO> captor = ArgumentCaptor.forClass(PlatformEventOutboxDO.class);
        verify(mapper).insert(captor.capture());
        assertEquals(41L, captor.getValue().getEventTopicId());
        assertEquals(PlatformEventOutboxStatus.PENDING, captor.getValue().getStatus());
        assertEquals(12.3, captor.getValue().getPayload().get("reading"));
    }

    @Test
    void enqueueWithoutTransactionPublishesImmediately() {
        PlatformEventOutboxDeliveryService delivery = org.mockito.Mockito.mock(PlatformEventOutboxDeliveryService.class);
        when(deliveryProvider.getIfAvailable()).thenReturn(delivery);
        doAnswer(invocation -> {
            PlatformEventOutboxDO row = invocation.getArgument(0);
            row.setId(9L);
            return null;
        }).when(mapper).insert(any(PlatformEventOutboxDO.class));

        service.enqueue(PlatformEventKinds.ALARM_LIFECYCLE, PlatformEventEnvelope.require(
                "evt-2", 33L, "alarm-co2-high-raised", 123L, 1L,
                "equipment", 13644L, "RAISED", Instant.parse("2026-11-05T01:00:00Z"),
                Map.of()));

        verify(delivery).deliver(9L);
    }

    @Test
    void kickFailurePutsTheEventBackInLine() {
        PlatformEventOutboxDeliveryService delivery = org.mockito.Mockito.mock(PlatformEventOutboxDeliveryService.class);
        when(deliveryProvider.getIfAvailable()).thenReturn(delivery);
        doAnswer(invocation -> {
            PlatformEventOutboxDO row = invocation.getArgument(0);
            row.setId(9L);
            return null;
        }).when(mapper).insert(any(PlatformEventOutboxDO.class));
        org.mockito.Mockito.doThrow(new RuntimeException("事件通道未打开")).when(delivery).deliver(9L);

        service.enqueue(PlatformEventKinds.ALARM_LIFECYCLE, PlatformEventEnvelope.require(
                "evt-3", 33L, "alarm-co2-high-raised", 123L, 1L,
                "equipment", 13644L, "RAISED", Instant.parse("2026-11-05T01:00:00Z"),
                Map.of()));

        verify(delivery).recordFailure(eq(9L), any(RuntimeException.class));
    }
}
