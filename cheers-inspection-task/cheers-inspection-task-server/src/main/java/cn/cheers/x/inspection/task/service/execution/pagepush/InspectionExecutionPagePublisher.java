package cn.cheers.x.inspection.task.service.execution.pagepush;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.api.dto.ReplayPoseReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
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
 * 巡检把这一次执行的轨迹或步骤写上账之后，按消息目录里的主题发给页面。
 * <p>主题格式和字段说明都在「推送主题」这一行上。占位是执行记录编号。
 * 名字规则这一列的编码是 F-b2803c19be774051b1af38d9e13a760a。
 * 轨迹报文本次写入的纬度、经度、高度、时间。步骤报文本次写入的步骤编号、状态、巡检点位置、电量、照片地址。
 * <p>不负责：解析设备操作码、在数据协议里登记页面主题、页面没订时改投递。
 * <p>禁止：账还没写上就发；把照片 Base64 放进页面报文；主题读不到时自己编一条。
 */
@Slf4j
@Component
public class InspectionExecutionPagePublisher {

    static final String TOPIC_TYPE = "push_topic";
    /** 推送主题型号上的「名字规则」。 */
    static final String NAME_PATTERN_FIELD = "F-b2803c19be774051b1af38d9e13a760a";
    static final String TRAJECTORY_CODE = "inspection_execution_trajectory";
    static final String STEP_CODE = "inspection_execution_step";
    static final String TOPIC_PLACEHOLDER = "{执行记录编号}";

    private final EntityRpcApi entityRpcApi;
    private final ObjectMapper objectMapper;
    private final boolean enabled;
    private final String broker;
    private final String clientId;
    private MqttClient client;

    public InspectionExecutionPagePublisher(
            EntityRpcApi entityRpcApi,
            ObjectMapper objectMapper,
            @Value("${inspection.page-push.enabled:false}") boolean enabled,
            @Value("${inspection.page-push.broker:tcp://127.0.0.1:1883}") String broker,
            @Value("${inspection.page-push.client-id:inspection-page-push}") String clientId) {
        this.entityRpcApi = entityRpcApi;
        this.objectMapper = objectMapper;
        this.enabled = enabled;
        this.broker = broker;
        this.clientId = clientId;
    }

    /**
     * 轨迹点已经写入这次执行之后调用。
     * x 是经度，y 是纬度，z 是高度。页面报文用纬度、经度、高度。
     */
    public void publishTrajectory(Long executionRecordId, ReplayPoseReqDTO pose) {
        if (!enabled || executionRecordId == null || pose == null) {
            return;
        }
        Map<String, Object> body = trajectoryBody(executionRecordId, pose);
        send(TRAJECTORY_CODE, executionRecordId, body);
    }

    /**
     * 策略已经把步骤结果写上这次执行之后调用。没有步骤更新则不发。
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
            send(STEP_CODE, event.getExecutionRecordId(), stepBody(event.getExecutionRecordId(), update));
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
        return body;
    }

    static String topicOf(String template, Long executionRecordId) {
        if (!StringUtils.hasText(template) || executionRecordId == null) {
            return null;
        }
        return template.replace(TOPIC_PLACEHOLDER, String.valueOf(executionRecordId));
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

    private void send(String protocolCode, Long executionRecordId, Map<String, Object> body) {
        String topic = topicOf(loadTopic(protocolCode), executionRecordId);
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

    private String loadTopic(String protocolCode) {
        try {
            CommonResult<EntityRespDTO> loaded = entityRpcApi.getEntityByCode(protocolCode, TOPIC_TYPE);
            if (loaded == null || !loaded.isSuccess() || loaded.getData() == null) {
                log.error("[inspection-page-push] 读不到推送主题 code={}", protocolCode);
                return null;
            }
            EntityRespDTO entity = loaded.getData();
            Object topic = fieldValue(entity.getBaseFields(), NAME_PATTERN_FIELD);
            if (topic == null) {
                topic = fieldValue(entity.getCustomFields(), NAME_PATTERN_FIELD);
            }
            if (!(topic instanceof String text) || !StringUtils.hasText(text)) {
                log.error("[inspection-page-push] 推送主题没有名字规则 code={}", protocolCode);
                return null;
            }
            return text;
        } catch (RuntimeException ex) {
            log.error("[inspection-page-push] 读取推送主题失败 code={} reason={}",
                    protocolCode, ex.getMessage());
            return null;
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

    private static Object fieldValue(Map<String, Object> fields, String key) {
        return fields == null ? null : fields.get(key);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> map(Object value) {
        if (value instanceof Map<?, ?> raw) {
            return (Map<String, Object>) raw;
        }
        return Map.of();
    }
}
