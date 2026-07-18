package cn.iocoder.yudao.module.iot.framework.rpc.config;

import cn.cheers.x.system.api.mail.MailSendApi;
import cn.cheers.x.system.api.notify.NotifyMessageSendApi;
import cn.cheers.x.system.api.sms.SmsSendApi;
import cn.cheers.x.system.api.user.AdminUserApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "iotRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {
        AdminUserApi.class, SmsSendApi.class, MailSendApi.class, NotifyMessageSendApi.class
})
public class RpcConfiguration {
}
