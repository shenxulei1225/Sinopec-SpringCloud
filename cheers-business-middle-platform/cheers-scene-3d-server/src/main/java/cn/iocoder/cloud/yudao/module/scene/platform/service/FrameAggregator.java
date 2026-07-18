package cn.iocoder.cloud.yudao.module.scene.platform.service;

import cn.iocoder.yudao.module.scene.platform.model.RuntimePacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 帧聚合器 - 核心组件
 * 
 * 职责：
 * 1. 收集每个数据通道 (telemetry/transform/alarm) 的变化
 * 2. 按帧周期 (16ms) 聚合变化
 * 3. 批量推送到 WebSocket
 * 
 * 这是"实时同步引擎"的核心，不是"消息系统"
 */
@Slf4j
@Service
public class FrameAggregator {

    /**
     * 帧周期（毫秒）- 60fps = 16.67ms
     */
    @Value("${scene.platform.frame-period:16}")
    private long framePeriodMs;

    /**
     * 实体状态存储 - 记录最新状态用于 Diff 检测
     */
    private final Map<String, EntityState> entityStateStore = new ConcurrentHashMap<>();

    /**
     * 当前帧的变化收集
     */
    private final Map<String, List<EntityChange>> currentFrameChanges = new ConcurrentHashMap<>();

    /**
     * 帧计数器
     */
    private volatile long frameCounter = 0;

    /**
     * 上一次推送的帧 ID（用于前端帧版本过滤）
     */
    private volatile long lastPushedFrameId = 0;

    /**
     * 订阅管理器
     */
    private final SubscriptionManager subscriptionManager;

    public FrameAggregator(SubscriptionManager subscriptionManager) {
        this.subscriptionManager = subscriptionManager;
        log.info("帧聚合器初始化完成，帧周期：{}ms", framePeriodMs);
    }

    /**
     * 接收 NATS 数据变化
     * 
     * @param packet NATS 传来的 RuntimePacket
     */
    public void onEntityChange(RuntimePacket packet) {
        String entityId = packet.getInstanceCode();
        String channel = resolveChannel(packet);
        
        // 1. 更新实体状态
        EntityState state = entityStateStore.computeIfAbsent(entityId, k -> new EntityState());
        state.setLastUpdateTime(Instant.now());
        state.setChannel(channel);
        
        // 2. 检测变化
        EntityChange change = detectChange(entityId, packet, state);
        if (change == null) {
            return; // 没有变化，不收集
        }
        
        // 3. 加入当前帧
        currentFrameChanges
                .computeIfAbsent(channel, k -> new CopyOnWriteArrayList<>())
                .add(change);
        
        log.debug("收集变化: entityId={}, channel={}, type={}", 
                entityId, channel, change.getChangeType());
    }

    /**
     * 帧聚合触发 - 定时调用（每 16ms）
     */
    public void onFrameTick() {
        frameCounter++;
        long currentFrameId = frameCounter;
        
        // 按通道处理变化
        for (Map.Entry<String, List<EntityChange>> entry : currentFrameChanges.entrySet()) {
            String channel = entry.getKey();
            List<EntityChange> changes = entry.getValue();
            
            if (changes.isEmpty()) {
                continue;
            }
            
            // 4. 批量推送
            pushToWebSocket(channel, changes, currentFrameId);
        }
        
        // 5. 清空当前帧
        currentFrameChanges.clear();
        
        log.debug("帧 {} 推送完成，共 {} 个通道", currentFrameId, currentFrameChanges.size());
    }

    /**
     * 解析数据通道类型
     */
    private String resolveChannel(RuntimePacket packet) {
        // 根据 actorType 或 payload 判断通道
        String actorType = packet.getActorType();
        if (actorType == null) {
            return "unknown";
        }
        
        // 默认映射规则（可配置化）
        if (actorType.equals("meter") || actorType.equals("valve") || actorType.equals("pipe")) {
            return "telemetry";
        } else if (actorType.equals("rover") || actorType.equals("drone")) {
            return "transform";
        } else if (actorType.equals("alarm")) {
            return "alarm";
        }
        
        return "telemetry"; // 默认
    }

    /**
     * 变化检测 - 只收集有变化的实体
     */
    private EntityChange detectChange(String entityId, RuntimePacket packet, EntityState state) {
        if (state.getLastPacket() == null) {
            // 首次推送
            return new EntityChange(entityId, packet, "INIT");
        }
        
        // TODO: 实现具体的 Diff 检测逻辑
        // 例如：数值变化超过阈值、位置变化超过阈值等
        // 这里简化处理，假设所有数据都需要推送
        
        return new EntityChange(entityId, packet, "UPDATE");
    }

    /**
     * 批量推送到 WebSocket
     */
    private void pushToWebSocket(String channel, List<EntityChange> changes, long frameId) {
        // 获取订阅了该通道的前端会话
        Set<String> subscribedSessions = subscriptionManager.getSubscribedSessions(channel);
        
        if (subscribedSessions.isEmpty()) {
            log.debug("通道 {} 没有订阅者，跳过推送", channel);
            return;
        }
        
        // 构建 RuntimeFrame
        RuntimeFrame runtimeFrame = new RuntimeFrame();
        runtimeFrame.setFrameId(frameId);
        runtimeFrame.setTimestamp(System.currentTimeMillis());
        runtimeFrame.setChannel(channel);
        runtimeFrame.setChanges(changes);
        
        // 推送到所有订阅者
        for (String sessionId : subscribedSessions) {
            subscriptionManager.pushToSession(sessionId, runtimeFrame);
        }
        
        lastPushedFrameId = frameId;
        log.info("推送帧 {} 到 {} 个会话，通道：{}，变化数：{}", 
                frameId, subscribedSessions.size(), channel, changes.size());
    }

    /**
     * 实体状态 - 用于 Diff 检测
     */
    public static class EntityState {
        private RuntimePacket lastPacket;
        private Instant lastUpdateTime;
        private String channel;

        public RuntimePacket getLastPacket() {
            return lastPacket;
        }

        public void setLastPacket(RuntimePacket lastPacket) {
            this.lastPacket = lastPacket;
        }

        public Instant getLastUpdateTime() {
            return lastUpdateTime;
        }

        public void setLastUpdateTime(Instant lastUpdateTime) {
            this.lastUpdateTime = lastUpdateTime;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }
    }

    /**
     * 实体变化 - 单个实体的变化
     */
    public static class EntityChange {
        private String entityId;
        private RuntimePacket packet;
        private String changeType; // INIT, UPDATE, DELETE

        public EntityChange(String entityId, RuntimePacket packet, String changeType) {
            this.entityId = entityId;
            this.packet = packet;
            this.changeType = changeType;
        }

        public String getEntityId() {
            return entityId;
        }

        public RuntimePacket getPacket() {
            return packet;
        }

        public String getChangeType() {
            return changeType;
        }
    }

    /**
     * RuntimeFrame - 帧数据结构
     */
    public static class RuntimeFrame {
        private Long frameId;
        private Long timestamp;
        private String channel;
        private List<EntityChange> changes;

        public Long getFrameId() {
            return frameId;
        }

        public void setFrameId(Long frameId) {
            this.frameId = frameId;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public List<EntityChange> getChanges() {
            return changes;
        }

        public void setChanges(List<EntityChange> changes) {
            this.changes = changes;
        }
    }
}
