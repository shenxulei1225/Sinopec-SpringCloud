package cn.cheers.x.system.framework.rpc.config;

import cn.cheers.x.infra.api.config.ConfigApi;
import cn.cheers.x.infra.api.file.FileApi;
import cn.cheers.x.infra.api.websocket.WebSocketSenderApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "systemRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {FileApi.class, WebSocketSenderApi.class, ConfigApi.class})
public class RpcConfiguration {
}
