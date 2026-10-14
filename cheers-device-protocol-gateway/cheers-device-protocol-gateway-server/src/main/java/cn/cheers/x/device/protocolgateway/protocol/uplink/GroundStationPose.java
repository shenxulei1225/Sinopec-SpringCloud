package cn.cheers.x.device.protocolgateway.protocol.uplink;

import cn.cheers.x.device.protocolgateway.api.opcode.TransportOpcode;
import cn.cheers.x.inspection.task.api.dto.ReplayPoseReqDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 从东古实时状态和定时状态里取出设备当前位置。
 * <p>经度写入 x，纬度写入 y，高度写入 z。坐标系是 WGS84。
 * 巡检点 operatePoint 不是飞机现在的位置，不取。
 * <p>不负责挑选执行记录，也不把这个点写进过程列表。
 */
final class GroundStationPose {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final long MILLIS_THRESHOLD = 1_000_000_000_000L;

    private GroundStationPose() {
    }

    /**
     * 没有经纬度时返回 null。高度缺了记 0。时间缺了用收到时刻。
     */
    static ReplayPoseReqDTO fromStatus(String deviceId, int opcode, String payloadJson, long nowMs) {
        if (opcode != TransportOpcode.REAL_TIME_STATUS.code()
                && opcode != TransportOpcode.TIMED_DEVICE_STATUS.code()) {
            return null;
        }
        JsonNode root = read(payloadJson);
        if (root == null) {
            return null;
        }
        JsonNode box = opcode == TransportOpcode.REAL_TIME_STATUS.code()
                ? root.path("data")
                : root.path("result");
        if (!box.isObject()) {
            return null;
        }
        Double longitude = number(box.get("lng"));
        Double latitude = number(box.get("lat"));
        if (longitude == null || latitude == null) {
            return null;
        }
        Double height = number(box.get("height"));
        ReplayPoseReqDTO pose = new ReplayPoseReqDTO();
        pose.setDeviceId(deviceId);
        pose.setT(epochMillis(box.get("time"), nowMs));
        pose.setX(longitude);
        pose.setY(latitude);
        pose.setZ(height == null ? 0D : height);
        pose.setYaw(0D);
        pose.setFrameRef("wgs84");
        return pose;
    }

    private static JsonNode read(String payloadJson) {
        if (payloadJson == null || payloadJson.isBlank()) {
            return null;
        }
        try {
            return MAPPER.readTree(payloadJson);
        } catch (Exception ex) {
            return null;
        }
    }

    private static Double number(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isNumber()) {
            return node.asDouble();
        }
        String text = node.asText("").trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static long epochMillis(JsonNode node, long nowMs) {
        Double value = number(node);
        if (value == null) {
            return nowMs;
        }
        long raw = value.longValue();
        if (raw <= 0) {
            return nowMs;
        }
        return raw < MILLIS_THRESHOLD ? raw * 1000L : raw;
    }
}
