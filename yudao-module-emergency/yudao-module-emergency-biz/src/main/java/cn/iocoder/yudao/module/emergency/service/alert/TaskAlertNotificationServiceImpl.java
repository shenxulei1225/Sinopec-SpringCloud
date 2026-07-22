package cn.iocoder.yudao.module.emergency.service.alert;

import cn.iocoder.yudao.module.emergency.dal.dataobject.alert.AlertConfigurationDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 任务告警通知：多渠道真发（system / sms / email）。
 */
@Slf4j
@Service
public class TaskAlertNotificationServiceImpl implements TaskAlertNotificationService {

    @Resource
    private EmergencyAlertChannelSender channelSender;

    @Override
    public void sendAlert(EmergencyTaskDO task, AlertConfigurationDO config, String message) {
        log.info("发送任务告警通知: taskId={}, taskCode={}, message={}",
                task.getId(), task.getTaskCode(), message);

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

        String alertContent = buildAlertContent(task, message);
        String title = "应急任务告警";
        for (String channel : channels) {
            try {
                channelSender.sendByChannel(channel, receivers, title, alertContent);
            } catch (Exception e) {
                log.error("通过{}渠道发送告警失败", channel, e);
            }
        }
    }

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
}
