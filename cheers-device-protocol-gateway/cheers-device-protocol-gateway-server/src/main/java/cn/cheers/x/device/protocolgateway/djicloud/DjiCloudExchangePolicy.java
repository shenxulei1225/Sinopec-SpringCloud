package cn.cheers.x.device.protocolgateway.djicloud;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 判断一包上云报文要不要记进执行记录的过程列表，以及能不能抽出回放位置。
 * <p>定时上报和连续杆量留在过程列表。飞机位置另存，供三维按实际轨迹回放。
 * <p>不负责挑选执行记录。
 */
final class DjiCloudExchangePolicy {

    private static final Pattern THING = Pattern.compile("^thing/product/([^/]+)/(.+)$");
    private static final Pattern SYS = Pattern.compile("^sys/product/([^/]+)/(.+)$");
    private DjiCloudExchangePolicy() {
    }

    /** 主题里的序列号。对不上上云主题时返回 null。 */
    static String serialOf(String topic) {
        Matcher matcher = match(topic);
        return matcher == null ? null : matcher.group(1);
    }

    /**
     * 过程列表留下这包原文。定时上报和连续杆量都留。
     * 对不上上云主题时不留。位置点不靠这份原文回放。
     */
    static boolean keepTranscript(String topic, JsonNode root) {
        return tail(topic) != null && root != null;
    }

    /**
     * 飞机定时上报里的位置。机场舱内上报没有经纬度，返回 null。
     * x 是经度，y 是纬度，z 是高度。坐标系是 WGS84，还没换成站场坐标。
     */
    static Pose poseOf(String topic, JsonNode root) {
        if (!"osd".equals(tail(topic)) || root == null) {
            return null;
        }
        JsonNode data = root.path("data");
        if (!data.isObject() || data.has("drone_in_dock") || !data.has("latitude") || !data.has("longitude")) {
            return null;
        }
        long t = root.path("timestamp").canConvertToLong()
                ? root.path("timestamp").asLong()
                : System.currentTimeMillis();
        double yaw = data.path("attitude_head").isNumber() ? data.path("attitude_head").asDouble() : 0;
        double height = data.path("height").isNumber() ? data.path("height").asDouble() : 0;
        return new Pose(t, data.path("longitude").asDouble(), data.path("latitude").asDouble(), height, yaw, "wgs84");
    }

    /** 报文里的飞行编号，就是这次执行记录编号。没有或不是数字时返回 null。 */
    static Long flightRecordId(JsonNode root) {
        if (root == null) {
            return null;
        }
        Long nested = digits(root.path("data").path("flight_id"));
        return nested != null ? nested : digits(root.path("flight_id"));
    }

    static String tid(JsonNode root) {
        if (root == null) {
            return null;
        }
        String tid = root.path("tid").asText("").trim();
        return tid.isEmpty() ? null : tid;
    }

    private static Matcher match(String topic) {
        if (topic == null) {
            return null;
        }
        Matcher thing = THING.matcher(topic);
        if (thing.matches()) {
            return thing;
        }
        Matcher sys = SYS.matcher(topic);
        return sys.matches() ? sys : null;
    }

    private static String tail(String topic) {
        Matcher matcher = match(topic);
        return matcher == null ? null : matcher.group(2);
    }

    private static Long digits(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        if (node.isNumber()) {
            return node.longValue();
        }
        if (!node.isTextual()) {
            return null;
        }
        String text = node.asText().trim();
        if (text.isEmpty() || !text.chars().allMatch(Character::isDigit)) {
            return null;
        }
        return Long.valueOf(text);
    }

    record Pose(long t, double x, double y, double z, double yaw, String frameRef) {
    }
}
