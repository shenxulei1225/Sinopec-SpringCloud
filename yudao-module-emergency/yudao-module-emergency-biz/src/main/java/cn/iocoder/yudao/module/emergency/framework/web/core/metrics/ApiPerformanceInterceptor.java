package cn.iocoder.yudao.module.emergency.framework.web.core.metrics;

import cn.iocoder.yudao.module.emergency.service.metrics.EmergencyMetricsService;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.annotation.Resource;

/**
 * API性能监控拦截器
 * 
 * @author 芋道源码
 */
@Component
@Slf4j
@ConditionalOnBean(MeterRegistry.class)
public class ApiPerformanceInterceptor implements HandlerInterceptor {

    @Resource
    private EmergencyMetricsService metricsService;

    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        START_TIME.set(System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        try {
            Long startTime = START_TIME.get();
            if (startTime != null) {
                long duration = System.currentTimeMillis() - startTime;
                String path = request.getRequestURI();
                String method = request.getMethod();
                
                // 记录API请求
                metricsService.recordApiRequest(path, method);
                
                // 记录API请求耗时
                metricsService.recordApiRequestDuration(path, method, duration);
                
                // 如果是错误响应，记录错误
                if (response.getStatus() >= 400 || ex != null) {
                    metricsService.recordApiError(path, method, String.valueOf(response.getStatus()));
                }
                
                // 记录慢请求（超过1秒）
                if (duration > 1000) {
                    log.warn("慢请求检测: {} {} 耗时 {}ms", method, path, duration);
                }
            }
        } catch (Exception e) {
            log.error("记录API性能指标失败", e);
        } finally {
            START_TIME.remove();
        }
    }
}



