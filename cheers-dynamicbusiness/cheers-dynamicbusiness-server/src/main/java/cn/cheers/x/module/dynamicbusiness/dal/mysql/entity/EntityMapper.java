package cn.cheers.x.module.dynamicbusiness.dal.mysql.entity;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 实体 Mapper 接口
 *
 * <h3>设计原则</h3>
 * <ul>
 *   <li><strong>只允许被 EntityRepositoryImpl 注入</strong>：业务 Service 禁止直接依赖本 Mapper</li>
 *   <li><strong>只继承 BaseMapperX</strong>：不添加业务 default 查询方法</li>
 *   <li><strong>表名占位符</strong>：EntityDO 注解为 {@code __entity_dynamic__}，须经 Repository 设置上下文后改写为 ent_*</li>
 * </ul>
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













































