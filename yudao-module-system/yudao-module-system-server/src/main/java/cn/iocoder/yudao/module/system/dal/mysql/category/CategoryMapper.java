package cn.iocoder.yudao.module.system.dal.mysql.category;

import cn.iocoder.yudao.framework.category.mapper.FrameworkCategoryMapper;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.category.CategoryDO;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface CategoryMapper extends FrameworkCategoryMapper<CategoryDO> {

    default CategoryDO selectByIdAndCategoryTypeCode(Long id, String categoryTypeCode) {
        return selectOne(new LambdaQueryWrapperX<CategoryDO>()
                .eq(CategoryDO::getId, id)
                .eqIfPresent(CategoryDO::getCategoryTypeCode, categoryTypeCode)
                .eq(CategoryDO::getDeleted, false));
    }

    default CategoryDO selectByNameAndParent(String name, Long parentId, String categoryTypeCode) {
        return selectOne(new LambdaQueryWrapperX<CategoryDO>()
                .eq(CategoryDO::getName, name)
                .eq(CategoryDO::getParentId, parentId)
                .eq(CategoryDO::getCategoryTypeCode, categoryTypeCode)
                .eq(CategoryDO::getDeleted, false));
    }

    default CategoryDO selectBySortAndParent(Integer sort, Long parentId, String categoryTypeCode) {
        LambdaQueryWrapperX<CategoryDO> wrapper = new LambdaQueryWrapperX<CategoryDO>()
                .eq(CategoryDO::getSort, sort)
                .eq(CategoryDO::getCategoryTypeCode, categoryTypeCode)
                .eq(CategoryDO::getDeleted, false);
        if (parentId == null) {
            wrapper.isNull(CategoryDO::getParentId);
        } else {
            wrapper.eq(CategoryDO::getParentId, parentId);
        }
        return selectOne(wrapper);
    }

    default List<CategoryDO> selectByCategoryTypeCode(String categoryTypeCode) {
        return selectList(new LambdaQueryWrapperX<CategoryDO>()
                .eq(CategoryDO::getCategoryTypeCode, categoryTypeCode)
                .eq(CategoryDO::getDeleted, false)
                .orderByAsc(CategoryDO::getSort, CategoryDO::getId));
    }

    default List<CategoryDO> selectByParentIdAndCategoryTypeCode(Long parentId, String categoryTypeCode) {
        LambdaQueryWrapperX<CategoryDO> wrapper = new LambdaQueryWrapperX<CategoryDO>();
        wrapper.eq(CategoryDO::getCategoryTypeCode, categoryTypeCode)
                .eq(CategoryDO::getDeleted, false)
                .orderByAsc(CategoryDO::getSort, CategoryDO::getId);
        if (parentId == null) {
            wrapper.isNull(CategoryDO::getParentId);
        } else {
            wrapper.eq(CategoryDO::getParentId, parentId);
        }
        return selectList(wrapper);
    }

    default List<CategoryDO> selectByParentIdsAndCategoryTypeCode(Collection<Long> parentIds, String categoryTypeCode, Integer status) {
        if (parentIds == null || parentIds.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapperX<CategoryDO> wrapper = new LambdaQueryWrapperX<CategoryDO>();
        wrapper.in(CategoryDO::getParentId, parentIds)
                .eq(CategoryDO::getCategoryTypeCode, categoryTypeCode)
                .eq(CategoryDO::getDeleted, false)
                .orderByAsc(CategoryDO::getSort, CategoryDO::getId);
        if (status != null) {
            wrapper.eq(CategoryDO::getStatus, status);
        }
        return selectList(wrapper);
    }

    default List<CategoryDO> selectByIdsAndNotDeleted(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapperX<CategoryDO>()
                .in(CategoryDO::getId, ids)
                .eq(CategoryDO::getDeleted, false));
    }

    default List<CategoryDO> searchLike(String keyword, String categoryTypeCode) {
        return selectList(new LambdaQueryWrapperX<CategoryDO>()
                .eq(CategoryDO::getCategoryTypeCode, categoryTypeCode)
                .and(StringUtils.isNotBlank(keyword), q -> q.like(CategoryDO::getName, keyword)
                        .or().like(CategoryDO::getCode, keyword)
                        .or().like(CategoryDO::getDescription, keyword))
                .eq(CategoryDO::getDeleted, false)
                .orderByAsc(CategoryDO::getSort, CategoryDO::getId));
    }

    default Long selectCountByCategoryTypeCode(String categoryTypeCode) {
        return selectCount(new LambdaQueryWrapperX<CategoryDO>()
                .eq(CategoryDO::getCategoryTypeCode, categoryTypeCode)
                .eq(CategoryDO::getDeleted, false));
    }

    default List<CategoryDO> selectSubtreeByPath(String treePath, String categoryTypeCode, Integer status) {
        LambdaQueryWrapperX<CategoryDO> wrapper = new LambdaQueryWrapperX<CategoryDO>();
        wrapper.eq(CategoryDO::getCategoryTypeCode, categoryTypeCode)
                .likeRight(CategoryDO::getTreePath, treePath)
                .eq(CategoryDO::getDeleted, false)
                .orderByAsc(CategoryDO::getLevel, CategoryDO::getSort, CategoryDO::getId);
        if (status != null) {
            wrapper.eq(CategoryDO::getStatus, status);
        }
        return selectList(wrapper);
    }
}
