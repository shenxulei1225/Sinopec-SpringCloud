package cn.iocoder.yudao.module.ai.framework.rpc.config;

import cn.cheers.x.infra.api.file.FileApi;
import cn.cheers.x.system.api.user.AdminUserApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "aiRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {FileApi.class, AdminUserApi.class})
public class RpcConfiguration {
}
