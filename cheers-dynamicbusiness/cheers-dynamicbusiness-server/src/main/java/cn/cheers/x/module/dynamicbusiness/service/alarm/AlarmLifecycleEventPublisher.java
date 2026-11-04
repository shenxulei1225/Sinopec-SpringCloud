package cn.cheers.x.module.dynamicbusiness.service.alarm;

import cn.cheers.x.module.dynamicbusiness.service.event.PlatformEventEnvelope;
import cn.cheers.x.module.dynamicbusiness.service.event.PlatformEventKinds;
import cn.cheers.x.module.dynamicbusiness.service.event.PlatformEventOutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 告警生命周期变化的可靠事件发布器。
 *
 * <p>告警写入方只提供规则、记录、来源和事实。本服务按规则引用的事件主题写 outbox。</p>
 * <p>不查询联动，也不调用联动入口，也不调用动作。</p>
 */
@Service
@RequiredArgsConstructor
public class AlarmLifecycleEventPublisher {

    private final AlarmRuleEventTopicResolver topicResolver;
    private final PlatformEventOutboxService outboxService;

    public void publish(
            long tenantId,
            long facilityId,
            long alarmRuleId,
            long alarmRecordId,
            long sourceEquipmentId,
            String phase,
            Map<String, Object> facts
    ) {
        AlarmRuleEventTopicResolver.EventTopic topic = topicResolver.resolve(alarmRuleId, phase);
        if (topic == null) {
            return;
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        if (facts != null) {
            payload.putAll(facts);
        }
        payload.put("alarmRuleId", alarmRuleId);
        payload.put("alarmRecordId", alarmRecordId);
        PlatformEventEnvelope event = PlatformEventEnvelope.require(
                UUID.randomUUID().toString(),
                topic.id(),
                topic.code(),
                tenantId,
                facilityId,
                "equipment",
                sourceEquipmentId,
                phase,
                Instant.now(),
                payload);
        outboxService.enqueue(PlatformEventKinds.ALARM_LIFECYCLE, event);
    }
}
