package cn.cheers.x.device.protocolgateway.protocol.monitor;

import cn.cheers.x.device.protocolgateway.api.message.CommandSendEnvelope;
import cn.cheers.x.device.protocolgateway.api.message.InstructionPackageItem;
import cn.cheers.x.device.protocolgateway.api.message.StartupExecuteEnvelope;
import cn.cheers.x.device.protocolgateway.api.message.TaskStopEnvelope;
import cn.cheers.x.device.protocolgateway.api.opcode.TransportOpcode;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import cn.cheers.x.device.protocolgateway.service.MissionDownlink;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 协议监控里，任务指令改用已生成执行记录的编号。
 * <p>负责：指令包、启动、终止、取包、删包带上这次执行记录编号。指令包里的序号沿用翻译结果。
 * <p>不负责：翻译动作、把上报写成完成。
 * <p>禁止：再填协议样例里的任务编号。
 */
public final class ProtocolMonitorGeneratedEnvelope {

    private ProtocolMonitorGeneratedEnvelope() {
    }

    public static boolean usesGeneratedTask(int opcode) {
        return opcode == TransportOpcode.COMMAND_SEND.code()
                || opcode == TransportOpcode.STARTUP_EXECUTE.code()
                || opcode == TransportOpcode.TASK_STOP.code()
                || opcode == TransportOpcode.GET_COMMAND_PACKAGE.code()
                || opcode == TransportOpcode.DELETE_COMMAND_PACKAGE.code();
    }

    public static String commandSend(
            EnvelopeJsonCodec codec,
            String msgId,
            String deviceId,
            String recordId,
            String packagesJson
    ) {
        return codec.encodeCommandSend(new CommandSendEnvelope(msgId, deviceId, recordId, packagesJson));
    }

    public static String startup(
            EnvelopeJsonCodec codec,
            String msgId,
            String deviceId,
            String recordId,
            long executionTimeEpochSeconds
    ) {
        return codec.encodeStartupExecute(new StartupExecuteEnvelope(
                msgId, recordId, deviceId, recordId, executionTimeEpochSeconds));
    }

    public static String stop(EnvelopeJsonCodec codec, String msgId, String deviceId, String recordId) {
        return codec.encodeTaskStop(new TaskStopEnvelope(msgId, recordId, deviceId));
    }

    /**
     * 取包、删包仍用说明书上的空包，只把已有的模板编号、任务编号换成这次执行记录编号。
     */
    public static Map<String, Object> withRecordId(Map<String, Object> packet, String recordId) {
        if (packet.containsKey("templateId")) {
            packet.put("templateId", recordId);
        }
        if (packet.containsKey("taskId")) {
            packet.put("taskId", recordId);
        }
        return packet;
    }

    /**
     * 把翻译结果里的巡检指令包收成一份。单独发出的指令不进这份包，但它占用的序号保留。
     */
    public static String packagesJson(EnvelopeJsonCodec codec, MissionDownlink downlink) {
        if (downlink == null || downlink.pieces() == null) {
            throw new IllegalArgumentException("这条待执行没有巡检指令包");
        }
        List<InstructionPackageItem> items = new ArrayList<>();
        for (MissionDownlink.DownlinkPiece piece : downlink.pieces()) {
            if (piece.commandPackage() == null || piece.commandPackage().packagesJson() == null
                    || piece.commandPackage().packagesJson().isBlank()) {
                continue;
            }
            for (Map<String, Object> item : codec.decodePackagesJson(piece.commandPackage().packagesJson())) {
                if (!(item.get("opcode") instanceof Number opcode) || !(item.get("sequence") instanceof Number sequence)) {
                    throw new IllegalArgumentException("指令包里的步骤缺少操作码或序号");
                }
                Object request = item.get("request");
                items.add(new InstructionPackageItem(
                        opcode.intValue(),
                        sequence.intValue(),
                        request instanceof Map<?, ?> map ? castRequest(map) : Map.of()));
            }
        }
        if (items.isEmpty()) {
            throw new IllegalArgumentException("这条待执行没有巡检指令包");
        }
        return codec.encodePackagesJson(items);
    }

    private static Map<String, Object> castRequest(Map<?, ?> map) {
        java.util.LinkedHashMap<String, Object> copy = new java.util.LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getKey() != null) {
                copy.put(String.valueOf(entry.getKey()), entry.getValue());
            }
        }
        return copy;
    }
}
