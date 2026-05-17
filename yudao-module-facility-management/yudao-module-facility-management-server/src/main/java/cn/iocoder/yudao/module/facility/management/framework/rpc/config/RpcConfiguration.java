package cn.iocoder.yudao.module.facility.management.framework.rpc.config;

import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * 设施管理模块 RPC 配置类
 *
 * <p>配置设施管理模块需要调用的远程服务接口。</p>
 */
@Configuration(value = "facilityRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(
        clients = {
                AdminUserApi.class          // 用户服务API
        },
        basePackages = {} // 明确指定空包，避免自动扫描
)
public class RpcConfiguration {
}
