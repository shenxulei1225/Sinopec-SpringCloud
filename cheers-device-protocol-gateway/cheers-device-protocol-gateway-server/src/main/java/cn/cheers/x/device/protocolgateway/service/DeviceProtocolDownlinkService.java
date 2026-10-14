package cn.cheers.x.device.protocolgateway.service;

import cn.cheers.x.device.protocolgateway.api.message.CommandSendEnvelope;
import cn.cheers.x.device.protocolgateway.api.message.StartupExecuteEnvelope;
import cn.cheers.x.device.protocolgateway.api.message.TaskStopEnvelope;
import cn.cheers.x.device.protocolgateway.api.opcode.InstructionExchangeCatalog;
import cn.cheers.x.device.protocolgateway.api.opcode.TransportOpcode;
import cn.cheers.x.device.protocolgateway.config.DeviceProtocolGatewayProperties;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import cn.cheers.x.device.protocolgateway.protocol.uplink.SyncReplyWaiter;
import cn.cheers.x.device.protocolgateway.protocol.exchange.RunningTaskExchangeRecorder;
import cn.cheers.x.device.protocolgateway.transport.DeviceTransport;
import cn.cheers.x.inspection.task.api.dto.TaskExchangeTranscriptReqDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * 协议下行：信封编码后经通信层发送；同步指令还要等到同号业务答卷。
 * <p>不负责任务编排与巡检对象解析；设备不在线时失败，不假装成功。
 * <p>禁止：写出即返回成功；把回执 500106 当答卷。
 */
@Service
@RequiredArgsConstructor
public class DeviceProtocolDownlinkService {

    private final EnvelopeJsonCodec envelopeJsonCodec;
    private final DeviceTransport deviceTransport;
    private final SyncReplyWaiter syncReplyWaiter;
    private final DeviceProtocolGatewayProperties properties;
    private final RunningTaskExchangeRecorder runningTaskExchangeRecorder;

    /**
     * 下发一条对照选中的完整指令（空包里没有 request 的那种）。成功只表示同号答卷 code=200。
     */
    public SyncDownlinkResult sendSyncCommand(String deviceId, String wireJson, int opcode, String msgId) {
        return sendSyncCommand(deviceId, wireJson, opcode, msgId, null);
    }

    /**
     * 平台这次已经选中执行记录时，把编号一并记下。指令正文没有任务 id 时，答卷仍归到这次。
     */
    public SyncDownlinkResult sendSyncCommand(
            String deviceId, String wireJson, int opcode, String msgId, Long executionRecordId) {
        requireDeviceId(deviceId);
        return sendAndAwait(deviceId, wireJson, opcode, msgId, executionRecordId);
    }

    /**
     * 下发指令包（外层 500104）。成功只表示对端同号答卷 code=200（包收下了），不是步骤做完。
     */
    public SyncDownlinkResult sendCommandPackage(CommandSendEnvelope envelope) {
        return sendCommandPackage(envelope, null);
    }

    /** 指令包正文没有任务 id。executionRecordId 是平台这次正在下发的执行记录。 */
    public SyncDownlinkResult sendCommandPackage(CommandSendEnvelope envelope, Long executionRecordId) {
        requireDeviceId(envelope.deviceId());
        return sendAndAwait(
                envelope.deviceId(),
                envelopeJsonCodec.encodeCommandSend(envelope),
                TransportOpcode.COMMAND_SEND.code(),
                envelope.msgId(),
                executionRecordId
        );
    }

    /**
     * 启动执行（外层 500201）。成功只表示启动请求收下了，是否真开跑看后来的任务状态上报。
     */
    public SyncDownlinkResult startupExecute(StartupExecuteEnvelope envelope) {
        return startupExecute(envelope, null);
    }

    public SyncDownlinkResult startupExecute(StartupExecuteEnvelope envelope, Long executionRecordId) {
        requireDeviceId(envelope.deviceId());
        return sendAndAwait(
                envelope.deviceId(),
                envelopeJsonCodec.encodeStartupExecute(envelope),
                TransportOpcode.STARTUP_EXECUTE.code(),
                envelope.msgId(),
                executionRecordId
        );
    }

    /**
     * 终止任务（外层 500205）。
     */
    public SyncDownlinkResult taskStop(TaskStopEnvelope envelope) {
        requireDeviceId(envelope.deviceId());
        return sendAndAwait(
                envelope.deviceId(),
                envelopeJsonCodec.encodeTaskStop(envelope),
                TransportOpcode.TASK_STOP.code(),
                envelope.msgId(),
                null
        );
    }

    /**
     * 同步下发：先登记等待，再写出，再等同号答卷。写出失败立刻撤销等待。
     */
    SyncDownlinkResult sendAndAwait(
            String deviceId, String wireJson, int opcode, String msgId, Long executionRecordId) {
        if (!InstructionExchangeCatalog.isSyncWaitResult(opcode)) {
            throw new IllegalArgumentException("非同步要结果的指令不得走等待：" + opcode);
        }
        if (msgId == null || msgId.isBlank()) {
            return SyncDownlinkResult.writeFailed("同步下发缺少消息 id");
        }
        runningTaskExchangeRecorder.remember(deviceId, msgId, executionRecordId);
        CompletableFuture<String> pendingReply = syncReplyWaiter.register(deviceId, msgId, opcode);
        boolean written = deviceTransport.sendText(deviceId, wireJson);
        if (!written) {
            runningTaskExchangeRecorder.forget(deviceId, msgId);
            syncReplyWaiter.cancel(deviceId, msgId, opcode);
            return SyncDownlinkResult.writeFailed("设备当前未连接或写出失败：" + deviceId);
        }
        String transcriptReason = runningTaskExchangeRecorder.recordNow(
                deviceId, TaskExchangeTranscriptReqDTO.OUTBOUND, wireJson, executionRecordId);
        Duration timeout = Duration.ofMillis(Math.max(1L, properties.getSyncReplyTimeoutMs()));
        return syncReplyWaiter.await(pendingReply, timeout)
                .map(reply -> toBusinessResult(reply, opcode, msgId).withTranscript(transcriptReason))
                .orElseGet(() -> SyncDownlinkResult.timeout(
                        "等待同号业务答卷超时 opcode=" + opcode + " msgId=" + msgId).withTranscript(transcriptReason));
    }

    private SyncDownlinkResult toBusinessResult(String replyJson, int opcode, String msgId) {
        Integer code = envelopeJsonCodec.readBusinessCode(replyJson);
        if (code != null && code == 200) {
            return SyncDownlinkResult.accepted(replyJson);
        }
        String reason = "同号答卷业务失败 opcode=" + opcode + " msgId=" + msgId
                + " code=" + code;
        return SyncDownlinkResult.rejected(replyJson, reason);
    }

    private static void requireDeviceId(String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            throw new IllegalArgumentException("必须提供 deviceId");
        }
    }
}
