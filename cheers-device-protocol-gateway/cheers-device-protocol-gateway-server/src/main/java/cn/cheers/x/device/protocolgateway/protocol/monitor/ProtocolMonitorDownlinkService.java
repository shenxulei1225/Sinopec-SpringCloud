package cn.cheers.x.device.protocolgateway.protocol.monitor;

import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorDownlinkResultDTO;
import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorInstructionListDTO;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.api.mission.DispatchAction;
import cn.cheers.x.device.protocolgateway.api.opcode.TransportOpcode;
import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolCodes;
import cn.cheers.x.device.protocolgateway.datacollection.DeviceRegistrationLookup;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import cn.cheers.x.device.protocolgateway.protocol.exchange.RunningTaskExchangeRecorder;
import cn.cheers.x.device.protocolgateway.service.MissionDownlink;
import cn.cheers.x.device.protocolgateway.service.MissionTranslationService;
import cn.cheers.x.device.protocolgateway.transport.DeviceTransport;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.api.InspectionDeviceUplinkApi;
import cn.cheers.x.inspection.task.api.dto.PatrolGeneratedTaskActionDTO;
import cn.cheers.x.inspection.task.api.dto.PatrolGeneratedTaskDownlinkDTO;
import cn.cheers.x.inspection.task.api.dto.PatrolGeneratedTaskOptionDTO;
import cn.cheers.x.inspection.task.api.dto.TaskExchangeTranscriptReqDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 监控页模拟下发：按这台设备已登记的默认协议取出下发空包，经现有连接写出。
 * <p>负责：列出可下发指令。巡检指令包写出生成时记下的那一份。启动、终止、取包、删包改用这台设备已生成执行记录的编号。其余指令只补空包里已有且还空着的设备编号、消息编号。定时上报再按页面选定的开关、频率和重复次数填写。启动推流再按页面选定的推流地址填写；页面没填时用这台已生成任务上分配的收流地址。写出后立刻返回，不等同号答卷。答卷随后出现在这台的报文窗口。
 * <p>不负责：另开一条设备连接；在这里自己编包内序号。序号由指令翻译按动作清单从 0 递增，单独发出的指令也占一个号。字段说明不要求必填、这次没有值的项，写进下发说明。
 * <p>禁止：设备没登记默认协议时挑一份说明书；任务指令再填协议样例编号；把大疆指令改从地面站连接发出；设备离线仍组包或调用巡检。
 */
@Service
@RequiredArgsConstructor
public class ProtocolMonitorDownlinkService {

    /** 页面上设备离线时下发按钮停用，接口再拦一次，两处用同一句。 */
    static final String OFFLINE_NOTE = "这台设备离线，不能下发。";

    private final DeviceRegistrationLookup registrationLookup;
    private final ProtocolMonitorDownlinkCatalog catalog;
    private final EnvelopeJsonCodec envelopeJsonCodec;
    private final DeviceTransport deviceTransport;
    private final RunningTaskExchangeRecorder runningTaskExchangeRecorder;
    private final InspectionDeviceUplinkApi inspectionDeviceUplinkApi;
    private final MissionTranslationService missionTranslationService;

    public ProtocolMonitorInstructionListDTO list(String deviceId) {
        DeviceRegistrationLookup.Registration registration = registrationLookup.registrationOf(deviceId);
        if (!registration.ok()) {
            return new ProtocolMonitorInstructionListDTO("", "", registration.reason(), List.of(), "");
        }
        return new ProtocolMonitorInstructionListDTO(
                registration.protocol(),
                ProtocolCodes.label(registration.protocol()),
                "",
                catalog.list(registration.protocol()),
                streamUrlOf(deviceId, null)
        );
    }

    public ProtocolMonitorDownlinkResultDTO send(String deviceId, int opcode) {
        return send(deviceId, opcode, null, null, null, null);
    }

    public ProtocolMonitorDownlinkResultDTO send(
            String deviceId, int opcode, Boolean enable, Integer frequency, Integer total) {
        return send(deviceId, opcode, enable, frequency, total, null);
    }

    public ProtocolMonitorDownlinkResultDTO send(
            String deviceId, int opcode, Boolean enable, Integer frequency, Integer total, String streamUrl) {
        return send(deviceId, opcode, enable, frequency, total, streamUrl, null);
    }

    /** 这台设备待执行和正在执行的已生成任务。巡检暂时读不到时返回空名单。 */
    public List<PatrolGeneratedTaskOptionDTO> tasks(String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            return List.of();
        }
        try {
            CommonResult<List<PatrolGeneratedTaskOptionDTO>> loaded =
                    inspectionDeviceUplinkApi.generatedTasks(deviceId.trim());
            if (loaded == null || !loaded.isSuccess() || loaded.getData() == null) {
                return List.of();
            }
            return loaded.getData();
        } catch (RuntimeException ex) {
            return List.of();
        }
    }

    public ProtocolMonitorDownlinkResultDTO send(
            String deviceId,
            int opcode,
            Boolean enable,
            Integer frequency,
            Integer total,
            String streamUrl,
            Long executionRecordId) {
        DeviceRegistrationLookup.Registration registration = registrationLookup.registrationOf(deviceId);
        if (!registration.ok()) {
            throw new IllegalArgumentException(registration.reason());
        }
        if (ProtocolCodes.isDjiCloud(registration.protocol())) {
            throw new IllegalArgumentException("这台走 MQTT 或航线文件，不能从地面站连接下发");
        }
        // 地面站设备离线就停在这里。后面的组包和巡检读取都不再做，避免失败被收成系统异常。
        String id = deviceId == null ? "" : deviceId.trim();
        if (!deviceTransport.isOnline(id)) {
            return new ProtocolMonitorDownlinkResultDTO(false, false, OFFLINE_NOTE, "");
        }
        String msgId = newMsgId();
        if (ProtocolMonitorGeneratedEnvelope.usesGeneratedTask(opcode)) {
            return sendGenerated(deviceId.trim(), registration.protocol(), opcode, msgId, executionRecordId);
        }
        ProtocolMonitorDownlinkCatalog.Prepared instruction = catalog.require(registration.protocol(), opcode);
        Map<String, Object> packet = ProtocolMonitorDownlinkFiller.fillIdentity(
                instruction.outbound(),
                deviceId == null ? "" : deviceId.trim(),
                msgId
        );
        if (opcode == TransportOpcode.SWITCH_TIMED_REPORT.code()) {
            if (enable == null) {
                throw new IllegalArgumentException("启动或关闭定时上报要指定开关");
            }
            ProtocolMonitorDownlinkFiller.applyReportSwitch(packet, enable, frequency, total);
        }
        if (opcode == TransportOpcode.STREAM_START.code()) {
            String address = streamUrl == null || streamUrl.isBlank()
                    ? streamUrlOf(deviceId, executionRecordId)
                    : streamUrl;
            ProtocolMonitorDownlinkFiller.applyStreamAddress(packet, address);
        }
        Object writtenMsgId = packet.get("msgId");
        if (!(writtenMsgId instanceof String text) || text.isBlank()) {
            throw new IllegalArgumentException("这条指令空包没有消息编号，无法下发");
        }
        String payloadJson = envelopeJsonCodec.encodeObject(packet);
        Long recordForTranscript = opcode == TransportOpcode.STREAM_START.code() ? executionRecordId : null;
        ProtocolMonitorDownlinkResultDTO sent = writeWithoutWaiting(deviceId.trim(), payloadJson, recordForTranscript);
        if (!sent.written()) {
            return sent;
        }
        if (opcode == TransportOpcode.STREAM_START.code()) {
            return new ProtocolMonitorDownlinkResultDTO(
                    sent.written(),
                    sent.businessSuccess(),
                    sent.note() + "推流地址是「" + packet.get("rtmp") + "」。",
                    sent.payloadJson());
        }
        if (opcode != TransportOpcode.SWITCH_TIMED_REPORT.code()) {
            return sent;
        }
        String times = countText(packet.get("total"));
        String detail = Boolean.TRUE.equals(enable)
                ? "这次是启动定时上报，每 " + packet.get("frequency") + " 秒一次，" + times + "。"
                : "这次是关闭定时上报。";
        return new ProtocolMonitorDownlinkResultDTO(
                sent.written(), sent.businessSuccess(), sent.note() + detail, sent.payloadJson());
    }

    /**
     * 巡检指令包用生成时记下的正文。没有记下时才按动作清单重译。
     * 启动、终止、取包、删包的编号来自这台设备已生成的执行记录。
     */
    private ProtocolMonitorDownlinkResultDTO sendGenerated(
            String deviceId, String protocol, int opcode, String msgId, Long executionRecordId) {
        PatrolGeneratedTaskDownlinkDTO task = requireGeneratedTask(deviceId, executionRecordId);
        if (task.getProtocolVersion() != null && !task.getProtocolVersion().equals(protocol)) {
            throw new IllegalArgumentException("这条执行记录的对接协议和这台设备登记的协议不一致");
        }
        String recordId = String.valueOf(task.getExecutionRecordId());
        List<String> notices = List.of();
        String payloadJson;
        boolean storedPackage = false;
        if (opcode == TransportOpcode.COMMAND_SEND.code()) {
            String packages = task.getCommandPackagesJson();
            if (packages != null && !packages.isBlank()) {
                storedPackage = true;
                String templateId = task.getCommandTemplateId() == null || task.getCommandTemplateId().isBlank()
                        ? recordId
                        : task.getCommandTemplateId().trim();
                payloadJson = ProtocolMonitorGeneratedEnvelope.commandSend(
                        envelopeJsonCodec, msgId, deviceId, templateId, packages);
            } else {
                MissionDownlink downlink = missionTranslationService.translate(
                        planOf(deviceId, protocol, recordId, task), false);
                notices = downlink.notices();
                payloadJson = ProtocolMonitorGeneratedEnvelope.commandSend(
                        envelopeJsonCodec,
                        msgId,
                        deviceId,
                        recordId,
                        ProtocolMonitorGeneratedEnvelope.packagesJson(envelopeJsonCodec, downlink));
            }
        } else {
            payloadJson = payloadForGenerated(deviceId, protocol, opcode, msgId, recordId, task);
        }
        runningTaskExchangeRecorder.remember(deviceId, msgId, task.getExecutionRecordId());
        ProtocolMonitorDownlinkResultDTO sent = writeWithoutWaiting(
                deviceId, payloadJson, task.getExecutionRecordId());
        if (!sent.written()) {
            runningTaskExchangeRecorder.forget(deviceId, msgId);
        }
        String name = task.getRecordName() == null ? recordId : task.getRecordName();
        String note = sent.note() + "。任务记录是「" + name + "」";
        if (storedPackage) {
            note = note + "。写出的是生成时记下的指令包";
        }
        if (!notices.isEmpty()) {
            note = note + "。" + String.join("。", notices);
        }
        return new ProtocolMonitorDownlinkResultDTO(
                sent.written(),
                sent.businessSuccess(),
                note,
                sent.payloadJson());
    }

    private PatrolGeneratedTaskDownlinkDTO requireGeneratedTask(String deviceId, Long executionRecordId) {
        CommonResult<PatrolGeneratedTaskDownlinkDTO> loaded = executionRecordId == null
                ? inspectionDeviceUplinkApi.generatedTask(deviceId)
                : inspectionDeviceUplinkApi.generatedTaskRecord(deviceId, executionRecordId);
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

    /**
     * 写到设备连接上就返回。不等同号答卷，也不在这条请求里等巡检把往来记完。
     * 答卷由收包路径抄进报文窗口。
     */
    private ProtocolMonitorDownlinkResultDTO writeWithoutWaiting(
            String deviceId, String payloadJson, Long executionRecordId) {
        boolean written = deviceTransport.sendText(deviceId, payloadJson);
        if (!written) {
            return new ProtocolMonitorDownlinkResultDTO(false, false, "设备当前未连接", payloadJson);
        }
        runningTaskExchangeRecorder.enqueue(
                deviceId, TaskExchangeTranscriptReqDTO.OUTBOUND, payloadJson, executionRecordId);
        return new ProtocolMonitorDownlinkResultDTO(true, false, "已提交下发，可以继续发下一条。设备答卷在左侧报文里看。", payloadJson);
    }

    /** 点中的任务上的收流地址。没点时用已经到点的那一条。没有任务或还没分配时返回空。 */
    private String streamUrlOf(String deviceId, Long executionRecordId) {
        if (deviceId == null || deviceId.isBlank()) {
            return "";
        }
        try {
            CommonResult<PatrolGeneratedTaskDownlinkDTO> loaded = executionRecordId == null
                    ? inspectionDeviceUplinkApi.generatedTask(deviceId.trim())
                    : inspectionDeviceUplinkApi.generatedTaskRecord(deviceId.trim(), executionRecordId);
            if (loaded == null || !loaded.isSuccess() || loaded.getData() == null) {
                return "";
            }
            String url = loaded.getData().getMediaStreamUrl();
            return url == null ? "" : url.trim();
        } catch (RuntimeException ex) {
            return "";
        }
    }

    private static String countText(Object total) {
        if (total instanceof Number number && number.intValue() == -1) {
            return "不限次数";
        }
        return "共 " + total + " 次";
    }

    private static String newMsgId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
