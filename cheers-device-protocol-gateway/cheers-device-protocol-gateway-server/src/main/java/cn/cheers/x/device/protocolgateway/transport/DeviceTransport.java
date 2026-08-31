package cn.cheers.x.device.protocolgateway.transport;

/**
 * 通讯层：按逻辑设备标识收发文本，不解析操作码。
 * <p>注册表键为稳定逻辑标识；当前连接会随断连重连变化。
 * <p>禁止在本接口实现内写业务动作码逻辑。
 */
public interface DeviceTransport {

    /**
     * 向已绑定会话发送文本。
     *
     * @param deviceId 逻辑设备标识
     * @param text     完整报文文本
     * @return true 已写出；false 会话不存在或不可写（调用方须显式处理，禁止假装成功）
     */
    boolean sendText(String deviceId, String text);

    /**
     * 当前设备是否在线（已建立可用会话）。
     */
    boolean isOnline(String deviceId);
}
