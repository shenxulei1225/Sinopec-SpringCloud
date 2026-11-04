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

/**
 * 把一座站的告警统计发到已经有的「场站告警」主题。
 * 名字是 alarm/facility/{场站编号}。一份报文给所有正在看这座站的人。
 * 不按某个人点开的级别拆开。消息服务没连上只记日志，不改告警记录。
 */
@Component
public class AlarmFacilityPublisher {

    private static final Logger log = LoggerFactory.getLogger(AlarmFacilityPublisher.class);
    private static final String FACILITY_PLACEHOLDER = "{facilityId}";

    private final ObjectMapper objectMapper;
    private final PushCatalogQueryService pushCatalogQueryService;
    private final boolean enabled;
    private final String broker;
    private final String clientId;
    private final String username;
    private final String password;
    private MqttClient client;

    public AlarmFacilityPublisher(
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
        this.clientId = clientId + "-alarm";
        this.username = username;
        this.password = password;
    }

    /** 这一座站刚算好的统计。场站编号空着不发。 */
    public void publish(AlarmFacilityStatsView stats) {
        if (!enabled || stats == null || stats.getFacilityId() <= 0) {
            return;
        }
        String topic = pushCatalogQueryService.filledTopic(
                PushCatalogCodes.ALARM_FACILITY_TOPIC, FACILITY_PLACEHOLDER, stats.getFacilityId());
        if (topic == null) {
            log.error("场站告警主题没有名字规则");
            return;
        }
        try {
            byte[] payload = objectMapper.writeValueAsBytes(messageOf(stats));
            connectedClient().publish(topic, payload, 0, false);
        } catch (JsonProcessingException ex) {
            log.error("场站 {} 的告警统计无法序列化", stats.getFacilityId());
        } catch (MqttException | RuntimeException ex) {
            log.warn("场站 {} 的告警统计没有发出：{}", stats.getFacilityId(), ex.getMessage());
        }
    }

    private java.util.Map<String, Object> messageOf(AlarmFacilityStatsView stats) {
        java.util.Map<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("messageKind", PushCatalogCodes.ALARM_FACILITY_TOPIC);
        body.put("facilityId", stats.getFacilityId());
        body.put("rootId", stats.getRootId());
        body.put("categories", stats.getCategories());
        body.put("changed", stats.getChanged());
        return body;
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
