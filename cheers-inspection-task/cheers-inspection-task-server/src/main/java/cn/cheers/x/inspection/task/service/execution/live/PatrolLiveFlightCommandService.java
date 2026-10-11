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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 实时画面上的起飞和返航。
 *
 * <p>负责：正在执行时，把一键起飞、一键返航按已保存对照单独发出。起飞只用这次执行快照里已经写下的起飞动作。
 * <p>不负责：生成航线文件，改执行记录状态，补目标点或高度。
 * <p>禁止：按钮没带到的高度写成 0；用返回充电那一步的坐标冒充返航。
 */
@Service
public class PatrolLiveFlightCommandService {

    static final String RECORD_TYPE = "task_record_patrol";
    static final String ACTION_TYPE = "action";
    /** 动作库里对上单独起飞报文的那一条。 */
    static final String TAKEOFF_COMMAND = "action-2b44a1f882f54438b6a1864e3636c889";
    /** 任务头上拆出来的起飞。快照里只有这一条时也认。 */
    static final String TAKEOFF_ON_TASK = "act-uav-takeoff";
    /** 动作库里对上一键返航。报文没有额外参数。 */
    static final String RETURN_HOME = "action-1022d966da2b45f7b73e776112e6884b";

    private static final String STATUS_IN_PROGRESS = "in_progress";
    private static final String COMMAND_TAKEOFF = "takeoff";
    private static final String COMMAND_RETURN_HOME = "returnHome";

    private final EntityRpcApi entityRpcApi;
    private final DeviceProtocolMissionApi missionApi;

    public PatrolLiveFlightCommandService(EntityRpcApi entityRpcApi, DeviceProtocolMissionApi missionApi) {
        this.entityRpcApi = entityRpcApi;
        this.missionApi = missionApi;
    }

    /**
     * 起飞读快照里的起飞动作再发。返航不带参数。
     * 没有正在执行、快照里没有起飞、或起飞缺了必填项时抛错，不发给网关。
     */
    public MissionStartRespDTO send(Long recordId, String command) {
        Execution execution = requireExecution(recordId);
        String kind = command == null ? "" : command.trim();
        DispatchAction action = switch (kind) {
            case COMMAND_TAKEOFF -> takeoffFromSnapshot(execution.snapshot());
            case COMMAND_RETURN_HOME -> returnHome();
            default -> throw ServiceExceptionUtil.invalidParamException("这块画面不能发这条操作");
        };
        String recordKey = String.valueOf(recordId);
        DeviceMissionPlan plan = new DeviceMissionPlan(
                execution.protocol(),
                execution.deviceId(),
                recordKey,
                recordKey,
                List.of(action));
        CommonResult<MissionStartRespDTO> rpc = missionApi.dispatchAndStartup(plan);
        if (rpc == null || !rpc.isSuccess() || rpc.getData() == null) {
            throw ServiceExceptionUtil.invalidParamException(
                    rpc != null && StringUtils.hasText(rpc.getMsg()) ? rpc.getMsg() : "发出失败");
        }
        return rpc.getData();
    }

    private DispatchAction takeoffFromSnapshot(Map<String, Object> snapshot) {
        Map<String, Object> written = null;
        Long actionId = null;
        String actionCode = null;
        for (Map<String, Object> item : actionMaps(snapshot.get("dispatchActions"))) {
            String code = text(item.get("actionCode"));
            if (!TAKEOFF_COMMAND.equals(code) && !TAKEOFF_ON_TASK.equals(code)) {
                continue;
            }
            if (written != null && !TAKEOFF_COMMAND.equals(code)) {
                continue;
            }
            Object rawId = item.get("actionId");
            if (!(rawId instanceof Number number) || number.longValue() <= 0) {
                throw ServiceExceptionUtil.invalidParamException("这次任务的起飞动作没有编号，还没有写入");
            }
            actionId = number.longValue();
            actionCode = code;
            written = item.get("params") instanceof Map<?, ?> params ? copyMap(params) : new LinkedHashMap<>();
            if (TAKEOFF_COMMAND.equals(code)) {
                break;
            }
        }
        if (written == null || actionId == null) {
            throw ServiceExceptionUtil.invalidParamException("这次任务没有记下起飞动作，还没有写入");
        }
        List<String> missing = missingTakeoff(written);
        if (!missing.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException(
                    "这次任务的起飞动作还没写下" + String.join("、", missing) + "，还没有写入");
        }
        return new DispatchAction(actionId, written, actionCode);
    }

    private DispatchAction returnHome() {
        EntityRespDTO action = requireData(
                entityRpcApi.getEntityByCode(RETURN_HOME, ACTION_TYPE),
                "动作库里没有一键返航");
        if (action.getId() == null || action.getId() <= 0) {
            throw ServiceExceptionUtil.invalidParamException("动作库里没有一键返航");
        }
        return new DispatchAction(action.getId(), Map.of(), RETURN_HOME);
    }

    private Execution requireExecution(Long recordId) {
        if (recordId == null || recordId <= 0) {
            throw ServiceExceptionUtil.invalidParamException("要指定这次正在执行的巡检");
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
        return new Execution(protocol, deviceId, snapshot);
    }

    private static List<String> missingTakeoff(Map<String, Object> params) {
        List<String> missing = new ArrayList<>();
        if (!hasTarget(params.get("FLD-LOC-014"))) {
            missing.add("目标点");
        }
        requireNumber(params, "security_takeoff_height", "安全起飞高度", missing);
        requireNumber(params, "rth_mode", "返航高度模式", missing);
        requireNumber(params, "rth_altitude", "返航高度", missing);
        requireNumber(params, "rc_lost_action", "失控动作", missing);
        requireNumber(params, "commander_mode_lost_action", "指点飞行失控动作", missing);
        requireNumber(params, "commander_flight_mode", "指点飞行模式", missing);
        requireNumber(params, "commander_flight_height", "指点飞行高度", missing);
        requireNumber(params, "max_speed", "最大速度", missing);
        if (!StringUtils.hasText(text(params.get("flight_id")))) {
            missing.add("飞行任务编号");
        }
        return missing;
    }

    private static boolean hasTarget(Object raw) {
        if (!(raw instanceof Map<?, ?> map)) {
            return false;
        }
        return present(map.get("latitude")) && present(map.get("longitude")) && present(map.get("height"));
    }

    private static void requireNumber(Map<String, Object> params, String key, String label, List<String> missing) {
        if (!present(params.get(key))) {
            missing.add(label);
        }
    }

    private static boolean present(Object raw) {
        if (raw instanceof Number number) {
            return Double.isFinite(number.doubleValue());
        }
        return StringUtils.hasText(text(raw));
    }

    private static List<Map<String, Object>> actionMaps(Object raw) {
        if (!(raw instanceof List<?> list)) {
            return List.of();
        }
        List<Map<String, Object>> actions = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof Map<?, ?> map) {
                actions.add(copyMap(map));
            }
        }
        return actions;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> snapshotOf(Object raw) {
        if (raw instanceof Map<?, ?> map) {
            return copyMap(map);
        }
        return Map.of();
    }

    private static Map<String, Object> copyMap(Map<?, ?> source) {
        Map<String, Object> copy = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            if (entry.getKey() != null) {
                copy.put(String.valueOf(entry.getKey()), entry.getValue());
            }
        }
        return copy;
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

    private record Execution(String protocol, String deviceId, Map<String, Object> snapshot) {
    }
}
