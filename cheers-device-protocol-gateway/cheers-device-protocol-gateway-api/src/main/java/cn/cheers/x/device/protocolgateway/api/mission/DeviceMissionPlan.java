package cn.cheers.x.device.protocolgateway.api.mission;

import java.util.List;

/**
 * 中立执行意图：网关翻译层入参。
 * <p>内容应由「本次巡检对象 + 巡检内容 + 路径」动态算出后填入；
 * 不是系统预制的固定指令包。网关不读巡检库表。
 *
 * @param protocolCode 对接协议编码（选适配器），如 {@code zhiren-robot-ws}
 * @param deviceId     逻辑设备标识（注册表用它找当前连接；不是瞬时连接句柄）
 * @param taskId       外部会话 id = 任务模块执行记录 id（写入 500201；上行回绑执行记录）
 * @param templateId   本次任务模板编号（地面站存包键；与 taskId 可不同）
 * @param waypoints    本次有序停靠与点上动作
 */
public record DeviceMissionPlan(
        String protocolCode,
        String deviceId,
        String taskId,
        String templateId,
        List<MissionWaypoint> waypoints
) {
}
