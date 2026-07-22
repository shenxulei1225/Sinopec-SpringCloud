package cn.iocoder.yudao.module.emergency.service.event;

import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.response.EmergencyResponseDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.response.EmergencyResponseMapper;
import cn.cheers.x.system.api.notify.NotifyMessageSendApi;
import cn.cheers.x.system.api.notify.dto.NotifySendSingleToUserReqDTO;
import cn.cheers.x.system.api.sms.SmsSendApi;
import cn.cheers.x.system.api.sms.dto.send.SmsSendSingleToUserReqDTO;
import cn.cheers.x.system.api.mail.MailSendApi;
import cn.cheers.x.system.api.mail.dto.MailSendSingleToUserReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 事件通知 Service 实现类
 * 
 * 实现事件创建、状态变更等关键节点的多渠道通知机制
 */
@Service
@Slf4j
public class EventNotificationServiceImpl implements EventNotificationService {

    @Resource
    private EmergencyResponseMapper responseMapper;

    @Resource
    private NotifyMessageSendApi notifyMessageSendApi;

    @Resource
    private SmsSendApi smsSendApi;

    @Resource
    private MailSendApi mailSendApi;

    /**
     * 是否启用通知功能
     */
    @Value("${emergency.notification.enabled:true}")
    private Boolean notificationEnabled;

    /**
     * 通知渠道配置（system/sms/email）
     */
    @Value("${emergency.notification.channels:system}")
    private List<String> notificationChannels;

    /**
     * 站内信模板编号（如果使用模板方式，需要预先创建模板）
     * 如果为空，则使用直接发送方式（需要系统支持）
     */
    @Value("${emergency.notification.template-code:}")
    private String notificationTemplateCode;

    @Override
    public void sendEventCreatedNotification(EmergencyEventDO event) {
        if (!notificationEnabled) {
            return;
        }

        try {
            log.info("发送事件创建通知: eventId={}, eventCode={}", event.getId(), event.getEventCode());

            String content = buildEventCreatedContent(event);
            List<Map<String, Object>> receivers = getEventNotificationReceivers(event);

            sendNotification(receivers, content, "事件创建通知");
        } catch (Exception e) {
            log.error("发送事件创建通知失败: eventId={}", event.getId(), e);
        }
    }

    @Override
    public void sendEventStatusChangedNotification(EmergencyEventDO event, String oldStatus, String newStatus) {
        if (!notificationEnabled) {
            return;
        }

        try {
            log.info("发送事件状态变更通知: eventId={}, oldStatus={}, newStatus={}", 
                    event.getId(), oldStatus, newStatus);

            String content = buildEventStatusChangedContent(event, oldStatus, newStatus);
            List<Map<String, Object>> receivers = getEventNotificationReceivers(event);

            sendNotification(receivers, content, "事件状态变更通知");
        } catch (Exception e) {
            log.error("发送事件状态变更通知失败: eventId={}", event.getId(), e);
        }
    }

    @Override
    public void sendEventConfirmedNotification(EmergencyEventDO event, String confirmResult) {
        if (!notificationEnabled) {
            return;
        }

        try {
            log.info("发送事件确认通知: eventId={}, confirmResult={}", event.getId(), confirmResult);

            String content = buildEventConfirmedContent(event, confirmResult);
            List<Map<String, Object>> receivers = getEventNotificationReceivers(event);

            sendNotification(receivers, content, "事件确认通知");
        } catch (Exception e) {
            log.error("发送事件确认通知失败: eventId={}", event.getId(), e);
        }
    }

    @Override
    public void sendResponseStartedNotification(EmergencyEventDO event, Long responseId) {
        if (!notificationEnabled) {
            return;
        }

        try {
            log.info("发送响应启动通知: eventId={}, responseId={}", event.getId(), responseId);

            EmergencyResponseDO response = responseMapper.selectById(responseId);
            String content = buildResponseStartedContent(event, response);
            List<Map<String, Object>> receivers = getEventNotificationReceivers(event);

            sendNotification(receivers, content, "响应启动通知");
        } catch (Exception e) {
            log.error("发送响应启动通知失败: eventId={}, responseId={}", event.getId(), responseId, e);
        }
    }

    @Override
    public void sendResponseUpgradedNotification(EmergencyEventDO event, Long responseId, 
                                                 String oldLevel, String newLevel) {
        if (!notificationEnabled) {
            return;
        }

        try {
            log.info("发送响应升级通知: eventId={}, responseId={}, oldLevel={}, newLevel={}", 
                    event.getId(), responseId, oldLevel, newLevel);

            EmergencyResponseDO response = responseMapper.selectById(responseId);
            String content = buildResponseUpgradedContent(event, response, oldLevel, newLevel);
            List<Map<String, Object>> receivers = getEventNotificationReceivers(event);

            sendNotification(receivers, content, "响应升级通知");
        } catch (Exception e) {
            log.error("发送响应升级通知失败: eventId={}, responseId={}", event.getId(), responseId, e);
        }
    }

    @Override
    public void sendResponseCancelledNotification(EmergencyEventDO event, Long responseId) {
        if (!notificationEnabled) {
            return;
        }

        try {
            log.info("发送响应取消通知: eventId={}, responseId={}", event.getId(), responseId);

            EmergencyResponseDO response = responseMapper.selectById(responseId);
            String content = buildResponseCancelledContent(event, response);
            List<Map<String, Object>> receivers = getEventNotificationReceivers(event);

            sendNotification(receivers, content, "响应取消通知");
        } catch (Exception e) {
            log.error("发送响应取消通知失败: eventId={}, responseId={}", event.getId(), responseId, e);
        }
    }

    /**
     * 获取事件通知接收人列表
     *
     * @param event 事件
     * @return 接收人列表
     */
    private List<Map<String, Object>> getEventNotificationReceivers(EmergencyEventDO event) {
        List<Map<String, Object>> receivers = new ArrayList<>();

        // 1. 事件负责人（commandOrg字段，这里简化处理）
        if (event.getCommandOrg() != null && !event.getCommandOrg().isEmpty()) {
            // TODO: 实际实现中应通过组织服务查询组织成员
            Map<String, Object> manager = Map.of(
                    "userId", "1", // 示例：实际应从组织查询
                    "userName", "事件负责人",
                    "role", "event_manager"
            );
            receivers.add(manager);
        }

        // 2. 事件创建人（creator字段）
        if (event.getCreator() != null && !event.getCreator().isEmpty()) {
            Map<String, Object> creator = Map.of(
                    "userId", event.getCreator(),
                    "userName", event.getCreator(),
                    "role", "creator"
            );
            receivers.add(creator);
        }

        // 3. 应急组织成员（通过事件关联的组织查询）
        // TODO: 实际实现中应通过组织服务查询组织成员

        return receivers;
    }

    /**
     * 发送通知（根据配置的渠道）
     *
     * @param receivers 接收人列表
     * @param content 通知内容
     * @param title 通知标题
     */
    private void sendNotification(List<Map<String, Object>> receivers, String content, String title) {
        if (receivers.isEmpty()) {
            log.warn("通知接收人列表为空，跳过发送");
            return;
        }

        for (String channel : notificationChannels) {
            try {
                switch (channel) {
                    case "system":
                        sendSystemNotification(receivers, content, title);
                        break;
                    case "sms":
                        sendSmsNotification(receivers, content, title);
                        break;
                    case "email":
                        sendEmailNotification(receivers, content, title);
                        break;
                    default:
                        log.warn("不支持的通知渠道: {}", channel);
                }
            } catch (Exception e) {
                log.error("通过{}渠道发送通知失败", channel, e);
                // 继续尝试其他渠道
            }
        }
    }

    /**
     * 发送系统通知（站内信）
     */
    private void sendSystemNotification(List<Map<String, Object>> receivers, String content, String title) {
        log.info("发送系统通知: title={}, receivers={}, content={}", title, receivers.size(), content);
        
        if (notifyMessageSendApi == null) {
            log.warn("站内信API未注入，跳过系统通知发送");
            return;
        }

        // 为每个接收者发送站内信
        for (Map<String, Object> receiver : receivers) {
            try {
                Object userIdObj = receiver.get("userId");
                if (userIdObj == null) {
                    log.warn("接收者用户ID为空，跳过发送: receiver={}", receiver);
                    continue;
                }

                Long userId;
                if (userIdObj instanceof Long) {
                    userId = (Long) userIdObj;
                } else if (userIdObj instanceof String) {
                    try {
                        userId = Long.parseLong((String) userIdObj);
                    } catch (NumberFormatException e) {
                        log.warn("接收者用户ID格式错误，跳过发送: userId={}", userIdObj);
                        continue;
                    }
                } else {
                    log.warn("接收者用户ID类型不支持，跳过发送: userId={}", userIdObj);
                    continue;
                }

                // 构建通知参数
                Map<String, Object> templateParams = new HashMap<>();
                templateParams.put("title", title);
                templateParams.put("content", content);

                // 如果配置了模板编号，使用模板方式发送
                if (notificationTemplateCode != null && !notificationTemplateCode.isEmpty()) {
                    NotifySendSingleToUserReqDTO reqDTO = new NotifySendSingleToUserReqDTO();
                    reqDTO.setUserId(userId);
                    reqDTO.setTemplateCode(notificationTemplateCode);
                    reqDTO.setTemplateParams(templateParams);
                    
                    notifyMessageSendApi.sendSingleMessageToAdmin(reqDTO);
                    log.debug("站内信发送成功: userId={}, title={}", userId, title);
                } else {
                    // 如果没有配置模板，记录日志提示需要配置模板或使用直接发送方式
                    log.info("未配置站内信模板编号，跳过模板方式发送。建议配置emergency.notification.template-code或使用直接发送API");
                }
            } catch (Exception e) {
                log.error("发送站内信失败: receiver={}", receiver, e);
                // 继续发送给其他接收者
            }
        }
    }

    /**
     * 发送短信通知
     */
    private void sendSmsNotification(List<Map<String, Object>> receivers, String content, String title) {
        log.info("发送短信通知: title={}, receivers={}", title, receivers.size());
        
        if (smsSendApi == null) {
            log.warn("短信API未注入，跳过短信通知发送");
            return;
        }

        // 为每个接收者发送短信
        for (Map<String, Object> receiver : receivers) {
            try {
                Object userIdObj = receiver.get("userId");
                if (userIdObj == null) {
                    log.warn("接收者用户ID为空，跳过发送: receiver={}", receiver);
                    continue;
                }

                Long userId;
                if (userIdObj instanceof Long) {
                    userId = (Long) userIdObj;
                } else if (userIdObj instanceof String) {
                    try {
                        userId = Long.parseLong((String) userIdObj);
                    } catch (NumberFormatException e) {
                        log.warn("接收者用户ID格式错误，跳过发送: userId={}", userIdObj);
                        continue;
                    }
                } else {
                    log.warn("接收者用户ID类型不支持，跳过发送: userId={}", userIdObj);
                    continue;
                }

                // 构建短信内容（短信通常有长度限制，需要精简内容）
                String smsContent = title + "：" + (content.length() > 50 ? content.substring(0, 50) + "..." : content);

                SmsSendSingleToUserReqDTO reqDTO = new SmsSendSingleToUserReqDTO();
                reqDTO.setUserId(userId);
                reqDTO.setTemplateCode("EMERGENCY_NOTIFICATION"); // 需要预先创建短信模板
                reqDTO.setTemplateParams(Map.of("content", smsContent));
                
                smsSendApi.sendSingleSmsToAdmin(reqDTO);
                log.debug("短信发送成功: userId={}, content={}", userId, smsContent);
            } catch (Exception e) {
                log.error("发送短信失败: receiver={}", receiver, e);
                // 继续发送给其他接收者
            }
        }
    }

    /**
     * 发送邮件通知
     */
    private void sendEmailNotification(List<Map<String, Object>> receivers, String content, String title) {
        log.info("发送邮件通知: title={}, receivers={}", title, receivers.size());
        
        if (mailSendApi == null) {
            log.warn("邮件API未注入，跳过邮件通知发送");
            return;
        }

        // 为每个接收者发送邮件
        for (Map<String, Object> receiver : receivers) {
            try {
                Object userIdObj = receiver.get("userId");
                if (userIdObj == null) {
                    log.warn("接收者用户ID为空，跳过发送: receiver={}", receiver);
                    continue;
                }

                Long userId;
                if (userIdObj instanceof Long) {
                    userId = (Long) userIdObj;
                } else if (userIdObj instanceof String) {
                    try {
                        userId = Long.parseLong((String) userIdObj);
                    } catch (NumberFormatException e) {
                        log.warn("接收者用户ID格式错误，跳过发送: userId={}", userIdObj);
                        continue;
                    }
                } else {
                    log.warn("接收者用户ID类型不支持，跳过发送: userId={}", userIdObj);
                    continue;
                }

                // 构建邮件内容
                Map<String, Object> templateParams = new HashMap<>();
                templateParams.put("title", title);
                templateParams.put("content", content);

                MailSendSingleToUserReqDTO reqDTO = new MailSendSingleToUserReqDTO();
                reqDTO.setUserId(userId);
                reqDTO.setTemplateCode("EMERGENCY_NOTIFICATION"); // 需要预先创建邮件模板
                reqDTO.setTemplateParams(templateParams);
                
                mailSendApi.sendSingleMailToAdmin(reqDTO);
                log.debug("邮件发送成功: userId={}, title={}", userId, title);
            } catch (Exception e) {
                log.error("发送邮件失败: receiver={}", receiver, e);
                // 继续发送给其他接收者
            }
        }
    }

    /**
     * 构建事件创建通知内容
     */
    private String buildEventCreatedContent(EmergencyEventDO event) {
        StringBuilder content = new StringBuilder();
        content.append("【事件创建通知】\n");
        content.append("事件编号：").append(event.getEventCode()).append("\n");
        if (event.getEventName() != null) {
            content.append("事件名称：").append(event.getEventName()).append("\n");
        }
        content.append("事件级别：").append(event.getEventLevel()).append("\n");
        if (event.getLocationAddress() != null) {
            content.append("发生地点：").append(event.getLocationAddress()).append("\n");
        }
        content.append("发现时间：").append(event.getDiscoveredAt()).append("\n");
        return content.toString();
    }

    /**
     * 构建事件状态变更通知内容
     */
    private String buildEventStatusChangedContent(EmergencyEventDO event, String oldStatus, String newStatus) {
        StringBuilder content = new StringBuilder();
        content.append("【事件状态变更通知】\n");
        content.append("事件编号：").append(event.getEventCode()).append("\n");
        content.append("状态变更：").append(oldStatus).append(" → ").append(newStatus).append("\n");
        return content.toString();
    }

    /**
     * 构建事件确认通知内容
     */
    private String buildEventConfirmedContent(EmergencyEventDO event, String confirmResult) {
        StringBuilder content = new StringBuilder();
        content.append("【事件确认通知】\n");
        content.append("事件编号：").append(event.getEventCode()).append("\n");
        content.append("确认结果：").append(confirmResult).append("\n");
        return content.toString();
    }

    /**
     * 构建响应启动通知内容
     */
    private String buildResponseStartedContent(EmergencyEventDO event, EmergencyResponseDO response) {
        StringBuilder content = new StringBuilder();
        content.append("【响应启动通知】\n");
        content.append("事件编号：").append(event.getEventCode()).append("\n");
        if (response != null) {
            content.append("响应编号：").append(response.getResponseNo()).append("\n");
            content.append("响应级别：").append(response.getResponseLevel()).append("\n");
        }
        return content.toString();
    }

    /**
     * 构建响应升级通知内容
     */
    private String buildResponseUpgradedContent(EmergencyEventDO event, EmergencyResponseDO response,
                                                String oldLevel, String newLevel) {
        StringBuilder content = new StringBuilder();
        content.append("【响应升级通知】\n");
        content.append("事件编号：").append(event.getEventCode()).append("\n");
        if (response != null) {
            content.append("响应编号：").append(response.getResponseNo()).append("\n");
        }
        content.append("响应级别变更：").append(oldLevel).append(" → ").append(newLevel).append("\n");
        return content.toString();
    }

    /**
     * 构建响应取消通知内容
     */
    private String buildResponseCancelledContent(EmergencyEventDO event, EmergencyResponseDO response) {
        StringBuilder content = new StringBuilder();
        content.append("【响应取消通知】\n");
        content.append("事件编号：").append(event.getEventCode()).append("\n");
        if (response != null) {
            content.append("响应编号：").append(response.getResponseNo()).append("\n");
        }
        return content.toString();
    }
}


