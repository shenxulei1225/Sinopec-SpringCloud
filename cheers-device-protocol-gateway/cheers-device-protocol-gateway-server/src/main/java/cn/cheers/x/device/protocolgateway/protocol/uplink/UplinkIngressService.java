package cn.cheers.x.device.protocolgateway.protocol.uplink;

import cn.cheers.x.device.protocolgateway.api.dto.DeviceUplinkEventDTO;
import cn.cheers.x.device.protocolgateway.api.opcode.InstructionExchangeCatalog;
import cn.cheers.x.device.protocolgateway.api.opcode.InstructionExchangeMode;
import cn.cheers.x.device.protocolgateway.datacollection.CollectionQualifyService;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import cn.cheers.x.device.protocolgateway.protocol.exchange.RunningTaskExchangeRecorder;
import cn.cheers.x.device.protocolgateway.protocol.monitor.ProtocolMonitorHub;
import cn.cheers.x.device.protocolgateway.transport.DeviceTransport;
import cn.cheers.x.inspection.task.api.dto.ReplayPoseReqDTO;
import cn.cheers.x.inspection.task.api.dto.TaskExchangeTranscriptReqDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 通信层收包入口：按固定顺序处理心跳、回执、同步答卷、其余上报。
 * <p>负责：设备心跳只记账（在线/监控/往来），立刻回执、完成正在等的同号答卷、将其余包送进总线；进监控窗口复制上报原文。收回的原文、心跳和回执只送进往来队列。状态里的经纬度另送回放位置队列，同一设备一秒内只留一个点。
 * <p>设备上行 {@code 500105} 不再写回心跳，避免与会对回心跳的设备 ping-pong；平台下发心跳仍由写出路径负责。
 * <p>不负责：填协议空包、读巡检库、判断巡检步骤成败、监控页展示、猜通道、按字段说明做合格核对、在这条线程上写执行账。
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
    private final RunningTaskExchangeRecorder runningTaskExchangeRecorder;
    private final GroundStationLiveStatusStore groundStationLiveStatusStore;
    private final ConcurrentMap<String, Long> lastPoseAt = new ConcurrentHashMap<>();

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
        final String msgId;
        try {
            opcode = envelopeJsonCodec.readOpcode(payloadJson);
            msgId = envelopeJsonCodec.readMsgId(payloadJson);
        } catch (IllegalArgumentException ex) {
            runningTaskExchangeRecorder.enqueue(deviceId, TaskExchangeTranscriptReqDTO.INBOUND, payloadJson);
            log.error("[device-protocol] 收包无法解析 opcode deviceId={} payload={}", deviceId, payloadJson);
            return;
        }
        Long linked = runningTaskExchangeRecorder.linked(deviceId, msgId);
        if (linked == null || linked <= 0L) {
            linked = null;
            runningTaskExchangeRecorder.enqueue(deviceId, TaskExchangeTranscriptReqDTO.INBOUND, payloadJson);
        } else {
            runningTaskExchangeRecorder.enqueue(
                    deviceId, TaskExchangeTranscriptReqDTO.INBOUND, payloadJson, linked);
        }
        offerPose(deviceId, opcode, payloadJson, linked);
        groundStationLiveStatusStore.remember(deviceId, opcode, payloadJson);
        InstructionExchangeMode mode = InstructionExchangeCatalog.modeOf(opcode);
        if (mode == InstructionExchangeMode.HEARTBEAT) {
            log.debug("[device-protocol] 收到心跳 deviceId={} msgId={}", deviceId, msgId);
            return;
        }
        if (mode == InstructionExchangeMode.ACK) {
            log.debug("[device-protocol] 收到回执 deviceId={} msgId={}", deviceId, msgId);
            return;
        }
        ackReceived(deviceId, msgId, opcode, linked);
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

    private void ackReceived(String deviceId, String msgId, int opcode, Long executionRecordId) {
        String ack = envelopeJsonCodec.encodeAck(msgId, opcode, nowEpochSeconds());
        boolean sent = deviceTransport.sendText(deviceId, ack);
        if (!sent) {
            log.warn("[device-protocol] 回执写出失败 deviceId={} msgId={} opcode={}", deviceId, msgId, opcode);
            return;
        }
        if (executionRecordId == null) {
            runningTaskExchangeRecorder.enqueue(deviceId, TaskExchangeTranscriptReqDTO.OUTBOUND, ack);
            return;
        }
        runningTaskExchangeRecorder.enqueue(
                deviceId, TaskExchangeTranscriptReqDTO.OUTBOUND, ack, executionRecordId);
    }

    /** 同一设备相邻位置点至少隔一秒。对不上执行记录时仍入队，由巡检决定挂不挂。 */
    private void offerPose(String deviceId, int opcode, String payloadJson, Long executionRecordId) {
        ReplayPoseReqDTO pose = GroundStationPose.fromStatus(
                deviceId, opcode, payloadJson, System.currentTimeMillis());
        if (pose == null || pose.getT() == null) {
            return;
        }
        Long previous = lastPoseAt.get(deviceId);
        if (previous != null && pose.getT() - previous < 1000L) {
            return;
        }
        lastPoseAt.put(deviceId, pose.getT());
        if (executionRecordId != null) {
            pose.setExecutionRecordId(executionRecordId);
        }
        runningTaskExchangeRecorder.enqueuePose(pose);
    }

    private static long nowEpochSeconds() {
        return System.currentTimeMillis() / 1000L;
    }
}
