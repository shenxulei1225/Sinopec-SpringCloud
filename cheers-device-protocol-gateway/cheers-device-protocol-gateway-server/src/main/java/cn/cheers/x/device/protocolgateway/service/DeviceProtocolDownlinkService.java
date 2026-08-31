package cn.cheers.x.device.protocolgateway.service;

import cn.cheers.x.device.protocolgateway.api.message.CommandSendEnvelope;
import cn.cheers.x.device.protocolgateway.api.message.StartupExecuteEnvelope;
import cn.cheers.x.device.protocolgateway.api.message.TaskStopEnvelope;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import cn.cheers.x.device.protocolgateway.transport.DeviceTransport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 协议下行：信封编码后经通讯层发送。
 * <p>不负责任务编排与巡检对象解析；设备不在线时返回 false，不假装成功。
 */
@Service
@RequiredArgsConstructor
public class DeviceProtocolDownlinkService {

    private final EnvelopeJsonCodec envelopeJsonCodec;
    private final DeviceTransport deviceTransport;

    /**
     * 下发指令包（外层 500104）。
     *
     * @return true 已写出；false 设备不在线或写出失败
     */
    public boolean sendCommandPackage(CommandSendEnvelope envelope) {
        requireDeviceId(envelope.deviceId());
        return deviceTransport.sendText(envelope.deviceId(), envelopeJsonCodec.encodeCommandSend(envelope));
    }

    /**
     * 启动执行（外层 500201）。
     */
    public boolean startupExecute(StartupExecuteEnvelope envelope) {
        requireDeviceId(envelope.deviceId());
        return deviceTransport.sendText(envelope.deviceId(), envelopeJsonCodec.encodeStartupExecute(envelope));
    }

    /**
     * 终止任务（外层 500205）。
     */
    public boolean taskStop(TaskStopEnvelope envelope) {
        requireDeviceId(envelope.deviceId());
        return deviceTransport.sendText(envelope.deviceId(), envelopeJsonCodec.encodeTaskStop(envelope));
    }

    private static void requireDeviceId(String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            throw new IllegalArgumentException("必须提供 deviceId");
        }
    }
}
