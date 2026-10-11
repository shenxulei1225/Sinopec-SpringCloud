package cn.cheers.x.device.protocolgateway.api.protocol;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 对接协议在下发链路上的能力画像：任务模块与协议网关共用，避免在各处硬编码协议分支。
 * <p>新增协议时优先在此登记能力位；头尾动作编码仍来自动作库，由任务按 means 展开。
 */
public final class ProtocolDispatchProfile {

    /** 动作库：机器人任务头（复合动作）。 */
    public static final String ACTION_ROBOT_HEAD = "act-robot-head";
    /** 动作库：无人机电量检查。 */
    public static final String ACTION_UAV_BATTERY_CHECK = "action-c6d414f4f24c442eb5947dccdf802b37";
    /** 动作库：大疆准备并启动航线。 */
    public static final String ACTION_UAV_WAYLINE_PREPARE = "act-uav-wayline-prepare";
    /** 动作库：任务级开始/结束录像。 */
    public static final String ACTION_TASK_RECORD_START = "startRecord";
    public static final String ACTION_TASK_RECORD_STOP = "stopRecord";

    /**
     * 任务步骤图头尾：动作库编码 + 展示标题 + 参数模板。
     * {@link ParamsTemplate#UAV_FLIGHT_MISSION} 表示写入任务/设备上的飞行计划字段。
     */
    public record MissionHeadTailStep(String actionCode, String title, ParamsTemplate paramsTemplate) {
    }

    public enum ParamsTemplate {
        NONE,
        UAV_FLIGHT_MISSION
    }

    /** 执行设备挂载槽：对照里的 slot 名 → 从主机挂载读哪类值。 */
    public enum ExecutorMountValueKind {
        /** 挂载点序号（如 gimbalPort、payloadPositionIndex）。 */
        MOUNT_POINT,
        /** 相机类型（orientedCameraType）。 */
        CAMERA_TYPE,
        /** 大疆上云用 payloadIndex，其它协议用 mountPoint（selected_camera 等）。 */
        PAYLOAD_INDEX_OR_MOUNT
    }

    private static final Map<String, ExecutorMountValueKind> EXECUTOR_MOUNT_SLOT_KINDS = Map.ofEntries(
            Map.entry("payloadPositionIndex", ExecutorMountValueKind.MOUNT_POINT),
            Map.entry("gimbalPort", ExecutorMountValueKind.MOUNT_POINT),
            Map.entry("orientedCameraType", ExecutorMountValueKind.CAMERA_TYPE),
            Map.entry("payload_position_index", ExecutorMountValueKind.PAYLOAD_INDEX_OR_MOUNT),
            Map.entry("selected_camera", ExecutorMountValueKind.PAYLOAD_INDEX_OR_MOUNT));

    private static final Set<String> TASK_LEVEL_RECORDING_PROTOCOLS = Set.of(
            ProtocolCodes.UAV_WS,
            ProtocolCodes.ROBOT_WS,
            ProtocolCodes.DJI_DOCK,
            ProtocolCodes.DJI_PILOT);

    private static final Set<String> SERVER_INGEST_PROTOCOLS = Set.of(
            ProtocolCodes.UAV_WS,
            ProtocolCodes.ROBOT_WS);

    private ProtocolDispatchProfile() {
    }

    /** 无人机或机器人执行方式，且协议已登记任务级全程录像。 */
    public static boolean taskLevelRecordingEnabled(String means, String protocolVersion) {
        if (!StringUtils.hasText(means) || !StringUtils.hasText(protocolVersion)) {
            return false;
        }
        String meansUpper = means.trim().toUpperCase();
        if (!"UAV".equals(meansUpper) && !"ROBOT".equals(meansUpper)) {
            return false;
        }
        return TASK_LEVEL_RECORDING_PROTOCOLS.contains(protocolVersion.trim());
    }

    /** 平台收流（试译/物化须写 {@code DeviceMissionPlan#mediaStreamUrl}）。 */
    public static boolean usesServerIngest(String protocolVersion) {
        if (!StringUtils.hasText(protocolVersion)) {
            return false;
        }
        return SERVER_INGEST_PROTOCOLS.contains(protocolVersion.trim());
    }

    /** 无人机开头步骤：电量检查等；东古与大疆上云支持，其它协议须扩展画像后再生成。 */
    public static boolean supportsUavOpeningSteps(String protocolVersion) {
        if (!StringUtils.hasText(protocolVersion)) {
            return false;
        }
        String protocol = protocolVersion.trim();
        return ProtocolCodes.isDjiCloud(protocol) || ProtocolCodes.UAV_WS.equals(protocol);
    }

    /** 大疆上云开头含「准备并启动航线」。 */
    public static boolean includesWaylinePrepareInOpening(String protocolVersion) {
        return ProtocolCodes.isDjiCloud(protocolVersion);
    }

    /** 大疆上云尾段含降落/返航动作链。 */
    public static boolean includesLandingTailAfterMission(String protocolVersion) {
        return ProtocolCodes.isDjiCloud(protocolVersion);
    }

    /** 机器人开头：展开动作库复合头；录像步骤由任务按画像另插。 */
    public static List<MissionHeadTailStep> robotOpeningSteps() {
        return List.of(new MissionHeadTailStep(ACTION_ROBOT_HEAD, "机器人任务头", ParamsTemplate.NONE));
    }

    /** 无人机开头：按对接协议登记动作库步骤顺序。 */
    public static List<MissionHeadTailStep> uavOpeningSteps(String protocolVersion) {
        if (!supportsUavOpeningSteps(protocolVersion)) {
            return List.of();
        }
        List<MissionHeadTailStep> steps = new ArrayList<>();
        steps.add(new MissionHeadTailStep(ACTION_UAV_BATTERY_CHECK, "电量检查", ParamsTemplate.NONE));
        if (includesWaylinePrepareInOpening(protocolVersion)) {
            steps.add(new MissionHeadTailStep(
                    ACTION_UAV_WAYLINE_PREPARE, "准备并启动航线", ParamsTemplate.UAV_FLIGHT_MISSION));
        }
        return List.copyOf(steps);
    }

    public static MissionHeadTailStep taskRecordStartStep() {
        return new MissionHeadTailStep(ACTION_TASK_RECORD_START, "开始录像", ParamsTemplate.NONE);
    }

    public static MissionHeadTailStep taskRecordStopStep() {
        return new MissionHeadTailStep(ACTION_TASK_RECORD_STOP, "结束录像", ParamsTemplate.NONE);
    }

    /**
     * 对照 EXECUTOR_MOUNT 槽名解析填包值。
     *
     * @param mountPoint    主机挂载点序号
     * @param cameraType    相机类型
     * @param payloadIndex  大疆 payloadIndex
     */
    public static Object resolveExecutorMountValue(
            String slotName,
            String protocolVersion,
            Object mountPoint,
            Object cameraType,
            Object payloadIndex) {
        if (!StringUtils.hasText(slotName)) {
            return null;
        }
        ExecutorMountValueKind kind = EXECUTOR_MOUNT_SLOT_KINDS.get(slotName.trim());
        if (kind == null) {
            return null;
        }
        return switch (kind) {
            case MOUNT_POINT -> mountPoint;
            case CAMERA_TYPE -> cameraType;
            case PAYLOAD_INDEX_OR_MOUNT -> ProtocolCodes.isDjiCloud(protocolVersion)
                    ? payloadIndex
                    : mountPoint;
        };
    }
}
