package cn.cheers.x.module.dynamicbusiness.service.event;

/**
 * 消息通道上的主题怎么从事件主题的名字规则填出来。
 *
 * <p>管什么：发布时填占位，订阅时把占位收成一层通配。</p>
 * <p>不管什么：不补名字规则，不调用联动，不决定页面订哪些主题。</p>
 */
public final class PlatformEventChannelTopics {

    /**
     * 动作执行状态变化发到消息通道时用的主题。
     * 这不是用户事件主题目录里的一行。
     */
    public static final String ACTION_STATUS = "platform/action-execution-status";

    private PlatformEventChannelTopics() {
    }

    /**
     * 用这场站和这台设备填名字规则。
     *
     * @return 填好的主题。规则空着、还剩占位或编号无效时返回 null，调用方拒绝发出。
     */
    public static String fill(String pattern, long facilityId, long sourceEntityId) {
        if (pattern == null || pattern.isBlank() || facilityId <= 0 || sourceEntityId <= 0) {
            return null;
        }
        String filled = pattern
                .replace("{deviceId}", Long.toString(sourceEntityId))
                .replace("{设备编号}", Long.toString(sourceEntityId))
                .replace("{facilityId}", Long.toString(facilityId))
                .replace("{场站编号}", Long.toString(facilityId));
        if (filled.indexOf('{') >= 0) {
            return null;
        }
        return filled;
    }

    /**
     * 把名字规则收成 MQTT 订阅过滤。每个占位变成一层。
     */
    public static String mqttFilter(String pattern) {
        if (pattern == null || pattern.isBlank()) {
            return null;
        }
        return pattern.replaceAll("\\{[^}]+\\}", "+");
    }
}
