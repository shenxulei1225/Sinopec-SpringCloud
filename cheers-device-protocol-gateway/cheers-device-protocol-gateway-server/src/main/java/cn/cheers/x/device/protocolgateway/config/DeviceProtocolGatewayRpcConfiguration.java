package cn.cheers.x.device.protocolgateway.config;

import cn.cheers.x.framework.web.core.util.WebFrameworkUtils;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import feign.RequestInterceptor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关读协议说明书目录的 RPC。
 * <p>设备口没有登录租户；读哪份说明书由配置指定，不猜设备身份。
 */
@Configuration
@EnableFeignClients(clients = {EntityRpcApi.class})
public class DeviceProtocolGatewayRpcConfiguration {

    @Bean
    public RequestInterceptor protocolCatalogTenantInterceptor(DeviceProtocolGatewayProperties properties) {
        return template -> template.header(
                WebFrameworkUtils.HEADER_TENANT_ID,
                String.valueOf(properties.getProtocolCatalogTenantId()));
    }
}
