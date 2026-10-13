package cn.cheers.x.device.protocolgateway.api.dto;

/**
 * 监控页可选设备：台账已登记默认协议的，以及当前在线或窗口里刚收过包的。
 *
 * @param deviceId     逻辑设备标识
 * @param online       正式对接口是否仍连着
 * @param lastEventAt  最近一条副本时间；没有则为空
 * @param name         巡检设备台账上的名称；对不上台账时为空
 * @param protocol     这台登记的默认协议；没有时为空
 */
public record ProtocolMonitorDeviceDTO(
        String deviceId,
        boolean online,
        Long lastEventAt,
        String name,
        String protocol
) {
}
