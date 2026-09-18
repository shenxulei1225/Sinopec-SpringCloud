package cn.cheers.x.module.dynamicbusiness.framework.rpc.config;

import cn.cheers.x.alarm.api.AlarmNotifyApi;
import cn.cheers.x.alarm.api.AlarmTriggerApi;
import cn.cheers.x.device.protocolgateway.api.DeviceProtocolMissionApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * 动态业务跨模块调用白名单。
 * <p>禁止空扫包。
 */
@Configuration(value = "dynamicbusinessRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(
        clients = {
                AlarmTriggerApi.class,
                AlarmNotifyApi.class,
                DeviceProtocolMissionApi.class
        },
        basePackages = {}
)
public class RpcConfiguration {
}
