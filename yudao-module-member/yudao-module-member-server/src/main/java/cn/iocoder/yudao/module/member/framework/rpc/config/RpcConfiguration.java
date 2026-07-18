package cn.iocoder.yudao.module.member.framework.rpc.config;

import cn.cheers.x.system.api.logger.LoginLogApi;
import cn.cheers.x.system.api.sms.SmsCodeApi;
import cn.cheers.x.system.api.social.SocialClientApi;
import cn.cheers.x.system.api.social.SocialUserApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "memberRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {SmsCodeApi.class, LoginLogApi.class, SocialUserApi.class, SocialClientApi.class})
public class RpcConfiguration {
}
