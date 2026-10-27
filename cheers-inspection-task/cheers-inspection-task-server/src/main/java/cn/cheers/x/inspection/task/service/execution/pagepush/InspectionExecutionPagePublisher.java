package cn.cheers.x.inspection.task.service.execution.pagepush;

import cn.cheers.x.inspection.task.api.dto.ReplayPoseReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.push.InspectionPushTopics;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyTriggerEventDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 按已经存好的名字规则，把巡检页面要看的字段发出去。
 * <p>名字规则在 {@link InspectionPushTopics}，和推送主题目录是同一份。发出时只填执行记录编号或设备编号。
 * 轨迹报纬度、经度、高度、时间。步骤报步骤编号、状态、巡检点位置、电量、照片地址。
 * 设备实时状态报这一帧飞机状态，不经过执行过程账。
 * <p>不负责：写过程账、解析设备操作码、页面没订时改投递。
 * <p>禁止：每发一封再查一次主题；主题不在这份规则里时自己编一条；把照片 Base64 放进页面报文。
 */
@Slf4j
@Component
public class InspectionExecutionPagePublisher {

    static final String TOPIC_PLACEHOLDER = "{执行记录编号}";

    private final ObjectMapper objectMapper;
    private final boolean enabled;
    private final String broker;
    private final String clientId;
    private MqttClient client;

    public InspectionExecutionPagePublisher(
            ObjectMapper objectMapper,
            @Value("${inspection.page-push.enabled:false}") boolean enabled,
            @Value("${inspection.page-push.broker:tcp://127.0.0.1:1883}") String broker,
            @Value("${inspection.page-push.client-id:inspection-page-push}") String clientId) {
        this.objectMapper = objectMapper;
        this.enabled = enabled;
        this.broker = broker;
        this.clientId = clientId;
    }

    /**
     * 这一帧飞机状态。设备编号填进已经存好的名字规则。没有设备编号不发。
     * 不写执行过程账。
     */
    public void publishDeviceStatus(String deviceId, Map<String, Object> fields) {
        if (!enabled || !StringUtils.hasText(deviceId)) {
            return;
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("messageKind", InspectionPushTopics.DEVICE_STATUS.code());
        if (fields != null) {
            body.putAll(fields);
        }
        send(InspectionPushTopics.DEVICE_STATUS.code(), null, deviceId, body);
    }

    /**
     * 轨迹点。x 是经度，y 是纬度，z 是高度。页面报文用纬度、经度、高度。
     * 记点由调用方另做，这里不看记点是否成功。
     */
    public void publishTrajectory(Long executionRecordId, ReplayPoseReqDTO pose) {
        if (!enabled || executionRecordId == null || pose == null) {
            return;
        }
        Map<String, Object> body = trajectoryBody(executionRecordId, pose);
        send(InspectionPushTopics.TRAJECTORY.code(), executionRecordId, null, body);
    }

    /**
     * 这次步骤结果。没有步骤更新则不发。
     */
    public void publishSteps(StrategyTriggerEventDTO event) {
        if (!enabled || event == null || event.getExecutionRecordId() == null) {
            return;
        }
        List<Map<String, Object>> updates = event.getStepUpdates();
        if (updates == null || updates.isEmpty()) {
            return;
        }
        for (Map<String, Object> update : updates) {
            send(InspectionPushTopics.STEP.code(), event.getExecutionRecordId(), null,
                    stepBody(event.getExecutionRecordId(), update));
        }
    }

    static Map<String, Object> trajectoryBody(Long executionRecordId, ReplayPoseReqDTO pose) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("executionRecordId", executionRecordId);
        body.put("lat", pose.getY());
        body.put("lng", pose.getX());
        body.put("height", pose.getZ());
        body.put("time", pose.getT());
        return body;
    }

    static Map<String, Object> stepBody(Long executionRecordId, Map<String, Object> update) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("executionRecordId", executionRecordId);
        body.put("stepCode", update == null ? null : update.get("stepCode"));
        body.put("status", update == null ? null : update.get("status"));
        Map<String, Object> payload = map(update == null ? null : update.get("resultPayload"));
        Map<String, Object> point = map(payload.get("operatePoint"));
        body.put("lat", point.get("lat"));
        body.put("lng", point.get("lng"));
        Map<String, Object> result = map(payload.get("result"));
        body.put("electricity", result.get("electricity"));
        body.put("photoAddresses", photoAddresses(result.get("photos")));
        Object receivedAt = payload.get("receivedAtEpochMs");
        if (receivedAt != null) {
            body.put("time", receivedAt);
        }
        return body;
    }

    static String topicOf(String template, Long executionRecordId) {
        return topicOf(template, executionRecordId, null);
    }

    /**
     * 名字规则里的执行记录编号、设备编号在发出时填上。缺了对应编号就不发。
     */
    static String topicOf(String template, Long executionRecordId, String deviceId) {
        if (!StringUtils.hasText(template)) {
            return null;
        }
        String topic = template;
        if (topic.contains(TOPIC_PLACEHOLDER)) {
            if (executionRecordId == null) {
                return null;
            }
            topic = topic.replace(TOPIC_PLACEHOLDER, String.valueOf(executionRecordId));
        }
        if (topic.contains("{设备编号}")) {
            if (!StringUtils.hasText(deviceId)) {
                return null;
            }
            topic = topic.replace("{设备编号}", deviceId.trim());
        }
        return topic;
    }

    /**
     * 指令行的状态变化。正文是名称、状态、正常或异常、事件字段。页面用它改同一行。
     * 名字规则对不上编号时不发。消息服务连不上只记日志。
     * 不读过程账。调用方已经有这些字段才发。
     */
    public void publishProcessNotice(
            String topicCode,
            Long executionRecordId,
            String deviceId,
            String summary,
            String eventStatus,
            String outcome,
            String msgId,
            String direction,
            Long receivedAtEpochMs,
            Map<String, Object> fields) {
        if (!enabled || !StringUtils.hasText(topicCode) || !StringUtils.hasText(eventStatus)) {
            return;
        }
        Map<String, Object> body = processBody(
                topicCode, executionRecordId, summary, eventStatus, outcome, msgId, direction, receivedAtEpochMs, fields);
        send(topicCode, executionRecordId, deviceId, body);
    }

    /**
     * 页面只认过程行上的这些字段。不带下发阶段，也不带报文原文。
     */
    static Map<String, Object> processBody(
            String topicCode,
            Long executionRecordId,
            String summary,
            String eventStatus,
            String outcome,
            String msgId,
            String direction,
            Long receivedAtEpochMs,
            Map<String, Object> fields) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("executionRecordId", executionRecordId);
        body.put("messageKind", topicCode);
        body.put("summary", summary);
        body.put("eventStatus", eventStatus);
        body.put("outcome", outcome);
        body.put("msgId", msgId);
        body.put("direction", direction);
        body.put("receivedAtEpochMs", receivedAtEpochMs);
        Map<String, Object> eventFields = new LinkedHashMap<>();
        if (fields != null) {
            eventFields.putAll(fields);
            eventFields.remove("commandPhase");
        }
        body.put("fields", eventFields);
        return body;
    }

    /**
     * 按这条标准事件自己的字段说明发出。正文就是样本上的转发字段。
     * 名字规则对不上编号时不发。
     */
    public void publishStandard(
            String topicCode, Long executionRecordId, String deviceId, Map<String, Object> fields) {
        if (!enabled || !StringUtils.hasText(topicCode)) {
            return;
        }
        Map<String, Object> body = new LinkedHashMap<>();
        if (executionRecordId != null) {
            body.put("executionRecordId", executionRecordId);
        }
        if (fields != null) {
            body.putAll(fields);
        }
        send(topicCode, executionRecordId, deviceId, body);
    }

    /**
     * 只收已经写成地址的照片。名为 data 的 Base64 不放进页面报文。
     */
    static List<String> photoAddresses(Object photos) {
        List<String> addresses = new ArrayList<>();
        if (!(photos instanceof List<?> list)) {
            return addresses;
        }
        for (Object item : list) {
            if (!(item instanceof Map<?, ?> photo)) {
                continue;
            }
            for (String key : List.of("url", "path", "address", "photoUrl", "fileUrl")) {
                Object value = photo.get(key);
                if (value instanceof String text && StringUtils.hasText(text)) {
                    addresses.add(text);
                }
            }
        }
        return addresses;
    }

    private void send(String protocolCode, Long executionRecordId, String deviceId, Map<String, Object> body) {
        String topic = topicOf(InspectionPushTopics.pattern(protocolCode), executionRecordId, deviceId);
        if (!StringUtils.hasText(topic)) {
            log.error("[inspection-page-push] 推送主题没有名字规则 code={} executionRecordId={}",
                    protocolCode, executionRecordId);
            return;
        }
        try {
            byte[] payload = objectMapper.writeValueAsBytes(body);
            publish(topic, payload);
        } catch (JsonProcessingException ex) {
            log.error("[inspection-page-push] 页面报文无法序列化 code={} executionRecordId={}",
                    protocolCode, executionRecordId, ex);
        } catch (RuntimeException ex) {
            log.error("[inspection-page-push] 页面报文没有发出 code={} topic={} reason={}",
                    protocolCode, topic, ex.getMessage());
        }
    }

    private void publish(String topic, byte[] payload) {
        try {
            MqttClient current = connectedClient();
            current.publish(topic, payload, 0, false);
        } catch (MqttException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }

    private synchronized MqttClient connectedClient() throws MqttException {
        if (client != null && client.isConnected()) {
            return client;
        }
        client = new MqttClient(broker, clientId, new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        client.connect(options);
        return client;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> map(Object value) {
        if (value instanceof Map<?, ?> raw) {
            return (Map<String, Object>) raw;
        }
        return Map.of();
    }
}
