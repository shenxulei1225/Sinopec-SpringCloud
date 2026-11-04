package cn.cheers.x.module.dynamicbusiness.service.event;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.service.push.PushCatalogQueryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlatformEventOutboxDeliveryServiceTest {

    @Mock
    private PlatformEventOutboxMapper mapper;
    @Mock
    private PushCatalogQueryService pushCatalogQueryService;
    @Mock
    private PlatformEventChannelPublisher publisher;

    private PlatformEventOutboxDeliveryService service;

    @BeforeEach
    void setUp() {
        service = new PlatformEventOutboxDeliveryService(
                mapper, pushCatalogQueryService, publisher, new ObjectMapper());
    }

    @Test
    void alarmEventPublishesToNameRuleTopic() throws Exception {
        when(mapper.claim(9L)).thenReturn(1);
        when(mapper.selectById(9L)).thenReturn(alarmRow());
        when(pushCatalogQueryService.namePatternByTopicId(33L)).thenReturn("alarm/co2-high/{deviceId}");

        service.deliver(9L);

        ArgumentCaptor<byte[]> payload = ArgumentCaptor.forClass(byte[].class);
        verify(publisher).publish(eq("alarm/co2-high/13644"), payload.capture());
        Map<?, ?> body = new ObjectMapper().readValue(payload.getValue(), Map.class);
        assertEquals("evt-1", body.get("eventId"));
        assertEquals(33, body.get("eventTopicId"));
        verify(mapper).updateById(any(PlatformEventOutboxDO.class));
    }

    @Test
    void missingNameRuleRefusesToPublish() {
        when(mapper.claim(9L)).thenReturn(1);
        when(mapper.selectById(9L)).thenReturn(alarmRow());
        when(pushCatalogQueryService.namePatternByTopicId(33L)).thenReturn(null);

        assertThrows(ServiceException.class, () -> service.deliver(9L));
        verify(publisher, never()).publish(any(), any());
    }

    @Test
    void actionStatusUsesPlatformTopic() {
        PlatformEventOutboxDO row = alarmRow();
        row.setEventTopicId(0L);
        row.setEventKind(PlatformEventKinds.ACTION_EXECUTION_STATUS_CHANGED);
        row.setEventCode(PlatformEventKinds.ACTION_EXECUTION_STATUS_CHANGED);
        when(mapper.claim(9L)).thenReturn(1);
        when(mapper.selectById(9L)).thenReturn(row);

        service.deliver(9L);

        verify(publisher).publish(eq(PlatformEventChannelTopics.ACTION_STATUS), any());
        verify(pushCatalogQueryService, never()).namePatternByTopicId(org.mockito.ArgumentMatchers.anyLong());
    }

    private static PlatformEventOutboxDO alarmRow() {
        PlatformEventOutboxDO row = new PlatformEventOutboxDO();
        row.setId(9L);
        row.setEventId("evt-1");
        row.setEventTopicId(33L);
        row.setEventCode("alarm-co2-high-raised");
        row.setEventKind(PlatformEventKinds.ALARM_LIFECYCLE);
        row.setFacilityId(1L);
        row.setSourceEntityTypeCode("equipment");
        row.setSourceEntityId(13644L);
        row.setPhase("RAISED");
        row.setOccurredAt(LocalDateTime.of(2026, 11, 5, 1, 0));
        row.setPayload(Map.of("reading", 1200));
        row.setTenantId(123L);
        return row;
    }
}
