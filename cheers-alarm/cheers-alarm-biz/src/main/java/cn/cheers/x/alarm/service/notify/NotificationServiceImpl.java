package cn.cheers.x.alarm.service.notify;

import cn.cheers.x.alarm.dal.dataobject.AlarmDO;
import cn.cheers.x.alarm.dal.dataobject.LinkageExecutionDO;
import cn.cheers.x.alarm.dal.mysql.AlarmMapper;
import cn.cheers.x.alarm.enums.AlarmLevelEnum;
import cn.cheers.x.system.api.mail.MailSendApi;
import cn.cheers.x.system.api.mail.dto.MailSendSingleToUserReqDTO;
import cn.cheers.x.system.api.notify.NotifyMessageSendApi;
import cn.cheers.x.system.api.notify.dto.NotifySendSingleToUserReqDTO;
import cn.cheers.x.system.api.sms.SmsSendApi;
import cn.cheers.x.system.api.sms.dto.send.SmsSendSingleToUserReqDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 告警通知服务实现类
 * 
 * <p>实现告警相关通知的发送，支持多种通知渠道：
 * <ul>
 *   <li>系统消息（站内信）</li>
 *   <li>短信（预留接口）</li>
 *   <li>邮件（预留接口）</li>
 *   <li>微信（预留接口）</li>
 *   <li>钉钉（预留接口）</li>
 * </ul>
 * </p>
 * 
 * <p>业务规则：
 * <ul>
 *   <li>BR-TIM-003：告警通知发送延迟不超过10秒</li>
 *   <li>BR-BIZ-006：联动重试3次后仍失败，需要人工介入，系统发送通知给值班员</li>
 *   <li>BR-BIZ-002：告警升级时通知更高级别人员</li>
 * </ul>
 * </p>
 *
 * @author 告警管理模块
 */
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Resource
    private AlarmMapper alarmMapper;

    @Autowired(required = false)
    private NotifyMessageSendApi notifyMessageSendApi;

    @Autowired(required = false)
    private SmsSendApi smsSendApi;

    @Autowired(required = false)
    private MailSendApi mailSendApi;

    /**
     * 是否启用通知功能
     */
    @Value("${alarm.notification.enabled:true}")
    private Boolean notificationEnabled;

    /**
     * 通知渠道配置（system/sms/email）
     */
    @Value("${alarm.notification.channels:system}")
    private List<String> notificationChannels;

    /**
     * 告警通知站内信模板编号
     */
    @Value("${alarm.notification.template.alarm:ALARM_NOTIFICATION}")
    private String alarmNotificationTemplateCode;

    /**
     * 联动失败通知站内信模板编号
     */
    @Value("${alarm.notification.template.linkage-failure:LINKAGE_FAILURE_NOTIFICATION}")
    private String linkageFailureTemplateCode;

    /**
     * 告警升级通知站内信模板编号
     */
    @Value("${alarm.notification.template.escalation:ALARM_ESCALATION_NOTIFICATION}")
    private String escalationTemplateCode;

    /**
     * 默认值班员用户ID列表（配置方式，实际应从值班系统获取）
     */
    @Value("${alarm.notification.default-operators:1}")
    private List<Long> defaultOperators;

    // ========== 告警通知 ==========

    @Override
    @Async
    public void sendAlarmNotification(AlarmDO alarm, List<Long> recipients) {
        if (!notificationEnabled || alarm == null) {
            return;
        }

        if (CollectionUtils.isEmpty(recipients)) {
            log.warn("告警通知接收人列表为空，跳过发送: alarmId={}", alarm.getId());
            return;
        }

        try {
            log.info("发送告警通知: alarmId={}, alarmCode={}, recipients={}", 
                    alarm.getId(), alarm.getAlarmCode(), recipients.size());

            String title = buildAlarmNotificationTitle(alarm);
            String content = buildAlarmNotificationContent(alarm);

            // 根据配置的渠道发送通知
            for (String channel : notificationChannels) {
                sendByChannel(channel, title, content, recipients);
            }
        } catch (Exception e) {
            log.error("发送告警通知失败: alarmId={}", alarm.getId(), e);
        }
    }

    @Override
    @Async
    public void sendAlarmNotification(AlarmDO alarm) {
        if (alarm == null) {
            return;
        }
        List<Long> recipients = getDefaultRecipientsByAlarmLevel(alarm.getAlarmLevel());
        sendAlarmNotification(alarm, recipients);
    }

    // ========== 联动失败通知 ==========

    @Override
    @Async
    public void sendLinkageFailureNotification(LinkageExecutionDO execution) {
        if (!notificationEnabled || execution == null) {
            return;
        }

        List<Long> recipients = getOnDutyOperators();
        sendLinkageFailureNotification(execution, recipients);
    }

    @Override
    @Async
    public void sendLinkageFailureNotification(LinkageExecutionDO execution, List<Long> recipients) {
        if (!notificationEnabled || execution == null) {
            return;
        }

        if (CollectionUtils.isEmpty(recipients)) {
            log.warn("联动失败通知接收人列表为空，跳过发送: executionId={}", execution.getId());
            return;
        }

        try {
            log.info("发送联动失败通知: executionId={}, alarmId={}, recipients={}", 
                    execution.getId(), execution.getAlarmId(), recipients.size());

            // 获取关联的告警信息
            AlarmDO alarm = null;
            if (execution.getAlarmId() != null) {
                alarm = alarmMapper.selectById(execution.getAlarmId());
            }

            String title = buildLinkageFailureTitle(execution);
            String content = buildLinkageFailureContent(execution, alarm);

            // 根据配置的渠道发送通知
            for (String channel : notificationChannels) {
                sendByChannel(channel, title, content, recipients);
            }
        } catch (Exception e) {
            log.error("发送联动失败通知失败: executionId={}", execution.getId(), e);
        }
    }

    // ========== 告警升级通知 ==========

    @Override
    @Async
    public void sendEscalationNotification(AlarmDO alarm) {
        if (alarm == null) {
            return;
        }
        List<Long> recipients = getEscalationRecipients(alarm.getAlarmLevel());
        sendEscalationNotification(alarm, recipients);
    }

    @Override
    @Async
    public void sendEscalationNotification(AlarmDO alarm, List<Long> recipients) {
        if (!notificationEnabled || alarm == null) {
            return;
        }

        if (CollectionUtils.isEmpty(recipients)) {
            log.warn("告警升级通知接收人列表为空，跳过发送: alarmId={}", alarm.getId());
            return;
        }

        try {
            log.info("发送告警升级通知: alarmId={}, alarmCode={}, recipients={}", 
                    alarm.getId(), alarm.getAlarmCode(), recipients.size());

            String title = buildEscalationTitle(alarm);
            String content = buildEscalationContent(alarm);

            // 根据配置的渠道发送通知
            for (String channel : notificationChannels) {
                sendByChannel(channel, title, content, recipients);
            }
        } catch (Exception e) {
            log.error("发送告警升级通知失败: alarmId={}", alarm.getId(), e);
        }
    }

    // ========== 批量通知 ==========

    @Override
    @Async
    public void batchSendAlarmNotifications(List<AlarmDO> alarms, List<Long> recipients) {
        if (!notificationEnabled || CollectionUtils.isEmpty(alarms)) {
            return;
        }

        for (AlarmDO alarm : alarms) {
            sendAlarmNotification(alarm, recipients);
        }
    }

    // ========== 通知渠道实现 ==========

    @Override
    public void sendSystemMessage(String title, String content, List<Long> recipients) {
        if (notifyMessageSendApi == null) {
            log.warn("站内信API未注入，跳过系统消息发送");
            return;
        }

        if (CollectionUtils.isEmpty(recipients)) {
            log.warn("系统消息接收人列表为空，跳过发送");
            return;
        }

        for (Long userId : recipients) {
            try {
                Map<String, Object> templateParams = new HashMap<>();
                templateParams.put("title", title);
                templateParams.put("content", content);

                NotifySendSingleToUserReqDTO reqDTO = new NotifySendSingleToUserReqDTO();
                reqDTO.setUserId(userId);
                reqDTO.setTemplateCode(alarmNotificationTemplateCode);
                reqDTO.setTemplateParams(templateParams);

                notifyMessageSendApi.sendSingleMessageToAdmin(reqDTO);
                log.debug("站内信发送成功: userId={}, title={}", userId, title);
            } catch (Exception e) {
                log.error("发送站内信失败: userId={}, title={}", userId, title, e);
            }
        }
    }

    @Override
    public void sendSmsNotification(String templateCode, Map<String, Object> params, List<String> mobiles) {
        if (smsSendApi == null) {
            log.warn("短信API未注入，跳过短信发送");
            return;
        }

        if (CollectionUtils.isEmpty(mobiles)) {
            log.warn("短信接收人列表为空，跳过发送");
            return;
        }

        for (String mobile : mobiles) {
            try {
                SmsSendSingleToUserReqDTO reqDTO = new SmsSendSingleToUserReqDTO();
                reqDTO.setMobile(mobile);
                reqDTO.setTemplateCode(templateCode);
                reqDTO.setTemplateParams(params);

                // 使用手机号直接发送
                smsSendApi.sendSingleSmsToAdmin(reqDTO);
                log.debug("短信发送成功: mobile={}, templateCode={}", mobile, templateCode);
            } catch (Exception e) {
                log.error("发送短信失败: mobile={}, templateCode={}", mobile, templateCode, e);
            }
        }
    }

    @Override
    public void sendEmailNotification(String subject, String content, List<String> emails) {
        if (mailSendApi == null) {
            log.warn("邮件API未注入，跳过邮件发送");
            return;
        }

        if (CollectionUtils.isEmpty(emails)) {
            log.warn("邮件接收人列表为空，跳过发送");
            return;
        }

        try {
            Map<String, Object> templateParams = new HashMap<>();
            templateParams.put("subject", subject);
            templateParams.put("content", content);

            // 为每个邮箱地址单独发送
            for (String email : emails) {
                try {
                    MailSendSingleToUserReqDTO reqDTO = new MailSendSingleToUserReqDTO();
                    reqDTO.setToMails(Collections.singletonList(email));
                    reqDTO.setTemplateCode("ALARM_EMAIL_NOTIFICATION");
                    reqDTO.setTemplateParams(templateParams);

                    mailSendApi.sendSingleMailToAdmin(reqDTO);
                    log.debug("邮件发送成功: email={}, subject={}", email, subject);
                } catch (Exception e) {
                    log.error("发送邮件失败: email={}, subject={}", email, subject, e);
                }
            }
        } catch (Exception e) {
            log.error("发送邮件失败: emails={}, subject={}", emails, subject, e);
        }
    }

    @Override
    public void sendWechatNotification(String templateId, Map<String, Object> params, List<String> openIds) {
        // 预留接口，暂不实现
        log.info("微信通知功能暂未实现: templateId={}, openIds={}", templateId, openIds);
    }

    @Override
    public void sendDingTalkNotification(String templateId, Map<String, Object> params, List<String> userIds) {
        // 预留接口，暂不实现
        log.info("钉钉通知功能暂未实现: templateId={}, userIds={}", templateId, userIds);
    }

    // ========== 通知配置查询 ==========

    @Override
    public List<Long> getDefaultRecipientsByAlarmLevel(String alarmLevel) {
        // 根据告警级别返回不同的接收人列表
        // 实际应用中应从配置或数据库获取
        List<Long> recipients = new ArrayList<>();
        
        if (AlarmLevelEnum.EMERGENCY.getLevel().equals(alarmLevel)) {
            // 紧急告警：通知所有管理人员
            recipients.addAll(defaultOperators);
            // TODO: 添加管理员用户
        } else if (AlarmLevelEnum.CRITICAL.getLevel().equals(alarmLevel)) {
            // 严重告警：通知值班员和主管
            recipients.addAll(defaultOperators);
        } else if (AlarmLevelEnum.WARNING.getLevel().equals(alarmLevel)) {
            // 警告告警：通知值班员
            recipients.addAll(defaultOperators);
        } else {
            // 信息告警：通知值班员
            recipients.addAll(defaultOperators);
        }
        
        return recipients;
    }

    @Override
    public List<Long> getOnDutyOperators() {
        // 获取当前值班员列表
        // 实际应用中应从值班系统获取
        return new ArrayList<>(defaultOperators);
    }

    @Override
    public List<Long> getEscalationRecipients(String alarmLevel) {
        // 获取升级通知接收人（更高级别人员）
        // 实际应用中应从配置或数据库获取
        List<Long> recipients = new ArrayList<>();
        
        // 升级通知应该通知更高级别的人员
        recipients.addAll(defaultOperators);
        // TODO: 根据告警级别添加更高级别的管理人员
        
        return recipients;
    }

    // ========== 私有方法 ==========

    /**
     * 根据渠道发送通知
     */
    private void sendByChannel(String channel, String title, String content, List<Long> recipients) {
        try {
            switch (channel.toLowerCase()) {
                case "system":
                    sendSystemMessage(title, content, recipients);
                    break;
                case "sms":
                    // 短信需要手机号，这里简化处理，实际应从用户服务获取
                    log.info("短信通知渠道暂未完全实现，跳过发送");
                    break;
                case "email":
                    // 邮件需要邮箱地址，这里简化处理，实际应从用户服务获取
                    log.info("邮件通知渠道暂未完全实现，跳过发送");
                    break;
                case "wechat":
                    log.info("微信通知渠道暂未实现，跳过发送");
                    break;
                case "dingtalk":
                    log.info("钉钉通知渠道暂未实现，跳过发送");
                    break;
                default:
                    log.warn("不支持的通知渠道: {}", channel);
            }
        } catch (Exception e) {
            log.error("通过{}渠道发送通知失败: title={}", channel, title, e);
        }
    }

    /**
     * 构建告警通知标题
     */
    private String buildAlarmNotificationTitle(AlarmDO alarm) {
        String levelText = getLevelText(alarm.getAlarmLevel());
        return String.format("【%s告警】%s", levelText, alarm.getAlarmTypePath());
    }

    /**
     * 构建告警通知内容
     */
    private String buildAlarmNotificationContent(AlarmDO alarm) {
        StringBuilder content = new StringBuilder();
        content.append("【告警通知】\n");
        content.append("告警编号：").append(alarm.getAlarmCode()).append("\n");
        content.append("告警级别：").append(getLevelText(alarm.getAlarmLevel())).append("\n");
        content.append("告警类型：").append(alarm.getAlarmTypePath()).append("\n");
        content.append("告警内容：").append(alarm.getAlarmContent()).append("\n");
        if (alarm.getDeviceName() != null) {
            content.append("关联设备：").append(alarm.getDeviceName()).append("\n");
        }
        if (alarm.getLocationName() != null) {
            content.append("位置信息：").append(alarm.getLocationName()).append("\n");
        }
        if (alarm.getTriggerValue() != null) {
            content.append("触发值：").append(alarm.getTriggerValue()).append("\n");
        }
        if (alarm.getThresholdValue() != null) {
            content.append("阈值：").append(alarm.getThresholdValue()).append("\n");
        }
        content.append("触发时间：").append(alarm.getCreateTime()).append("\n");
        return content.toString();
    }

    /**
     * 构建联动失败通知标题
     */
    private String buildLinkageFailureTitle(LinkageExecutionDO execution) {
        return String.format("【联动执行失败】需要人工介入 - %s", execution.getTargetDeviceName());
    }

    /**
     * 构建联动失败通知内容
     */
    private String buildLinkageFailureContent(LinkageExecutionDO execution, AlarmDO alarm) {
        StringBuilder content = new StringBuilder();
        content.append("【联动执行失败通知】\n");
        content.append("执行记录ID：").append(execution.getId()).append("\n");
        if (alarm != null) {
            content.append("关联告警：").append(alarm.getAlarmCode()).append("\n");
            content.append("告警内容：").append(alarm.getAlarmContent()).append("\n");
        }
        content.append("动作类型：").append(execution.getActionType()).append("\n");
        if (execution.getTargetDeviceName() != null) {
            content.append("目标设备：").append(execution.getTargetDeviceName()).append("\n");
        }
        content.append("重试次数：").append(execution.getRetryCount()).append("\n");
        if (execution.getErrorMessage() != null) {
            content.append("错误信息：").append(execution.getErrorMessage()).append("\n");
        }
        content.append("\n请及时进行人工处理！");
        return content.toString();
    }

    /**
     * 构建告警升级通知标题
     */
    private String buildEscalationTitle(AlarmDO alarm) {
        String levelText = getLevelText(alarm.getAlarmLevel());
        return String.format("【告警升级】%s告警超时未确认 - %s", levelText, alarm.getAlarmCode());
    }

    /**
     * 构建告警升级通知内容
     */
    private String buildEscalationContent(AlarmDO alarm) {
        StringBuilder content = new StringBuilder();
        content.append("【告警升级通知】\n");
        content.append("以下告警超时未确认，请立即处理！\n\n");
        content.append("告警编号：").append(alarm.getAlarmCode()).append("\n");
        content.append("告警级别：").append(getLevelText(alarm.getAlarmLevel())).append("\n");
        content.append("告警类型：").append(alarm.getAlarmTypePath()).append("\n");
        content.append("告警内容：").append(alarm.getAlarmContent()).append("\n");
        if (alarm.getDeviceName() != null) {
            content.append("关联设备：").append(alarm.getDeviceName()).append("\n");
        }
        if (alarm.getLocationName() != null) {
            content.append("位置信息：").append(alarm.getLocationName()).append("\n");
        }
        content.append("触发时间：").append(alarm.getCreateTime()).append("\n");
        content.append("升级时间：").append(alarm.getEscalationTime()).append("\n");
        content.append("\n请立即确认并处理此告警！");
        return content.toString();
    }

    /**
     * 获取告警级别文本
     */
    private String getLevelText(String level) {
        if (AlarmLevelEnum.EMERGENCY.getLevel().equals(level)) {
            return "紧急";
        } else if (AlarmLevelEnum.CRITICAL.getLevel().equals(level)) {
            return "严重";
        } else if (AlarmLevelEnum.WARNING.getLevel().equals(level)) {
            return "警告";
        } else {
            return "信息";
        }
    }

}
