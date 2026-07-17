package cn.iocoder.yudao.module.scene.platform.framework.rpc.config;

import cn.iocoder.yudao.module.infra.api.file.FileApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "scenePlatformRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = FileApi.class)
public class RpcConfiguration {
}
