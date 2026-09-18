package cn.cheers.x.device.protocolgateway.protocol.processor;

import cn.cheers.x.device.protocolgateway.api.channel.AccessChannelCodes;
import cn.cheers.x.device.protocolgateway.api.dto.DeviceUplinkEventDTO;
import cn.cheers.x.device.protocolgateway.api.identity.UplinkIdentity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 巡检通道已识别时的占位处理。
 * <p>负责：声明巡检单口已经认出通道、设备、报文种类。
 * <p>不负责：写入任务执行过程台账（该方法尚未登记落地）。
 * <p>禁止：假装已写入过程台账；处理工业通道报文。
 */
@Slf4j
@Component
public class InspectionIdentifyOnlyProcessor implements UplinkProcessor {

    @Override
    public boolean supports(UplinkIdentity identity) {
        return AccessChannelCodes.INSPECTION.equals(identity.channelCode());
    }

    @Override
    public void process(UplinkIdentity identity, DeviceUplinkEventDTO event) {
        log.info("[device-protocol] 已识别巡检采集样本 channel={} deviceId={} messageKind={} qualify={}；"
                        + "过程追加由巡检已登记方法消费样本",
                identity.channelCode(), identity.deviceId(), identity.messageKind(),
                event.sample().protocolQualify());
    }
}
