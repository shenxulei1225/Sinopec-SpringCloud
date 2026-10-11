package cn.cheers.x.device.protocolgateway.service;

import cn.cheers.x.device.protocolgateway.api.message.CommandSendEnvelope;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolCodes;
import cn.cheers.x.device.protocolgateway.djicloud.DjiCloudCommandSender;
import cn.cheers.x.device.protocolgateway.djicloud.DjiCloudOnlineRegistry;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import cn.cheers.x.device.protocolgateway.service.MissionDownlink.DownlinkPiece;
import cn.cheers.x.device.protocolgateway.transport.DeviceTransport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 能力验收：按对照翻译并可选下发。
 * <p>仅用于协议能力联调，不是巡检任务「开始执行」入口。不发启动执行。
 */
@Service
@RequiredArgsConstructor
public class DeviceProtocolCapabilityService {

    private final MissionTranslationService missionTranslationService;
    private final EnvelopeJsonCodec envelopeJsonCodec;
    private final DeviceProtocolDownlinkService downlinkService;
    private final DeviceTransport deviceTransport;
    private final DjiCloudOnlineRegistry cloudOnline;
    private final DjiCloudCommandSender cloudSender;

    /**
     * 预览翻译结果，不发送。有巡检指令包时 envelope 仍是那一包；wireJson 按发出顺序拼每条报文。
     */
    public CapabilityPreview preview(DeviceMissionPlan plan) {
        return toPreview(missionTranslationService.translate(plan), plan);
    }

    /**
     * 翻译后按顺序写出。离线或未等到同号成功答卷时 sent=false，不假装成功。
     */
    public CapabilityDispatchResult dispatch(DeviceMissionPlan plan) {
        MissionDownlink downlink = missionTranslationService.translate(plan);
        CapabilityPreview preview = toPreview(downlink, plan);
        boolean sent = false;
        if (preview.online()) {
            sent = true;
            for (DownlinkPiece piece : downlink.pieces()) {
                boolean accepted;
                if (piece.direct() != null && piece.direct().cloudService()) {
                    accepted = cloudSender.send(piece.direct()).accepted();
                } else if (piece.direct() != null) {
                    accepted = downlinkService.sendSyncCommand(
                            plan.deviceId(),
                            piece.direct().wireJson(),
                            piece.direct().opcode(),
                            piece.direct().msgId()).accepted();
                } else {
                    accepted = downlinkService.sendCommandPackage(piece.commandPackage()).accepted();
                }
                if (!accepted) {
                    sent = false;
                    break;
                }
            }
        }
        return new CapabilityDispatchResult(preview, sent);
    }

    private CapabilityPreview toPreview(MissionDownlink downlink, DeviceMissionPlan plan) {
        String deviceId = plan.deviceId();
        CommandSendEnvelope envelope = null;
        List<String> wires = new ArrayList<>();
        for (DownlinkPiece piece : downlink.pieces()) {
            if (piece.direct() != null) {
                wires.add(piece.direct().wireJson());
            } else {
                envelope = piece.commandPackage();
                wires.add(envelopeJsonCodec.encodeCommandSend(envelope));
            }
        }
        return new CapabilityPreview(envelope, String.join("\n", wires), online(plan.protocolVersion(), deviceId));
    }

    private boolean online(String protocolVersion, String deviceId) {
        if (ProtocolCodes.isDjiCloud(protocolVersion)) {
            return cloudOnline.isOnline(deviceId);
        }
        return deviceTransport.isOnline(deviceId);
    }

    public record CapabilityPreview(CommandSendEnvelope envelope, String wireJson, boolean online) {
    }

    public record CapabilityDispatchResult(CapabilityPreview preview, boolean sent) {
    }
}
