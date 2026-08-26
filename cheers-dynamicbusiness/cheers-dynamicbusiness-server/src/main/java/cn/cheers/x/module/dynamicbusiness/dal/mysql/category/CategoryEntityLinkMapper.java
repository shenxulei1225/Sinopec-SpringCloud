package cn.cheers.x.module.dynamicbusiness.dal.mysql.category;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryEntityLinkDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类与实体链接 Mapper
 *
 * @author 基础服务模块
 */
@Mapper
public interface CategoryEntityLinkMapper extends BaseMapperX<CategoryEntityLinkDO> {

    default CategoryEntityLinkDO selectByCategoryId(Long categoryId) {
        return selectOne(new LambdaQueryWrapperX<CategoryEntityLinkDO>()
                .eq(CategoryEntityLinkDO::getCategoryId, categoryId));
    }

    default CategoryEntityLinkDO selectByEntityId(Long entityId) {
        return selectOne(new LambdaQueryWrapperX<CategoryEntityLinkDO>()
                .eq(CategoryEntityLinkDO::getEntityId, entityId));
    }

    default CategoryEntityLinkDO selectByEntityIdAndEntityTypeCode(Long entityId, String entityTypeCode) {
        LambdaQueryWrapperX<CategoryEntityLinkDO> query = new LambdaQueryWrapperX<CategoryEntityLinkDO>()
                .eq(CategoryEntityLinkDO::getEntityId, entityId);
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            query.eq(CategoryEntityLinkDO::getEntityTypeCode, entityTypeCode.trim());
        }
        return selectOne(query);
    }

    default int deleteByCategoryId(Long categoryId) {
        return delete(new LambdaQueryWrapperX<CategoryEntityLinkDO>()
                .eq(CategoryEntityLinkDO::getCategoryId, categoryId));
    }

    default int deleteByEntityId(Long entityId) {
        return delete(new LambdaQueryWrapperX<CategoryEntityLinkDO>()
                .eq(CategoryEntityLinkDO::getEntityId, entityId));
    }

    default int deleteByEntityIdAndEntityTypeCode(Long entityId, String entityTypeCode) {
        LambdaQueryWrapperX<CategoryEntityLinkDO> query = new LambdaQueryWrapperX<CategoryEntityLinkDO>()
                .eq(CategoryEntityLinkDO::getEntityId, entityId);
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            query.eq(CategoryEntityLinkDO::getEntityTypeCode, entityTypeCode.trim());
        }
        return delete(query);
    }

    /**
     * 批量根据分类ID查询链接（用于优化批量删除时的 N+1 查询问题）
     */
    default List<CategoryEntityLinkDO> selectByCategoryIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new ArrayList<>();
        }
        return selectList(new LambdaQueryWrapperX<CategoryEntityLinkDO>()
                .in(CategoryEntityLinkDO::getCategoryId, categoryIds));
    }

    /**
     * 当前分类种类下已通过「分类即实体」链接挂接的实体 id（排除保留「未分类」桶节点）。
     * <p>表名经 DynamicTableName 路由到 {@code dynamic_category_entity_link_t{tenantId}}，
     * 不得改回无后缀基表。</p>
     */
    @Select("""
            SELECT DISTINCT cel.entity_id
            FROM dynamic_category_entity_link cel
            INNER JOIN dynamic_category c ON c.id = cel.category_id AND c.deleted = FALSE
            WHERE cel.deleted = FALSE
              AND cel.entity_type_code = #{entityTypeCode}
              AND c.category_type_code = #{categoryTypeCode}
              AND UPPER(c.code) NOT LIKE '%UNCATEGORIZED%'
            ORDER BY cel.entity_id ASC
            """)
    List<Long> selectDistinctEntityIdsByCategoryTypeCode(@Param("categoryTypeCode") String categoryTypeCode,
                                                         @Param("entityTypeCode") String entityTypeCode);
}
