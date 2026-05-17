package cn.iocoder.yudao.module.scene.platform.websocket;

import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.ActorRuntimeRespVO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorInstanceDO;
import cn.iocoder.yudao.module.scene.platform.model.RuntimePacket;
import cn.iocoder.yudao.module.scene.platform.service.scene.DataDispatcherService;
import cn.iocoder.yudao.module.scene.platform.service.scene.SceneRuntimeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Actor 运行时 WebSocket 推送服务
 * 核心职责：
 * 1. 定时推送 Redis 中 Actor 运行时数据给 UE 前端
 * 2. 处理前端连接/断开
 * 3. 管理 WebSocket 会话
 *
 * @author Sinopec
 */
@Slf4j
@Component
@ServerEndpoint("/ws/scene-actor/{sceneCode}/{userId}")
public class ActorInstanceWebSocketService {

    /**
     * 推送间隔（毫秒），默认 1 秒
     */
    private static final long PUSH_INTERVAL_MS = 1000;

    @Resource
    private SceneRuntimeService sceneRuntimeService;

    @Resource
    private ObjectMapper objectMapper;

    /**
     * WebSocket 会话管理: sceneCode:userKey -> Session
     */
    private final Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sceneCode") String sceneCode,
                       @PathParam("userId") String userId) {
        String userKey = sceneCode + ":" + userId;
        sessionMap.put(userKey, session);

        // 增加在线用户数
        sceneRuntimeService.updateSceneOnlineCount(sceneCode, 1);

        log.info("[WebSocket] 用户连接成功, sceneCode={}, userId={}, sessionId={}",
                sceneCode, userId, session.getId());

        // 发送欢迎消息
        sendWelcomeMessage(session, sceneCode, userId);
    }

    /**
     * 连接关闭调用
     */
    @OnClose
    public void onClose(Session session, @PathParam("sceneCode") String sceneCode,
                        @PathParam("userId") String userId) {
        String userKey = sceneCode + ":" + userId;
        sessionMap.remove(userKey);

        // 减少在线用户数
        sceneRuntimeService.updateSceneOnlineCount(sceneCode, -1);

        log.info("[WebSocket] 用户断开连接, sceneCode={}, userId={}, sessionId={}",
                sceneCode, userId, session.getId());
    }

    /**
     * 收到消息调用（暂时保留，用于未来扩展）
     */
    @OnMessage
    public void onMessage(String message, @PathParam("sceneCode") String sceneCode,
                          @PathParam("userId") String userId) {
        log.info("[WebSocket] 收到消息, sceneCode={}, userId={}, message={}",
                sceneCode, userId, message);
        // TODO: 处理前端上报的数据（如手动更新 Actor 位置）
    }

    /**
     * 连接错误调用
     */
    @OnError
    public void onError(Session session, Throwable error, @PathParam("sceneCode") String sceneCode) {
        log.error("[WebSocket] 连接错误, sceneCode={}, sessionId={}, error={}",
                sceneCode, session.getId(), error.getMessage(), error);
    }

    /**
     * 定时推送：每秒推送所有场景的 Actor 运行时数据
     */
    @Scheduled(fixedDelay = PUSH_INTERVAL_MS, initialDelay = PUSH_INTERVAL_MS)
    public void pushRuntimeUpdates() {
        try {
            // 遍历所有场景的在线用户
            for (Map.Entry<String, Session> entry : sessionMap.entrySet()) {
                String userKey = entry.getKey();
                Session session = entry.getValue();

                if (!session.isOpen()) {
                    // 会话已关闭，清理
                    sessionMap.remove(userKey);
                    continue;
                }

                String sceneCode = userKey.split(":")[0];
                pushSceneRuntimeData(session, sceneCode);
            }
        } catch (Exception e) {
            log.error("[WebSocket] 推送运行时数据失败", e);
        }
    }

    /**
     * 推送场景运行时数据给指定 Session
     */
    private void pushSceneRuntimeData(Session session, String sceneCode) {
        try {
            // 获取场景中所有 Actor 的运行时数据
            List<ActorRuntimeRespVO> actors = sceneRuntimeService.getAllActorsRuntime(sceneCode);

            if (actors == null || actors.isEmpty()) {
                return;
            }

            // 转换为 JSON 字符串
            String jsonMessage = objectMapper.writeValueAsString(actors);

            // 发送消息
            synchronized (session) {
                session.getBasicRemote().sendText(jsonMessage);
            }

            log.debug("[WebSocket] 推送运行时数据, sceneCode={}, actorCount={}",
                    sceneCode, actors.size());
        } catch (IOException e) {
            log.error("[WebSocket] 推送运行时数据失败, sceneCode={}", sceneCode, e);
        }
    }

    /**
     * 发送欢迎消息
     */
    private void sendWelcomeMessage(Session session, String sceneCode, String userId) {
        try {
            Map<String, Object> welcomeMsg = Map.of(
                    "type", "welcome",
                    "message", "连接成功",
                    "sceneCode", sceneCode,
                    "userId", userId
            );
            String jsonMessage = objectMapper.writeValueAsString(welcomeMsg);
            synchronized (session) {
                session.getBasicRemote().sendText(jsonMessage);
            }
        } catch (IOException e) {
            log.error("[WebSocket] 发送欢迎消息失败, sceneCode={}", sceneCode, e);
        }
    }

    /**
     * 手动推送（用于事件驱动更新，如前端拖拽 Actor）
     */
    public void pushUpdate(String sceneCode, ActorInstanceDO actorUpdate) {
        String userKeyPrefix = sceneCode + ":";
        for (Map.Entry<String, Session> entry : sessionMap.entrySet()) {
            if (entry.getKey().startsWith(userKeyPrefix)) {
                Session session = entry.getValue();
                if (session.isOpen()) {
                    try {
                        String jsonMessage = objectMapper.writeValueAsString(actorUpdate);
                        synchronized (session) {
                            session.getBasicRemote().sendText(jsonMessage);
                        }
                    } catch (IOException e) {
                        log.error("[WebSocket] 推送更新失败, sceneCode={}", sceneCode, e);
                    }
                }
            }
        }
    }

    /**
     * 获取在线用户数
     */
    public int getOnlineUsers(String sceneCode) {
        return (int) sessionMap.keySet().stream()
                .filter(key -> key.startsWith(sceneCode + ":"))
                .count();
    }

    /**
     * 获取所有在线用户数
     */
    public int getAllOnlineUsers() {
        return sessionMap.size();
    }

    /**
     * 广播 Actor 位置更新
     *
     * @param sceneCode    场景编码
     * @param instanceId   Actor 实例 ID
     * @param instanceCode Actor 实例编码
     * @param transform    变换信息（位置/旋转/缩放）
     */
    public void broadcastPositionUpdate(String sceneCode, Long instanceId,
            String instanceCode, cn.iocoder.yudao.module.scene.platform.model.Transform transform) {
        pushEvent(sceneCode, "positionUpdate", Map.of(
                "instanceId", instanceId,
                "instanceCode", instanceCode,
                "transform", transform
        ));
    }

    /**
     * 广播 Actor 可见性变更
     *
     * @param sceneCode    场景编码
     * @param instanceId   Actor 实例 ID
     * @param instanceCode Actor 实例编码
     * @param visible      是否可见
     */
    public void broadcastVisibilityChange(String sceneCode, Long instanceId,
            String instanceCode, Boolean visible) {
        pushEvent(sceneCode, "visibilityChange", Map.of(
                "instanceId", instanceId,
                "instanceCode", instanceCode,
                "visible", visible
        ));
    }

    /**
     * 推送事件到指定场景的所有在线客户端
     */
    private void pushEvent(String sceneCode, String eventType, Map<String, Object> eventData) {
        String userKeyPrefix = sceneCode + ":";
        try {
            Map<String, Object> msg = new java.util.HashMap<>();
            msg.put("type", eventType);
            msg.putAll(eventData);
            String jsonMessage = objectMapper.writeValueAsString(msg);

            for (Map.Entry<String, Session> entry : sessionMap.entrySet()) {
                if (entry.getKey().startsWith(userKeyPrefix) && entry.getValue().isOpen()) {
                    synchronized (entry.getValue()) {
                        entry.getValue().getBasicRemote().sendText(jsonMessage);
                    }
                }
            }
        } catch (IOException e) {
            log.error("[WebSocket] 推送事件失败, sceneCode={}, eventType={}", sceneCode, eventType, e);
        }
    }
}