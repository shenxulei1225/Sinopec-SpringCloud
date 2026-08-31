package cn.cheers.x.device.protocolgateway.protocol.uplink;

import cn.cheers.x.device.protocolgateway.api.dto.DeviceUplinkEventDTO;
import cn.cheers.x.device.protocolgateway.api.opcode.TransportOpcode;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 上行分发：按外层操作码分支；业务态通过可配置回调推送，网关不读巡检库。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UplinkDispatcher {

    private final EnvelopeJsonCodec envelopeJsonCodec;
    private final DeviceUplinkNotifier deviceUplinkNotifier;

    public void dispatch(String deviceId, String payloadJson) {
        int opcode = envelopeJsonCodec.readOpcode(payloadJson);
        TransportOpcode transportOpcode;
        try {
            transportOpcode = TransportOpcode.fromCode(opcode);
        } catch (IllegalArgumentException ex) {
            log.warn("[device-protocol] 未知上行 opcode={} deviceId={} payload={}", opcode, deviceId, payloadJson);
            return;
        }
        switch (transportOpcode) {
            case HEARTBEAT -> log.debug("[device-protocol] 心跳 deviceId={}", deviceId);
            case ACK -> log.debug("[device-protocol] ACK deviceId={}", deviceId);
            case TIMED_DEVICE_STATUS -> log.info("[device-protocol] 定时状态 deviceId={}", deviceId);
            case COMMAND_RESULT -> log.info("[device-protocol] 指令结果 deviceId={}", deviceId);
            case TASK_STATUS -> log.info("[device-protocol] 任务状态 deviceId={}", deviceId);
            case FAULT_REPORT -> log.warn("[device-protocol] 故障上报 deviceId={}", deviceId);
            case LINK_STATE_EVENT -> log.info("[device-protocol] 链路事件 deviceId={}", deviceId);
            default -> log.info("[device-protocol] 上行 opcode={} deviceId={}", transportOpcode, deviceId);
        }
        // 心跳过于频繁，不推业务；其余上行推给任务侧回写
        if (transportOpcode != TransportOpcode.HEARTBEAT && transportOpcode != TransportOpcode.ACK) {
            deviceUplinkNotifier.notifyBusiness(new DeviceUplinkEventDTO(
                    deviceId,
                    opcode,
                    payloadJson,
                    System.currentTimeMillis()
            ));
        }
    }
}
