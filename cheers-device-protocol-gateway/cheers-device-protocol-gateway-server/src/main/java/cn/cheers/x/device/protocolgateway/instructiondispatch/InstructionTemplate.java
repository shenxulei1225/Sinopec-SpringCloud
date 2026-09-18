package cn.cheers.x.device.protocolgateway.instructiondispatch;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 协议指令主包空包。填包只改副本，不改目录原文。
 */
public record InstructionTemplate(int opcode, Map<String, Object> outbound) {

    public InstructionTemplate {
        if (opcode <= 0) {
            throw new IllegalArgumentException("协议指令缺少操作码");
        }
        outbound = outbound == null
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(outbound));
    }
}
