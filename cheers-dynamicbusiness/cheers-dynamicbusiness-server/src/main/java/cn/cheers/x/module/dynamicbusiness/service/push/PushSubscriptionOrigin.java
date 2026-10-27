package cn.cheers.x.module.dynamicbusiness.service.push;

/**
 * 认出这场订阅是哪个画面发起的。
 * 对象列只写订阅方的菜单名。不写谁的名字，也不写正在看哪一次执行。
 * 不负责连接消息服务，也不新建事件主题。
 */
public final class PushSubscriptionOrigin {

    static final String PATROL_LAYER = "patrol-layer";

    private PushSubscriptionOrigin() {
    }

    /**
     * 巡检图层的订阅方是菜单「场站实时监控」。
     * 网关回调没有画面名时，巡检执行或设备主题仍算这个画面。
     * 认不出的画面返回空，调用方不写入订阅行。
     */
    public static String subscriberName(String place, String topic) {
        if (PATROL_LAYER.equals(place)) {
            return PushCatalogCodes.STATION_LIVE_MONITOR;
        }
        if ((place == null || place.isBlank()) && isPatrolTopic(topic)) {
            return PushCatalogCodes.STATION_LIVE_MONITOR;
        }
        return null;
    }

    private static boolean isPatrolTopic(String topic) {
        if (topic == null || topic.isBlank()) {
            return false;
        }
        String value = topic.trim();
        return value.startsWith("inspection/execution/") || value.startsWith("inspection/device/");
    }
}
