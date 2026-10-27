package cn.cheers.x.iot.gateway.protocol.emqx;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 浏览器页面连上 EMQX 时用的连接身份。
 * 客户端编号以 page- 开头。用户名是 page:{租户编号}:{用户编号}。
 * 主题允许告警、设备位置、设备状态，以及这次巡检执行和这台设备的主题。和推送主题目录里写入的规则相同。
 * 不负责写目录，也不负责把消息送出去。
 */
public final class PushPageConnection {

    public static final String CLIENT_PREFIX = "page-";

    private static final List<Pattern> TOPIC_PATTERNS = List.of(
            Pattern.compile("^alarm/facility/[A-Za-z0-9_-]+$"),
            Pattern.compile("^device/[A-Za-z0-9_-]+/position$"),
            Pattern.compile("^device/[A-Za-z0-9_-]+/status$"),
            Pattern.compile("^inspection/execution/[1-9][0-9]*/#$"),
            Pattern.compile("^inspection/device/[A-Za-z0-9._-]+/#$"));

    private PushPageConnection() {
    }

    public static boolean isPageClient(String clientId) {
        return clientId != null && clientId.startsWith(CLIENT_PREFIX);
    }

    public static boolean topicAllowed(String topic) {
        if (topic == null || topic.isBlank()) {
            return false;
        }
        String value = topic.trim();
        for (Pattern pattern : TOPIC_PATTERNS) {
            if (pattern.matcher(value).matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从用户名取出租户和用户。格式不对时返回 null。
     */
    public static Identity parseUsername(String username) {
        if (username == null || !username.startsWith("page:")) {
            return null;
        }
        String[] parts = username.split(":", 3);
        if (parts.length != 3 || !parts[1].matches("[1-9][0-9]*") || !parts[2].matches("[1-9][0-9]*")) {
            return null;
        }
        return new Identity(Long.parseLong(parts[1]), Long.parseLong(parts[2]));
    }

    public record Identity(long tenantId, long userId) {
    }
}
