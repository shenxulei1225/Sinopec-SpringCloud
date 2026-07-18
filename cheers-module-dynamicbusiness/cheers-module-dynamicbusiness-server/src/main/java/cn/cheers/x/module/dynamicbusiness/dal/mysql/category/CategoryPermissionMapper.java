package cn.cheers.x.module.dynamicbusiness.dal.mysql.category;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryPermissionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

/**
 * 分类级权限 Mapper
 */
@Mapper
public interface CategoryPermissionMapper extends BaseMapperX<CategoryPermissionDO> {

    /**
     * 根据角色ID查询权限列表
     */
    default List<CategoryPermissionDO> selectByRoleId(Long roleId) {
        return selectList(new LambdaQueryWrapperX<CategoryPermissionDO>()
                .eq(CategoryPermissionDO::getRoleId, roleId));
    }

    /**
     * 根据分类ID查询权限列表
     */
    default List<CategoryPermissionDO> selectByCategoryId(Long categoryId) {
        return selectList(new LambdaQueryWrapperX<CategoryPermissionDO>()
                .eq(CategoryPermissionDO::getCategoryId, categoryId));
    }

    /**
     * 根据角色ID和分类ID查询权限
     */
    default CategoryPermissionDO selectByRoleIdAndCategoryId(Long roleId, Long categoryId) {
        return selectOne(new LambdaQueryWrapperX<CategoryPermissionDO>()
                .eq(CategoryPermissionDO::getRoleId, roleId)
                .eq(CategoryPermissionDO::getCategoryId, categoryId));
    }

    /**
     * 根据角色ID列表查询可访问的分类ID列表
     */
    @Select("<script>" +
            "SELECT DISTINCT category_id FROM dynamic_category_permission " +
            "WHERE deleted = false AND can_view = true AND role_id IN " +
            "<foreach collection='roleIds' item='roleId' open='(' separator=',' close=')'>" +
            "#{roleId}" +
            "</foreach>" +
            "</script>")
    List<Long> selectViewableCategoryIdsByRoleIds(@Param("roleIds") Collection<Long> roleIds);

    /**
     * 根据角色ID列表查询可管理的分类ID列表
     */
    @Select("<script>" +
            "SELECT DISTINCT category_id FROM dynamic_category_permission " +
            "WHERE deleted = false AND can_manage = true AND role_id IN " +
            "<foreach collection='roleIds' item='roleId' open='(' separator=',' close=')'>" +
            "#{roleId}" +
            "</foreach>" +
            "</script>")
    List<Long> selectManageableCategoryIdsByRoleIds(@Param("roleIds") Collection<Long> roleIds);

    /**
     * 删除角色的所有分类权限
     */
    default void deleteByRoleId(Long roleId) {
        delete(new LambdaQueryWrapperX<CategoryPermissionDO>()
                .eq(CategoryPermissionDO::getRoleId, roleId));
    }

    /**
     * 删除分类的所有权限
     */
    default void deleteByCategoryId(Long categoryId) {
        delete(new LambdaQueryWrapperX<CategoryPermissionDO>()
                .eq(CategoryPermissionDO::getCategoryId, categoryId));
    }
}
