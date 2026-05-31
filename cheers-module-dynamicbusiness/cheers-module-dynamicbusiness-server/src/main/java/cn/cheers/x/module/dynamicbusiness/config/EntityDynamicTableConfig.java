package cn.cheers.x.module.dynamicbusiness.config;

import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameHandler;

/**
 * Entity 动态表名配置（兼容保留）
 *
 * <p>早期通过 {@link BeanPostProcessor} 在 {@link MybatisPlusInterceptor} 初始化后
 * 动态插入 {@link DynamicTableNameInnerInterceptor}。为避免与全局 MyBatis 配置产生
 * 多实例冲突，当前系统模块改为在 {@link SystemMybatisPlusConfig} 中显式定义
 * {@link MybatisPlusInterceptor} Bean，并在其中注册动态表名拦截器。</p>
 *
 * <p>本类仅保留为空实现，防止旧代码引用报错，不再实际向拦截器列表中添加内容。</p>
 *
 * @author 基础服务模块
 * @see EntityTableNameHandler
 * @see SystemMybatisPlusConfig
 */
public class EntityDynamicTableConfig {

    // 该类作为历史兼容占位，避免旧代码引用报错。
    // 实际的 MybatisPlus 拦截器配置已经迁移至 SystemMybatisPlusConfig。

}
































