package cn.cheers.x.device.protocolgateway.djicloud;

import cn.cheers.x.device.protocolgateway.api.channel.AccessChannelCodes;
import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.device.protocolgateway.api.datacollection.ProtocolQualifyStatus;
import cn.cheers.x.device.protocolgateway.api.dto.DeviceUplinkEventDTO;
import cn.cheers.x.device.protocolgateway.datacollection.CatalogLookup;
import cn.cheers.x.device.protocolgateway.datacollection.ProtocolInstructionCatalog;
import cn.cheers.x.device.protocolgateway.datacollection.StandardEventForwarder;
import cn.cheers.x.device.protocolgateway.protocol.uplink.UplinkEventPublisher;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 大疆上云服务答卷收成标准事件。
 * <p>负责：按报文里的方法名找到转发到主题，把返回码放进样本。
 * <p>不负责：写巡检账、用操作码 0 去对某一条指令、没有方法名时猜一条主题。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DjiCloudStandardEventPublisher {

    private final ProtocolInstructionCatalog catalog;
    private final UplinkEventPublisher uplinkEventPublisher;
    private final ObjectMapper objectMapper;

    /**
     * 主题最后一段是设备编号。答卷没有方法名，或该方法没有转发主题，则不发标准事件。
     */
    public void publishReply(String topic, byte[] payload) {
        String deviceId = deviceId(topic);
        if (deviceId.isBlank() || payload == null) {
            return;
        }
        Map<String, Object> body;
        try {
            body = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ex) {
            return;
        }
        Object method = body.get("method");
        if (!(method instanceof String methodName) || methodName.isBlank()) {
            return;
        }
        CatalogLookup lookup = catalog.findByProtocolMethod(methodName);
        if (!lookup.ok() || lookup.forwardTopic() == null || lookup.forwardTopic().isBlank()) {
            return;
        }
        Map<String, Object> fields = new LinkedHashMap<>(StandardEventForwarder.project(body, lookup.rows()));
        if (!fields.containsKey("code")) {
            Object result = resultCode(body.get("data"));
            if (result != null) {
                fields.put("code", result);
            }
        }
        if (fields.isEmpty()) {
            return;
        }
        String msgId = body.get("tid") == null ? "" : String.valueOf(body.get("tid"));
        Long recordId = flightRecordId(body);
        uplinkEventPublisher.publish(DeviceUplinkEventDTO.of(new CollectionSample(
                AccessChannelCodes.INSPECTION,
                deviceId,
                lookup.forwardTopic(),
                ProtocolQualifyStatus.QUALIFIED,
                java.util.List.of(),
                fields,
                recordId,
                msgId,
                System.currentTimeMillis())));
    }

    /** 大疆飞行编号就是这次执行记录编号。没有就空着，不按设备编号猜。 */
    private static Long flightRecordId(Map<String, Object> body) {
        Long direct = digits(body.get("flight_id"));
        if (direct != null) {
            return direct;
        }
        Object data = body.get("data");
        if (data instanceof Map<?, ?> map) {
            return digits(map.get("flight_id"));
        }
        return null;
    }

    private static Long digits(Object value) {
        if (value instanceof Number number) {
            long parsed = number.longValue();
            return parsed > 0L ? parsed : null;
        }
        if (!(value instanceof String text) || text.isBlank() || !text.chars().allMatch(Character::isDigit)) {
            return null;
        }
        long parsed = Long.parseLong(text.trim());
        return parsed > 0L ? parsed : null;
    }

    private static String deviceId(String topic) {
        if (topic == null) {
            return "";
        }
        String[] parts = topic.split("/");
        if (parts.length < 3) {
            return "";
        }
        return parts[2] == null ? "" : parts[2].trim();
    }

    private static Object resultCode(Object data) {
        if (!(data instanceof Map<?, ?> map)) {
            return null;
        }
        return map.get("result");
    }
}
