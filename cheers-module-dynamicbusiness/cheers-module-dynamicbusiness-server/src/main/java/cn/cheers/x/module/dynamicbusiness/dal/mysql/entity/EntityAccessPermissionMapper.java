package cn.cheers.x.module.dynamicbusiness.dal.mysql.entity;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityAccessPermissionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 实体访问权限 Mapper
 */
@Mapper
public interface EntityAccessPermissionMapper extends BaseMapperX<EntityAccessPermissionDO> {

    /**
     * 根据角色ID查询权限列表
     */
    default List<EntityAccessPermissionDO> selectByRoleId(Long roleId) {
        return selectList(new LambdaQueryWrapperX<EntityAccessPermissionDO>()
                .eq(EntityAccessPermissionDO::getRoleId, roleId)
                .eq(EntityAccessPermissionDO::getDeleted, false));
    }

    /**
     * 根据实体ID查询权限列表
     */
    default List<EntityAccessPermissionDO> selectByEntityId(Long entityId) {
        return selectList(new LambdaQueryWrapperX<EntityAccessPermissionDO>()
                .eq(EntityAccessPermissionDO::getEntityId, entityId)
                .eq(EntityAccessPermissionDO::getDeleted, false));
    }

    /**
     * 根据角色ID和实体ID查询权限
     */
    default EntityAccessPermissionDO selectByRoleIdAndEntityId(Long roleId, Long entityId) {
        return selectOne(new LambdaQueryWrapperX<EntityAccessPermissionDO>()
                .eq(EntityAccessPermissionDO::getRoleId, roleId)
                .eq(EntityAccessPermissionDO::getEntityId, entityId)
                .eq(EntityAccessPermissionDO::getDeleted, false));
    }

    /**
     * 根据角色ID列表查询可访问的实体ID列表
     */
    default List<Long> selectAccessibleEntityIds(Collection<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return selectList(new LambdaQueryWrapperX<EntityAccessPermissionDO>()
                        .select(EntityAccessPermissionDO::getEntityId)
                        .in(EntityAccessPermissionDO::getRoleId, roleIds)
                        .eq(EntityAccessPermissionDO::getCanView, true)
                        .eq(EntityAccessPermissionDO::getDeleted, false))
                .stream()
                .map(EntityAccessPermissionDO::getEntityId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    /**
     * 安全版：角色集合为空时直接返回空，避免 IN () 问题
     */
    default List<Long> selectAccessibleEntityIdsSafe(Collection<Long> roleIds) {
        return selectAccessibleEntityIds(roleIds);
    }

    /**
     * 删除角色的所有实体访问权限
     */
    default void deleteByRoleId(Long roleId) {
        delete(new LambdaQueryWrapperX<EntityAccessPermissionDO>()
                .eq(EntityAccessPermissionDO::getRoleId, roleId));
    }

    /**
     * 删除实体的所有访问权限
     */
    default void deleteByEntityId(Long entityId) {
        delete(new LambdaQueryWrapperX<EntityAccessPermissionDO>()
                .eq(EntityAccessPermissionDO::getEntityId, entityId));
    }
}
