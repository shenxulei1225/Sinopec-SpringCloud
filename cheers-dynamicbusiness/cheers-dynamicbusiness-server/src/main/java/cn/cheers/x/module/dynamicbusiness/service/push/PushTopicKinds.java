package cn.cheers.x.module.dynamicbusiness.service.push;

import cn.cheers.x.module.dynamicbusiness.api.push.InspectionPushTopics;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 推送主题的种类，以及页面订阅能不能写入订阅账。
 * 数据目录里的种类行由这里写出去，给人看。
 * 巡检图层订的是这次执行或这台设备下面的主题。这两种过滤也允许写入订阅账。
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
    /** 实时画面上的飞机状态。跟设备走，和旧的「设备状态」不是同一条。 */
    public static final Kind DEVICE_REALTIME = kind(InspectionPushTopics.DEVICE_STATUS);
    /** 巡检图层上的当前位置。名字规则和页面报文一致。 */
    public static final Kind EXECUTION_TRAJECTORY = kind(InspectionPushTopics.TRAJECTORY);
    /** 巡检图层上的步骤完成情况。 */
    public static final Kind EXECUTION_STEP = kind(InspectionPushTopics.STEP);
    /** 这一次执行的整次状态。 */
    public static final Kind EXECUTION_RUN = kind(InspectionPushTopics.RUN);
    /** 指令包已收下。下发和答卷都走这一条，窗口里仍是同一行。 */
    public static final Kind COMMAND_ACCEPTED = kind(InspectionPushTopics.COMMAND_ACCEPTED);
    public static final Kind START_ACCEPTED = kind(InspectionPushTopics.START_ACCEPTED);
    public static final Kind STOP_ACCEPTED = kind(InspectionPushTopics.STOP_ACCEPTED);
    public static final Kind REPORT_ACCEPTED = kind(InspectionPushTopics.REPORT_ACCEPTED);
    public static final Kind DEVICE_FAULT = kind(InspectionPushTopics.DEVICE_FAULT);
    public static final Kind COMMAND_PACKAGE = kind(InspectionPushTopics.COMMAND_PACKAGE);
    public static final Kind COMMAND_PACKAGE_DELETED = kind(InspectionPushTopics.COMMAND_PACKAGE_DELETED);
    public static final Kind COMMAND_PACKAGE_CATALOG = kind(InspectionPushTopics.COMMAND_PACKAGE_CATALOG);
    public static final Kind LIVE_START_ACCEPTED = kind(InspectionPushTopics.LIVE_START_ACCEPTED);
    public static final Kind LIVE_STOP_ACCEPTED = kind(InspectionPushTopics.LIVE_STOP_ACCEPTED);
    public static final Kind DEVICE_PRESENCE = kind(InspectionPushTopics.DEVICE_PRESENCE);

    public static final List<Kind> ALL = List.of(
            ALARM_FACILITY,
            DEVICE_POSITION,
            DEVICE_STATUS,
            DEVICE_REALTIME,
            EXECUTION_TRAJECTORY,
            EXECUTION_STEP,
            EXECUTION_RUN,
            COMMAND_ACCEPTED,
            START_ACCEPTED,
            STOP_ACCEPTED,
            REPORT_ACCEPTED,
            DEVICE_FAULT,
            COMMAND_PACKAGE,
            COMMAND_PACKAGE_DELETED,
            COMMAND_PACKAGE_CATALOG,
            LIVE_START_ACCEPTED,
            LIVE_STOP_ACCEPTED,
            DEVICE_PRESENCE);

    private static Kind kind(InspectionPushTopics.Topic topic) {
        return new Kind(topic.code(), topic.name(), topic.namePattern());
    }

    private static final List<Pattern> PATTERNS = ALL.stream()
            .map(kind -> Pattern.compile(toRegex(kind.namePattern)))
            .toList();

    /**
     * 巡检图层实际订的是「这次执行下面全部」和「这台设备下面全部」。
     * 一条已经填好编号的具体主题也同样收下。
     */
    private static final List<Pattern> PAGE_FILTERS = List.of(
            Pattern.compile("^inspection/execution/[1-9][0-9]*/#$"),
            Pattern.compile("^inspection/device/[A-Za-z0-9._-]+/#$"),
            Pattern.compile("^inspection/execution/[1-9][0-9]*/[A-Za-z0-9_-]+$"),
            Pattern.compile("^inspection/device/[A-Za-z0-9._-]+/[A-Za-z0-9_-]+$"));

    private PushTopicKinds() {
    }

    /**
     * 这条主题能否写入推送订阅。
     * 固定种类、巡检图层的整段过滤、以及已经填好编号的巡检主题，都收下。
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
        for (Pattern pattern : PAGE_FILTERS) {
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
