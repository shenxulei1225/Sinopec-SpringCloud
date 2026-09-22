package cn.cheers.x.module.platform.orchestration.framework.security.config;

import cn.cheers.x.framework.security.config.AuthorizeRequestsCustomizer;
import cn.cheers.x.module.platform.orchestration.enums.ApiConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

/**
 * platform-orchestration Security：编排 RPC 供 inspection 等模块 Feign 调用，路径免登录。
 */
@Configuration(proxyBeanMethods = false, value = "platformOrchestrationSecurityConfiguration")
public class SecurityConfiguration {

    @Bean("platformOrchestrationAuthorizeRequestsCustomizer")
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
                registry.requestMatchers(ApiConstants.PREFIX + "/**").permitAll();
            }

        };
    }

}
