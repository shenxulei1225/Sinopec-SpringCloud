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
 * 外层信封编解码：下发信封、回执、心跳回显、读操作码/消息 id/业务 code。
 * <p>不负责交互方式分流、不等待答卷。禁止把回执编成带业务 code 的成功卷。
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
        JsonNode opcode = readRoot(payloadJson).get("opcode");
        if (opcode == null || !opcode.isNumber()) {
            throw new IllegalArgumentException("上报报文缺少 opcode");
        }
        return opcode.asInt();
    }

    /**
     * 读消息 id。缺省返回空串，不猜一个默认 id。
     */
    public String readMsgId(String payloadJson) {
        JsonNode msgId = readRoot(payloadJson).get("msgId");
        if (msgId == null || msgId.isNull()) {
            return "";
        }
        return msgId.asText("");
    }

    /**
     * 读同号业务答卷的外壳 code。回执没有该字段，返回 empty。
     */
    public Integer readBusinessCode(String payloadJson) {
        JsonNode code = readRoot(payloadJson).get("code");
        if (code == null || !code.isNumber()) {
            return null;
        }
        return code.asInt();
    }

    /**
     * 通信层回执：表示收到了；不当业务成功。
     */
    public String encodeAck(String msgId, int originalOpcode, long epochSeconds) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("opcode", originalOpcode);
        data.put("time", epochSeconds);
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("opcode", TransportOpcode.ACK.code());
        root.put("msgId", msgId == null ? "" : msgId);
        root.put("data", data);
        return write(root, "序列化回执失败");
    }

    /**
     * 心跳对回：原包加上 time，操作码仍是 500105，不改成 500106。
     */
    public String encodeHeartbeatEcho(String payloadJson, long epochSeconds) {
        try {
            JsonNode root = objectMapper.readTree(payloadJson);
            Map<String, Object> echo = objectMapper.convertValue(root, new TypeReference<>() {
            });
            echo.put("time", epochSeconds);
            echo.put("opcode", TransportOpcode.HEARTBEAT.code());
            return write(echo, "序列化心跳回显失败");
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("解析心跳报文失败", e);
        }
    }

    private JsonNode readRoot(String payloadJson) {
        try {
            return objectMapper.readTree(payloadJson);
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
