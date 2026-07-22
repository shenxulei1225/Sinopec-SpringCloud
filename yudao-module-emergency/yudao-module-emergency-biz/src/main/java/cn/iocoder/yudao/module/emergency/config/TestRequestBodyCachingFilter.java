package cn.iocoder.yudao.module.emergency.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.ReadListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 测试环境下，为解决 MockMvc 下请求体只能被读取一次的问题，
 * 对应急模块相关接口的 HttpServletRequest 进行缓存包装，
 * 使得 {@link jakarta.servlet.ServletRequest#getInputStream()} 和
 * {@link jakarta.servlet.ServletRequest#getReader()} 可以被多次调用。
 *
 * 只在 {@code test} Profile 下生效，避免影响生产环境。
 */
@Profile("test")
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TestRequestBodyCachingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) { // Java 8 兼容：避免使用 pattern matching
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            if (shouldWrap(httpRequest)) {
            CachedBodyHttpServletRequest wrapped = new CachedBodyHttpServletRequest(httpRequest);
            chain.doFilter(wrapped, response);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private boolean shouldWrap(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri == null) {
            return false;
        }
        // 仅对应急模块的 JSON 请求做包装，范围尽量小
        if (!uri.startsWith("/admin-api/emergency/")) {
            return false;
        }
        String contentType = request.getContentType();
        return contentType != null && contentType.startsWith("application/json");
    }

    /**
     * 简单的请求包装器，缓存请求体字节，支持重复读取。
     */
    private static class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

        private final byte[] body;

        CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
            super(request);
            body = request.getInputStream().readAllBytes();
        }

        @Override
        public ServletInputStream getInputStream() {
            ByteArrayInputStream bais = new ByteArrayInputStream(body);
            return new ServletInputStream() {
                @Override
                public int read() {
                    return bais.read();
                }

                @Override
                public boolean isFinished() {
                    return bais.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                    // no-op for sync IO
                }
            };
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
        }
    }
}





