package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolDispatchProfile;
import cn.cheers.x.inspection.task.service.execution.steptree.TaskStepTreeGenerator;
import org.springframework.util.StringUtils;

/**
 * 巡检任务级全程录像：按对接协议分流，检查项里不再重复开停录。
 * <p>东古/机器人 WebSocket：200201/200202，收流地址在物化待执行时按计划点分配。
 * 大疆上云：机内 camera_recording_start/stop，不带 stream_url。
 */
public final class TaskPatrolRecordingSupport {

    public static final String ACTION_START_RECORD = "startRecord";
    public static final String ACTION_STOP_RECORD = "stopRecord";
    public static final String PARAM_STREAM_URL = "stream_url";
    public static final String SNAPSHOT_TASK_RECORD_INGEST_URL = "taskRecordIngestUrl";
    public static final String SNAPSHOT_TASK_RECORD_STREAM_NAME = "taskRecordStreamName";
    /** 执行账 custom_fields：本次任务级录像的收流与回放绑定（与轨迹会话 media channel 摘要同源）。 */
    public static final String CUSTOM_FIELD_TASK_PATROL_RECORD_MEDIA = "task_patrol_record_media";

    private TaskPatrolRecordingSupport() {
    }

    /** 无人机或机器人执行方式，且协议已登记全程录像能力。 */
    public static boolean taskLevelRecordingEnabled(String means, String protocolVersion) {
        if (!StringUtils.hasText(means) || !StringUtils.hasText(protocolVersion)) {
            return false;
        }
        String meansUpper = means.trim().toUpperCase();
        if (!TaskStepTreeGenerator.MEANS_UAV.equals(meansUpper)
                && !TaskStepTreeGenerator.MEANS_ROBOT.equals(meansUpper)) {
            return false;
        }
        return ProtocolDispatchProfile.taskLevelRecordingEnabled(means, protocolVersion);
    }

    /** 服务器收流写在 request.streamUrl。物化时必须写入 stream_url。 */
    public static boolean usesServerIngest(String protocolVersion) {
        return ProtocolDispatchProfile.usesServerIngest(protocolVersion);
    }

    public static boolean isTaskLevelRecordAction(String actionCode) {
        if (!StringUtils.hasText(actionCode)) {
            return false;
        }
        String code = actionCode.trim();
        return ACTION_START_RECORD.equals(code) || ACTION_STOP_RECORD.equals(code);
    }

    /** 检查方法步骤里去掉点级开停录，避免与任务头尾重复。 */
    public static boolean dropFromInspectionMethod(String actionCode, String means, String protocolVersion) {
        return taskLevelRecordingEnabled(means, protocolVersion) && isTaskLevelRecordAction(actionCode);
    }

}
