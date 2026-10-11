package cn.cheers.x.device.protocolgateway.protocol.processor;

import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.device.protocolgateway.datacollection.CatalogLookup;
import cn.cheers.x.device.protocolgateway.datacollection.DeviceProtocolVersionCatalog;
import cn.cheers.x.device.protocolgateway.datacollection.ProtocolInstructionCatalog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 分发前按这台设备的协议书过滤。
 * <p>负责：协议书里没有的操作码，不送给该型号的处理模块。
 * <p>不负责：写执行记录、改步骤、猜另一本协议书。
 * <p>禁止：机器人协议没有的起飞、降落、升降、录像，仍送进机器人处理。
 */
@Component
@RequiredArgsConstructor
public class ProtocolBookAdmission {

    private final DeviceProtocolVersionCatalog deviceProtocolVersions;
    private final ProtocolInstructionCatalog instructionCatalog;

    /**
     * 外层操作码和包内操作码都在这台设备的协议书里，才允许分发。
     * 设备没登记协议版本时不允许分发。
     */
    public Decision decide(CollectionSample sample) {
        Optional<String> version = deviceProtocolVersions.firstVersion(sample.deviceId());
        if (version.isEmpty()) {
            return Decision.reject("设备未登记协议版本，不送给处理模块");
        }
        String protocolVersion = version.get();
        Integer outer = parseOpcode(sample.messageKind());
        if (outer == null) {
            return Decision.reject("报文种类不是操作码，不送给处理模块");
        }
        if (!inBook(outer, protocolVersion)) {
            return Decision.reject("协议书没有操作码 " + outer + "，不送给处理模块");
        }
        for (Integer inner : innerOpcodes(sample.fields())) {
            if (!inBook(inner, protocolVersion)) {
                return Decision.reject("协议书没有操作码 " + inner + "，不送给处理模块");
            }
        }
        return Decision.admit(protocolVersion);
    }

    private boolean inBook(int opcode, String protocolVersion) {
        CatalogLookup lookup = instructionCatalog.findMainPacket(opcode, protocolVersion);
        return lookup != null && lookup.ok();
    }

    /**
     * 任务包某一步的操作码在 packages 里。有多条时每一条都要在协议书里。
     */
    static List<Integer> innerOpcodes(Map<String, Object> fields) {
        List<Integer> opcodes = new ArrayList<>();
        if (fields == null) {
            return opcodes;
        }
        Object packages = fields.get("packages");
        if (packages instanceof Map<?, ?> one) {
            addOpcode(opcodes, one.get("opcode"));
        } else if (packages instanceof List<?> list) {
            for (Object item : list) {
                if (item instanceof Map<?, ?> one) {
                    addOpcode(opcodes, one.get("opcode"));
                }
            }
        }
        return opcodes;
    }

    private static void addOpcode(List<Integer> opcodes, Object raw) {
        Integer opcode = parseOpcode(raw);
        if (opcode != null) {
            opcodes.add(opcode);
        }
    }

    static Integer parseOpcode(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Number number) {
            return number.intValue();
        }
        String text = String.valueOf(raw).trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public record Decision(boolean admitted, String protocolVersion, String reason) {

        public static Decision admit(String protocolVersion) {
            return new Decision(true, protocolVersion, null);
        }

        public static Decision reject(String reason) {
            return new Decision(false, null, reason);
        }
    }
}
