package cn.iocoder.cloud.yudao.module.scene.platform.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 订阅管理器 - 管理前端对数据通道的订阅关系
 * 
 * 核心职责：
 * 1. 记录哪些 WebSocket 会话订阅了哪些数据通道
 * 2. 提供按通道查询订阅会话的能力
 * 3. 提供按会话查询订阅通道的能力
 */
@Slf4j
@Component
public class SubscriptionManager {

    /**
     * 通道 -> 会话集合
     */
    private final ConcurrentHashMap<String, CopyOnWriteArraySet<String>> channelSubscriptions = new ConcurrentHashMap<>();

    /**
     * 会话 -> 通道集合
     */
    private final ConcurrentHashMap<String, CopyOnWriteArraySet<String>> sessionChannels = new ConcurrentHashMap<>();

    /**
     * 数据通道常量
     */
    public static final String CHANNEL_TELEMETRY = "telemetry";    // 仪表/阀门数据
    public static final String CHANNEL_TRANSFORM = "transform";     // 巡检设备位置
    public static final String CHANNEL_ALARM = "alarm";             // 告警数据

    /**
     * 注册订阅 - 前端连接时调用
     * 
     * @param sessionId WebSocket 会话 ID
     * @param channels 订阅的通道列表
     */
    public void subscribe(String sessionId, Set<String> channels) {
        if (channels == null || channels.isEmpty()) {
            log.warn("订阅请求为空，sessionId={}", sessionId);
            return;
        }

        log.info("订阅数据通道: sessionId={}, channels={}", sessionId, channels);

        for (String channel : channels) {
            // 通道 -> 会话
            channelSubscriptions
                    .computeIfAbsent(channel, k -> new CopyOnWriteArraySet<>())
                    .add(sessionId);
            
            // 会话 -> 通道
            sessionChannels
                    .computeIfAbsent(sessionId, k -> new CopyOnWriteArraySet<>())
                    .add(channel);
        }
    }

    /**
     * 取消订阅 - 前端断开连接时调用
     * 
     * @param sessionId WebSocket 会话 ID
     */
    public void unsubscribe(String sessionId) {
        Set<String> channels = sessionChannels.remove(sessionId);
        if (channels == null || channels.isEmpty()) {
            return;
        }

        log.info("取消订阅所有通道: sessionId={}, channels={}", sessionId, channels);

        for (String channel : channels) {
            CopyOnWriteArraySet<String> sessions = channelSubscriptions.get(channel);
            if (sessions != null) {
                sessions.remove(sessionId);
                if (sessions.isEmpty()) {
                    channelSubscriptions.remove(channel);
                }
            }
        }
    }

    /**
     * 取消特定通道的订阅
     * 
     * @param sessionId WebSocket 会话 ID
     * @param channel 通道名称
     */
    public void unsubscribeChannel(String sessionId, String channel) {
        Set<String> channels = sessionChannels.get(sessionId);
        if (channels == null || !channels.remove(channel)) {
            return;
        }

        CopyOnWriteArraySet<String> sessions = channelSubscriptions.get(channel);
        if (sessions != null) {
            sessions.remove(sessionId);
            if (sessions.isEmpty()) {
                channelSubscriptions.remove(channel);
            }
        }

        if (channels.isEmpty()) {
            sessionChannels.remove(sessionId);
        }

        log.debug("取消订阅通道: sessionId={}, channel={}", sessionId, channel);
    }

    /**
     * 获取订阅了指定通道的所有会话
     * 
     * @param channel 通道名称
     * @return 会话 ID 集合
     */
    public Set<String> getSubscribedSessions(String channel) {
        CopyOnWriteArraySet<String> sessions = channelSubscriptions.get(channel);
        return sessions != null ? Collections.unmodifiableSet(sessions) : Collections.emptySet();
    }

    /**
     * 获取会话订阅的所有通道
     * 
     * @param sessionId WebSocket 会话 ID
     * @return 通道名称集合
     */
    public Set<String> getSessionChannels(String sessionId) {
        Set<String> channels = sessionChannels.get(sessionId);
        return channels != null ? Collections.unmodifiableSet(channels) : Collections.emptySet();
    }

    /**
     * 推送数据到指定会话
     * 
     * @param sessionId WebSocket 会话 ID
     * @param data 推送的数据
     */
    public void pushToSession(String sessionId, Object data) {
        // TODO: 实际通过 WebSocket 会话发送
        // 当前仅记录日志
        log.debug("推送数据到会话: sessionId={}, data=Frame{}", 
                sessionId, 
                data instanceof FrameAggregator.RuntimeFrame 
                        ? ((FrameAggregator.RuntimeFrame) data).getFrameId() 
                        : "unknown");
    }

    /**
     * 获取所有活跃会话
     */
    public Set<String> getAllSessions() {
        return Collections.unmodifiableSet(sessionChannels.keySet());
    }

    /**
     * 获取某通道的订阅数
     */
    public int getSubscriptionCount(String channel) {
        CopyOnWriteArraySet<String> sessions = channelSubscriptions.get(channel);
        return sessions != null ? sessions.size() : 0;
    }

    /**
     * 清空所有订阅（系统关闭时调用）
     */
    public void clear() {
        channelSubscriptions.clear();
        sessionChannels.clear();
        log.info("所有订阅已清空");
    }
}
