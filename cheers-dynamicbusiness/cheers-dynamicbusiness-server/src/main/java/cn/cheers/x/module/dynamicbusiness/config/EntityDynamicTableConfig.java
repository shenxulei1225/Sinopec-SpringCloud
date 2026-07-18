package cn.cheers.x.module.dynamicbusiness.config;

import cn.hutool.extra.spring.SpringUtil;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 兜底：框架 {@code CheersMybatisAutoConfiguration} 在 bean 覆盖后仅注册分页插件，
 * 导致 DEDICATED 业务（如 equipment → ent_equipment）仍查 dynamic_entity。
 *
 * <p>主定义见 {@link SystemMybatisPlusConfig}；本类在最终 Bean 初始化后重建拦截器链并置于最前。</p>
 */
@Configuration
@Slf4j
public class EntityDynamicTableConfig {

    @Bean
    public static BeanPostProcessor entityDynamicTableNameInjector() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                if (!"mybatisPlusInterceptor".equals(beanName) || !(bean instanceof MybatisPlusInterceptor interceptor)) {
                    return bean;
                }
                if (interceptor.getInterceptors().stream().anyMatch(DynamicTableNameInnerInterceptor.class::isInstance)) {
                    return bean;
                }
                MybatisPlusInterceptor rebuilt = new MybatisPlusInterceptor();
                DynamicTableNameInnerInterceptor dynamic = new DynamicTableNameInnerInterceptor();
                dynamic.setTableNameHandler(new DelegatingEntityTableNameHandler());
                rebuilt.addInnerInterceptor(dynamic);
                for (InnerInterceptor inner : interceptor.getInterceptors()) {
                    rebuilt.addInnerInterceptor(inner);
                }
                log.warn("[EntityDynamicTableConfig] mybatisPlusInterceptor 缺少动态表名插件，已重建拦截器链（DEDICATED 表路由）");
                return rebuilt;
            }
        };
    }

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
