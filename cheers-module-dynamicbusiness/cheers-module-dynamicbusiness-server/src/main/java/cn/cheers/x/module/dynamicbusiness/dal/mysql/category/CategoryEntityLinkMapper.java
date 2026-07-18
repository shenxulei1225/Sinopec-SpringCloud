package cn.cheers.x.module.dynamicbusiness.dal.mysql.category;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryEntityLinkDO;
import org.apache.ibatis.annotations.Mapper;

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

    default int deleteByCategoryId(Long categoryId) {
        return delete(new LambdaQueryWrapperX<CategoryEntityLinkDO>()
                .eq(CategoryEntityLinkDO::getCategoryId, categoryId));
    }

    default int deleteByEntityId(Long entityId) {
        return delete(new LambdaQueryWrapperX<CategoryEntityLinkDO>()
                .eq(CategoryEntityLinkDO::getEntityId, entityId));
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
}