package cn.iocoder.yudao.module.emergency.framework.rpc.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Feign 请求拦截器
 *
 * 用于记录和修改 Feign 请求信息
 */
@Component
@Slf4j
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        log.debug("Feign 请求拦截器 - URL: {}, 方法: {}",
            template.url(), template.method());

        // 可以在这里添加自定义的请求头或其他处理逻辑
        // 例如添加认证信息、跟踪信息等
    }
}
