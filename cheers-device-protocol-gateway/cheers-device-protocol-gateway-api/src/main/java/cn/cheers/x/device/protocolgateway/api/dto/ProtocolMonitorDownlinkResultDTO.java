package cn.cheers.x.device.protocolgateway.api.dto;

/**
 * 一次模拟下发的结果。写出还不等于对端收下。
 *
 * @param written         是否已经写到这台设备的连接上
 * @param businessSuccess 同步指令是否等到同号答卷且 code=200；不等答卷的指令为 false
 * @param note            给页面的结果
 * @param payloadJson     实际写出的报文
 */
public record ProtocolMonitorDownlinkResultDTO(
        boolean written,
        boolean businessSuccess,
        String note,
        String payloadJson
) {
}
