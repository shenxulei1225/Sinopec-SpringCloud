package cn.cheers.x.module.dynamicbusiness.service.event;

import java.time.Instant;
import java.util.Map;

/**
 * 消息通道上的一条运行时事件。
 */
public record PlatformEventMessage(
        String eventId,
        long eventTopicId,
        String eventCode,
        String eventKind,
        long tenantId,
        long facilityId,
        String sourceEntityTypeCode,
        long sourceEntityId,
        String phase,
        Instant occurredAt,
        Map<String, Object> payload
) {
}
