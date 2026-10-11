package cn.cheers.x.inspection.task.service.execution.live;

import cn.cheers.x.device.protocolgateway.api.DeviceProtocolMissionApi;
import cn.cheers.x.device.protocolgateway.api.dto.MissionStartRespDTO;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.api.mission.DispatchAction;
import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolCodes;
import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 巡检图层执行中的画面上，把这一下负载操作按已保存对照发出去。
 *
 * <p>负责：只接受执行中的账；设备和协议用开跑时写下的快照；动作按编码到动作库取编号。
 * <p>不负责：改执行记录状态；回读摄像机台账；把缺的坐标或倍数补成画面中心。
 * <p>禁止：待执行或已结束的账也发；遥控端发框选变焦。
 */
@Service
public class PatrolLiveCameraCommandService {

    static final String RECORD_TYPE = "task_record_patrol";
    static final String ACTION_TYPE = "action";
    private static final String STATUS_IN_PROGRESS = "in_progress";
    private static final Set<String> LENSES = Set.of("wide", "zoom", "ir");
    private static final Set<String> NO_LENS = Set.of(
            "screenDrag", "cameraMode", "takePhoto", "photoStop", "startRecord", "stopRecord",
            "gimbalReset", "screenSplit", "photoStorage", "videoStorage",
            "irMeteringMode", "irMeteringPoint", "irMeteringArea");
    private static final Set<String> WIDE_OR_ZOOM = Set.of(
            "focus", "focusValue", "focusMode", "exposureMode", "exposureSet",
            "aperture", "shutter", "iso");
    private static final Set<String> STORAGE = Set.of("current", "wide", "zoom", "ir");
    private static final Set<String> SHARED = Set.of(
            "zoom", "focus", "screenDrag", "cameraAim", "focusValue", "focusMode",
            "cameraMode", "takePhoto", "photoStop", "startRecord", "stopRecord",
            "gimbalReset", "screenSplit", "exposureMode", "exposureSet",
            "aperture", "shutter", "iso",
            "photoStorage", "videoStorage", "irMeteringMode", "irMeteringPoint", "irMeteringArea");

    private final EntityRpcApi entityRpcApi;
    private final DeviceProtocolMissionApi missionApi;

    public PatrolLiveCameraCommandService(EntityRpcApi entityRpcApi, DeviceProtocolMissionApi missionApi) {
        this.entityRpcApi = entityRpcApi;
        this.missionApi = missionApi;
    }

    public MissionStartRespDTO send(Long recordId, String actionCode, Map<String, Object> params) {
        if (recordId == null || recordId <= 0) {
            throw ServiceExceptionUtil.invalidParamException("要指定这次正在执行的巡检");
        }
        String code = actionCode == null ? "" : actionCode.trim();
        if (!SHARED.contains(code) && !"frameZoom".equals(code)) {
            throw ServiceExceptionUtil.invalidParamException("这块画面不能发这条操作");
        }
        EntityRespDTO record = requireData(entityRpcApi.getEntity(recordId, RECORD_TYPE), "这次巡检账不存在");
        Map<String, Object> fields = record.getCustomFields() == null ? Map.of() : record.getCustomFields();
        if (!STATUS_IN_PROGRESS.equals(text(fields.get("execution_status")))) {
            throw ServiceExceptionUtil.invalidParamException("只有正在执行时才能在画面上操作");
        }
        Map<String, Object> snapshot = snapshotOf(fields.get("standard_snapshot"));
        String protocol = text(snapshot.get("protocolVersion"));
        String deviceId = text(snapshot.get("logicalDeviceId"));
        if (!ProtocolCodes.isDjiCloud(protocol) || !StringUtils.hasText(deviceId)) {
            throw ServiceExceptionUtil.invalidParamException("这次执行没有记下大疆机场或大疆 Pilot 的设备，不能从画面发出");
        }
        if ("frameZoom".equals(code) && !ProtocolCodes.DJI_DOCK.equals(protocol)) {
            throw ServiceExceptionUtil.invalidParamException("遥控端没有框选变焦");
        }
        Map<String, Object> bag = checkedParams(code, params == null ? Map.of() : params);
        EntityRespDTO action = requireData(
                entityRpcApi.getEntityByCode(code, ACTION_TYPE),
                "动作库里没有这条负载操作");
        if (action.getId() == null || action.getId() <= 0) {
            throw ServiceExceptionUtil.invalidParamException("动作库里没有这条负载操作");
        }
        String recordKey = String.valueOf(recordId);
        DeviceMissionPlan plan = new DeviceMissionPlan(
                protocol,
                deviceId,
                recordKey,
                recordKey,
                List.of(new DispatchAction(action.getId(), bag, code)));
        CommonResult<MissionStartRespDTO> rpc = missionApi.dispatchAndStartup(plan);
        if (rpc == null || !rpc.isSuccess() || rpc.getData() == null) {
            throw ServiceExceptionUtil.invalidParamException(
                    rpc != null && StringUtils.hasText(rpc.getMsg()) ? rpc.getMsg() : "发出失败");
        }
        return rpc.getData();
    }

    private static Map<String, Object> checkedParams(String code, Map<String, Object> params) {
        Map<String, Object> bag = new LinkedHashMap<>();
        bag.put("selected_camera", requiredText(params, "selected_camera", "请先选择摄像机"));
        if (!NO_LENS.contains(code)) {
            String lensKey = "focus".equals(code) ? "point_focus_camera_type" : "camera_type";
            String lens = requiredText(params, lensKey, "请先选择相机类型");
            if (!LENSES.contains(lens)) {
                throw ServiceExceptionUtil.invalidParamException("相机类型只能是广角、变焦或红外");
            }
            if (WIDE_OR_ZOOM.contains(code) && "ir".equals(lens)) {
                throw ServiceExceptionUtil.invalidParamException("红外这一路没有这项");
            }
            bag.put(lensKey, lens);
        }
        switch (code) {
            case "zoom" -> bag.put("zoom_factor", zoomFactor(params, text(bag.get("camera_type"))));
            case "focus" -> {
                bag.put("point_focus_x", unit(params, "point_focus_x"));
                bag.put("point_focus_y", unit(params, "point_focus_y"));
            }
            case "frameZoom" -> {
                bag.put("locked", requiredBoolean(params));
                bag.put("frame_x", unit(params, "frame_x"));
                bag.put("frame_y", unit(params, "frame_y"));
                bag.put("frame_width", unit(params, "frame_width"));
                bag.put("frame_height", unit(params, "frame_height"));
            }
            case "screenDrag" -> {
                bag.put("locked", requiredBoolean(params));
                bag.put("pitch_speed", requiredFinite(params, "pitch_speed"));
                bag.put("yaw_speed", requiredFinite(params, "yaw_speed"));
            }
            case "cameraAim" -> {
                bag.put("locked", requiredBoolean(params));
                bag.put("aim_x", unit(params, "aim_x"));
                bag.put("aim_y", unit(params, "aim_y"));
            }
            case "focusValue" -> bag.put("focus_value", requiredFinite(params, "focus_value"));
            case "focusMode" -> bag.put("focus_mode", oneOf(params, "focus_mode", Set.of(0, 1, 2),
                    "对焦模式只能是手动、单次自动或连续自动"));
            case "cameraMode" -> bag.put("camera_mode", oneOf(params, "camera_mode", Set.of(0, 1, 2, 3),
                    "拍摄模式只能是拍照、录像、智能低光或全景拍照"));
            case "photoStop" -> {
                // 停止拍照只带选中的摄像机。
            }
            case "takePhoto", "startRecord", "stopRecord" -> {
                // 与任务里是同一条动作。对照读的是负载位置，画面上选的摄像机写到这一格。
                bag.put("payload_position_index", bag.get("selected_camera"));
            }
            case "gimbalReset" -> bag.put("reset_mode", oneOf(params, "reset_mode", Set.of(0, 1, 2, 3),
                    "云台重置只能是回中、向下、偏航回中或俯仰向下"));
            case "screenSplit" -> bag.put("split_enabled", requiredBooleanKey(params, "split_enabled", "请标明是否打开分屏"));
            case "exposureMode" -> bag.put("exposure_mode", oneOf(params, "exposure_mode", Set.of(1, 2, 3, 4),
                    "曝光模式只能是自动、快门优先、光圈优先或手动"));
            case "exposureSet" -> bag.put("exposure_value", oneOf(params, "exposure_value", exposureValues(),
                    "曝光值不在相机给出的档位里"));
            case "aperture" -> bag.put("aperture_value", oneOf(params, "aperture_value", apertureValues(),
                    "光圈不在这台画面给出的档位里"));
            case "shutter" -> bag.put("shutter_value", oneOf(params, "shutter_value", shutterValues(),
                    "快门不在相机给出的档位里"));
            case "iso" -> bag.put("iso_value", oneOf(params, "iso_value", isoValues(),
                    "感光度不在相机给出的档位里"));
            case "photoStorage" -> bag.put("photo_storage_settings", storage(params, "photo_storage_settings"));
            case "videoStorage" -> bag.put("video_storage_settings", storage(params, "video_storage_settings"));
            case "irMeteringMode" -> bag.put("meter_mode", oneOf(params, "meter_mode", Set.of(0, 1, 2),
                    "测温只能是关闭、点测温或区域测温"));
            case "irMeteringPoint" -> {
                bag.put("meter_x", unit(params, "meter_x"));
                bag.put("meter_y", unit(params, "meter_y"));
            }
            case "irMeteringArea" -> {
                bag.put("meter_x", unit(params, "meter_x"));
                bag.put("meter_y", unit(params, "meter_y"));
                bag.put("meter_width", unit(params, "meter_width"));
                bag.put("meter_height", unit(params, "meter_height"));
            }
            default -> throw ServiceExceptionUtil.invalidParamException("这块画面不能发这条操作");
        }
        return bag;
    }

    /** 画面上给出的光圈档。大于 0 的数就是实际光圈乘 100。0 是自动。 */
    private static Set<Integer> apertureValues() {
        return Set.of(0, 280, 320, 400, 560, 800, 1100, 1600);
    }

    /** 官网快门档 0 到 59 是时间，60 是自动。 */
    private static Set<Integer> shutterValues() {
        Set<Integer> values = new java.util.HashSet<>();
        for (int value = 0; value <= 60; value++) {
            values.add(value);
        }
        return values;
    }

    /** 官网感光度档。0 自动，255 固定，其余是感光度数字对应的档位。 */
    private static Set<Integer> isoValues() {
        return Set.of(0, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 255);
    }

    private static Set<Integer> exposureValues() {
        Set<Integer> values = new java.util.HashSet<>();
        for (int value = 1; value <= 31; value++) {
            values.add(value);
        }
        values.add(255);
        return values;
    }

    private static double zoomFactor(Map<String, Object> params, String lens) {
        double value = requiredFinite(params, "zoom_factor");
        double max = "ir".equals(lens) ? 20 : 200;
        if (value < 2 || value > max) {
            throw ServiceExceptionUtil.invalidParamException(
                    "ir".equals(lens) ? "红外变焦倍数是 2 到 20" : "可见光变焦倍数是 2 到 200");
        }
        return value;
    }

    private static double unit(Map<String, Object> params, String key) {
        double value = requiredFinite(params, key);
        if (value < 0 || value > 1) {
            throw ServiceExceptionUtil.invalidParamException("画面上的位置要在 0 到 1 之间");
        }
        return value;
    }

    private static String requiredText(Map<String, Object> params, String key, String message) {
        String text = text(params.get(key));
        if (!StringUtils.hasText(text)) {
            throw ServiceExceptionUtil.invalidParamException(message);
        }
        return text;
    }

    private static int oneOf(Map<String, Object> params, String key, Set<Integer> allowed, String message) {
        int value = (int) requiredFinite(params, key);
        if (!allowed.contains(value)) {
            throw ServiceExceptionUtil.invalidParamException(message);
        }
        return value;
    }

    private static boolean requiredBooleanKey(Map<String, Object> params, String key, String message) {
        Object raw = params.get(key);
        if (!(raw instanceof Boolean value)) {
            throw ServiceExceptionUtil.invalidParamException(message);
        }
        return value;
    }

    private static List<String> storage(Map<String, Object> params, String key) {
        Object raw = params.get(key);
        if (!(raw instanceof List<?> list) || list.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException("请选择要存储的镜头");
        }
        List<String> chosen = new java.util.ArrayList<>();
        for (Object item : list) {
            String text = text(item);
            if (!STORAGE.contains(text)) {
                throw ServiceExceptionUtil.invalidParamException("存储镜头只能是当前、广角、变焦或红外");
            }
            chosen.add(text);
        }
        return chosen;
    }

    private static boolean requiredBoolean(Map<String, Object> params) {
        Object raw = params.get("locked");
        if (!(raw instanceof Boolean value)) {
            throw ServiceExceptionUtil.invalidParamException("请标明机头是否锁定");
        }
        return value;
    }

    private static double requiredFinite(Map<String, Object> params, String key) {
        Object raw = params.get(key);
        if (!(raw instanceof Number number) || !Double.isFinite(number.doubleValue())) {
            throw ServiceExceptionUtil.invalidParamException("这一下还没有数，不能发出");
        }
        return number.doubleValue();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> snapshotOf(Object raw) {
        if (raw instanceof Map<?, ?> map) {
            Map<String, Object> copy = new LinkedHashMap<>();
            map.forEach((key, value) -> copy.put(String.valueOf(key), value));
            return copy;
        }
        return Map.of();
    }

    private static String text(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }

    private static EntityRespDTO requireData(CommonResult<EntityRespDTO> result, String missing) {
        if (result == null || !result.isSuccess() || result.getData() == null) {
            throw ServiceExceptionUtil.invalidParamException(missing);
        }
        return result.getData();
    }
}
