package cn.cheers.x.device.protocolgateway.api.channel;

/**
 * 接入通道编码：标明这包是从哪条监听进来的。
 * <p>负责：给识别结果一个稳定通道名。
 * <p>不负责：监听、拆包、挑选处理方法。
 * <p>禁止：用设备编号猜通道；通道空了默认成巡检。
 */
public final class AccessChannelCodes {

    /** 巡检 WebSocket 单口（路径最后一段是设备编号，与旧系统一致） */
    public static final String INSPECTION = "inspection";

    /** 工业设备接入通道。第一期只声明身份，不监听、不处理 */
    public static final String INDUSTRIAL = "industrial";

    private AccessChannelCodes() {
    }
}
