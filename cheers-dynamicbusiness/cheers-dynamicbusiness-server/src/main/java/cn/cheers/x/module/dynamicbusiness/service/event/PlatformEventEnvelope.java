package cn.cheers.x.module.dynamicbusiness.service.event;

import cn.cheers.x.framework.common.exception.ServiceException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 平台内部事件的稳定信封。
 *
 * <p>负责：在生产方、outbox 和消费方之间传递事件主题身份、来源和载荷。</p>
 * <p>不负责：按名称寻找事件主题、保存告警记录或决定执行哪个动作。</p>
 */
public record PlatformEventEnvelope(
        String eventId,
        long eventTopicId,
        String eventCode,
        long tenantId,
        long facilityId,
        String sourceEntityTypeCode,
        long sourceEntityId,
        String phase,
        Instant occurredAt,
        Map<String, Object> attributes
) {

    /**
     * 校验系统边界并复制载荷，防止发布后被调用方继续修改。
     */
    public static PlatformEventEnvelope require(
            String eventId,
            Long eventTopicId,
            String eventCode,
            Long tenantId,
            Long facilityId,
            String sourceEntityTypeCode,
            Long sourceEntityId,
            String phase,
            Instant occurredAt,
            Map<String, Object> attributes
    ) {
        if (blank(eventId) || eventTopicId == null || eventTopicId <= 0
                || blank(eventCode) || tenantId == null || tenantId <= 0
                || facilityId == null || facilityId <= 0
                || blank(sourceEntityTypeCode) || sourceEntityId == null || sourceEntityId <= 0
                || occurredAt == null) {
            throw new ServiceException(400, "事件缺少主题、来源或发生时间");
        }
        Map<String, Object> copied = attributes == null
                ? Map.of()
                : Map.copyOf(new LinkedHashMap<>(attributes));
        return new PlatformEventEnvelope(
                eventId.trim(),
                eventTopicId,
                eventCode.trim(),
                tenantId,
                facilityId,
                sourceEntityTypeCode.trim(),
                sourceEntityId,
                blank(phase) ? null : phase.trim(),
                occurredAt,
                copied);
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
