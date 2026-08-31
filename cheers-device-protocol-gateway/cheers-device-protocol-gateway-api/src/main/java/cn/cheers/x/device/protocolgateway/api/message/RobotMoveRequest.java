package cn.cheers.x.device.protocolgateway.api.message;

/**
 * 机器人移动动作 request 字段（对照联调样例）。
 *
 * @param pointId 勘察地图点位 id（必填）
 * @param lat     纬度
 * @param lng     经度
 * @param heading 航向角
 */
public record RobotMoveRequest(
        String pointId,
        String lat,
        String lng,
        Double heading
) {
}
