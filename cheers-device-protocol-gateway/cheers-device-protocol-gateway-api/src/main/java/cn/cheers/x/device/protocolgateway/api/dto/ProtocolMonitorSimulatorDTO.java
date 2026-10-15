package cn.cheers.x.device.protocolgateway.api.dto;

/**
 * 协议监控页上一个模拟器现在的状态。
 *
 * @param role     uav 或 robot
 * @param label    页面上的名字
 * @param deviceId 这个进程连上后冒充的设备编号
 * @param phase     stopped 未启动，starting 正在启动或还没连上，online 已经连上设备口
 * @param note      给页面的一句说明
 * @param processUp 模拟器端口已经在听。只有这时停止才会停掉进程
 */
public record ProtocolMonitorSimulatorDTO(
        String role,
        String label,
        String deviceId,
        String phase,
        String note,
        boolean processUp
) {
}
