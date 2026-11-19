package cn.cheers.x.module.dynamicbusiness.service.event;

import java.util.Map;

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
     * 用这台设备和事件载荷里的归属编号填名字规则。
     * 名字里写了 {facilityId} 或 {场站编号}，载荷里就要有字段编码 facility_id 的归属。
     * 名字里写了 {字段编码}，就用这条归属的编号。没有对应归属就拒绝发出。
     *
     * @return 填好的主题。规则空着、还剩占位或编号无效时返回 null，调用方拒绝发出。
     */
    public static String fill(String pattern, long facilityId, long sourceEntityId) {
        Map<String, Long> ownership = facilityId > 0
                ? Map.of("facility_id", facilityId)
                : Map.of();
        return fill(pattern, sourceEntityId, ownership);
    }

    public static String fill(String pattern, long sourceEntityId, Map<String, Long> ownership) {
        if (pattern == null || pattern.isBlank() || sourceEntityId <= 0) {
            return null;
        }
        Map<String, Long> refs = ownership == null ? Map.of() : ownership;
        Long facility = refs.get("facility_id");
        boolean wantsFacility = pattern.contains("{facilityId}") || pattern.contains("{场站编号}");
        if (wantsFacility && (facility == null || facility <= 0)) {
            return null;
        }
        String filled = pattern
                .replace("{deviceId}", Long.toString(sourceEntityId))
                .replace("{设备编号}", Long.toString(sourceEntityId));
        if (facility != null && facility > 0) {
            filled = filled
                    .replace("{facilityId}", Long.toString(facility))
                    .replace("{场站编号}", Long.toString(facility));
        }
        for (Map.Entry<String, Long> ref : refs.entrySet()) {
            if (ref.getKey() == null || ref.getKey().isBlank() || ref.getValue() == null || ref.getValue() <= 0) {
                continue;
            }
            filled = filled.replace("{" + ref.getKey().trim() + "}", Long.toString(ref.getValue()));
        }
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
