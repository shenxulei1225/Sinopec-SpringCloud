package cn.cheers.x.alarm.service.websocket;

import cn.hutool.core.collection.CollUtil;
import cn.cheers.x.framework.common.enums.UserTypeEnum;
import cn.cheers.x.alarm.controller.admin.vo.websocket.AlarmWebSocketMessage;
import cn.cheers.x.alarm.enums.WebSocketMessageTypeConstants;
import cn.cheers.x.infra.api.websocket.WebSocketSenderApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 告警 WebSocket 批量推送服务
 * 
 * <p>用于优化高频告警场景下的 WebSocket 推送性能：
 * <ul>
 *   <li>消息聚合：将短时间内的多条消息合并为一次批量推送</li>
 *   <li>频率控制：限制推送频率，避免客户端过载</li>
 *   <li>异步处理：使用队列缓冲消息，避免阻塞业务线程</li>
 * </ul>
 * </p>
 *
 * @author 告警管理模块
 */
@Service
@Slf4j
public class AlarmWebSocketBatchService {

    /** 消息队列 */
    private final ConcurrentLinkedQueue<AlarmWebSocketMessage> messageQueue = new ConcurrentLinkedQueue<>();

    /** 批量推送大小阈值 */
    private static final int BATCH_SIZE_THRESHOLD = 10;

    /** 最大批量大小 */
    private static final int MAX_BATCH_SIZE = 50;

    /** 是否正在处理 */
    private final AtomicBoolean processing = new AtomicBoolean(false);

    @Resource
    private WebSocketSenderApi webSocketSenderApi;

    /**
     * 添加消息到队列
     * 
     * <p>消息会被缓冲，然后批量推送</p>
     *
     * @param message 告警消息
     */
    public void enqueueMessage(AlarmWebSocketMessage message) {
        if (message == null) {
            return;
        }
        messageQueue.offer(message);
        
        // 如果队列达到阈值，立即触发推送
        if (messageQueue.size() >= BATCH_SIZE_THRESHOLD) {
            processBatch();
        }
    }

    /**
     * 定时批量推送消息
     * 
     * <p>每500毫秒执行一次，将队列中的消息批量推送</p>
     */
    @Scheduled(fixedDelay = 500)
    public void scheduledBatchPush() {
        processBatch();
    }

    /**
     * 处理批量推送
     */
    private void processBatch() {
        // 防止并发处理
        if (!processing.compareAndSet(false, true)) {
            return;
        }
        
        try {
            List<AlarmWebSocketMessage> batch = new ArrayList<>();
            
            // 从队列中取出消息
            AlarmWebSocketMessage message;
            while ((message = messageQueue.poll()) != null && batch.size() < MAX_BATCH_SIZE) {
                batch.add(message);
            }
            
            if (CollUtil.isEmpty(batch)) {
                return;
            }
            
            // 批量推送
            if (batch.size() == 1) {
                // 单条消息直接推送
                pushSingleMessage(batch.get(0));
            } else {
                // 多条消息批量推送
                pushBatchMessages(batch);
            }
            
            log.debug("[processBatch][批量推送告警消息] count={}", batch.size());
            
        } catch (Exception e) {
            log.error("[processBatch][批量推送告警消息失败]", e);
        } finally {
            processing.set(false);
        }
    }

    /**
     * 推送单条消息
     */
    private void pushSingleMessage(AlarmWebSocketMessage message) {
        try {
            webSocketSenderApi.sendObject(UserTypeEnum.ADMIN.getValue(),
                    WebSocketMessageTypeConstants.ALARM_NEW, message);
        } catch (Exception e) {
            log.error("[pushSingleMessage][推送单条告警消息失败] alarmId={}", message.getId(), e);
        }
    }

    /**
     * 批量推送消息
     */
    private void pushBatchMessages(List<AlarmWebSocketMessage> messages) {
        try {
            // 使用批量消息类型推送
            webSocketSenderApi.sendObject(UserTypeEnum.ADMIN.getValue(),
                    WebSocketMessageTypeConstants.ALARM_BATCH, messages);
        } catch (Exception e) {
            log.error("[pushBatchMessages][批量推送告警消息失败] count={}", messages.size(), e);
        }
    }

    /**
     * 获取队列中待处理的消息数量
     *
     * @return 待处理消息数量
     */
    public int getPendingMessageCount() {
        return messageQueue.size();
    }

    /**
     * 清空消息队列
     * 
     * <p>用于系统维护或重置</p>
     */
    public void clearQueue() {
        messageQueue.clear();
        log.info("[clearQueue][清空告警消息队列]");
    }

}
