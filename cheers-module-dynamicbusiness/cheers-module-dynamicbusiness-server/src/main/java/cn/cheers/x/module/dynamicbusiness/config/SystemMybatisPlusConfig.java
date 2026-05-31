package cn.cheers.x.module.dynamicbusiness.config;

import cn.hutool.extra.spring.SpringUtil;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * dynamicbusiness 模块的 MyBatis-Plus 配置
 *
 * <p>显式定义 {@link MybatisPlusInterceptor} Bean，并在其中注册 Entity 动态表名拦截器。</p>
 *
 * <p>通过使用相同的 Bean 名称 "mybatisPlusInterceptor" 并配合框架层的
 * {@code @ConditionalOnMissingBean(MybatisPlusInterceptor.class)}，确保本模块的动态表名拦截器
 * 一定会被真正生效的 {@link MybatisPlusInterceptor} 使用。</p>
 */
@Configuration
@Slf4j
public class SystemMybatisPlusConfig {

    /**
     * 定义 MybatisPlusInterceptor，注意此处不再直接依赖 EntityTableNameHandler，
     * 避免在 SqlSessionFactory 构建阶段触发 Mapper 相关 Bean 的初始化，造成循环依赖。
     */
    @Bean("mybatisPlusInterceptor")
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 1. Entity 动态表名拦截器（必须放在分页拦截器之前）
        DynamicTableNameInnerInterceptor dynamicTableNameInterceptor = new DynamicTableNameInnerInterceptor();
        // 通过代理延迟获取真正的 EntityTableNameHandler，避免在 Bean 构建阶段触发 Mapper 依赖
        dynamicTableNameInterceptor.setTableNameHandler(new DelegatingEntityTableNameHandler());
        interceptor.addInnerInterceptor(dynamicTableNameInterceptor);

        // 2. 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());

        // 3. 乐观锁插件（用于预案步骤的并发控制）
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        log.info("[SystemMybatisPlusConfig] 已注册自定义 MybatisPlusInterceptor, 包含动态表名拦截器代理={}, 当前拦截器数量={}",
                dynamicTableNameInterceptor, interceptor.getInterceptors().size());
        return interceptor;
    }

    /**
     * 延迟委托给真正的 EntityTableNameHandler，避免在构造 MybatisPlusInterceptor 时就初始化 Mapper / SqlSessionFactory。
     */
    private static class DelegatingEntityTableNameHandler implements TableNameHandler {

        private volatile EntityTableNameHandler delegate;

        private EntityTableNameHandler getDelegate() {
            if (delegate == null) {
                synchronized (this) {
                    if (delegate == null) {
                        delegate = SpringUtil.getBean(EntityTableNameHandler.class);
                    }
                }
            }
            return delegate;
        }

        @Override
        public String dynamicTableName(String sql, String tableName) {
            return getDelegate().dynamicTableName(sql, tableName);
        }
    }
}

