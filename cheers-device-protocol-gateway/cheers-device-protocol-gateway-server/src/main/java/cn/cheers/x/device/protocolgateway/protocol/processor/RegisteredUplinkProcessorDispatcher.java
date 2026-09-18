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
 * <p>负责：把总线事件交给 supports 为 true 的方法。
 * <p>不负责：监听、拆包、发明处理方法。
 * <p>禁止：没有方法登记时猜成巡检写台账。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RegisteredUplinkProcessorDispatcher implements UplinkEventConsumer {

    private final List<UplinkProcessor> processors;

    @Override
    public void consume(DeviceUplinkEventDTO event) {
        UplinkIdentity identity = event.identity();
        boolean matched = false;
        for (UplinkProcessor processor : processors) {
            if (!processor.supports(identity)) {
                continue;
            }
            matched = true;
            processor.process(identity, event);
        }
        if (!matched) {
            log.error("[device-protocol] 未登记处理方法，不猜业务 channel={} deviceId={} messageKind={}",
                    identity.channelCode(), identity.deviceId(), identity.messageKind());
        }
    }
}
