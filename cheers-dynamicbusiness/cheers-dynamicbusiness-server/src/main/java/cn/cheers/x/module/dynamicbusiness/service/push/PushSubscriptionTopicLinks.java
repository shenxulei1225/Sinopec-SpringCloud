package cn.cheers.x.module.dynamicbusiness.service.push;

import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 一场订阅覆盖哪些事件主题。
 * 整段过滤「这次执行下面全部」「这台设备下面全部」对上名字规则的前缀。
 * 已经填好编号的一条主题，对上那一条名字规则。
 * 不负责写入引用，也不改对象列的名称。
 */
final class PushSubscriptionTopicLinks {

    private PushSubscriptionTopicLinks() {
    }

    static List<Long> coveredIds(String topic, String patternFieldCode, List<EntityRespDTO> topics) {
        List<Long> ids = new ArrayList<>();
        if (topic == null || topics == null) {
            return ids;
        }
        String value = topic.trim();
        String wildcardPrefix = wildcardPrefix(value);
        for (EntityRespDTO row : topics) {
            if (row == null || row.getId() == null) {
                continue;
            }
            String pattern = text(row, patternFieldCode);
            if (pattern == null) {
                continue;
            }
            if (wildcardPrefix != null) {
                if (pattern.startsWith(wildcardPrefix)) {
                    ids.add(row.getId());
                }
                continue;
            }
            if (Pattern.compile(PushTopicKinds.toRegex(pattern)).matcher(value).matches()) {
                ids.add(row.getId());
            }
        }
        return ids;
    }

    /**
     * inspection/execution/15/# → inspection/execution/
     * 不是这种整段过滤时返回空，改走单条名字规则。
     */
    private static String wildcardPrefix(String topic) {
        int slash = topic.lastIndexOf("/#");
        if (slash < 0 || slash != topic.length() - 2) {
            return null;
        }
        int folder = topic.lastIndexOf('/', slash - 1);
        if (folder <= 0) {
            return null;
        }
        return topic.substring(0, folder + 1);
    }

    private static String text(EntityRespDTO row, String code) {
        String fromBase = text(row.getBaseFields(), code);
        if (fromBase != null) {
            return fromBase;
        }
        return text(row.getCustomFields(), code);
    }

    private static String text(java.util.Map<String, Object> fields, String code) {
        if (fields == null || fields.get(code) == null) {
            return null;
        }
        String value = String.valueOf(fields.get(code)).trim();
        return value.isEmpty() ? null : value;
    }
}
