package cn.cheers.x.device.protocolgateway.api.dto;

/**
 * 监控页模拟下发：只指定设备和指令。报文由服务端按空包生成。
 *
 * @param deviceId 当前选中的逻辑设备标识
 * @param opcode   要下发的操作码
 */
public record ProtocolMonitorDownlinkRequest(
        String deviceId,
        int opcode
) {
}
