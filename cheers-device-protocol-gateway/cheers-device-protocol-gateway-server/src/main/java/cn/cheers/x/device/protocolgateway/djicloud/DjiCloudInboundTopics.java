package cn.cheers.x.device.protocolgateway.djicloud;

import java.util.List;

/**
 * 大疆上云里，设备发给云平台、因此本进程要订的 MQTT 主题。
 * <p>官网「发送者 → 订阅者」里，设备 &gt; 云平台的才订。
 * 云平台发给设备的服务下发、事件答复、请求答复、上线答复、属性设置、指令飞行下行，由设备去订，本进程不订。
 * <p>Pilot 的 HTTPS、WebSocket、JSBridge 不是 MQTT，订不了。
 */
public final class DjiCloudInboundTopics {

    private DjiCloudInboundTopics() {
    }

    public static List<Subscription> subscriptions() {
        return List.of(
                new Subscription("sys/product/+/status", 1),
                new Subscription("thing/product/+/osd", 0),
                new Subscription("thing/product/+/state", 1),
                new Subscription("thing/product/+/events", 1),
                new Subscription("thing/product/+/requests", 1),
                new Subscription("thing/product/+/services_reply", 1),
                new Subscription("thing/product/+/property/set_reply", 1),
                new Subscription("thing/product/+/drc/up", 1)
        );
    }

    /** 云平台写出、由设备订阅的主题。本进程不得订。 */
    public static List<String> cloudPublishedFilters() {
        return List.of(
                "thing/product/+/services",
                "thing/product/+/events_reply",
                "thing/product/+/requests_reply",
                "sys/product/+/status_reply",
                "thing/product/+/property/set",
                "thing/product/+/drc/down"
        );
    }

    public record Subscription(String filter, int qos) {
    }
}
