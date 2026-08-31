package cn.cheers.x.device.protocolgateway.service;

import cn.cheers.x.device.protocolgateway.api.message.CommandSendEnvelope;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import cn.cheers.x.device.protocolgateway.transport.DeviceTransport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 能力验收：翻译执行意图并可选下发。
 * <p>仅用于协议能力联调，不是巡检任务「开始执行」入口。
 */
@Service
@RequiredArgsConstructor
public class DeviceProtocolCapabilityService {

    private final MissionTranslationService missionTranslationService;
    private final EnvelopeJsonCodec envelopeJsonCodec;
    private final DeviceProtocolDownlinkService downlinkService;
    private final DeviceTransport deviceTransport;

    /**
     * 预览：动态翻译为 500104 报文文本；不发送。
     */
    public CapabilityPreview preview(DeviceMissionPlan plan) {
        CommandSendEnvelope envelope = missionTranslationService.translate(plan);
        String wireJson = envelopeJsonCodec.encodeCommandSend(envelope);
        return new CapabilityPreview(envelope, wireJson, deviceTransport.isOnline(plan.deviceId()));
    }

    /**
     * 翻译后尝试下发；离线时仍返回预览，sent=false。
     */
    public CapabilityDispatchResult dispatch(DeviceMissionPlan plan) {
        CapabilityPreview preview = preview(plan);
        boolean sent = false;
        if (preview.online()) {
            sent = downlinkService.sendCommandPackage(preview.envelope());
        }
        return new CapabilityDispatchResult(preview, sent);
    }

    public record CapabilityPreview(CommandSendEnvelope envelope, String wireJson, boolean online) {
    }

    public record CapabilityDispatchResult(CapabilityPreview preview, boolean sent) {
    }
}
