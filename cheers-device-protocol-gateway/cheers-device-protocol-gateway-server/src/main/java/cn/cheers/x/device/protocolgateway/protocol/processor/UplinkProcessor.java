package cn.cheers.x.device.protocolgateway.protocol.processor;

import cn.cheers.x.device.protocolgateway.api.dto.DeviceUplinkEventDTO;
import cn.cheers.x.device.protocolgateway.api.identity.UplinkIdentity;

/**
 * 已登记的上报处理方法。
 * <p>负责：按识别结果声明自己能不能处理，以及处理什么。
 * <p>不负责：接入监听、拆包、猜通道。
 * <p>禁止：supports 为 false 时仍写巡检台账；未登记却假装已处理。
 */
public interface UplinkProcessor {

    /**
     * 这条识别结果是否由本方法处理。
     * 对不上就返回 false，让登记表继续找；全部对不上则暴露缺口。
     */
    boolean supports(UplinkIdentity identity);

    /**
     * 处理一条已识别的上报。调用前必须 supports 为 true。
     */
    /**
     * 处理一条采集样本。业务只认 event.sample()，不要再拆原始报文。
     */
    void process(UplinkIdentity identity, DeviceUplinkEventDTO event);
}
