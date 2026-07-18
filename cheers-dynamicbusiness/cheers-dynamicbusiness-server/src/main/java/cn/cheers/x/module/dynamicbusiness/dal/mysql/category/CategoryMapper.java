package cn.cheers.x.module.dynamicbusiness.dal.mysql.category;

import cn.cheers.x.module.dynamicbusiness.framework.category.mapper.FrameworkCategoryMapper;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryDO;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 系统分类 Mapper
 * 
 * @author yudao
 */
@org.apache.ibatis.annotations.Mapper
public interface CategoryMapper extends FrameworkCategoryMapper<CategoryDO> {

    /**
     * 统计分类类型下的分类数量
     */
    default Long countByCategoryTypeCode(String categoryTypeCode) {
        return selectCount(new LambdaQueryWrapperX<CategoryDO>()
                .eq(CategoryDO::getCategoryTypeCode, categoryTypeCode)
                .eq(CategoryDO::getDeleted, false));
    }

    /**
     * 搜索分类（按名称、编码、描述）
     */
    default List<CategoryDO> searchLike(String keyword, String categoryTypeCode) {
        return selectList(new LambdaQueryWrapperX<CategoryDO>()
                .eq(StringUtils.isNotBlank(categoryTypeCode), CategoryDO::getCategoryTypeCode, categoryTypeCode)
                .and(StringUtils.isNotBlank(keyword), q -> q.like(CategoryDO::getName, keyword)
                        .or().like(CategoryDO::getCode, keyword)
                        .or().like(CategoryDO::getDescription, keyword))
                .orderByDesc(CategoryDO::getCreateTime));
    }

    /**
     * 根据名称和父分类ID查询分类（用于唯一性验证）
     */
    @Override
    default CategoryDO selectByNameAndParent(String name, Long parentId, String categoryTypeCode) {
        return selectOne(new LambdaQueryWrapperX<CategoryDO>()
                .eq(CategoryDO::getName, name)
                .eq(CategoryDO::getParentId, parentId)
                .eq(CategoryDO::getCategoryTypeCode, categoryTypeCode)
                .eq(CategoryDO::getDeleted, false));
    }

    /**
     * 根据排序和父分类ID查询分类（用于校验同一父分类下排序不重复）
     */
    @Override
    default CategoryDO selectBySortAndParent(Integer sort, Long parentId, String categoryTypeCode) {
        return selectOne(new LambdaQueryWrapperX<CategoryDO>()
                .eq(CategoryDO::getSort, sort)
                .eq(CategoryDO::getParentId, parentId)
                .eq(CategoryDO::getCategoryTypeCode, categoryTypeCode)
                .eq(CategoryDO::getDeleted, false));
    }

    /**
     * 根据分类类型查询分类列表
     */
    @Override
    default List<CategoryDO> selectByCategoryTypeCode(String categoryTypeCode) {
        return selectList(new LambdaQueryWrapperX<CategoryDO>()
                .eq(CategoryDO::getCategoryTypeCode, categoryTypeCode)
                .eq(CategoryDO::getDeleted, false)
                .orderByAsc(CategoryDO::getSort));
    }
    /**
     * 根据分类ID和分类类型查询分类列表 单个分类查询
     */
    @Override
    default CategoryDO selectByIdAndCategoryTypeCode(Long id, String categoryTypeCode) {
        return selectOne(new LambdaQueryWrapperX<CategoryDO>()
                .eq(CategoryDO::getId, id)
                .eqIfPresent(CategoryDO::getCategoryTypeCode, categoryTypeCode)
                .eq(CategoryDO::getDeleted, false));
    }

    /**
    * 根据父分类ID和分类类型查询子分类列表
    */
    @Override
    default List<CategoryDO> selectByParentIdAndCategoryTypeCode(Long parentId, String categoryTypeCode) {
        return selectList(new LambdaQueryWrapperX<CategoryDO>()
                .eq(CategoryDO::getParentId, parentId)
                .eq(CategoryDO::getCategoryTypeCode, categoryTypeCode)
                .eq(CategoryDO::getDeleted, false)
                .orderByAsc(CategoryDO::getSort));
    }
    /**
     * 搜索分类（按分类类型）
     */
    default List<CategoryDO> searchLikeByCategoryType(String keyword, String categoryTypeCode) {
        return selectList(new LambdaQueryWrapperX<CategoryDO>()
                .eq(StringUtils.isNotBlank(categoryTypeCode), CategoryDO::getCategoryTypeCode, categoryTypeCode)
                .and(StringUtils.isNotBlank(keyword), q -> q.like(CategoryDO::getName, keyword)
                        .or().like(CategoryDO::getCode, keyword)
                        .or().like(CategoryDO::getDescription, keyword))
                .orderByDesc(CategoryDO::getCreateTime));
    }


    /**
     * 根据父分类ID集合批量查询子分类（用于递归查询优化）
     *
     * @param parentIds 父分类ID集合
     * @param categoryTypeCode 分类类型编码
     * @param status 状态过滤（null 表示不过滤）
     * @return 子分类列表
     */
    default List<CategoryDO> selectByParentIdsAndCategoryTypeCode(List<Long> parentIds, String categoryTypeCode, Integer status) {
        if (parentIds == null || parentIds.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapperX<CategoryDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.in(CategoryDO::getParentId, parentIds)
                .eq(CategoryDO::getCategoryTypeCode, categoryTypeCode)
                .eq(CategoryDO::getDeleted, false)
                .orderByAsc(CategoryDO::getSort);
        if (status != null) {
            wrapper.eq(CategoryDO::getStatus, status);
        }
        return selectList(wrapper);
    }

    /**
     * 根据树路径前缀查询子树列表
     *
     * @param treePathPrefix 树路径前缀（通常是根节点的 treePath）
     * @param categoryTypeCode 分类类型编码
     * @param status 状态过滤（null 表示不过滤）
     * @return 子树节点列表
     */
    default List<CategoryDO> selectSubtreeByPath(String treePathPrefix, String categoryTypeCode, Integer status) {
        if (StringUtils.isBlank(treePathPrefix)) {
            return List.of();
        }
        LambdaQueryWrapperX<CategoryDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.likeRight(CategoryDO::getTreePath, treePathPrefix)
                .eq(StringUtils.isNotBlank(categoryTypeCode), CategoryDO::getCategoryTypeCode, categoryTypeCode)
                .eq(CategoryDO::getDeleted, false)
                .orderByAsc(CategoryDO::getSort);
        if (status != null) {
            wrapper.eq(CategoryDO::getStatus, status);
        }
        return selectList(wrapper);
    }
}

