package cn.iocoder.yudao.module.emergency;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication(scanBasePackages = {"cn.iocoder.yudao.module.emergency", "cn.cheers.x.framework"})
@ComponentScan(
    basePackages = {"cn.iocoder.yudao.module.emergency", "cn.cheers.x.framework"},
    excludeFilters = {
        // 排除 Swagger/Knife4j 相关配置类，避免测试环境加载
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = ".*\\.swagger\\.config\\..*"
        ),
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = ".*\\.Knife4jOpenApiCustomizer"
        ),
        // 排除 Tenant 相关配置类，使用 Mock Bean 替代
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = ".*\\.tenant\\.config\\..*"
        ),
        // 排除 Security RPC 相关配置类，依赖 Feign 等外部依赖
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = ".*\\.security\\.config\\.YudaoSecurityRpcAutoConfiguration"
        ),
        // 排除 Tenant RPC 相关配置类
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = ".*\\.tenant\\.config\\.YudaoTenantRpcAutoConfiguration"
        ),
        // 排除 Security 相关配置类，依赖外部API
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = ".*\\.security\\.config\\..*"
        ),
        // 排除 API 日志相关拦截器，避免请求体读取冲突
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = ".*\\.apilog\\..*"
        )
    }
)
public class EmergencyTestApplication {
}

















