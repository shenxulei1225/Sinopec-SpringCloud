package cn.cheers.x.module.dynamicbusiness.service.entity.refcategory;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;

import java.util.Map;

/**
 * REF 变更后，把「分类即实体」目标投影为分类–实体关联（场景 2 浏览用），并在组合 3 下同步分类–分类。
 *
 * <p>标准能力：凡单选 / 多选 REF，只要目标实体存在分类即实体 link，即挂/解对应分类；
 * 不按主体类型或字段白名单限制。</p>
 *
 * <p>分工：②③ {@link cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityCategoryRelationService}；
 * ④ {@link EntityRefCategoryCategoryProjectionService}（只 upsert，REF 解绑不删 CC）。</p>
 *
 * <p>与 {@code EntityRelationSyncService} 分离：后者只维护实体–实体关系表。</p>
 */
public interface EntityRefCategoryProjectionService {

    /**
     * 创建实体后：对请求中的 REF 目标（有分类即实体 link）建立分类–实体关联。
     */
    void projectOnCreate(EntityDO entity, Map<String, Object> fieldValues);

    /**
     * 更新实体后：仅处理本次请求出现的 REF 字段。
     * <ul>
     *   <li>单选：旧目标有 link 则解绑，新目标有 link 则挂上；清空则只解旧</li>
     *   <li>多选：按目标 id 集合差量，删掉的解绑、新增的挂上</li>
     * </ul>
     */
    void projectOnUpdate(EntityDO entity, Map<String, Object> newFieldValues, Map<String, Object> oldFieldValues);

    /**
     * 按设施专用表已有所属区域列，回填设施 → 区域分类的分类–实体关联（历史兼容）。
     *
     * @return 成功挂靠条数（含已存在幂等成功）
     */
    int backfillFacilityRegionCategoryRelations();
}
