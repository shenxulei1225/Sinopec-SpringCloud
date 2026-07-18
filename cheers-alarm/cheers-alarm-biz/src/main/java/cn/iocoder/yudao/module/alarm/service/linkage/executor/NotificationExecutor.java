package cn.iocoder.yudao.module.alarm.service.linkage.executor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.alarm.enums.LinkageActionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 通知执行器
 * 
 * <p>负责发送告警通知，支持多种通知渠道：</p>
 * <ul>
 *   <li>SMS - 短信通知</li>
 *   <li>EMAIL - 邮件通知</li>
 *   <li>WECHAT - 微信通知</li>
 *   <li>DINGTALK - 钉钉通知</li>
 *   <li>SYSTEM - 系统消息</li>
 * </ul>
 * 
 * <p>动作配置示例：</p>
 * <pre>
 * {
 *   "type": "NOTIFICATION",
 *   "channels": ["SMS", "WECHAT"],
 *   "recipients": ["user1", "user2"],
 *   "template": "ALARM_NOTIFY"
 * }
 * </pre>
 *
 * @author 告警管理模块
 */
@Slf4j
@Component
public class NotificationExecutor extends AbstractLinkageActionExecutor {

    @Override
    public LinkageActionTypeEnum getActionType() {
        return LinkageActionTypeEnum.NOTIFICATION;
    }

    @Override
    public int getPriority() {
        // 通知优先级较低
        return 10;
    }

    @Override
    protected String validateConfig(LinkageActionContext context) {
        // 验证通知渠道
        Object channelsObj = context.getActionConfigMap().get("channels");
        if (channelsObj == null) {
            return "通知渠道不能为空";
        }
        
        // 验证接收人
        Object recipientsObj = context.getActionConfigMap().get("recipients");
        if (recipientsObj == null) {
            return "通知接收人不能为空";
        }
        
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected LinkageActionResult doExecute(LinkageActionContext context) {
        try {
            // 1. 解析配置
            List<String> channels = (List<String>) context.getActionConfigMap().get("channels");
            List<String> recipients = (List<String>) context.getActionConfigMap().get("recipients");
            String template = context.getConfigString("template");
            
            if (CollUtil.isEmpty(channels)) {
                return LinkageActionResult.failed("通知渠道为空", false);
            }
            if (CollUtil.isEmpty(recipients)) {
                return LinkageActionResult.failed("通知接收人为空", false);
            }
            
            // 2. 构建通知内容
            String content = buildNotificationContent(context, template);
            
            // 3. 发送通知
            int successCount = 0;
            int failCount = 0;
            StringBuilder resultBuilder = new StringBuilder();
            
            for (String channel : channels) {
                try {
                    boolean sent = sendNotification(channel, recipients, content, context);
                    if (sent) {
                        successCount++;
                        resultBuilder.append(channel).append(":成功 ");
                    } else {
                        failCount++;
                        resultBuilder.append(channel).append(":失败 ");
                    }
                } catch (Exception e) {
                    failCount++;
                    resultBuilder.append(channel).append(":异常 ");
                    log.error("[通知执行器] 发送通知失败: channel={}, error={}", channel, e.getMessage());
                }
            }
            
            // 4. 返回结果
            if (failCount == 0) {
                return LinkageActionResult.success(
                        String.format("通知发送成功，共%d个渠道，%d个接收人", successCount, recipients.size()));
            } else if (successCount > 0) {
                return LinkageActionResult.success(
                        String.format("通知部分发送成功：%s", resultBuilder.toString().trim()));
            } else {
                return LinkageActionResult.failed("所有通知渠道发送失败");
            }
            
        } catch (Exception e) {
            log.error("[通知执行器] 执行异常", e);
            return LinkageActionResult.failed("通知发送异常：" + e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected LinkageActionResult doDryRun(LinkageActionContext context) {
        List<String> channels = (List<String>) context.getActionConfigMap().get("channels");
        List<String> recipients = (List<String>) context.getActionConfigMap().get("recipients");
        
        return LinkageActionResult.dryRunSuccess(
                String.format("模拟发送通知：渠道=%s，接收人=%s", 
                        channels != null ? channels : "[]",
                        recipients != null ? recipients.size() + "人" : "0人"));
    }

    /**
     * 构建通知内容
     *
     * @param context  执行上下文
     * @param template 模板名称
     * @return 通知内容
     */
    private String buildNotificationContent(LinkageActionContext context, String template) {
        // 如果有告警信息，使用告警内容
        if (context.getAlarm() != null) {
            return String.format("[%s] %s - %s", 
                    context.getAlarm().getAlarmLevel(),
                    context.getAlarm().getAlarmTypePath(),
                    context.getAlarm().getAlarmContent());
        }
        
        // 否则使用默认内容
        return "告警联动通知";
    }

    /**
     * 发送通知
     *
     * @param channel    通知渠道
     * @param recipients 接收人列表
     * @param content    通知内容
     * @param context    执行上下文
     * @return 是否发送成功
     */
    private boolean sendNotification(String channel, List<String> recipients, 
                                     String content, LinkageActionContext context) {
        // TODO: 实际实现需要调用通知服务
        // 这里预留接口，后续集成实际的通知服务
        
        log.info("[通知执行器] 发送通知: channel={}, recipients={}, content={}", 
                channel, recipients, StrUtil.sub(content, 0, 100));
        
        // 模拟发送成功
        switch (channel.toUpperCase()) {
            case "SMS":
                // 调用短信服务
                return true;
            case "EMAIL":
                // 调用邮件服务
                return true;
            case "WECHAT":
                // 调用微信服务
                return true;
            case "DINGTALK":
                // 调用钉钉服务
                return true;
            case "SYSTEM":
                // 发送系统消息
                return true;
            default:
                log.warn("[通知执行器] 不支持的通知渠道: {}", channel);
                return false;
        }
    }

}
