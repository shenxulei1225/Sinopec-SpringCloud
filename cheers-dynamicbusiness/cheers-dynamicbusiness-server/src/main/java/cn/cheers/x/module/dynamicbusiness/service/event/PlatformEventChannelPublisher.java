package cn.cheers.x.module.dynamicbusiness.service.event;

import cn.cheers.x.framework.common.exception.ServiceException;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 把已经入账的平台事件发到消息通道。
 *
 * <p>管什么：按给定主题发出正文。</p>
 * <p>不管什么：不调用联动入口，不补名字规则，不决定谁该执行动作。</p>
 */
@Component
public class PlatformEventChannelPublisher {

    private static final int AT_LEAST_ONCE = 1;

    private final boolean enabled;
    private final String broker;
    private final String clientId;
    private final String username;
    private final String password;
    private MqttClient client;

    public PlatformEventChannelPublisher(
            @Value("${dynamicbusiness.event-channel.enabled:false}") boolean enabled,
            @Value("${dynamicbusiness.event-channel.broker:tcp://127.0.0.1:1883}") String broker,
            @Value("${dynamicbusiness.event-channel.publisher-client-id:platform-event-publisher}") String clientId,
            @Value("${dynamicbusiness.event-channel.username:platform:event}") String username,
            @Value("${dynamicbusiness.event-channel.password:}") String password) {
        this.enabled = enabled;
        this.broker = broker;
        this.clientId = clientId;
        this.username = username;
        this.password = password;
    }

    /**
     * 发出这一条事件。通道没打开或没连上就失败，由 outbox 稍后重试。
     */
    public void publish(String topic, byte[] payload) {
        if (!enabled) {
            throw new ServiceException(500, "事件通道未打开");
        }
        if (topic == null || topic.isBlank() || payload == null) {
            throw new ServiceException(400, "事件缺少通道主题或正文");
        }
        try {
            connectedClient().publish(topic, payload, AT_LEAST_ONCE, false);
        } catch (MqttException ex) {
            throw new ServiceException(500, "事件没有发到消息通道：" + ex.getMessage());
        }
    }

    private synchronized MqttClient connectedClient() throws MqttException {
        if (client != null && client.isConnected()) {
            return client;
        }
        client = new MqttClient(broker, clientId, new MemoryPersistence());
        client.connect(MqttChannelConnectOptions.require(username, password, false));
        return client;
    }
}
