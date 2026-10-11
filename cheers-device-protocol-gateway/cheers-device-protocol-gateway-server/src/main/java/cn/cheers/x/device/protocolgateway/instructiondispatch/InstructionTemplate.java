package cn.cheers.x.device.protocolgateway.instructiondispatch;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 协议指令主包空包。填包只改副本，不改目录原文。
 */
public record InstructionTemplate(
        int opcode,
        Map<String, Object> outbound,
        String communicationProtocol,
        String mqttTopic
) {

    public InstructionTemplate(int opcode, Map<String, Object> outbound) {
        this(opcode, outbound, "", "");
    }

    public InstructionTemplate {
        boolean cloudMethod = outbound != null
                && outbound.get("method") instanceof String method
                && !method.isBlank();
        boolean httpCall = outbound != null
                && outbound.get("httpMethod") instanceof String httpMethod
                && !httpMethod.isBlank();
        boolean bridgeCall = outbound != null
                && outbound.get("bridge") instanceof String bridge
                && !bridge.isBlank();
        boolean socketPush = outbound != null
                && outbound.get("biz_code") instanceof String bizCode
                && !bizCode.isBlank();
        boolean waylineAction = outbound != null
                && outbound.get("actionActuatorFunc") instanceof String actuator
                && !actuator.isBlank();
        if (opcode <= 0 && !cloudMethod && !httpCall && !bridgeCall && !socketPush && !waylineAction) {
            throw new IllegalArgumentException("协议指令缺少操作码");
        }
        outbound = outbound == null
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(outbound));
        communicationProtocol = communicationProtocol == null ? "" : communicationProtocol;
        mqttTopic = mqttTopic == null ? "" : mqttTopic;
    }
}
