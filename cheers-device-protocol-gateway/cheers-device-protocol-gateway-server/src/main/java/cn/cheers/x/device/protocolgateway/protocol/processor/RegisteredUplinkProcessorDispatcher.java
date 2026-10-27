package cn.cheers.x.device.protocolgateway.protocol.processor;

import cn.cheers.x.device.protocolgateway.api.dto.DeviceUplinkEventDTO;
import cn.cheers.x.device.protocolgateway.api.identity.UplinkIdentity;
import cn.cheers.x.device.protocolgateway.protocol.uplink.UplinkEventConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 按识别结果只跑已登记的处理方法。
 * <p>负责：协议书里有的报文，才交给对得上的型号处理。已经收成标准事件的报文也交给这台设备的处理模块。
 * <p>不负责：监听、拆包、发明处理方法。
 * <p>禁止：协议书没有的操作码仍送给该型号的处理模块。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RegisteredUplinkProcessorDispatcher implements UplinkEventConsumer {

    private final List<UplinkProcessor> processors;
    private final ProtocolBookAdmission protocolBookAdmission;

    @Override
    public void consume(DeviceUplinkEventDTO event) {
        UplinkIdentity identity = event.identity();
        ProtocolBookAdmission.Decision decision = protocolBookAdmission.decide(event.sample());
        if (!decision.admitted()) {
            log.error("[device-protocol] {} channel={} deviceId={} messageKind={}",
                    decision.reason(), identity.channelCode(), identity.deviceId(), identity.messageKind());
            return;
        }
        boolean matched = false;
        for (UplinkProcessor processor : processors) {
            if (!processor.supports(identity)) {
                continue;
            }
            matched = true;
            processor.process(identity, event);
        }
        if (!matched) {
            log.error("[device-protocol] 未登记处理方法，不猜业务 channel={} deviceId={} messageKind={} protocolVersion={}",
                    identity.channelCode(), identity.deviceId(), identity.messageKind(), decision.protocolVersion());
        }
    }
}
