package cn.cheers.x.device.protocolgateway.protocol.uplink;

import cn.cheers.x.device.protocolgateway.api.dto.DeviceUplinkEventDTO;

/**
 * 上报消费者：在收包线程之外处理总线事件。
 * <p>HTTP 回调若实现本接口，只算过渡适配，不是正式主路径。
 */
public interface UplinkEventConsumer {

    void consume(DeviceUplinkEventDTO event);
}
