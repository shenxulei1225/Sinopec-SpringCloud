package cn.cheers.x.device.protocolgateway.service;

import cn.cheers.x.device.protocolgateway.api.dto.MissionStartRespDTO;
import cn.cheers.x.device.protocolgateway.api.message.CommandSendEnvelope;
import cn.cheers.x.device.protocolgateway.api.message.StartupExecuteEnvelope;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import cn.cheers.x.device.protocolgateway.transport.DeviceTransport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 正式下行编排：翻译 → 500104 → 500201。
 * <p>不读巡检库表；不查设备台账。逻辑设备当前无连接时返回失败，不假装成功。
 */
@Service
@RequiredArgsConstructor
public class DeviceProtocolMissionService {

    private final MissionTranslationService missionTranslationService;
    private final DeviceProtocolDownlinkService downlinkService;
    private final EnvelopeJsonCodec envelopeJsonCodec;
    private final DeviceTransport deviceTransport;

    /**
     * 翻译执行意图并下发指令包与启动执行。
     *
     * @param plan 已含 protocolCode、逻辑设备标识、waypoints
     * @return success 仅当两步均写出成功
     */
    public MissionStartRespDTO dispatchAndStartup(DeviceMissionPlan plan) {
        if (plan == null || plan.deviceId() == null || plan.deviceId().isBlank()) {
            return fail(false, false, false, "必须提供逻辑设备标识 deviceId", null);
        }
        CommandSendEnvelope command = missionTranslationService.translate(plan);
        String wireJson = envelopeJsonCodec.encodeCommandSend(command);
        boolean online = deviceTransport.isOnline(plan.deviceId());
        if (!online) {
            return fail(false, false, false, "设备当前未连接：" + plan.deviceId(), wireJson);
        }
        boolean commandSent = downlinkService.sendCommandPackage(command);
        if (!commandSent) {
            return fail(true, false, false, "指令包写出失败：" + plan.deviceId(), wireJson);
        }
        // taskId / templateId 分开：业务回绑用 taskId，地面站存包键用 templateId
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
        boolean startupSent = downlinkService.startupExecute(startup);
        if (!startupSent) {
            return fail(true, true, false, "启动执行写出失败：" + plan.deviceId(), wireJson);
        }
        return new MissionStartRespDTO(true, true, true, true, null, wireJson);
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
}
