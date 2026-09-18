package cn.cheers.x.device.protocolgateway.api.identity;

/**
 * 上报识别结果：先认出是哪条通道、哪台设备、哪种报文，再谈处理方法。
 * <p>负责：承载识别身份。
 * <p>不负责：监听、拆包、写巡检台账。
 * <p>禁止：通道或报文种类为空时猜成巡检。
 *
 * @param channelCode 接入通道（巡检 / 工业等）
 * @param deviceId    设备编号
 * @param messageKind 报文种类；巡检通道即正文操作码
 */
public record UplinkIdentity(
        String channelCode,
        String deviceId,
        String messageKind
) {

    public UplinkIdentity {
        if (channelCode == null || channelCode.isBlank()) {
            throw new IllegalArgumentException("上报识别结果缺少接入通道，禁止默认成巡检");
        }
        if (deviceId == null || deviceId.isBlank()) {
            throw new IllegalArgumentException("上报识别结果缺少设备编号");
        }
        if (messageKind == null || messageKind.isBlank()) {
            throw new IllegalArgumentException("上报识别结果缺少报文种类");
        }
    }
}
