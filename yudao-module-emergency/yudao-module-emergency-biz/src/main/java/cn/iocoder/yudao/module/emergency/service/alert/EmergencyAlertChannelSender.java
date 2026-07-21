package cn.iocoder.yudao.module.emergency.service.alert;

import cn.cheers.x.system.api.mail.MailSendApi;
import cn.cheers.x.system.api.mail.dto.MailSendSingleToUserReqDTO;
import cn.cheers.x.system.api.notify.NotifyMessageSendApi;
import cn.cheers.x.system.api.notify.dto.NotifySendSingleToUserReqDTO;
import cn.cheers.x.system.api.sms.SmsSendApi;
import cn.cheers.x.system.api.sms.dto.send.SmsSendSingleToUserReqDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应急告警多渠道真发（对齐 EventNotificationServiceImpl 所用 system API）。
 * 缺模板 / 缺 userId → 显式打日志并跳过该接收人，不假装已发成功。
 */
@Component
@Slf4j
public class EmergencyAlertChannelSender {

    @Resource
    private NotifyMessageSendApi notifyMessageSendApi;
    @Resource
    private SmsSendApi smsSendApi;
    @Resource
    private MailSendApi mailSendApi;

    @Value("${emergency.notification.template-code:}")
    private String notifyTemplateCode;

    @Value("${emergency.alert.sms-template-code:EMERGENCY_ALERT}")
    private String smsTemplateCode;

    @Value("${emergency.alert.mail-template-code:EMERGENCY_ALERT}")
    private String mailTemplateCode;

    public void sendByChannel(String channel, List<Map<String, Object>> receivers, String title, String content) {
        if (channel == null || channel.isBlank()) {
            return;
        }
        switch (channel.trim().toLowerCase()) {
            case "sms" -> sendSms(receivers, title, content);
            case "email", "mail" -> sendMail(receivers, title, content);
            case "system", "notify" -> sendSystem(receivers, title, content);
            default -> log.warn("[EmergencyAlertChannelSender] unsupported channel={}", channel);
        }
    }

    private void sendSystem(List<Map<String, Object>> receivers, String title, String content) {
        if (notifyMessageSendApi == null) {
            log.warn("[EmergencyAlertChannelSender] NotifyMessageSendApi missing, skip system notify");
            return;
        }
        if (notifyTemplateCode == null || notifyTemplateCode.isBlank()) {
            log.warn("[EmergencyAlertChannelSender] emergency.notification.template-code empty, skip system notify");
            return;
        }
        for (Map<String, Object> receiver : receivers) {
            Long userId = parseUserId(receiver);
            if (userId == null) {
                continue;
            }
            try {
                Map<String, Object> params = new HashMap<>();
                params.put("title", title);
                params.put("content", content);
                NotifySendSingleToUserReqDTO req = new NotifySendSingleToUserReqDTO();
                req.setUserId(userId);
                req.setTemplateCode(notifyTemplateCode);
                req.setTemplateParams(params);
                notifyMessageSendApi.sendSingleMessageToAdmin(req);
                log.info("[EmergencyAlertChannelSender] system notify sent userId={}", userId);
            } catch (Exception ex) {
                log.error("[EmergencyAlertChannelSender] system notify failed userId={}", userId, ex);
            }
        }
    }

    private void sendSms(List<Map<String, Object>> receivers, String title, String content) {
        if (smsSendApi == null) {
            log.warn("[EmergencyAlertChannelSender] SmsSendApi missing, skip sms");
            return;
        }
        String smsContent = title + "：" + (content.length() > 50 ? content.substring(0, 50) + "..." : content);
        for (Map<String, Object> receiver : receivers) {
            Long userId = parseUserId(receiver);
            if (userId == null) {
                continue;
            }
            try {
                SmsSendSingleToUserReqDTO req = new SmsSendSingleToUserReqDTO();
                req.setUserId(userId);
                req.setTemplateCode(smsTemplateCode);
                req.setTemplateParams(Map.of("content", smsContent));
                smsSendApi.sendSingleSmsToAdmin(req);
                log.info("[EmergencyAlertChannelSender] sms sent userId={}", userId);
            } catch (Exception ex) {
                log.error("[EmergencyAlertChannelSender] sms failed userId={}", userId, ex);
            }
        }
    }

    private void sendMail(List<Map<String, Object>> receivers, String title, String content) {
        if (mailSendApi == null) {
            log.warn("[EmergencyAlertChannelSender] MailSendApi missing, skip mail");
            return;
        }
        for (Map<String, Object> receiver : receivers) {
            Long userId = parseUserId(receiver);
            if (userId == null) {
                continue;
            }
            try {
                Map<String, Object> params = new HashMap<>();
                params.put("title", title);
                params.put("content", content);
                MailSendSingleToUserReqDTO req = new MailSendSingleToUserReqDTO();
                req.setUserId(userId);
                req.setTemplateCode(mailTemplateCode);
                req.setTemplateParams(params);
                mailSendApi.sendSingleMailToAdmin(req);
                log.info("[EmergencyAlertChannelSender] mail sent userId={}", userId);
            } catch (Exception ex) {
                log.error("[EmergencyAlertChannelSender] mail failed userId={}", userId, ex);
            }
        }
    }

    private Long parseUserId(Map<String, Object> receiver) {
        if (receiver == null) {
            return null;
        }
        Object userIdObj = receiver.get("userId");
        if (userIdObj == null) {
            log.warn("[EmergencyAlertChannelSender] receiver missing userId: {}", receiver);
            return null;
        }
        if (userIdObj instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(userIdObj.toString());
        } catch (NumberFormatException ex) {
            log.warn("[EmergencyAlertChannelSender] invalid userId={}", userIdObj);
            return null;
        }
    }
}
