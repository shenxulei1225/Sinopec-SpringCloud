package cn.iocoder.yudao.module.scene.platform.service.scene;

import cn.iocoder.yudao.module.scene.platform.model.RuntimePacket;
import cn.iocoder.yudao.module.scene.platform.websocket.ActorInstanceWebSocketService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据分发服务 - 按 Actor 类型分发数据到 WebSocket
 * 核心职责：
 * 1. 接收 NATS 消息
 * 2. 通过变化检测引擎判断是否推送
 * 3. 按 Actor 类型分发到对应的 WebSocket 连接
 */
@Slf4j
@Service
public class DataDispatcherService {

    /**
     * 在线客户端订阅信息
     * key: sceneCode:userId
     * value: 订阅的 Actor 类型列表
     */
    private final Map<String, List<String>> subscriptions = new ConcurrentHashMap<>();

    /**
     * 在线客户端 Actor 订阅映射
     * key: sceneCode:userId
     * value: 订阅的 Actor 实例 ID 列表
     */
    private final Map<String, List<Long>> actorSubscriptions = new ConcurrentHashMap<>();

    @Resource
    private ChangeDetectionEngine changeDetectionEngine;

    @Resource
    private ActorInstanceWebSocketService webSocketService;

    /**
     * 处理运行时数据包
     * 由 NATS 消费者或 WebSocket Gateway 调用
     *
     * @param packet 运行时数据包
     */
    public void handleRuntimePacket(RuntimePacket packet) {
        if (!changeDetectionEngine.shouldPush(packet)) {
            return; // 变化未超过阈值，跳过推送
        }

        RuntimePacket triggeredPacket = RuntimePacket.builder()
                .type(packet.getType())
                .sceneCode(packet.getSceneCode())
                .instanceId(packet.getInstanceId())
                .instanceCode(packet.getInstanceCode())
                .actorType(packet.getActorType())
                .metric(packet.getMetric())
                .value(packet.getValue())
                .transform(packet.getTransform())
                .extra(packet.getExtra())
                .version(packet.getVersion())
                .timestamp(System.currentTimeMillis())
                .triggered(true)
                .build();

        dispatchByActorType(triggeredPacket);
    }

    private void dispatchByActorType(RuntimePacket packet) {
        String sceneCode = packet.getSceneCode();

        switch (packet.getActorType()) {
            case "gauge":
                pushToScene(sceneCode, packet);
                log.debug("[DataDispatcher] 推送仪表数据, sceneCode={}, instanceId={}, value={}",
                        sceneCode, packet.getInstanceId(), packet.getValue());
                break;

            case "inspector":
                pushToScene(sceneCode, packet);
                log.debug("[DataDispatcher] 推送巡检设备位置, sceneCode={}, instanceId={}, position=({},{},{})",
                        sceneCode, packet.getInstanceId(),
                        packet.getTransform() != null && packet.getTransform().getLocation() != null ? packet.getTransform().getLocation().getX() : 0,
                        packet.getTransform() != null && packet.getTransform().getLocation() != null ? packet.getTransform().getLocation().getY() : 0,
                        packet.getTransform() != null && packet.getTransform().getLocation() != null ? packet.getTransform().getLocation().getZ() : 0);
                break;

            case "valve":
            case "pipe":
                pushToScene(sceneCode, packet);
                log.debug("[DataDispatcher] 推送{}数据, sceneCode={}, instanceId={}, metric={}, value={}",
                        packet.getActorType(), sceneCode, packet.getInstanceId(),
                        packet.getMetric(), packet.getValue());
                break;

            default:
                log.warn("[DataDispatcher] 未知 Actor 类型，跳过分发, actorType={}", packet.getActorType());
                break;
        }
    }

    private void pushToScene(String sceneCode, RuntimePacket packet) {
        // TODO: 转换为统一 runtime 推送格式并下发到 WebSocket
        log.debug("[DataDispatcher] 推送数据到场景, sceneCode={}, instanceId={}", sceneCode, packet.getInstanceId());
    }

    public void subscribeActorType(String sessionKey, String actorType) {
        List<String> currentTypes = subscriptions.computeIfAbsent(sessionKey, k -> new java.util.ArrayList<>());
        if (!currentTypes.contains(actorType)) {
            currentTypes.add(actorType);
            log.info("[DataDispatcher] 客户端订阅 Actor 类型, sessionKey={}, actorType={}",
                    sessionKey, actorType);
        }
    }

    public void subscribeActor(String sessionKey, Long instanceId) {
        List<Long> currentIds = actorSubscriptions.computeIfAbsent(sessionKey, k -> new java.util.ArrayList<>());
        if (!currentIds.contains(instanceId)) {
            currentIds.add(instanceId);
            log.info("[DataDispatcher] 客户端订阅 Actor 实例, sessionKey={}, instanceId={}",
                    sessionKey, instanceId);
        }
    }

    public void unsubscribeActorType(String sessionKey, String actorType) {
        List<String> currentTypes = subscriptions.get(sessionKey);
        if (currentTypes != null) {
            currentTypes.remove(actorType);
            log.info("[DataDispatcher] 客户端取消订阅 Actor 类型, sessionKey={}, actorType={}",
                    sessionKey, actorType);
        }
    }

    public void unsubscribeActor(String sessionKey) {
        actorSubscriptions.remove(sessionKey);
        log.info("[DataDispatcher] 客户端取消订阅 Actor 实例, sessionKey={}", sessionKey);
    }

    public List<String> getSubscribedActorTypes(String sessionKey) {
        return subscriptions.getOrDefault(sessionKey, List.of());
    }

    public List<Long> getSubscribedActors(String sessionKey) {
        return actorSubscriptions.getOrDefault(sessionKey, List.of());
    }

    public void clearSubscriptions(String sessionKey) {
        subscriptions.remove(sessionKey);
        actorSubscriptions.remove(sessionKey);
        log.info("[DataDispatcher] 清除客户端订阅信息, sessionKey={}", sessionKey);
    }
}
