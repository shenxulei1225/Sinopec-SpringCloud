package cn.cheers.x.device.protocolgateway.protocol.envelope;

import cn.cheers.x.device.protocolgateway.api.message.CommandSendEnvelope;
import cn.cheers.x.device.protocolgateway.api.message.InstructionPackageItem;
import cn.cheers.x.device.protocolgateway.api.message.StartupExecuteEnvelope;
import cn.cheers.x.device.protocolgateway.api.message.TaskStopEnvelope;
import cn.cheers.x.device.protocolgateway.api.opcode.TransportOpcode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 外层信封编解码：将指令包序列化为协议要求的 packages JSON 字符串。
 */
@Component
@RequiredArgsConstructor
public class EnvelopeJsonCodec {

    private final ObjectMapper objectMapper;

    public String encodePackagesJson(List<InstructionPackageItem> items) {
        try {
            List<Map<String, Object>> array = items.stream().map(this::toPackageElement).toList();
            return objectMapper.writeValueAsString(array);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("序列化 packages 失败", e);
        }
    }

    public String encodeCommandSend(CommandSendEnvelope envelope) {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("msgId", envelope.msgId());
        root.put("opcode", TransportOpcode.COMMAND_SEND.code());
        if (envelope.deviceId() != null && !envelope.deviceId().isBlank()) {
            root.put("deviceId", envelope.deviceId());
        }
        root.put("templateId", envelope.templateId());
        root.put("packages", envelope.packagesJson());
        return write(root, "序列化指令下发信封失败");
    }

    public String encodeStartupExecute(StartupExecuteEnvelope envelope) {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("opcode", TransportOpcode.STARTUP_EXECUTE.code());
        root.put("msgId", envelope.msgId());
        root.put("taskId", envelope.taskId());
        root.put("deviceId", envelope.deviceId());
        root.put("templateId", envelope.templateId());
        root.put("executionTime", envelope.executionTimeEpochSeconds());
        return write(root, "序列化启动执行信封失败");
    }

    public String encodeTaskStop(TaskStopEnvelope envelope) {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("opcode", TransportOpcode.TASK_STOP.code());
        root.put("msgId", envelope.msgId());
        root.put("taskId", envelope.taskId());
        root.put("deviceId", envelope.deviceId());
        return write(root, "序列化任务终止信封失败");
    }

    public List<Map<String, Object>> decodePackagesJson(String packagesJson) {
        try {
            return objectMapper.readValue(packagesJson, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("解析 packages 失败", e);
        }
    }

    public int readOpcode(String payloadJson) {
        try {
            JsonNode root = objectMapper.readTree(payloadJson);
            JsonNode opcode = root.get("opcode");
            if (opcode == null || !opcode.isNumber()) {
                throw new IllegalArgumentException("上报报文缺少 opcode");
            }
            return opcode.asInt();
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("解析上报报文失败", e);
        }
    }

    private String write(Map<String, Object> root, String errorMessage) {
        try {
            return objectMapper.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(errorMessage, e);
        }
    }

    private Map<String, Object> toPackageElement(InstructionPackageItem item) {
        Map<String, Object> element = new LinkedHashMap<>();
        element.put("opcode", item.opcode());
        element.put("sequence", item.sequence());
        element.put("request", item.request());
        return element;
    }
}
