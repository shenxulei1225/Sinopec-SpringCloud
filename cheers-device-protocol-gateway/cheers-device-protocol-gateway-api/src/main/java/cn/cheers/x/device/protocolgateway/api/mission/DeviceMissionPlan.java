package cn.cheers.x.device.protocolgateway.api.mission;

import java.util.List;

/**
 * 指令下发入参：协议版本 + 已排好的动作列表。
 * <p>网关只按对照表填空包，不发明起飞/移动/降落顺序。
 * <p>禁止：用厂商名冒充协议版本；动作列表空了还组包。
 *
 * @param protocolVersion 对接协议版本（如 {@code robot-ws} / {@code uav-ws}）
 * @param deviceId        逻辑设备标识
 * @param taskId          这次任务台账（执行记录）id，回执和上报用它挂回同一行
 * @param templateId      本次任务模板编号
 * @param actions         已排好的动作；每步带动作 id 和参数袋
 */
public record DeviceMissionPlan(
        String protocolVersion,
        String deviceId,
        String taskId,
        String templateId,
        List<DispatchAction> actions
) {
}
