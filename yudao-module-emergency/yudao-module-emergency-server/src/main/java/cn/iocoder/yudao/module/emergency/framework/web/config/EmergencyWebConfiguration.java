package cn.iocoder.yudao.module.emergency.framework.web.config;

import cn.cheers.x.framework.swagger.config.CheersSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * emergency 模块的 web 组件的 Configuration
 *
 * @author 芋道源码
 */
@Configuration(proxyBeanMethods = false)
public class EmergencyWebConfiguration implements WebMvcConfigurer {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * emergency 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi emergencyGroupedOpenApi() {
        return CheersSwaggerAutoConfiguration.buildGroupedOpenApi("emergency");
    }

    /**
     * 注册API性能监控拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 尝试获取 ApiPerformanceInterceptor Bean（如果存在）
        try {
            HandlerInterceptor interceptor = applicationContext.getBean(
                    "apiPerformanceInterceptor", HandlerInterceptor.class);
            if (interceptor != null) {
                registry.addInterceptor(interceptor)
                        .addPathPatterns("/admin-api/emergency/**")
                        .excludePathPatterns("/actuator/**");
            }
        } catch (Exception e) {
            // Bean 不存在时忽略,不影响其他功能
        }
    }

}

















