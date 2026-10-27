package cn.cheers.x.device.protocolgateway.config;

import cn.cheers.x.device.protocolgateway.enums.ApiConstants;
import cn.cheers.x.framework.security.config.AuthorizeRequestsCustomizer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

/**
 * 设备对接口不走管理端登录。
 * <p>负责：放行路径末段为设备编号的那一条连接。放行巡检来读当前电量的 /rpc-api/device-protocol。
 * <p>不负责：协议实时监控页。那一页在 /admin-api 下，仍要登录。
 * <p>禁止：把 /admin-api 整段放行。
 */
@Component
@RequiredArgsConstructor
public class DeviceSocketAuthorizeRequestsCustomizer extends AuthorizeRequestsCustomizer {

    private final DeviceProtocolGatewayProperties properties;

    @Override
    public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry.requestMatchers(properties.getPathPattern()).permitAll();
        registry.requestMatchers(ApiConstants.PREFIX + "/**").permitAll();
    }
}
