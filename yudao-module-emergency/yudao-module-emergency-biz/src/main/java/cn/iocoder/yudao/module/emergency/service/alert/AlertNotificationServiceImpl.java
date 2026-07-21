package cn.iocoder.yudao.module.emergency.service.alert;

import cn.iocoder.yudao.module.emergency.dal.dataobject.alert.AlertConfigurationDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandStepDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 指令步骤告警通知：多渠道真发（system / sms / email）。
 */
@Service
@Slf4j
public class AlertNotificationServiceImpl implements AlertNotificationService {

    @Resource
    private EmergencyAlertChannelSender channelSender;

    @Override
    public void sendAlert(EmergencyCommandStepDO step, AlertConfigurationDO config, String message) {
        log.info("发送告警通知: stepId={}, message={}", step.getId(), message);

        List<Map<String, Object>> receivers = config.getReceivers();
        if (receivers == null || receivers.isEmpty()) {
            log.warn("告警配置中没有接收对象");
            return;
        }

        List<String> channels = config.getNotificationChannels();
        if (channels == null || channels.isEmpty()) {
            log.warn("告警配置中没有通知渠道");
            return;
        }

        String alertContent = buildAlertContent(step, message);
        String title = "应急指令步骤告警";
        for (String channel : channels) {
            try {
                channelSender.sendByChannel(channel, receivers, title, alertContent);
            } catch (Exception e) {
                log.error("通过{}渠道发送告警失败", channel, e);
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
}
