package cn.cheers.x.device.protocolgateway.service;

import cn.cheers.x.device.protocolgateway.api.dto.MissionStartRespDTO;
import cn.cheers.x.device.protocolgateway.api.dto.MissionTranslateCommitRespDTO;
import cn.cheers.x.device.protocolgateway.api.dto.MissionTranslatePreviewRespDTO;
import cn.cheers.x.device.protocolgateway.api.dto.MissionTranslatedDispatchReqDTO;
import cn.cheers.x.device.protocolgateway.api.message.StartupExecuteEnvelope;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolCodes;
import cn.cheers.x.device.protocolgateway.djicloud.DjiCloudCommandSender;
import cn.cheers.x.device.protocolgateway.djicloud.DjiCloudOnlineRegistry;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import cn.cheers.x.device.protocolgateway.service.MissionDownlink.DownlinkPiece;
import cn.cheers.x.device.protocolgateway.transport.DeviceTransport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 正式下行编排：按对照翻译后按顺序写出，有巡检指令包时再发启动执行。
 * <p>不读巡检库表；不查设备台账。逻辑设备当前无连接时返回失败，不假装成功。
 * <p>success 仅当每一条都等到同号业务答卷且 code=200。写出成功还不算成功。
 * <p>禁止：按动作名称决定某一步走单独指令还是进指令包。
 */
@Service
@RequiredArgsConstructor
public class DeviceProtocolMissionService {

    private final MissionTranslationService missionTranslationService;
    private final DeviceProtocolDownlinkService downlinkService;
    private final EnvelopeJsonCodec envelopeJsonCodec;
    private final DeviceTransport deviceTransport;
    private final DjiCloudOnlineRegistry cloudOnline;
    private final DjiCloudCommandSender cloudSender;

    /**
     * 翻译执行意图并按顺序下发。有巡检指令包时，包写成功后再发启动执行。
     *
     * @param plan 已含协议版本、动作列表、逻辑设备标识
     * @return success 仅当每条报文都收到同号成功答卷；有指令包时启动执行也须成功
     */
    public MissionTranslatePreviewRespDTO previewTranslate(DeviceMissionPlan plan) {
        try {
            missionTranslationService.translate(plan, false);
            return new MissionTranslatePreviewRespDTO(true, null);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return new MissionTranslatePreviewRespDTO(false, translateFailureReason(ex));
        }
    }

    public MissionTranslateCommitRespDTO commitTranslate(DeviceMissionPlan plan) {
        MissionDownlink downlink = requireTranslated(plan, true);
        return MissionDownlinkSnapshotCodec.toCommitResp(downlink);
    }

    public MissionStartRespDTO dispatchTranslated(MissionTranslatedDispatchReqDTO req) {
        if (req == null || req.deviceId() == null || req.deviceId().isBlank()) {
            return fail(false, false, false, "必须提供逻辑设备标识 deviceId", null);
        }
        if (req.pieces() == null || req.pieces().isEmpty()) {
            return fail(false, false, false, "缺少已翻译的下行报文", null);
        }
        DeviceMissionPlan plan = new DeviceMissionPlan(
                req.protocolVersion(),
                req.deviceId(),
                req.taskId(),
                req.templateId(),
                List.of());
        MissionDownlink downlink = MissionDownlinkSnapshotCodec.fromDispatchReq(req);
        return dispatchDownlink(plan, downlink);
    }

    public MissionStartRespDTO dispatchAndStartup(DeviceMissionPlan plan) {
        if (plan == null || plan.deviceId() == null || plan.deviceId().isBlank()) {
            return fail(false, false, false, "必须提供逻辑设备标识 deviceId", null);
        }
        MissionDownlink downlink;
        try {
            downlink = requireTranslated(plan, true);
        } catch (IllegalArgumentException ex) {
            return fail(false, false, false, ex.getMessage(), null);
        }
        return dispatchDownlink(plan, downlink);
    }

    private MissionDownlink requireTranslated(DeviceMissionPlan plan, boolean persistWaylineFile) {
        try {
            return missionTranslationService.translate(plan, persistWaylineFile);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            throw new IllegalArgumentException(translateFailureReason(ex), ex);
        }
    }

    private MissionStartRespDTO dispatchDownlink(DeviceMissionPlan plan, MissionDownlink downlink) {
        if (ProtocolCodes.isDjiCloud(plan.protocolVersion())) {
            return dispatchCloud(plan, downlink);
        }
        boolean online = deviceTransport.isOnline(plan.deviceId());
        if (!online) {
            return fail(false, false, false, "设备当前未连接：" + plan.deviceId(), null);
        }
        String shownWire = null;
        boolean wroteCommand = false;
        boolean sentPackage = false;
        Long recordId = executionRecordId(plan.taskId());
        for (DownlinkPiece piece : downlink.pieces()) {
            SyncDownlinkResult result;
            if (piece.direct() != null) {
                shownWire = piece.direct().wireJson();
                result = downlinkService.sendSyncCommand(
                        plan.deviceId(),
                        piece.direct().wireJson(),
                        piece.direct().opcode(),
                        piece.direct().msgId(),
                        recordId);
            } else {
                shownWire = envelopeJsonCodec.encodeCommandSend(piece.commandPackage());
                result = downlinkService.sendCommandPackage(piece.commandPackage(), recordId);
                sentPackage = true;
            }
            if (!result.accepted()) {
                return fail(true, wroteCommand || result.written(), false,
                        result.failureReason(), shownWire);
            }
            wroteCommand = true;
        }
        if (!sentPackage) {
            return new MissionStartRespDTO(
                    true, true, true, false, null, shownWire,
                    downlink.waylineFileId(), downlink.waylineFingerprint());
        }
        String wireTaskId = (plan.taskId() != null && !plan.taskId().isBlank())
                ? plan.taskId().trim()
                : plan.templateId();
        StartupExecuteEnvelope startup = new StartupExecuteEnvelope(
                newMsgId(),
                wireTaskId,
                plan.deviceId(),
                plan.templateId(),
                System.currentTimeMillis() / 1000L
        );
        SyncDownlinkResult startupResult = downlinkService.startupExecute(startup, recordId);
        if (!startupResult.accepted()) {
            return fail(true, true, startupResult.written(),
                    startupResult.failureReason(), shownWire);
        }
        return new MissionStartRespDTO(
                true, true, true, true, null, shownWire,
                downlink.waylineFileId(), downlink.waylineFingerprint());
    }

    /**
     * 大疆上云：按对照发出的官方服务报文逐条等 services_reply。
     * <p>不发地面站启动执行。result=0 只表示设备收下，任务是否做完仍由执行记录判断。
     * <p>deviceId 必须是已经报过拓扑的网关序列号。
     */
    private MissionStartRespDTO dispatchCloud(DeviceMissionPlan plan, MissionDownlink downlink) {
        if (!cloudOnline.isOnline(plan.deviceId())) {
            return fail(false, false, false, "设备当前未连接：" + plan.deviceId(), null);
        }
        String shownWire = null;
        boolean wroteCommand = false;
        for (DownlinkPiece piece : downlink.pieces()) {
            if (piece.direct() == null || !piece.direct().cloudService()) {
                return fail(true, wroteCommand, false, "上云协议不使用地面站指令包", shownWire);
            }
            shownWire = piece.direct().wireJson();
            SyncDownlinkResult result = cloudSender.send(piece.direct());
            if (!result.accepted()) {
                return fail(true, wroteCommand || result.written(), false, result.failureReason(), shownWire);
            }
            wroteCommand = true;
        }
        return new MissionStartRespDTO(
                true, true, true, false, null, shownWire,
                downlink.waylineFileId(), downlink.waylineFingerprint());
    }

    /**
     * 计划上的任务 id 是这次执行记录编号。不是数字时不拿任务模板 id 来顶。
     */
    static Long executionRecordId(String taskId) {
        if (taskId == null) {
            return null;
        }
        String text = taskId.trim();
        if (text.isEmpty() || !text.chars().allMatch(Character::isDigit)) {
            return null;
        }
        try {
            long value = Long.parseLong(text);
            return value > 0 ? value : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static MissionStartRespDTO fail(
            boolean online,
            boolean commandSent,
            boolean startupSent,
            String reason,
            String wireJson) {
        return new MissionStartRespDTO(false, online, commandSent, startupSent, reason, wireJson);
    }

    private static String newMsgId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private static String translateFailureReason(RuntimeException ex) {
        return ex.getMessage() == null || ex.getMessage().isBlank()
                ? "指令对照不完整，请到指令协议对接配置"
                : ex.getMessage();
    }
}
