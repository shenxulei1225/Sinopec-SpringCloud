package cn.cheers.x.module.dynamicbusiness.service.linkage;

import cn.cheers.x.framework.tenant.core.service.TenantFrameworkService;
import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import cn.cheers.x.module.dynamicbusiness.service.event.MqttChannelConnectOptions;
import cn.cheers.x.module.dynamicbusiness.service.event.PlatformEventChannelTopics;
import cn.cheers.x.module.dynamicbusiness.service.push.PushCatalogQueryService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 联动作为独立订户去订消息通道。
 *
 * <p>管什么：按告警事件主题已经写下的名字规则订阅，收到后再交给联动入口。</p>
 * <p>不管什么：不发布事件，不判断告警是否成立，不让告警包直接调用联动。</p>
 */
@Component
@ConditionalOnProperty(prefix = "dynamicbusiness.event-channel", name = "enabled", havingValue = "true")
@Slf4j
public class LinkageEventChannelSubscriber implements MqttCallbackExtended {

    private static final int AT_LEAST_ONCE = 1;

    private final TenantFrameworkService tenantFrameworkService;
    private final PushCatalogQueryService pushCatalogQueryService;
    private final LinkageEventChannelConsumer consumer;
    private final String broker;
    private final String clientId;
    private final String username;
    private final String password;
    private final Set<String> subscribed = new LinkedHashSet<>();
    private MqttClient client;

    public LinkageEventChannelSubscriber(
            TenantFrameworkService tenantFrameworkService,
            PushCatalogQueryService pushCatalogQueryService,
            LinkageEventChannelConsumer consumer,
            @Value("${dynamicbusiness.event-channel.broker:tcp://127.0.0.1:1883}") String broker,
            @Value("${dynamicbusiness.event-channel.subscriber-client-id:linkage-event-subscriber}") String clientId,
            @Value("${dynamicbusiness.event-channel.username:platform:event}") String username,
            @Value("${dynamicbusiness.event-channel.password:}") String password) {
        this.tenantFrameworkService = tenantFrameworkService;
        this.pushCatalogQueryService = pushCatalogQueryService;
        this.consumer = consumer;
        this.broker = broker;
        this.clientId = clientId;
        this.username = username;
        this.password = password;
    }

    @PostConstruct
    public void start() {
        refreshSubscriptions();
    }

    @Scheduled(fixedDelayString = "${dynamicbusiness.event-channel.subscribe-delay-ms:10000}")
    public void refreshSubscriptions() {
        try {
            subscribe(currentFilters());
        } catch (RuntimeException ex) {
            log.error("联动没有订上事件通道：{}", ex.getMessage());
        }
    }

    @Override
    public void connectComplete(boolean reconnect, String serverUri) {
        subscribed.clear();
        refreshSubscriptions();
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.warn("联动事件通道断开：{}", cause == null ? "未知原因" : cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        byte[] payload = message == null ? null : message.getPayload();
        consumer.accept(payload);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // 联动只订不发。
    }

    @PreDestroy
    public synchronized void close() {
        if (client == null) {
            return;
        }
        try {
            client.disconnect();
            client.close();
        } catch (MqttException ignored) {
            // 关闭失败不挡住进程退出。
        }
        client = null;
        subscribed.clear();
    }

    private Set<String> currentFilters() {
        Set<String> filters = new LinkedHashSet<>();
        filters.add(PlatformEventChannelTopics.ACTION_STATUS);
        List<Long> tenantIds = tenantFrameworkService.getTenantIds();
        if (tenantIds == null) {
            return filters;
        }
        for (Long tenantId : tenantIds) {
            if (tenantId == null || tenantId <= 0) {
                continue;
            }
            TenantUtils.execute(tenantId, () -> addAlarmFilters(filters));
        }
        return filters;
    }

    private void addAlarmFilters(Set<String> filters) {
        for (String pattern : pushCatalogQueryService.alarmEventNamePatterns()) {
            String filter = PlatformEventChannelTopics.mqttFilter(pattern);
            if (filter != null) {
                filters.add(filter);
            }
        }
    }

    private synchronized void subscribe(Set<String> filters) {
        try {
            MqttClient current = connectedClient();
            for (String filter : filters) {
                if (subscribed.add(filter)) {
                    current.subscribe(filter, AT_LEAST_ONCE);
                }
            }
        } catch (MqttException ex) {
            throw new IllegalStateException("联动没有订上事件通道：" + ex.getMessage(), ex);
        }
    }

    private synchronized MqttClient connectedClient() throws MqttException {
        if (client != null && client.isConnected()) {
            return client;
        }
        client = new MqttClient(broker, clientId, new MemoryPersistence());
        client.setCallback(this);
        client.connect(MqttChannelConnectOptions.require(username, password, false));
        return client;
    }
}
