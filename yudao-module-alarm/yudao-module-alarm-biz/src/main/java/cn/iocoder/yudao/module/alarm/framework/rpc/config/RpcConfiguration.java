package cn.iocoder.yudao.module.alarm.framework.rpc.config;

import cn.iocoder.yudao.framework.common.biz.system.category.CategoryCommonApi;
import cn.iocoder.yudao.module.infra.api.websocket.WebSocketSenderApi;
import cn.iocoder.yudao.module.system.api.mail.MailSendApi;
import cn.iocoder.yudao.module.system.api.notify.NotifyMessageSendApi;
import cn.iocoder.yudao.module.system.api.sms.SmsSendApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * 告警模块 RPC 配置类
 * 
 * <p>配置告警模块需要调用的远程服务接口。</p>
 *
 * @author 告警管理模块
 */
@Configuration(value = "alarmRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(
        clients = {
                AdminUserApi.class,         // 用户服务API
                CategoryCommonApi.class,    // 分类服务API（告警类型管理）
                SmsSendApi.class,           // 短信发送API
                MailSendApi.class,          // 邮件发送API
                NotifyMessageSendApi.class, // 站内信发送API
                WebSocketSenderApi.class    // WebSocket发送API
        },
        basePackages = {} // 明确指定空包，避免自动扫描
)
public class RpcConfiguration {
}
