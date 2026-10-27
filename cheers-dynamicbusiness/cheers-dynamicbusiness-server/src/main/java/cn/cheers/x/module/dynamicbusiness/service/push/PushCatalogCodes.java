package cn.cheers.x.module.dynamicbusiness.service.push;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 事件主题目录和事件订阅目录的注册编码、列名。
 * 不负责投递，也不负责页面什么时候去订阅。
 */
public final class PushCatalogCodes {

    /** 数据目录分组。侧栏只显示这一个名字。 */
    public static final String GROUP_NAME = "事件";
    /** 已经写成「事件分组」的分组，准备目录时改成「事件」。 */
    public static final String LEGACY_GROUP_NAME = "事件分组";

    /** 给人看的主题种类目录。 */
    public static final String TOPIC_TYPE = "event_topic";
    public static final String TOPIC_CATALOG_NAME = "事件主题";
    public static final String LEGACY_TOPIC_TYPE = "push_topic";
    public static final String LEGACY_TOPIC_NAME = "推送主题";
    public static final String TOPIC_MODEL = "主题种类";
    /** 当前有哪些连接在听。断开后删除。一行是订阅方。 */
    public static final String SUBSCRIPTION_TYPE = "event_subscription";
    public static final String SUBSCRIPTION_CATALOG_NAME = "事件订阅";
    public static final String LEGACY_SUBSCRIPTION_TYPE = "push_subscription";
    public static final String LEGACY_SUBSCRIPTION_NAME = "推送订阅";
    public static final String SUBSCRIPTION_MODEL = "当前订阅";

    /** 推送主题上的文本列。编码由字段创建生成，按这个名字找回。 */
    public static final String NAME_PATTERN = "名字规则";
    /** 推送订阅上的三列。编码同样按名字找回。 */
    public static final String CLIENT_ID = "连接编号";
    public static final String TOPIC_NAME = "主题名";
    public static final String LISTENER_USER_ID = "监听人";
    /** 数据目录里直接读的那一句：谁在哪个画面听什么。新的订阅行不再写这句话。 */
    public static final String ORIGIN_TEXT = "发起说明";
    /** 这一行订阅方正在听的事件主题。详情栏读这个引用。 */
    public static final String SUBSCRIBED_TOPICS = "订阅的主题";
    /** 巡检图层所在菜单。对象列只显示这个订阅方。 */
    public static final String STATION_LIVE_MONITOR = "场站实时监控";

    private static final int MAX_TEXT = 255;

    private PushCatalogCodes() {
    }

    /**
     * 同一条连接听同一个主题，编码固定，目录里只会有一行。
     */
    /**
     * 同一条连接是一个订阅方，编码固定，目录里只会有一行。
     * 这一行可以引用多个事件主题。
     */
    public static String subscriberCode(String clientId) {
        return "ps-" + sha256Hex(clientId).substring(0, 32);
    }

    public static String subscriptionCode(String clientId, String topic) {
        return "ps-" + sha256Hex(clientId + "\n" + topic).substring(0, 32);
    }

    public static void requireTextLength(String label, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + "为空");
        }
        if (value.length() > MAX_TEXT) {
            throw new IllegalArgumentException(label + "超过 255 字，没有写入目录");
        }
    }

    private static String sha256Hex(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(digest.length * 2);
            for (byte item : digest) {
                hex.append(String.format("%02x", item));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 不可用", ex);
        }
    }
}
