package cn.cheers.x.device.protocolgateway.api.vehicle;

import java.util.Map;

/**
 * 按这一家已经配好的指令对照，发一条车辆 HTTPS 指令。
 *
 * @param deviceCode 设备账上的设备编号
 * @param actionCode {@link VehicleGateActions} 里的编码
 * @param params 对照里的来源字段。键是参数槽
 */
public record VehicleGateCommandReq(String deviceCode, String actionCode, Map<String, Object> params) {
}
