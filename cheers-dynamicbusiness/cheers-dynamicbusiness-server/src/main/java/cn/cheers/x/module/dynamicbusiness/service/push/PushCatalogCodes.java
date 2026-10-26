package cn.cheers.x.module.dynamicbusiness.service.push;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 推送主题目录和推送订阅目录的注册编码、列名。
 * 不负责投递，也不负责页面什么时候去订阅。
 */
public final class PushCatalogCodes {

    /** 给人看的主题种类目录。 */
    public static final String TOPIC_TYPE = "push_topic";
    public static final String TOPIC_MODEL = "主题种类";
    /** 当前有哪些连接在听。断开后删除。 */
    public static final String SUBSCRIPTION_TYPE = "push_subscription";
    public static final String SUBSCRIPTION_MODEL = "当前订阅";

    /** 推送主题上的文本列。编码由字段创建生成，按这个名字找回。 */
    public static final String NAME_PATTERN = "名字规则";
    /** 推送订阅上的三列。编码同样按名字找回。 */
    public static final String CLIENT_ID = "连接编号";
    public static final String TOPIC_NAME = "主题名";
    public static final String LISTENER_USER_ID = "监听人";

    private static final int MAX_TEXT = 255;

    private PushCatalogCodes() {
    }

    /**
     * 同一条连接听同一个主题，编码固定，目录里只会有一行。
     */
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
