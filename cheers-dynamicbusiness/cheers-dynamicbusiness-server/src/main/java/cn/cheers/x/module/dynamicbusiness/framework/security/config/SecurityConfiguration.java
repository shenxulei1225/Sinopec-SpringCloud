package cn.cheers.x.module.dynamicbusiness.framework.security.config;

import cn.cheers.x.framework.security.config.AuthorizeRequestsCustomizer;
import cn.cheers.x.module.dynamicbusiness.enums.ApiConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

/**
 * dynamicbusiness Security：实体 / 执行 / 策略等 RPC 供巡检等到点开跑链路 Feign 调用，路径免登录。
 */
@Configuration(proxyBeanMethods = false, value = "dynamicbusinessSecurityConfiguration")
public class SecurityConfiguration {

    @Bean("dynamicbusinessAuthorizeRequestsCustomizer")
    public AuthorizeRequestsCustomizer authorizeRequestsCustomizer() {
        return new AuthorizeRequestsCustomizer() {

            @Override
            public void customize(
                    AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
                registry.requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/swagger-ui").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll();
                registry.requestMatchers("/actuator").permitAll()
                        .requestMatchers("/actuator/**").permitAll();
                registry.requestMatchers("/druid/**").permitAll();
                registry.requestMatchers(ApiConstants.DYNAMICBUSINESS_PREFIX + "/**").permitAll();
                registry.requestMatchers(ApiConstants.PREFIX + "/**").permitAll();
            }

        };
    }

}
