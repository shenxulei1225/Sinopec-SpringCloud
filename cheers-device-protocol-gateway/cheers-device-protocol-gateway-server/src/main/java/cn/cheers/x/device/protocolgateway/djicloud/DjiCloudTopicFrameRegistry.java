package cn.cheers.x.device.protocolgateway.djicloud;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 状态变更、事件上报、指令飞行上行的最新一帧。按主题里的序列号留在本进程内存。
 * <p>不写巡检账，不把机场报文当成飞机状态。
 * <p>事件 need_reply 为 1 时，按官网答复：同一事务编号，result 为 0，表示已收到且未报错。不在这里判断任务成败。
 */
@Component
public class DjiCloudTopicFrameRegistry {

    private static final Pattern FRAME = Pattern.compile(
            "^thing/product/([^/]+)/(state|events|drc/up|property/set_reply)$");

    private final ObjectMapper objectMapper;
    private final ConcurrentMap<String, byte[]> latest = new ConcurrentHashMap<>();

    public DjiCloudTopicFrameRegistry(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 收包线程之外调用。不是这三类主题时不记。事件要回复时返回回复主题和正文。
     */
    public Reply accept(String topic, byte[] payload) {
        Matcher matcher = topic == null ? null : FRAME.matcher(topic);
        if (matcher == null || !matcher.matches() || payload == null) {
            return null;
        }
        latest.put(topic, payload.clone());
        if (!topic.endsWith("/events")) {
            return null;
        }
        return eventReply(matcher.group(1), payload);
    }

    public byte[] latest(String topic) {
        return topic == null ? null : latest.get(topic);
    }

    private Reply eventReply(String deviceSn, byte[] payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            if (root.path("need_reply").asInt(0) != 1) {
                return null;
            }
            ObjectNode body = objectMapper.createObjectNode();
            body.put("tid", root.path("tid").asText(""));
            body.put("bid", root.path("bid").asText(""));
            body.put("timestamp", System.currentTimeMillis());
            body.put("gateway", deviceSn);
            body.put("method", root.path("method").asText(""));
            body.putObject("data").put("result", 0);
            return new Reply(
                    "thing/product/" + deviceSn + "/events_reply",
                    objectMapper.writeValueAsString(body));
        } catch (Exception ex) {
            return null;
        }
    }

    public record Reply(String topic, String json) {
    }
}
