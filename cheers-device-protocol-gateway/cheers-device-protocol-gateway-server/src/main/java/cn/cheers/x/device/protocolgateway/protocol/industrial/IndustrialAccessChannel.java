package cn.cheers.x.device.protocolgateway.protocol.industrial;

import cn.cheers.x.device.protocolgateway.api.channel.AccessChannelCodes;

/**
 * 工业设备接入通道。
 * <p>负责：将来在本通道上监听、拆包、产出带工业通道的识别结果。
 * <p>不负责：巡检 WebSocket、写入任务执行过程台账。
 * <p>第一期：只声明通道身份，不监听、不处理。
 * <p>禁止：把工业报文接到巡检单口；未登记处理方法就当巡检写。
 */
public interface IndustrialAccessChannel {

    /** 本通道编码，固定为工业。 */
    default String channelCode() {
        return AccessChannelCodes.INDUSTRIAL;
    }
}
