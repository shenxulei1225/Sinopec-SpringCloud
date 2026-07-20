package cn.iocoder.yudao.module.emergency.framework.rpc.config;

import cn.cheers.x.framework.common.biz.infra.logger.ApiAccessLogCommonApi;
import cn.cheers.x.framework.common.biz.system.category.CategoryCommonApi;
import cn.cheers.x.system.api.user.AdminUserApi;
import cn.cheers.x.system.api.notify.NotifyMessageSendApi;
import cn.cheers.x.system.api.sms.SmsSendApi;
import cn.cheers.x.system.api.mail.MailSendApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Emergency 模块的 RPC 配置类
 *
 * 配置 Feign 客户端扫描路径，支持调用其他微服务
 */
@Configuration(value = "emergencyRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(
        clients = {
            AdminUserApi.class,        // 用户服务API
            CategoryCommonApi.class,   // 分类服务API（调用System模块的分类功能，使用CommonApi）
            NotifyMessageSendApi.class, // 站内信发送API
            SmsSendApi.class,          // 短信发送API
            MailSendApi.class          // 邮件发送API
        }, // 明确指定需要的 Feign 客户端
        basePackages = {} // 明确指定空包，避免自动扫描
)
@Import({FeignConfig.class, LoadBalancerConfig.class, FeignRequestInterceptor.class, FeignTestController.class})
public class RpcConfiguration {
}














