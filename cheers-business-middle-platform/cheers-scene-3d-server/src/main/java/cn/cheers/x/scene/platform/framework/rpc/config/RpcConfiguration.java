package cn.cheers.x.scene.platform.framework.rpc.config;

import cn.cheers.x.infra.api.file.FileApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "scenePlatformRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = FileApi.class)
public class RpcConfiguration {
}
