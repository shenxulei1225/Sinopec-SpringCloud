package cn.cheers.x.module.dynamicbusiness.service.event;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 运行时事件在消息通道上的正文。
 *
 * <p>管什么：生产方和消费方用同一份字段读写。</p>
 * <p>不管什么：不填主题名，不猜缺字段。</p>
 */
public final class PlatformEventMessageCodec {

    private PlatformEventMessageCodec() {
    }

    public static Map<String, Object> toMap(PlatformEventMessage message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("eventId", message.eventId());
        body.put("eventTopicId", message.eventTopicId());
        body.put("eventCode", message.eventCode());
        body.put("eventKind", message.eventKind());
        body.put("tenantId", message.tenantId());
        body.put("facilityId", message.facilityId());
        body.put("sourceEntityTypeCode", message.sourceEntityTypeCode());
        body.put("sourceEntityId", message.sourceEntityId());
        body.put("phase", message.phase());
        body.put("occurredAt", message.occurredAt() == null ? null : message.occurredAt().toString());
        body.put("payload", message.payload() == null ? Map.of() : message.payload());
        return body;
    }

    /**
     * 读出一条运行时事件。没有事件编号就当作不是这条通道上的事件。
     */
    @SuppressWarnings("unchecked")
    public static PlatformEventMessage fromMap(Map<?, ?> body) {
        if (body == null) {
            return null;
        }
        String eventId = text(body.get("eventId"));
        String eventKind = text(body.get("eventKind"));
        if (eventId == null || eventKind == null) {
            return null;
        }
        Object payloadRaw = body.get("payload");
        Map<String, Object> payload = payloadRaw instanceof Map<?, ?> nested
                ? copyPayload(nested)
                : Map.of();
        Instant occurredAt = parseInstant(body.get("occurredAt"));
        if (occurredAt == null) {
            return null;
        }
        return new PlatformEventMessage(
                eventId,
                longValue(body.get("eventTopicId")),
                text(body.get("eventCode")),
                eventKind,
                longValue(body.get("tenantId")),
                longValue(body.get("facilityId")),
                text(body.get("sourceEntityTypeCode")),
                longValue(body.get("sourceEntityId")),
                text(body.get("phase")),
                occurredAt,
                payload);
    }

    private static Map<String, Object> copyPayload(Map<?, ?> nested) {
        Map<String, Object> payload = new LinkedHashMap<>();
        nested.forEach((key, value) -> {
            if (key != null) {
                payload.put(String.valueOf(key), value);
            }
        });
        return Map.copyOf(payload);
    }

    private static Instant parseInstant(Object raw) {
        if (raw == null) {
            return null;
        }
        try {
            return Instant.parse(String.valueOf(raw));
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private static long longValue(Object raw) {
        if (raw instanceof Number number) {
            return number.longValue();
        }
        if (raw == null) {
            return 0L;
        }
        try {
            return Long.parseLong(String.valueOf(raw));
        } catch (NumberFormatException ignored) {
            return 0L;
        }
    }

    private static String text(Object raw) {
        if (raw == null) {
            return null;
        }
        String value = String.valueOf(raw).trim();
        return value.isEmpty() ? null : value;
    }
}
