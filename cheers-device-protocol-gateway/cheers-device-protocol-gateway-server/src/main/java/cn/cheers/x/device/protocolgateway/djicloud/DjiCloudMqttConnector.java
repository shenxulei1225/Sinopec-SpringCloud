package cn.cheers.x.device.protocolgateway.djicloud;

import cn.cheers.x.device.protocolgateway.config.DeviceProtocolGatewayProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 大疆上云 MQTT：机场和 Pilot 连到同一个代理后，本类应答上线，并订下设备推上来的全部 MQTT 主题。
 * <p>飞机定时上报收成设备实时状态交给巡检。状态变更、事件上报、指令飞行上行只留各自主题的最新一帧。服务答卷再按方法名收成标准事件。这里不写巡检账。
 * <p>不负责地面站 WebSocket，不写下发航线。Pilot 的 HTTPS、WebSocket、JSBridge 不是 MQTT，这里不订。
 * <p>禁止：代理没配上仍标设备在线；许可没配齐仍回 config；把机场报文当成飞机状态。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DjiCloudMqttConnector implements MqttCallbackExtended {

    private final DeviceProtocolGatewayProperties properties;
    private final DjiCloudOnlineRegistry onlineRegistry;
    private final DjiCloudDeviceStatusForwarder deviceStatus;
    private final DjiCloudServiceReplyWaiter serviceReplies;
    private final DjiCloudTopicFrameRegistry topicFrames;
    private final DjiCloudExchangeJournal exchangeJournal;
    private final DjiCloudStandardEventPublisher standardEvents;
    private final DjiCloudUplink uplink = new DjiCloudUplink(new ObjectMapper());
    private final ExecutorService replies = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "dji-cloud-reply");
        thread.setDaemon(true);
        return thread;
    });

    private MqttClient client;

    /**
     * 进程起来后再连。开关关闭，或代理地址为空，都不建连接。
     */
    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        DeviceProtocolGatewayProperties.DjiCloud cloud = properties.getDjiCloud();
        if (cloud == null || !cloud.isEnabled()) {
            log.info("大疆上云未打开，不连接 MQTT");
            return;
        }
        if (cloud.getBrokerUrl() == null || cloud.getBrokerUrl().isBlank()) {
            log.error("大疆上云已打开，但没有 MQTT 地址，设备无法上线");
            return;
        }
        connect(cloud);
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        subscribe();
        log.info("大疆上云 MQTT 已连接 {}", serverURI);
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.error("大疆上云 MQTT 连接断开", cause);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        byte[] payload = message.getPayload() == null ? new byte[0] : message.getPayload().clone();
        replies.execute(() -> handle(topic, payload));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // 发出去即可，不把写出当成任务成功。
    }

    @PreDestroy
    public void stop() {
        replies.shutdownNow();
        if (client == null) {
            return;
        }
        try {
            if (client.isConnected()) {
                client.disconnect();
            }
            client.close();
        } catch (MqttException ex) {
            log.error("关闭大疆上云 MQTT 失败", ex);
        }
    }

    private void connect(DeviceProtocolGatewayProperties.DjiCloud cloud) {
        try {
            client = new MqttClient(cloud.getBrokerUrl(), cloud.getClientId(), new MemoryPersistence());
            client.setCallback(this);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            if (cloud.getUsername() != null && !cloud.getUsername().isBlank()) {
                options.setUserName(cloud.getUsername());
                options.setPassword(cloud.getPassword() == null ? new char[0] : cloud.getPassword().toCharArray());
            }
            client.connect(options);
        } catch (MqttException ex) {
            log.error("大疆上云 MQTT 连接失败，设备无法上线", ex);
        }
    }

    private void subscribe() {
        if (client == null || !client.isConnected()) {
            return;
        }
        try {
            for (DjiCloudInboundTopics.Subscription subscription : DjiCloudInboundTopics.subscriptions()) {
                client.subscribe(subscription.filter(), subscription.qos());
            }
        } catch (MqttException ex) {
            log.error("订阅大疆上云主题失败", ex);
        }
    }

    private void handle(String topic, byte[] payload) {
        exchangeJournal.acceptInbound(topic, payload);
        if (topic != null && topic.endsWith("/osd")) {
            deviceStatus.accept(topic, payload);
            return;
        }
        if (topic != null && topic.endsWith("/services_reply")) {
            serviceReplies.completeFromPayload(payload);
            standardEvents.publishReply(topic, payload);
            return;
        }
        DjiCloudTopicFrameRegistry.Reply frameReply = topicFrames.accept(topic, payload);
        if (frameReply != null) {
            publish(frameReply.topic(), frameReply.json());
        }
        if (topic != null && (topic.endsWith("/state")
                || topic.endsWith("/events")
                || topic.endsWith("/drc/up")
                || topic.endsWith("/property/set_reply"))) {
            return;
        }
        DjiCloudDecision decision = uplink.accept(topic, payload, credentials(), organization());
        if (decision instanceof DjiCloudDecision.Reply reply) {
            publish(reply);
            return;
        }
        if (decision instanceof DjiCloudDecision.MissingLicense missing) {
            log.error("上云许可未配置，未回复 config，网关 {}", missing.gatewaySn());
            return;
        }
        if (decision instanceof DjiCloudDecision.MissingOrganization missing) {
            log.error("上云组织未配置，未回复组织绑定，网关 {}", missing.gatewaySn());
            return;
        }
        if (decision instanceof DjiCloudDecision.Ignore ignore) {
            log.debug("上云入站未应答：{}", ignore.reason());
        }
    }

    /**
     * 写出一条已经填好的上云报文。MQTT 没连上时返回 false，不假装已发出。
     */
    public boolean publish(String topic, String json) {
        if (client == null || !client.isConnected() || topic == null || json == null) {
            return false;
        }
        try {
            client.publish(topic, json.getBytes(StandardCharsets.UTF_8), 1, false);
            exchangeJournal.acceptOutbound(topic, json);
            return true;
        } catch (MqttException ex) {
            log.error("上云报文发送失败 {}", topic, ex);
            return false;
        }
    }

    private void publish(DjiCloudDecision.Reply reply) {
        if (client == null || !client.isConnected()) {
            log.error("上云应答发不出去，MQTT 不在线，网关 {}", reply.gatewaySn());
            return;
        }
        try {
            client.publish(reply.topic(), reply.json().getBytes(StandardCharsets.UTF_8), 1, false);
            exchangeJournal.acceptOutbound(reply.topic(), reply.json());
        } catch (MqttException ex) {
            log.error("上云应答发送失败，网关 {}", reply.gatewaySn(), ex);
            return;
        }
        if (reply.topology()) {
            onlineRegistry.markOnline(reply.gatewaySn(), reply.subDeviceSns());
        }
    }

    private DjiCloudOrganization organization() {
        DeviceProtocolGatewayProperties.DjiCloud cloud = properties.getDjiCloud();
        return new DjiCloudOrganization(cloud.getOrganizationId(), cloud.getOrganizationName());
    }

    private DjiCloudCredentials credentials() {
        DeviceProtocolGatewayProperties.DjiCloud cloud = properties.getDjiCloud();
        return new DjiCloudCredentials(
                cloud.getAppId(), cloud.getAppKey(), cloud.getAppLicense(),
                cloud.getNtpServerHost(), cloud.getNtpServerPort());
    }
}
