package cn.cheers.x.product.framework.rpc.config;

import cn.cheers.x.member.api.level.MemberLevelApi;
import cn.cheers.x.member.api.user.MemberUserApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "productRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {MemberUserApi.class, MemberLevelApi.class})
public class RpcConfiguration {
}
