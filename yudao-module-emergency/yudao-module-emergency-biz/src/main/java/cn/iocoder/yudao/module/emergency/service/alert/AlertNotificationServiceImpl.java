package cn.iocoder.yudao.module.emergency.service.alert;

import cn.iocoder.yudao.module.emergency.dal.dataobject.alert.AlertConfigurationDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandStepDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 告警通知 Service 实现类
 * 
 * 支持多渠道通知：短信、邮件、系统通知
 */
@Service
@Slf4j
public class AlertNotificationServiceImpl implements AlertNotificationService {

    @Override
    public void sendAlert(EmergencyCommandStepDO step, AlertConfigurationDO config, String message) {
        log.info("发送告警通知: stepId={}, message={}", step.getId(), message);

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
        String alertContent = buildAlertContent(step, message);

        // 根据渠道发送通知
        for (String channel : channels) {
            try {
                sendByChannel(channel, receivers, alertContent);
            } catch (Exception e) {
                log.error("通过{}渠道发送告警失败", channel, e);
                // 继续尝试其他渠道
            }
        }
    }

    private String buildAlertContent(EmergencyCommandStepDO step, String message) {
        StringBuilder content = new StringBuilder();
        content.append(message).append("\n");
        content.append("指令步骤ID: ").append(step.getId()).append("\n");
        content.append("步骤内容: ").append(step.getStepContent()).append("\n");
        if (step.getTimeLimitMinutes() != null) {
            content.append("执行时限: ").append(step.getTimeLimitMinutes()).append("分钟\n");
        }
        if (step.getStartTime() != null) {
            content.append("开始时间: ").append(step.getStartTime()).append("\n");
        }
        return content.toString();
    }

    private void sendByChannel(String channel, List<Map<String, Object>> receivers, String content) {
        switch (channel) {
            case "sms":
                sendSms(receivers, content);
                break;
            case "email":
                sendEmail(receivers, content);
                break;
            case "system":
                sendSystemNotification(receivers, content);
                break;
            default:
                log.warn("不支持的通知渠道: {}", channel);
        }
    }

    private void sendSms(List<Map<String, Object>> receivers, String content) {
        log.info("发送短信通知: receivers={}, content={}", receivers, content);
        // TODO: 集成短信服务
    }

    private void sendEmail(List<Map<String, Object>> receivers, String content) {
        log.info("发送邮件通知: receivers={}, content={}", receivers, content);
        // TODO: 集成邮件服务
    }

    private void sendSystemNotification(List<Map<String, Object>> receivers, String content) {
        log.info("发送系统通知: receivers={}, content={}", receivers, content);
        // TODO: 集成系统通知服务（如WebSocket、站内信等）
    }
}

