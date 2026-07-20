package cn.iocoder.yudao.module.emergency.framework.rpc.config;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Feign 客户端配置类
 *
 * 配置 Feign 客户端的日志、错误处理、重试机制等
 */
@Configuration
@Slf4j
public class FeignConfig {

    /**
     * Feign 日志级别配置
     * NONE: 不记录日志
     * BASIC: 记录请求方法、URL、响应状态码及执行时间
     * HEADERS: 记录 BASIC 级别的内容，加上请求和响应的头信息
     * FULL: 记录请求和响应的头信息、正文及元数据
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * Feign 请求超时配置
     * 注意：Request.Options 构造函数已废弃，但 Feign 尚未提供替代方案
     * 待 Feign 提供新的 API 后，需要更新此方法
     */
    @Bean
    @SuppressWarnings("deprecation")
    public Request.Options options() {
        // 使用新的构造函数，添加 followRedirects 参数（默认 true）
        return new Request.Options(
            5000,   // 连接超时：5秒（毫秒）
            10000,  // 读取超时：10秒（毫秒）
            true    // 是否跟随重定向
        );
    }

    /**
     * Feign 重试配置
     */
    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(
            100,    // 初始间隔：100毫秒
            1000,   // 最大间隔：1秒
            3       // 最大重试次数
        );
    }

    /**
     * Feign 错误解码器
     * 用于自定义错误处理逻辑
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }

    /**
     * 自定义 Feign 错误解码器
     */
    public static class FeignErrorDecoder implements ErrorDecoder {

        private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FeignErrorDecoder.class);
        private final ErrorDecoder defaultErrorDecoder = new Default();

        @Override
        public Exception decode(String methodKey, feign.Response response) {
            log.error("Feign 调用失败 - 方法: {}, 状态码: {}, 原因: {}",
                methodKey, response.status(), response.reason());

            // 记录请求详情
            try {
                if (response.body() != null) {
                    log.error("Feign 响应内容: {}", response.body().toString());
                }
            } catch (Exception e) {
                log.error("读取响应内容失败", e);
            }

            // 对于 502 错误，记录更详细的信息
            if (response.status() == 502) {
                log.error("检测到 502 Bad Gateway 错误，这通常表示上游服务不可用或网络问题");
                log.error("请检查目标服务是否正常运行，以及网络连通性");
            }

            // 返回默认的错误处理
            return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}
