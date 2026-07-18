package cn.cheers.x.module.dynamicbusiness.dal.mysql.entity;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 实体 Mapper 接口
 *
 * <h3>设计原则</h3>
 * <ul>
 *   <li><strong>只继承 BaseMapperX</strong>：不添加任何 default 方法或 @Select 注解方法</li>
 *   <li><strong>所有实体表查询通过 Repository 层</strong>：Repository 负责构建查询条件和表名路由</li>
 *   <li><strong>使用 MyBatis-Plus 提供的方法</strong>：insert、selectById、selectList、selectPage 等</li>
 * </ul>
 *
 * <h3>为什么不在 Mapper 中添加 default 方法？</h3>
 * <ul>
 *   <li>职责分离：Mapper 只负责 SQL 执行，查询条件构建应该在 Repository 层</li>
 *   <li>表名路由：所有实体表查询都需要通过拦截器处理表名，应该在 Repository 层统一处理</li>
 *   <li>代码集中：查询逻辑集中在 Repository 层，便于维护和测试</li>
 * </ul>
 *
 * <h3>关系表查询</h3>
 * <p>关系表查询方法（如 selectEntityIdsByCategoryIds）应该在 EntityCategoryRelationMapper 中，不在本 Mapper 中。</p>
 */
@Mapper
public interface EntityMapper extends BaseMapperX<EntityDO> {

    // 只继承 BaseMapperX 的所有方法：
    // - insert(entity)
    // - insertBatch(entities)
    // - selectById(id)
    // - selectList(wrapper)
    // - selectPage(page, wrapper)
    // - updateById(entity)
    // - deleteById(id)
    // - selectCount(wrapper)
    // 等等...

    // 不添加任何 default 方法
    // 不添加任何 @Select/@Insert/@Update/@Delete 注解方法
    // 所有查询通过 Repository 层调用，由 Repository 构建 LambdaQueryWrapperX
}













































