package cn.cheers.x.device.protocolgateway.protocol.monitor;

import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorDownlinkResultDTO;
import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorInstructionListDTO;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.api.mission.DispatchAction;
import cn.cheers.x.device.protocolgateway.api.opcode.InstructionExchangeCatalog;
import cn.cheers.x.device.protocolgateway.api.opcode.TransportOpcode;
import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolCodes;
import cn.cheers.x.device.protocolgateway.datacollection.DeviceRegistrationLookup;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import cn.cheers.x.device.protocolgateway.service.DeviceProtocolDownlinkService;
import cn.cheers.x.device.protocolgateway.service.MissionDownlink;
import cn.cheers.x.device.protocolgateway.service.MissionTranslationService;
import cn.cheers.x.device.protocolgateway.service.SyncDownlinkResult;
import cn.cheers.x.device.protocolgateway.transport.DeviceTransport;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.api.InspectionDeviceUplinkApi;
import cn.cheers.x.inspection.task.api.dto.PatrolGeneratedTaskActionDTO;
import cn.cheers.x.inspection.task.api.dto.PatrolGeneratedTaskDownlinkDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 监控页模拟下发：按这台设备已登记的默认协议取出下发空包，经现有连接写出。
 * <p>负责：列出可下发指令。任务指令改用这台设备已生成执行记录的编号和翻译出的指令包。其余指令只补空包里已有且还空着的设备编号、消息编号。同步指令等待同号答卷。
 * <p>不负责：另开一条设备连接；在这里自己编包内序号。序号由指令翻译按动作清单从 0 递增，单独发出的指令也占一个号。字段说明不要求必填、这次没有值的项，写进下发说明。
 * <p>禁止：设备没登记默认协议时挑一份说明书；任务指令再填协议样例编号；把大疆指令改从地面站连接发出。
 */
@Service
@RequiredArgsConstructor
public class ProtocolMonitorDownlinkService {

    private final DeviceRegistrationLookup registrationLookup;
    private final ProtocolMonitorDownlinkCatalog catalog;
    private final EnvelopeJsonCodec envelopeJsonCodec;
    private final DeviceProtocolDownlinkService downlinkService;
    private final DeviceTransport deviceTransport;
    private final InspectionDeviceUplinkApi inspectionDeviceUplinkApi;
    private final MissionTranslationService missionTranslationService;

    public ProtocolMonitorInstructionListDTO list(String deviceId) {
        DeviceRegistrationLookup.Registration registration = registrationLookup.registrationOf(deviceId);
        if (!registration.ok()) {
            return new ProtocolMonitorInstructionListDTO("", "", registration.reason(), List.of());
        }
        return new ProtocolMonitorInstructionListDTO(
                registration.protocol(),
                ProtocolCodes.label(registration.protocol()),
                "",
                catalog.list(registration.protocol())
        );
    }

    public ProtocolMonitorDownlinkResultDTO send(String deviceId, int opcode) {
        DeviceRegistrationLookup.Registration registration = registrationLookup.registrationOf(deviceId);
        if (!registration.ok()) {
            throw new IllegalArgumentException(registration.reason());
        }
        if (ProtocolCodes.isDjiCloud(registration.protocol())) {
            throw new IllegalArgumentException("这台走 MQTT 或航线文件，不能从地面站连接下发");
        }
        String msgId = newMsgId();
        if (ProtocolMonitorGeneratedEnvelope.usesGeneratedTask(opcode)) {
            return sendGenerated(deviceId.trim(), registration.protocol(), opcode, msgId);
        }
        ProtocolMonitorDownlinkCatalog.Prepared instruction = catalog.require(registration.protocol(), opcode);
        Map<String, Object> packet = ProtocolMonitorDownlinkFiller.fillIdentity(
                instruction.outbound(),
                deviceId == null ? "" : deviceId.trim(),
                msgId
        );
        Object writtenMsgId = packet.get("msgId");
        if (!(writtenMsgId instanceof String text) || text.isBlank()) {
            throw new IllegalArgumentException("这条指令空包没有消息编号，无法下发");
        }
        String payloadJson = envelopeJsonCodec.encodeObject(packet);
        if (InstructionExchangeCatalog.isSyncWaitResult(opcode)) {
            SyncDownlinkResult result = downlinkService.sendSyncCommand(
                    deviceId.trim(),
                    payloadJson,
                    opcode,
                    text
            );
            return toResult(result, payloadJson);
        }
        boolean written = deviceTransport.sendText(deviceId.trim(), payloadJson);
        return new ProtocolMonitorDownlinkResultDTO(
                written,
                false,
                written ? "已写出" : "设备当前未连接",
                payloadJson
        );
    }

    /**
     * 任务指令的编号来自这台设备已生成的执行记录。指令包按那份动作清单翻译，序号从 0 递增。
     */
    private ProtocolMonitorDownlinkResultDTO sendGenerated(
            String deviceId, String protocol, int opcode, String msgId) {
        PatrolGeneratedTaskDownlinkDTO task = requireGeneratedTask(deviceId);
        if (task.getProtocolVersion() != null && !task.getProtocolVersion().equals(protocol)) {
            throw new IllegalArgumentException("这条执行记录的对接协议和这台设备登记的协议不一致");
        }
        String recordId = String.valueOf(task.getExecutionRecordId());
        List<String> notices = List.of();
        String payloadJson;
        if (opcode == TransportOpcode.COMMAND_SEND.code()) {
            MissionDownlink downlink = missionTranslationService.translate(
                    planOf(deviceId, protocol, recordId, task), false);
            notices = downlink.notices();
            payloadJson = ProtocolMonitorGeneratedEnvelope.commandSend(
                    envelopeJsonCodec,
                    msgId,
                    deviceId,
                    recordId,
                    ProtocolMonitorGeneratedEnvelope.packagesJson(envelopeJsonCodec, downlink));
        } else {
            payloadJson = payloadForGenerated(deviceId, protocol, opcode, msgId, recordId, task);
        }
        SyncDownlinkResult result = downlinkService.sendSyncCommand(deviceId, payloadJson, opcode, msgId);
        ProtocolMonitorDownlinkResultDTO sent = toResult(result, payloadJson);
        String name = task.getRecordName() == null ? recordId : task.getRecordName();
        String note = sent.note() + "。任务记录是「" + name + "」";
        if (!notices.isEmpty()) {
            note = note + "。" + String.join("。", notices);
        }
        return new ProtocolMonitorDownlinkResultDTO(
                sent.written(),
                sent.businessSuccess(),
                note,
                sent.payloadJson());
    }

    private PatrolGeneratedTaskDownlinkDTO requireGeneratedTask(String deviceId) {
        CommonResult<PatrolGeneratedTaskDownlinkDTO> loaded = inspectionDeviceUplinkApi.generatedTask(deviceId);
        if (loaded == null || !loaded.isSuccess() || loaded.getData() == null
                || loaded.getData().getExecutionRecordId() == null) {
            throw new IllegalArgumentException(
                    loaded != null && loaded.getMsg() != null && !loaded.getMsg().isBlank()
                            ? loaded.getMsg()
                            : "读不到这台设备已生成的任务");
        }
        return loaded.getData();
    }

    private String payloadForGenerated(
            String deviceId,
            String protocol,
            int opcode,
            String msgId,
            String recordId,
            PatrolGeneratedTaskDownlinkDTO task
    ) {
        if (opcode == TransportOpcode.STARTUP_EXECUTE.code()) {
            if (task.getExecutionTimeEpochSeconds() == null) {
                throw new IllegalArgumentException("这条执行记录没有计划时刻");
            }
            return ProtocolMonitorGeneratedEnvelope.startup(
                    envelopeJsonCodec, msgId, deviceId, recordId, task.getExecutionTimeEpochSeconds());
        }
        if (opcode == TransportOpcode.TASK_STOP.code()) {
            return ProtocolMonitorGeneratedEnvelope.stop(envelopeJsonCodec, msgId, deviceId, recordId);
        }
        ProtocolMonitorDownlinkCatalog.Prepared instruction = catalog.require(protocol, opcode);
        Map<String, Object> packet = ProtocolMonitorGeneratedEnvelope.withRecordId(
                ProtocolMonitorDownlinkFiller.fillIdentity(instruction.outbound(), deviceId, msgId),
                recordId);
        return envelopeJsonCodec.encodeObject(packet);
    }

    private static DeviceMissionPlan planOf(
            String deviceId, String protocol, String recordId, PatrolGeneratedTaskDownlinkDTO task) {
        List<DispatchAction> actions = new ArrayList<>();
        if (task.getActions() != null) {
            for (PatrolGeneratedTaskActionDTO action : task.getActions()) {
                actions.add(new DispatchAction(action.getActionId(), action.getParams(), action.getActionCode()));
            }
        }
        return new DeviceMissionPlan(
                protocol,
                deviceId,
                recordId,
                recordId,
                actions,
                task.getExecutorEquipmentId(),
                null,
                task.getMediaStreamUrl());
    }

    private static ProtocolMonitorDownlinkResultDTO toResult(SyncDownlinkResult result, String payloadJson) {
        if (!result.written()) {
            return new ProtocolMonitorDownlinkResultDTO(false, false, result.failureReason(), payloadJson);
        }
        if (result.accepted()) {
            return new ProtocolMonitorDownlinkResultDTO(true, true, "同号答卷已收下", payloadJson);
        }
        return new ProtocolMonitorDownlinkResultDTO(true, false, result.failureReason(), payloadJson);
    }

    private static String newMsgId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
