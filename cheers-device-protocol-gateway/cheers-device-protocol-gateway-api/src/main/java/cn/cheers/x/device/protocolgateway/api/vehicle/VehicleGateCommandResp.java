package cn.cheers.x.device.protocolgateway.api.vehicle;

/**
 * 一条车辆指令有没有真正发给厂商。
 *
 * @param sent 厂商返回成功
 * @param message 没发出或被拒绝时的原因。发出时为空
 */
public record VehicleGateCommandResp(boolean sent, String message) {
}
