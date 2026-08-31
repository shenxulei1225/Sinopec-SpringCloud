package cn.cheers.x.device.protocolgateway.api.mission;

/**
 * 中立停靠点：有序路径上的一点；巡检侧日后把路网结果映射成此结构。
 *
 * @param pointId 点位 id（机器人协议必填）
 * @param lat     纬度
 * @param lng     经度
 * @param heading 航向（机器人联调样例有）
 * @param height  高度（无人机用；机器人可空）
 * @param action  到达后动作
 */
public record MissionWaypoint(
        String pointId,
        String lat,
        String lng,
        Double heading,
        String height,
        MissionPointActionType action
) {
    public MissionWaypoint {
        if (action == null) {
            action = MissionPointActionType.NONE;
        }
    }
}
