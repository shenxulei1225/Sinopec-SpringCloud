package cn.cheers.x.scene.platform.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Actor 实例 WebSocket 处理器
 * <p>
 * 支持前端通过 /ws/actor/{sceneCode} 连接，接收位置更新消息。
 */
@Component
@ServerEndpoint("/ws/actor/{sceneCode}")
public class ActorInstanceWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(ActorInstanceWebSocketHandler.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /** 心跳间隔 (毫秒) */
    private static final long HEARTBEAT_INTERVAL = 30000;

    /** 场景Code → 在线会话映射 */
    private static final ConcurrentHashMap<String, ConcurrentHashMap<Session, ActorInstanceWebSocketHandler>> ONLINE_SESSIONS = new ConcurrentHashMap<>();

    /** 会话 → 本实例映射 */
    private final ConcurrentHashMap<Session, ActorInstanceWebSocketHandler> sessionMap = new ConcurrentHashMap<>();

    /** 所属场景Code */
    private String sceneCode;

    /** 心跳定时器 */
    private jakarta.servlet.http.HttpSession httpSession;

    @OnOpen
    public void onOpen(Session session, @PathParam("sceneCode") String sceneCode) {
        this.sceneCode = sceneCode;
        this.sessionMap.put(session, this);

        ONLINE_SESSIONS
                .computeIfAbsent(sceneCode, k -> new ConcurrentHashMap<>())
                .put(session, this);

        log.info("[ActorInstanceWebSocketHandler] 场景[{}] 新连接: sessionId={}", sceneCode, session.getId());
        send(session, new WebSocketMessage<>(WebSocketMessage.TYPE_OPEN, "connected"));
    }

    @OnClose
    public void onClose(Session session) {
        this.sessionMap.remove(session);
        ConcurrentHashMap<Session, ActorInstanceWebSocketHandler> sceneSessions = ONLINE_SESSIONS.get(sceneCode);
        if (sceneSessions != null) {
            sceneSessions.remove(session);
            if (sceneSessions.isEmpty()) {
                ONLINE_SESSIONS.remove(sceneCode);
            }
        }
        log.info("[ActorInstanceWebSocketHandler] 场景[{}] 连接关闭: sessionId={}", sceneCode, session.getId());
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("[ActorInstanceWebSocketHandler] 场景[{}] WebSocket 错误: sessionId={}", sceneCode, session.getId(), error);
    }

    /**
     * 向场景所有在线客户端广播消息
     */
    public static void broadcast(String sceneCode, Object payload) {
        ConcurrentHashMap<Session, ActorInstanceWebSocketHandler> sessions = ONLINE_SESSIONS.get(sceneCode);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }
        for (Session session : sessions.keySet()) {
            send(session, new WebSocketMessage<>(WebSocketMessage.TYPE_DATA, payload));
        }
    }

    /**
     * 向指定会话发送消息
     */
    private static void send(Session session, Object msg) {
        if (session == null || !session.isOpen()) {
            return;
        }
        try {
            session.getBasicRemote().sendText(OBJECT_MAPPER.writeValueAsString(msg));
        } catch (IOException e) {
            LoggerFactory.getLogger(ActorInstanceWebSocketHandler.class)
                    .error("[ActorInstanceWebSocketHandler] 发送消息失败: sessionId={}", session.getId(), e);
        }
    }

    /**
     * 通用 WebSocket 消息封装
     */
    public static class WebSocketMessage<T> {

        public static final String TYPE_DATA = "data";
        public static final String TYPE_OPEN = "open";
        public static final String TYPE_HEARTBEAT = "heartbeat";
        public static final String TYPE_ERROR = "error";

        private String type;
        private T data;
        private Long timestamp;

        public WebSocketMessage() {
        }

        public WebSocketMessage(String type, T data) {
            this.type = type;
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
