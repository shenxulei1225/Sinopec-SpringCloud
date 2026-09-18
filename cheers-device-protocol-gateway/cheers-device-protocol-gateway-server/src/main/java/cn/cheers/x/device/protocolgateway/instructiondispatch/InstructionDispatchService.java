package cn.cheers.x.device.protocolgateway.instructiondispatch;

import cn.cheers.x.device.protocolgateway.api.message.CommandSendEnvelope;
import cn.cheers.x.device.protocolgateway.api.message.InstructionPackageItem;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.api.mission.DispatchAction;
import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolCodes;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 指令下发：按对照表把动作填进空包，再打成一次指令包。
 * <p>负责：对照查找、填包、组 500104 信封。
 * <p>不负责：发明头尾动作、等答卷、读巡检库。
 * <p>禁止：按厂商名写死填包；对照缺失还组包。
 */
@Service
@RequiredArgsConstructor
public class InstructionDispatchService {

    private final ProtocolMappingCatalog catalog;
    private final EnvelopeJsonCodec envelopeJsonCodec;

    public CommandSendEnvelope translate(DeviceMissionPlan plan) {
        if (plan == null || plan.deviceId() == null || plan.deviceId().isBlank()) {
            throw new IllegalArgumentException("必须提供逻辑设备标识");
        }
        if (!ProtocolCodes.isKnown(plan.protocolVersion())) {
            throw new IllegalArgumentException("协议版本须是 robot-ws 或 uav-ws，不能用厂商名");
        }
        if (plan.actions() == null || plan.actions().isEmpty()) {
            throw new IllegalArgumentException("下发动作列表不能为空；网关不发明起飞/移动顺序");
        }
        List<InstructionPackageItem> items = new ArrayList<>();
        int sequence = 0;
        for (DispatchAction action : plan.actions()) {
            items.add(fillOne(plan.protocolVersion(), action, sequence++));
        }
        return new CommandSendEnvelope(
                newMsgId(),
                plan.deviceId(),
                plan.templateId(),
                envelopeJsonCodec.encodePackagesJson(items)
        );
    }

    private InstructionPackageItem fillOne(String protocolVersion, DispatchAction action, int sequence) {
        MappingRow mapping = catalog.find(action.actionId(), protocolVersion)
                .orElseThrow(() -> new IllegalArgumentException(
                        "找不到对照：动作 " + action.actionId() + " / 版本 " + protocolVersion));
        InstructionTemplate template = catalog.loadInstruction(mapping.instructionId());
        Map<String, Object> filled = InstructionPacketFiller.fill(
                template.outbound(), mapping.slots(), action.params());
        return new InstructionPackageItem(template.opcode(), sequence, requestOf(filled));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> requestOf(Map<String, Object> packet) {
        Object request = packet.get("request");
        if (request instanceof Map<?, ?> map) {
            return new LinkedHashMap<>((Map<String, Object>) map);
        }
        return new LinkedHashMap<>();
    }

    private static String newMsgId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
