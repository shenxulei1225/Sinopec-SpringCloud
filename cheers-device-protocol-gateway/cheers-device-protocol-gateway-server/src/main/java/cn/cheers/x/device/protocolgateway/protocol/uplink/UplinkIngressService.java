package cn.cheers.x.device.protocolgateway.protocol.uplink;

import cn.cheers.x.device.protocolgateway.api.dto.DeviceUplinkEventDTO;
import cn.cheers.x.device.protocolgateway.api.opcode.InstructionExchangeCatalog;
import cn.cheers.x.device.protocolgateway.api.opcode.InstructionExchangeMode;
import cn.cheers.x.device.protocolgateway.datacollection.CollectionQualifyService;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import cn.cheers.x.device.protocolgateway.protocol.monitor.ProtocolMonitorHub;
import cn.cheers.x.device.protocolgateway.transport.DeviceTransport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 通信层收包入口：按固定顺序处理心跳、回执、同步答卷、其余上报。
 * <p>负责：心跳回显、立刻回执、完成正在等的同号答卷、将其余包送进总线；进监控窗口复制上报原文。
 * <p>不负责：填协议空包、读巡检库、判断巡检步骤成败、监控页展示、猜通道、按字段说明做合格核对。
 * <p>禁止：在本方法里 HTTP / 写库；把回执当成业务成功；通道空了默认成巡检；把原文当业务主契约。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UplinkIngressService {

    private final EnvelopeJsonCodec envelopeJsonCodec;
    private final DeviceTransport deviceTransport;
    private final SyncReplyWaiter syncReplyWaiter;
    private final UplinkEventPublisher uplinkEventPublisher;
    private final ProtocolMonitorHub protocolMonitorHub;
    private final CollectionQualifyService collectionQualifyService;

    /**
     * 处理设备发来的一包。通道由接入口传入，本方法不猜。
     * 缺通道或缺操作码则暴露错误并结束，不猜成巡检、心跳或回执。
     */
    public void handle(String channelCode, String deviceId, String payloadJson) {
        if (channelCode == null || channelCode.isBlank()) {
            log.error("[device-protocol] 收包缺少接入通道，不往下走 deviceId={}", deviceId);
            return;
        }
        protocolMonitorHub.copyInbound(deviceId, payloadJson);
        final int opcode;
        try {
            opcode = envelopeJsonCodec.readOpcode(payloadJson);
        } catch (IllegalArgumentException ex) {
            log.error("[device-protocol] 收包无法解析 opcode deviceId={} payload={}", deviceId, payloadJson);
            return;
        }
        String msgId = envelopeJsonCodec.readMsgId(payloadJson);
        InstructionExchangeMode mode = InstructionExchangeCatalog.modeOf(opcode);
        if (mode == InstructionExchangeMode.HEARTBEAT) {
            echoHeartbeat(deviceId, payloadJson);
            return;
        }
        if (mode == InstructionExchangeMode.ACK) {
            log.debug("[device-protocol] 收到回执 deviceId={} msgId={}", deviceId, msgId);
            return;
        }
        ackReceived(deviceId, msgId, opcode);
        if (mode == InstructionExchangeMode.SYNC_WAIT_RESULT
                && syncReplyWaiter.completeIfPending(deviceId, msgId, opcode, payloadJson)) {
            log.debug("[device-protocol] 同步答卷已对上 deviceId={} msgId={} opcode={}", deviceId, msgId, opcode);
            return;
        }
        uplinkEventPublisher.publish(DeviceUplinkEventDTO.of(
                collectionQualifyService.fromStructuredUplink(
                        channelCode,
                        deviceId,
                        opcode,
                        msgId,
                        payloadJson,
                        System.currentTimeMillis()
                )));
    }

    private void echoHeartbeat(String deviceId, String payloadJson) {
        boolean sent = deviceTransport.sendText(
                deviceId, envelopeJsonCodec.encodeHeartbeatEcho(payloadJson, nowEpochSeconds()));
        if (!sent) {
            log.warn("[device-protocol] 心跳回显写出失败 deviceId={}", deviceId);
        }
    }

    private void ackReceived(String deviceId, String msgId, int opcode) {
        boolean sent = deviceTransport.sendText(
                deviceId, envelopeJsonCodec.encodeAck(msgId, opcode, nowEpochSeconds()));
        if (!sent) {
            log.warn("[device-protocol] 回执写出失败 deviceId={} msgId={} opcode={}", deviceId, msgId, opcode);
        }
    }

    private static long nowEpochSeconds() {
        return System.currentTimeMillis() / 1000L;
    }
}
