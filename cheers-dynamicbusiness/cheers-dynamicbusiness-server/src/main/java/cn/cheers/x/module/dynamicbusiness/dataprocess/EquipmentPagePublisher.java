package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.module.dynamicbusiness.service.event.MqttChannelConnectOptions;
import cn.cheers.x.module.dynamicbusiness.service.push.PushCatalogCodes;
import cn.cheers.x.module.dynamicbusiness.service.push.PushCatalogQueryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 正常读数按已经有的「设备状态」发给页面。
 * 名字规则是 device/{deviceId}/status。不发到巡检的飞机状态。
 * 消息服务没连上只记日志。不订阅网关，也不写历史。
 */
@Component
public class EquipmentPagePublisher implements EquipmentStatusPublisher {

    private static final Logger log = LoggerFactory.getLogger(EquipmentPagePublisher.class);
    private static final String DEVICE_PLACEHOLDER = "{deviceId}";

    private final ObjectMapper objectMapper;
    private final PushCatalogQueryService pushCatalogQueryService;
    private final boolean enabled;
    private final String broker;
    private final String clientId;
    private final String username;
    private final String password;
    private MqttClient client;

    public EquipmentPagePublisher(
            ObjectMapper objectMapper,
            PushCatalogQueryService pushCatalogQueryService,
            @Value("${equipment.page-push.enabled:false}") boolean enabled,
            @Value("${equipment.page-push.broker:tcp://127.0.0.1:1883}") String broker,
            @Value("${equipment.page-push.client-id:equipment-page-push}") String clientId,
            @Value("${equipment.page-push.username:platform:event}") String username,
            @Value("${equipment.page-push.password:}") String password) {
        this.objectMapper = objectMapper;
        this.pushCatalogQueryService = pushCatalogQueryService;
        this.enabled = enabled;
        this.broker = broker;
        this.clientId = clientId;
        this.username = username;
        this.password = password;
    }

    /**
     * 这一条正常读数。设备编号填进已有名字规则。编号或点号空着不发。
     */
    @Override
    public void publish(long deviceId, String pointCode, double value, Instant sampledAt) {
        if (!enabled || deviceId <= 0 || pointCode == null || pointCode.isBlank()) {
            return;
        }
        String topic = pushCatalogQueryService.filledTopic(
                PushCatalogCodes.DEVICE_STATUS_TOPIC, DEVICE_PLACEHOLDER, deviceId);
        if (topic == null) {
            log.error("设备状态主题没有名字规则");
            return;
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("messageKind", PushCatalogCodes.DEVICE_STATUS_TOPIC);
        body.put("deviceId", deviceId);
        body.put("pointCode", pointCode);
        body.put("value", value);
        body.put("sampledAt", sampledAt == null ? null : sampledAt.toString());
        try {
            byte[] payload = objectMapper.writeValueAsBytes(body);
            publish(topic, payload);
        } catch (JsonProcessingException ex) {
            log.error("设备 {} 的页面报文无法序列化", deviceId);
        } catch (RuntimeException ex) {
            log.warn("设备 {} 的读数没有发给页面：{}", deviceId, ex.getMessage());
        }
    }

    private void publish(String topic, byte[] payload) {
        try {
            connectedClient().publish(topic, payload, 0, false);
        } catch (MqttException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }

    private synchronized MqttClient connectedClient() throws MqttException {
        if (client != null && client.isConnected()) {
            return client;
        }
        client = new MqttClient(broker, clientId, new MemoryPersistence());
        client.connect(MqttChannelConnectOptions.require(username, password, true));
        return client;
    }
}
