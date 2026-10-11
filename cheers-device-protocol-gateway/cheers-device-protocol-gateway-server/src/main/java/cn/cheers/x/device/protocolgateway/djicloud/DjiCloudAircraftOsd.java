package cn.cheers.x.device.protocolgateway.djicloud;

import cn.cheers.x.device.protocolgateway.api.dto.AircraftFlightStatusDTO;
import cn.cheers.x.device.protocolgateway.api.dto.CameraExposureStatusDTO;
import cn.cheers.x.device.protocolgateway.api.dto.PayloadGimbalAttitudeDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 把飞机定时上报拆成画面上要看的字段，并留下这一帧已经报上的摄像机曝光档。
 * <p>只认带高度、水平速度、离返航点距离或剩余飞行时间的报文。机场报文不记。
 * <p>曝光档只抄广角和变焦的模式、感光度、快门、曝光值。没有的键留空。
 * 不负责：把曝光值写成航线里的环境亮度；编一个光圈。定时上报没有光圈。
 * <p>禁止：把读不到的电量 32767 当成百分比；缺的键填 0。
 */
final class DjiCloudAircraftOsd {

    private static final int BATTERY_UNREADABLE = 32767;
    private static final Map<Integer, String> GEARS = Map.of(
            0, "A", 1, "P", 2, "NAV", 3, "FPV", 4, "FARM",
            5, "S", 6, "F", 7, "M", 8, "G", 9, "T");
    private static final Map<Integer, String> MODES = Map.ofEntries(
            Map.entry(0, "待机"),
            Map.entry(1, "起飞准备"),
            Map.entry(2, "起飞准备完毕"),
            Map.entry(3, "手动飞行"),
            Map.entry(4, "自动起飞"),
            Map.entry(5, "航线飞行"),
            Map.entry(6, "全景拍照"),
            Map.entry(7, "智能跟随"),
            Map.entry(8, "ADS-B 躲避"),
            Map.entry(9, "自动返航"),
            Map.entry(10, "自动降落"),
            Map.entry(11, "强制降落"),
            Map.entry(12, "三桨叶降落"),
            Map.entry(13, "升级中"),
            Map.entry(14, "未连接"),
            Map.entry(15, "APAS"),
            Map.entry(16, "虚拟摇杆状态"),
            Map.entry(17, "指令飞行"),
            Map.entry(18, "空中 RTK 收敛模式"));

    private final ObjectMapper objectMapper;

    DjiCloudAircraftOsd(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    AircraftFlightStatusDTO parse(String aircraftSn, byte[] payload) {
        JsonNode root = read(payload);
        if (root == null) {
            return null;
        }
        JsonNode data = root.get("data");
        if (data == null || !data.isObject() || data.has("drone_in_dock") || !isAircraft(data)) {
            return null;
        }
        JsonNode battery = data.get("battery");
        JsonNode position = data.get("position_state");
        JsonNode storage = data.get("storage");
        JsonNode camera = firstCamera(data.get("cameras"));
        Integer recording = whole(camera, "recording_state");
        return new AircraftFlightStatusDTO(
                null,
                aircraftSn,
                root.has("timestamp") && root.get("timestamp").canConvertToLong()
                        ? root.get("timestamp").asLong() : null,
                gear(whole(data, "gear")),
                flightMode(whole(data, "mode_code")),
                batteryPercent(battery),
                whole(battery, "remain_flight_time"),
                whole(position, "quality"),
                whole(position, "gps_number"),
                whole(position, "rtk_number"),
                decimal(data, "home_distance"),
                decimal(data, "horizontal_speed"),
                decimal(data, "elevation"),
                decimal(data, "vertical_speed"),
                whole(camera, "remain_photo_num"),
                whole(camera, "remain_record_duration"),
                recording != null && recording == 1 ? whole(camera, "record_time") : null,
                wholeLong(storage, "total"),
                wholeLong(storage, "used"),
                whole(data, "attitude_head"),
                cameraExposures(data.get("cameras")),
                gimbalAttitudes(data));
    }

    private static boolean isAircraft(JsonNode data) {
        if (data.has("elevation") || data.has("horizontal_speed") || data.has("home_distance")) {
            return true;
        }
        JsonNode battery = data.get("battery");
        return battery != null && battery.has("remain_flight_time");
    }

    /**
     * 大疆定时上报把每台云台的当前俯仰放在负载编号下面，字段是 gimbal_pitch，单位度。
     * 实时画面从这里接着拨。没有这一键就不造 0。
     */
    private static List<PayloadGimbalAttitudeDTO> gimbalAttitudes(JsonNode data) {
        List<PayloadGimbalAttitudeDTO> rows = new ArrayList<>();
        var fields = data.fields();
        while (fields.hasNext()) {
            var field = fields.next();
            JsonNode node = field.getValue();
            if (node == null || !node.isObject() || !node.has("gimbal_pitch") || !node.get("gimbal_pitch").isNumber()) {
                continue;
            }
            JsonNode indexNode = node.get("payload_index");
            String payloadIndex = indexNode != null && indexNode.isTextual() ? indexNode.asText() : field.getKey();
            if (payloadIndex == null || payloadIndex.isBlank()) {
                continue;
            }
            rows.add(new PayloadGimbalAttitudeDTO(payloadIndex, node.get("gimbal_pitch").asDouble()));
        }
        return rows.isEmpty() ? null : List.copyOf(rows);
    }

    private static JsonNode firstCamera(JsonNode cameras) {
        if (cameras == null || !cameras.isArray()) {
            return null;
        }
        for (JsonNode camera : cameras) {
            if (camera.isObject() && (camera.has("remain_photo_num")
                    || camera.has("remain_record_duration") || camera.has("record_time"))) {
                return camera;
            }
        }
        return null;
    }

    /** 只留下报了曝光档的摄像机。整帧都没有这些键时返回 null，不造一条空摄像机。 */
    private static List<CameraExposureStatusDTO> cameraExposures(JsonNode cameras) {
        if (cameras == null || !cameras.isArray()) {
            return null;
        }
        List<CameraExposureStatusDTO> rows = new ArrayList<>();
        for (JsonNode camera : cameras) {
            CameraExposureStatusDTO row = exposureOf(camera);
            if (row != null) {
                rows.add(row);
            }
        }
        return rows.isEmpty() ? null : List.copyOf(rows);
    }

    private static CameraExposureStatusDTO exposureOf(JsonNode camera) {
        if (camera == null || !camera.isObject()) {
            return null;
        }
        Integer wideMode = whole(camera, "wide_exposure_mode");
        Integer wideIso = whole(camera, "wide_iso");
        Integer wideShutter = whole(camera, "wide_shutter_speed");
        Integer wideExposure = whole(camera, "wide_exposure_value");
        Integer zoomMode = whole(camera, "zoom_exposure_mode");
        Integer zoomIso = whole(camera, "zoom_iso");
        Integer zoomShutter = whole(camera, "zoom_shutter_speed");
        Integer zoomExposure = whole(camera, "zoom_exposure_value");
        if (wideMode == null && wideIso == null && wideShutter == null && wideExposure == null
                && zoomMode == null && zoomIso == null && zoomShutter == null && zoomExposure == null) {
            return null;
        }
        JsonNode payload = camera.get("payload_index");
        String payloadIndex = payload != null && payload.isTextual() ? payload.asText() : null;
        return new CameraExposureStatusDTO(
                payloadIndex,
                wideMode, wideIso, wideShutter, wideExposure,
                zoomMode, zoomIso, zoomShutter, zoomExposure);
    }

    private static String gear(Integer code) {
        if (code == null) {
            return null;
        }
        return GEARS.getOrDefault(code, "挡位 " + code);
    }

    private static String flightMode(Integer code) {
        if (code == null) {
            return null;
        }
        return MODES.getOrDefault(code, "飞行状态 " + code);
    }

    private static Integer batteryPercent(JsonNode battery) {
        Integer percent = whole(battery, "capacity_percent");
        if (percent != null && percent == BATTERY_UNREADABLE) {
            return null;
        }
        return percent;
    }

    private JsonNode read(byte[] payload) {
        if (payload == null || payload.length == 0) {
            return null;
        }
        try {
            return objectMapper.readTree(payload);
        } catch (Exception ex) {
            return null;
        }
    }

    private static Integer whole(JsonNode node, String field) {
        if (node == null || !node.has(field) || !node.get(field).isNumber()) {
            return null;
        }
        return node.get(field).asInt();
    }

    private static Long wholeLong(JsonNode node, String field) {
        if (node == null || !node.has(field) || !node.get(field).isNumber()) {
            return null;
        }
        return node.get(field).asLong();
    }

    private static Double decimal(JsonNode node, String field) {
        if (node == null || !node.has(field) || !node.get(field).isNumber()) {
            return null;
        }
        double value = node.get(field).asDouble();
        return Double.isFinite(value) ? value : null;
    }
}
