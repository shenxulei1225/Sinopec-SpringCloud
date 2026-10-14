package cn.cheers.x.device.protocolgateway.protocol.uplink;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 东古地面站最近一次实时状态或定时状态里报上的电量。
 * <p>收包时只记在这个进程的内存里。开跑来读。不写巡检账。
 * <p>电量不是剩余可飞秒数。这里不换算秒数。
 */
@Component
public class GroundStationLiveStatusStore {

    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, Frame> latest = new ConcurrentHashMap<>();

    public GroundStationLiveStatusStore(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 500101 的电量在 data 里。500103 的电量在 result 里。其它包不记。
     */
    public void remember(String deviceId, int opcode, String payloadJson) {
        if (deviceId == null || deviceId.isBlank() || payloadJson == null || payloadJson.isBlank()) {
            return;
        }
        if (opcode != 500101 && opcode != 500103) {
            return;
        }
        String electricity = readElectricity(opcode, payloadJson);
        latest.put(deviceId.trim(), new Frame(electricity, System.currentTimeMillis()));
    }

    public Frame latest(String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            return null;
        }
        return latest.get(deviceId.trim());
    }

    private String readElectricity(int opcode, String payloadJson) {
        try {
            JsonNode root = objectMapper.readTree(payloadJson);
            JsonNode node = opcode == 500101
                    ? root.path("data").path("electricity")
                    : root.path("result").path("electricity");
            if (node.isMissingNode() || node.isNull()) {
                return null;
            }
            String text = node.asText().trim();
            return text.isEmpty() ? null : text;
        } catch (Exception ex) {
            return null;
        }
    }

    public record Frame(String electricity, long receivedAtEpochMs) {
    }
}
