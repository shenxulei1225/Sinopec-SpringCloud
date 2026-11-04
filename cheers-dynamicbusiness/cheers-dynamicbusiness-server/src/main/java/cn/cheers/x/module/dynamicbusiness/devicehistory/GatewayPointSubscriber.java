package cn.cheers.x.module.dynamicbusiness.devicehistory;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

/**
 * 把点位对照里的读点订到 FactoryTalk Linx Gateway。
 * 一条会话、一条订阅。节点必须是这次浏览对上的。
 * 解析后的样本按一秒一批交出去。不写历史，不读策略，不订写点。对不上就不订。
 * 同一次浏览对上的写点只记在这条会话上，供控制写入。不把写点放进订阅。
 */
@Component
public class GatewayPointSubscriber {

    private static final Logger log = LoggerFactory.getLogger(GatewayPointSubscriber.class);
    private static final int BATCH = 200;
    private static final long RETRY_MILLIS = 60_000L;
    private static final double PUBLISH_MILLIS = 1000.0d;

    private final GatewaySubscriptionProperties properties;
    private final GatewayPointCatalog catalog;
    private final DevicePointHistoryProperties historyProperties;
    private final PointSampleBatchReporter reporter;
    private final GatewaySessionWrites sessionWrites;
    private final ConcurrentLinkedQueue<ParsedPointSample> pending = new ConcurrentLinkedQueue<>();

    private volatile boolean running;
    private volatile OpcUaClient client;
    private ScheduledExecutorService flush;

    public GatewayPointSubscriber(GatewaySubscriptionProperties properties, GatewayPointCatalog catalog,
                                  DevicePointHistoryProperties historyProperties, PointSampleBatchReporter reporter,
                                  GatewaySessionWrites sessionWrites) {
        this.properties = properties;
        this.catalog = catalog;
        this.historyProperties = historyProperties;
        this.reporter = reporter;
        this.sessionWrites = sessionWrites;
    }

    /** 启动后在后台订。连不上就等一分钟再试。不挡住业务服务起来。 */
    @PostConstruct
    public void start() {
        if (!properties.isEnabled()) {
            log.info("点位订阅已关闭");
            return;
        }
        running = true;
        flush = Executors.newSingleThreadScheduledExecutor(task -> {
            Thread thread = new Thread(task, "point-gateway-flush");
            thread.setDaemon(true);
            return thread;
        });
        flush.scheduleWithFixedDelay(this::flushPending, 1, 1, TimeUnit.SECONDS);
        Thread thread = new Thread(this::loop, "point-gateway-subscribe");
        thread.setDaemon(true);
        thread.start();
    }

    @PreDestroy
    public void stop() {
        running = false;
        flushPending();
        if (flush != null) {
            flush.shutdown();
        }
        disconnect();
    }

    private void loop() {
        while (running) {
            try {
                subscribeOnce();
                watchSession();
            } catch (Exception ex) {
                log.warn("点位还没订上 Gateway：{}", ex.getMessage());
            } finally {
                disconnect();
            }
            sleep();
        }
    }

    private void subscribeOnce() throws Exception {
        if (!properties.hasServerUrl()) {
            throw new IllegalStateException("没有 Gateway 地址");
        }
        if (!historyProperties.isReady()) {
            throw new IllegalStateException("历史库未配置");
        }
        List<GatewayPointRef> points = catalog.loadReadPoints();
        if (points.isEmpty()) {
            throw new IllegalStateException("点位对照里没有读点");
        }
        List<GatewayPointRef> writes = catalog.loadWritePoints();
        OpcUaClient session = OpcUaClient.create(properties.getServerUrl());
        session.connect().get(15, TimeUnit.SECONDS);
        client = session;
        List<GatewaySeenNode> seen = GatewayAddressWalker.walk(session);
        Map<String, GatewayPointRef> matched = GatewayBrowseMatcher.match(points, seen);
        Map<String, GatewayPointRef> writeMatched = GatewayBrowseMatcher.match(writes, seen);
        log.info("读点 {} 个，浏览对上 {} 个", points.size(), matched.size());
        log.info("写点 {} 个，浏览对上 {} 个，这些不放进订阅", writes.size(), writeMatched.size());
        if (matched.isEmpty()) {
            throw new IllegalStateException("浏览没有对上任何读点");
        }
        sessionWrites.attach(session, writeMatched);
        UaSubscription subscription = session.getSubscriptionManager().createSubscription(PUBLISH_MILLIS).get(15, TimeUnit.SECONDS);
        createItems(subscription, matched);
        log.info("已向 Gateway 订上 {} 个读点", matched.size());
    }

    private void createItems(UaSubscription subscription, Map<String, GatewayPointRef> matched) throws Exception {
        List<Map.Entry<String, GatewayPointRef>> rows = List.copyOf(matched.entrySet());
        AtomicInteger handle = new AtomicInteger(1);
        for (int from = 0; from < rows.size(); from += BATCH) {
            List<Map.Entry<String, GatewayPointRef>> batch = rows.subList(from, Math.min(from + BATCH, rows.size()));
            List<MonitoredItemCreateRequest> requests = new ArrayList<>();
            for (Map.Entry<String, GatewayPointRef> row : batch) {
                requests.add(request(row.getKey(), handle.getAndIncrement()));
            }
            List<UaMonitoredItem> items = subscription.createMonitoredItems(
                    TimestampsToReturn.Both, requests, (item, index) -> bind(item, matched)).get(30, TimeUnit.SECONDS);
            logBad(items);
        }
    }

    private static MonitoredItemCreateRequest request(String nodeId, int handle) {
        ReadValueId read = new ReadValueId(NodeId.parse(nodeId), AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE);
        MonitoringParameters parameters = new MonitoringParameters(uint(handle), PUBLISH_MILLIS, null, uint(10), true);
        return new MonitoredItemCreateRequest(read, MonitoringMode.Reporting, parameters);
    }

    private void bind(UaMonitoredItem item, Map<String, GatewayPointRef> matched) {
        StatusCode status = item.getStatusCode();
        if (status != null && !status.isGood()) {
            return;
        }
        String nodeId = item.getReadValueId().getNodeId().toParseableString();
        GatewayPointRef point = matched.get(nodeId);
        if (point == null) {
            log.warn("监视项对不上点位对照，不记历史：{}", nodeId);
            return;
        }
        item.setValueConsumer(value -> accept(point, value));
    }

    private void accept(GatewayPointRef point, DataValue value) {
        Double engineering = GatewaySampleValue.engineering(value.getValue() == null ? null : value.getValue().getValue());
        if (engineering == null) {
            log.warn("点位 {} 这次没有工程值，不记历史", point.getBindingCode());
            return;
        }
        String quality = value.getStatusCode() != null && value.getStatusCode().isGood()
                ? DevicePointHistoryRules.QUALITY_NORMAL
                : DevicePointHistoryRules.QUALITY_ABNORMAL;
        pending.add(new ParsedPointSample(
                point.getTenantId(),
                point.getDeviceId(),
                point.getPointCode(),
                point.getBindingCode(),
                point.getZoneId(),
                sampledAt(value),
                engineering,
                quality));
    }

    /** 这一秒收到的样本一次交出。空的不交。失败留在日志里，不在这里分类。 */
    private void flushPending() {
        List<ParsedPointSample> batch = new ArrayList<>();
        ParsedPointSample sample;
        while ((sample = pending.poll()) != null) {
            batch.add(sample);
        }
        if (batch.isEmpty()) {
            return;
        }
        try {
            reporter.accept(batch);
        } catch (RuntimeException ex) {
            log.warn("这一批 {} 条读数没有交出去：{}", batch.size(), ex.getMessage());
        }
    }

    private static Instant sampledAt(DataValue value) {
        Instant source = instant(value.getSourceTime());
        if (source != null) {
            return source;
        }
        Instant server = instant(value.getServerTime());
        return server == null ? Instant.now() : server;
    }

    private static Instant instant(DateTime time) {
        if (time == null || time.isNull()) {
            return null;
        }
        return time.getJavaInstant();
    }

    private static void logBad(List<UaMonitoredItem> items) {
        for (UaMonitoredItem item : items) {
            StatusCode status = item.getStatusCode();
            if (status != null && !status.isGood()) {
                log.warn("Gateway 没有收下这个点：{} {}", item.getReadValueId().getNodeId(), status);
            }
        }
    }

    private void watchSession() throws InterruptedException {
        while (running && sessionOpen()) {
            Thread.sleep(15_000L);
        }
    }

    private boolean sessionOpen() {
        OpcUaClient session = client;
        if (session == null) {
            return false;
        }
        try {
            return session.getSession().get(2, TimeUnit.SECONDS) != null;
        } catch (Exception ex) {
            return false;
        }
    }

    private void disconnect() {
        sessionWrites.detach();
        OpcUaClient session = client;
        client = null;
        if (session == null) {
            return;
        }
        try {
            session.disconnect().get(5, TimeUnit.SECONDS);
        } catch (Exception ex) {
            log.warn("断开 Gateway 时出错：{}", ex.getMessage());
        }
    }

    private void sleep() {
        try {
            Thread.sleep(RETRY_MILLIS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            running = false;
        }
    }
}
