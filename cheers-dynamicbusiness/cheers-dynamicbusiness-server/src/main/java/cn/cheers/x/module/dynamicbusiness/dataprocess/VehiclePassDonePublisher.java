package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.module.dynamicbusiness.api.security.VehiclePassEventCodes;
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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 把一次已经写下的进入或离开发到「已经进出」主题。
 * 名字是 vehicle/pass/done/{场站编号}。安防图层订的是这一句。
 * 不负责写进出账。主题没有名字规则时不发，也不另编主题。
 * 禁止：把识别到车、还没放行的那一句发到这里。
 */
@Component
public class VehiclePassDonePublisher {

    private static final Logger log = LoggerFactory.getLogger(VehiclePassDonePublisher.class);
    private static final String FACILITY_PLACEHOLDER = "{facilityId}";

    private final ObjectMapper objectMapper;
    private final PushCatalogQueryService pushCatalogQueryService;
    private final boolean enabled;
    private final String broker;
    private final String clientId;
    private final String username;
    private final String password;
    private MqttClient client;

    public VehiclePassDonePublisher(
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
        this.clientId = clientId + "-vehicle-pass";
        this.username = username;
        this.password = password;
    }

    /**
     * 进出时间已经写下。场站或车牌空着不发。
     * 方向用车道上的进场或出场。摄像机编号空着时，图层只更新名单，不标点。
     */
    public void publish(long facilityId, String plate, String direction, String gate, String at, Long cameraId) {
        if (!enabled || facilityId <= 0 || plate == null || plate.isBlank()) {
            return;
        }
        String topic = pushCatalogQueryService.filledTopic(
                PushCatalogCodes.VEHICLE_PASS_DONE_TOPIC, FACILITY_PLACEHOLDER, facilityId);
        if (topic == null) {
            log.error("已经进出主题没有名字规则");
            return;
        }
        try {
            byte[] payload = objectMapper.writeValueAsBytes(messageOf(facilityId, plate, direction, gate, at, cameraId));
            connectedClient().publish(topic, payload, 0, false);
        } catch (JsonProcessingException ex) {
            log.error("场站 {} 的已经进出无法序列化", facilityId);
        } catch (MqttException | RuntimeException ex) {
            log.warn("场站 {} 的已经进出没有发出：{}", facilityId, ex.getMessage());
        }
    }

    private Map<String, Object> messageOf(long facilityId, String plate, String direction, String gate, String at, Long cameraId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("messageKind", VehiclePassEventCodes.DONE);
        body.put("facilityId", facilityId);
        body.put("plate", plate.trim());
        body.put("direction", direction == null ? "" : direction);
        body.put("gate", gate == null ? "" : gate);
        body.put("at", at == null ? "" : at);
        body.put("equipmentId", cameraId == null || cameraId <= 0 ? 0 : cameraId);
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
