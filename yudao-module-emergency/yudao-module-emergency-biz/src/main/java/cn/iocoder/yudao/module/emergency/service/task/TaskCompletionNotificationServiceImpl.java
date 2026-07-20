package cn.iocoder.yudao.module.emergency.service.task;

import cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 任务完成通知 Service 实现类
 * 
 * 实现任务完成时的多渠道通知机制
 * 
 * @author 应急管理系统
 */
@Slf4j
@Service
public class TaskCompletionNotificationServiceImpl implements TaskCompletionNotificationService {

    @Override
    public void sendCompletionNotification(EmergencyTaskDO task) {
        String message = String.format("任务【%s】已完成", task.getTitle());
        sendCompletionNotification(task, message);
    }

    @Override
    public void sendCompletionNotification(EmergencyTaskDO task, String message) {
        try {
            log.info("发送任务完成通知: taskId={}, taskCode={}, title={}", 
                    task.getId(), task.getTaskCode(), task.getTitle());

            // 1. 获取通知接收人列表
            List<Map<String, Object>> receivers = getNotificationReceivers(task);
            
            if (receivers.isEmpty()) {
                log.warn("任务完成通知：未找到接收人, taskId={}", task.getId());
                return;
            }

            // 2. 构建通知内容
            String content = buildNotificationContent(task, message);

            // 3. 发送系统通知（站内信、WebSocket推送）
            sendSystemNotification(receivers, content, task);

            // 4. 发送短信通知（可选，根据配置）
            // sendSmsNotification(receivers, content, task);

            // 5. 发送邮件通知（可选，根据配置）
            // sendEmailNotification(receivers, content, task);

            log.info("任务完成通知发送成功: taskId={}, receivers={}", task.getId(), receivers.size());
        } catch (Exception e) {
            log.error("发送任务完成通知失败: taskId={}", task.getId(), e);
            // 通知失败不应影响任务完成流程，只记录日志
        }
    }

    /**
     * 获取通知接收人列表
     * 
     * @param task 任务
     * @return 接收人列表，每个元素包含userId、userName等信息
     */
    private List<Map<String, Object>> getNotificationReceivers(EmergencyTaskDO task) {
        List<Map<String, Object>> receivers = new ArrayList<>();

        // 1. 任务分配人（assignee字段）
        if (task.getAssignee() != null && !task.getAssignee().isEmpty()) {
            // assignee字段可能存储用户ID或用户名，这里简化处理
            Map<String, Object> assignee = Map.of(
                    "userId", task.getAssignee(), // 假设assignee存储的是用户ID
                    "userName", task.getAssignee(),
                    "role", "assignee"
            );
            receivers.add(assignee);
        }

        // 3. 事件负责人（通过事件ID查询，这里简化处理）
        // TODO: 实际实现中应通过事件服务查询事件负责人
        if (task.getEventId() != null) {
            // 示例：假设事件负责人ID为1（实际应从事件表查询）
            // Map<String, Object> eventManager = Map.of(
            //         "userId", eventManagerId,
            //         "userName", "事件负责人",
            //         "role", "event_manager"
            // );
            // receivers.add(eventManager);
        }

        // 4. 响应负责人（通过响应ID查询，这里简化处理）
        // TODO: 实际实现中应通过响应服务查询响应负责人
        if (task.getResponseId() != null) {
            // 示例：假设响应负责人ID为1（实际应从响应表查询）
            // Map<String, Object> responseManager = Map.of(
            //         "userId", responseManagerId,
            //         "userName", "响应负责人",
            //         "role", "response_manager"
            // );
            // receivers.add(responseManager);
        }

        return receivers;
    }

    /**
     * 构建通知内容
     * 
     * @param task 任务
     * @param message 自定义消息
     * @return 通知内容
     */
    private String buildNotificationContent(EmergencyTaskDO task, String message) {
        StringBuilder content = new StringBuilder();
        content.append("【任务完成通知】\n");
        content.append("任务编号：").append(task.getTaskCode()).append("\n");
        content.append("任务标题：").append(task.getTitle()).append("\n");
        if (task.getCompleteTime() != null) {
            content.append("完成时间：").append(task.getCompleteTime()).append("\n");
        }
        content.append("通知内容：").append(message);
        return content.toString();
    }

    /**
     * 发送系统通知（站内信、WebSocket推送）
     * 
     * @param receivers 接收人列表
     * @param content 通知内容
     * @param task 任务信息
     */
    private void sendSystemNotification(List<Map<String, Object>> receivers, String content, EmergencyTaskDO task) {
        log.info("发送系统通知: taskId={}, receivers={}, content={}", 
                task.getId(), receivers.size(), content);
        
        // TODO: 集成系统通知服务
        // 1. 站内信通知
        // systemNotificationService.sendInboxMessage(receivers, content);
        
        // 2. WebSocket实时推送
        // webSocketService.pushNotification(receivers, content);
        
        // 3. 系统消息中心
        // messageCenterService.addMessage(receivers, "任务完成", content);
        
        // 示例实现（实际应调用具体的通知服务）
        for (Map<String, Object> receiver : receivers) {
            log.debug("发送系统通知给用户: userId={}, userName={}, content={}", 
                    receiver.get("userId"), receiver.get("userName"), content);
        }
    }

    /**
     * 发送短信通知（可选）
     * 
     * @param receivers 接收人列表
     * @param content 通知内容
     * @param task 任务信息
     */
    private void sendSmsNotification(List<Map<String, Object>> receivers, String content, EmergencyTaskDO task) {
        log.info("发送短信通知: taskId={}, receivers={}", task.getId(), receivers.size());
        
        // TODO: 集成短信服务
        // smsService.sendBatch(receivers, content);
        
        // 示例实现（实际应调用具体的短信服务）
        for (Map<String, Object> receiver : receivers) {
            log.debug("发送短信通知给用户: userId={}, content={}", 
                    receiver.get("userId"), content);
        }
    }

    /**
     * 发送邮件通知（可选）
     * 
     * @param receivers 接收人列表
     * @param content 通知内容
     * @param task 任务信息
     */
    private void sendEmailNotification(List<Map<String, Object>> receivers, String content, EmergencyTaskDO task) {
        log.info("发送邮件通知: taskId={}, receivers={}", task.getId(), receivers.size());
        
        // TODO: 集成邮件服务
        // emailService.sendBatch(receivers, "任务完成通知", content);
        
        // 示例实现（实际应调用具体的邮件服务）
        for (Map<String, Object> receiver : receivers) {
            log.debug("发送邮件通知给用户: userId={}, content={}", 
                    receiver.get("userId"), content);
        }
    }
}

