package cn.cheers.x.module.dynamicbusiness.service.push;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 推送主题的三种固定种类。
 * 数据目录里的种类行由这里写出去，给人看。
 * 订阅能不能写入，也只认这里的名字规则。
 * 不负责按目录里改过的规则放行，也不负责真正把消息送出去。
 */
public final class PushTopicKinds {

    public record Kind(String code, String name, String namePattern) {
    }

    public static final Kind ALARM_FACILITY = new Kind(
            "alarm_facility", "场站告警", "alarm/facility/{facilityId}");
    public static final Kind DEVICE_POSITION = new Kind(
            "device_position", "设备位置", "device/{deviceId}/position");
    public static final Kind DEVICE_STATUS = new Kind(
            "device_status", "设备状态", "device/{deviceId}/status");

    public static final List<Kind> ALL = List.of(ALARM_FACILITY, DEVICE_POSITION, DEVICE_STATUS);

    private static final List<Pattern> PATTERNS = ALL.stream()
            .map(kind -> Pattern.compile(toRegex(kind.namePattern)))
            .toList();

    private PushTopicKinds() {
    }

    /**
     * 这条主题是否属于三种固定名字规则之一。
     * 花括号那一段只允许字母、数字、下划线和短横线。
     */
    public static boolean matches(String topic) {
        if (topic == null || topic.isBlank()) {
            return false;
        }
        String value = topic.trim();
        for (Pattern pattern : PATTERNS) {
            if (pattern.matcher(value).matches()) {
                return true;
            }
        }
        return false;
    }

    static String toRegex(String namePattern) {
        StringBuilder regex = new StringBuilder("^");
        int cursor = 0;
        while (cursor < namePattern.length()) {
            int open = namePattern.indexOf('{', cursor);
            if (open < 0) {
                regex.append(Pattern.quote(namePattern.substring(cursor)));
                break;
            }
            int close = namePattern.indexOf('}', open);
            if (close < 0) {
                throw new IllegalArgumentException("名字规则缺少右花括号: " + namePattern);
            }
            regex.append(Pattern.quote(namePattern.substring(cursor, open)));
            regex.append("[A-Za-z0-9_-]+");
            cursor = close + 1;
        }
        regex.append('$');
        return regex.toString();
    }
}
