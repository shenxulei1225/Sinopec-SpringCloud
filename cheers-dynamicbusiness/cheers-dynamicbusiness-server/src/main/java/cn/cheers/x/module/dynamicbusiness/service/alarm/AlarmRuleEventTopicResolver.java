package cn.cheers.x.module.dynamicbusiness.service.alarm;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.dataprocess.AlarmTypeCatalog;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityRpcFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 从告警规则的标准引用字段读取发布事件主题。
 *
 * <p>禁止：按告警名称寻找事件、读取字段描述或给缺失规则补默认主题。</p>
 */
@Service
@RequiredArgsConstructor
public class AlarmRuleEventTopicResolver {

    public static final String ALARM_RULE_DIRECTORY = "alarm_rule";
    public static final String EVENT_TOPIC_DIRECTORY = "event_topic";
    public static final String RAISED_EVENT_TOPIC_FIELD = "alarm_raised_event_topic_ref";
    public static final String CLEARED_EVENT_TOPIC_FIELD = "alarm_cleared_event_topic_ref";

    private final EntityRpcFacadeService entityRpcFacadeService;

    public EventTopic resolve(long alarmRuleId, String phase) {
        EntityRespDTO rule = entityRpcFacadeService.get(alarmRuleId, ALARM_RULE_DIRECTORY);
        if (rule == null) {
            throw new ServiceException(404, "告警规则不存在");
        }
        String fieldCode = AlarmTypeCatalog.PHASE_CLEARED.equals(phase)
                ? CLEARED_EVENT_TOPIC_FIELD
                : RAISED_EVENT_TOPIC_FIELD;
        Long topicId = FacilityOwningFieldCodes.extractId(value(rule, fieldCode));
        if (topicId == null && AlarmTypeCatalog.PHASE_CLEARED.equals(phase)) {
            return null;
        }
        if (topicId == null) {
            throw new ServiceException(400, "告警规则没有关联告警事件");
        }
        EntityRespDTO topic = entityRpcFacadeService.get(topicId, EVENT_TOPIC_DIRECTORY);
        String code = topic == null ? null : text(topic.getBaseFields(), "code");
        if (code == null || code.isBlank()) {
            throw new ServiceException(400, "告警规则关联的事件主题没有稳定编码");
        }
        return new EventTopic(topicId, code.trim());
    }

    private static Object value(EntityRespDTO entity, String fieldCode) {
        Object value = entity.getCustomFields() == null ? null : entity.getCustomFields().get(fieldCode);
        if (value == null && entity.getBaseFields() != null) {
            value = entity.getBaseFields().get(fieldCode);
        }
        return value;
    }

    private static String text(Map<String, Object> values, String key) {
        return values == null || values.get(key) == null ? null : String.valueOf(values.get(key));
    }

    public record EventTopic(long id, String code) {
    }
}
