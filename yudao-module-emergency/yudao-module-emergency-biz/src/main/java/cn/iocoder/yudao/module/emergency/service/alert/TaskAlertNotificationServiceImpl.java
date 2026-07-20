package cn.iocoder.yudao.module.emergency.service.alert;

import cn.iocoder.yudao.module.emergency.dal.dataobject.alert.AlertConfigurationDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 任务告警通知 Service 实现类
 * 
 * @author 系统生成
 */
@Slf4j
@Service
public class TaskAlertNotificationServiceImpl implements TaskAlertNotificationService {
    
    @Override
    public void sendAlert(EmergencyTaskDO task, AlertConfigurationDO config, String message) {
        log.info("发送任务告警通知: taskId={}, taskCode={}, message={}", task.getId(), task.getTaskCode(), message);
        
        // 获取接收对象
        List<Map<String, Object>> receivers = config.getReceivers();
        if (receivers == null || receivers.isEmpty()) {
            log.warn("告警配置中没有接收对象");
            return;
        }
        
        // 获取通知渠道
        List<String> channels = config.getNotificationChannels();
        if (channels == null || channels.isEmpty()) {
            log.warn("告警配置中没有通知渠道");
            return;
        }
        
        // 构建告警内容
        String alertContent = buildAlertContent(task, message);
        
        // 通过各个渠道发送告警
        for (String channel : channels) {
            try {
                sendByChannel(channel, receivers, alertContent);
            } catch (Exception e) {
                log.error("通过{}渠道发送告警失败", channel, e);
            }
        }
    }
    
    /**
     * 构建告警内容
     */
    private String buildAlertContent(EmergencyTaskDO task, String message) {
        StringBuilder content = new StringBuilder();
        content.append(message).append("\n");
        content.append("任务编号：").append(task.getTaskCode()).append("\n");
        content.append("任务标题：").append(task.getTitle()).append("\n");
        if (task.getStartTime() != null) {
            content.append("开始时间：").append(task.getStartTime()).append("\n");
        }
        if (task.getTimeLimit() != null) {
            content.append("执行时限：").append(task.getTimeLimit()).append("分钟\n");
        }
        if (task.getDueTime() != null) {
            content.append("截止时间：").append(task.getDueTime()).append("\n");
        }
        return content.toString();
    }
    
    /**
     * 通过指定渠道发送告警
     */
    private void sendByChannel(String channel, List<Map<String, Object>> receivers, String content) {
        // TODO: 实现具体的通知渠道发送逻辑
        // 支持短信、邮件、系统通知等渠道
        log.info("通过{}渠道发送告警: receivers={}, content={}", channel, receivers, content);
        
        // 示例实现（实际应该调用具体的通知服务）
        switch (channel.toLowerCase()) {
            case "sms":
                log.info("发送短信告警: receivers={}", receivers);
                // TODO: 调用短信服务
                break;
            case "email":
                log.info("发送邮件告警: receivers={}", receivers);
                // TODO: 调用邮件服务
                break;
            case "system":
                log.info("发送系统通知: receivers={}", receivers);
                // TODO: 调用系统通知服务
                break;
            default:
                log.warn("未知的通知渠道: {}", channel);
        }
    }
}

