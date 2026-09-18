package cn.cheers.x.device.protocolgateway.api.dto;

/**
 * 监控页可选设备：当前在线，或内存窗口里刚收过包的设备。
 *
 * @param deviceId     逻辑设备标识
 * @param online       正式对接口是否仍连着
 * @param lastEventAt  最近一条副本时间；没有则为空
 */
public record ProtocolMonitorDeviceDTO(
        String deviceId,
        boolean online,
        Long lastEventAt
) {
}
