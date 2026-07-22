package cn.cheers.x.module.dynamicbusiness.dal.mysql.category;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryUserRelationDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryUserRelationMapper extends BaseMapperX<CategoryUserRelationDO> {

    default CategoryUserRelationDO selectByCategoryAndUser(Long categoryId, Long userId) {
        return selectOne(new LambdaQueryWrapperX<CategoryUserRelationDO>()
                .eq(CategoryUserRelationDO::getCategoryId, categoryId)
                .eq(CategoryUserRelationDO::getUserId, userId)
                .eq(CategoryUserRelationDO::getDeleted, false));
    }

    default List<CategoryUserRelationDO> selectByCategoryId(Long categoryId) {
        return selectList(new LambdaQueryWrapperX<CategoryUserRelationDO>()
                .eq(CategoryUserRelationDO::getCategoryId, categoryId)
                .eq(CategoryUserRelationDO::getDeleted, false)
                .orderByAsc(CategoryUserRelationDO::getSort, CategoryUserRelationDO::getId));
    }

    default List<Long> selectUserIdsByCategoryId(Long categoryId) {
        return selectByCategoryId(categoryId).stream()
                .map(CategoryUserRelationDO::getUserId)
                .toList();
    }
}
