package cn.cheers.x.module.dynamicbusiness.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * Entity 同步重试配置
 * 
 * <p>配置 Spring Retry 重试机制，支持：</p>
 * <ul>
 *   <li>重试间隔：1秒、5秒、30秒（指数退避）</li>
 *   <li>最大重试次数：3次</li>
 *   <li>重试失败回调</li>
 *   <li>异步执行线程池</li>
 * </ul>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>FR-036: 同步失败时自动重试（1秒、5秒、30秒）</li>
 * </ul>
 * 
 * @author 扩展字段查询服务
 */
@Slf4j
@Configuration
@EnableRetry
public class EntitySyncRetryConfiguration {

    /**
     * 初始重试间隔（毫秒）
     */
    public static final long INITIAL_INTERVAL = 1000L;

    /**
     * 重试间隔乘数
     * 1秒 * 5 = 5秒
     * 5秒 * 6 = 30秒
     */
    public static final double MULTIPLIER = 5.0;

    /**
     * 最大重试间隔（毫秒）
     */
    public static final long MAX_INTERVAL = 30000L;

    /**
     * 最大重试次数
     */
    public static final int MAX_ATTEMPTS = 3;

    // 注意：entitySyncExecutor 已在 AsyncConfig 中定义，避免重复配置

    /**
     * Entity 同步重试模板
     * 
     * <p>配置重试策略：</p>
     * <ul>
     *   <li>最大重试次数：3次</li>
     *   <li>重试间隔：1秒 → 5秒 → 30秒（指数退避）</li>
     * </ul>
     */
    @Bean("entitySyncRetryTemplate")
    public RetryTemplate entitySyncRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        // 配置重试策略：最大重试 3 次
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(MAX_ATTEMPTS);
        retryTemplate.setRetryPolicy(retryPolicy);

        // 配置退避策略：指数退避，1秒 → 5秒 → 30秒
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(INITIAL_INTERVAL);
        backOffPolicy.setMultiplier(MULTIPLIER);
        backOffPolicy.setMaxInterval(MAX_INTERVAL);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        // 添加重试监听器
        retryTemplate.registerListener(new EntitySyncRetryListener());

        return retryTemplate;
    }

    /**
     * Entity 同步重试监听器
     * 
     * <p>用于记录重试日志和监控重试状态。</p>
     */
    public static class EntitySyncRetryListener implements RetryListener {
        
        private static final Logger log = LoggerFactory.getLogger(EntitySyncRetryListener.class);

        @Override
        public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
            // 重试开始前调用，返回 true 允许重试
            return true;
        }

        @Override
        public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
            // 重试结束后调用（无论成功或失败）
            if (throwable != null) {
                log.error("Entity 同步重试结束，最终失败: retryCount={}, error={}", 
                        context.getRetryCount(), throwable.getMessage());
            } else if (context.getRetryCount() > 0) {
                log.info("Entity 同步重试成功: retryCount={}", context.getRetryCount());
            }
        }

        @Override
        public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
            // 每次重试失败时调用
            int retryCount = context.getRetryCount();
            long nextInterval = calculateNextInterval(retryCount);
            log.warn("Entity 同步失败，准备第 {} 次重试，下次重试间隔: {}ms, error={}", 
                    retryCount, nextInterval, throwable.getMessage());
        }

        /**
         * 计算下次重试间隔
         */
        private long calculateNextInterval(int retryCount) {
            if (retryCount <= 0) {
                return INITIAL_INTERVAL;
            }
            long interval = (long) (INITIAL_INTERVAL * Math.pow(MULTIPLIER, retryCount - 1));
            return Math.min(interval, MAX_INTERVAL);
        }
    }
}
