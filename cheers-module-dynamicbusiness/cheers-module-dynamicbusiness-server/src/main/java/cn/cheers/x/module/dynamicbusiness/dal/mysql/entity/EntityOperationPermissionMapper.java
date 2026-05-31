package cn.cheers.x.module.dynamicbusiness.dal.mysql.entity;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityOperationPermissionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 实体操作权限 Mapper
 */
@Mapper
public interface EntityOperationPermissionMapper extends BaseMapperX<EntityOperationPermissionDO> {

    /**
     * 根据角色ID查询权限列表
     */
    default List<EntityOperationPermissionDO> selectByRoleId(Long roleId) {
        return selectList(new LambdaQueryWrapperX<EntityOperationPermissionDO>()
                .eq(EntityOperationPermissionDO::getRoleId, roleId)
                .eq(EntityOperationPermissionDO::getDeleted, false));
    }

    /**
     * 根据实体ID查询权限列表
     */
    default List<EntityOperationPermissionDO> selectByEntityId(Long entityId) {
        return selectList(new LambdaQueryWrapperX<EntityOperationPermissionDO>()
                .eq(EntityOperationPermissionDO::getEntityId, entityId)
                .eq(EntityOperationPermissionDO::getDeleted, false));
    }

    /**
     * 根据模型ID查询权限列表
     */
    default List<EntityOperationPermissionDO> selectByModelId(Long modelId) {
        return selectList(new LambdaQueryWrapperX<EntityOperationPermissionDO>()
                .eq(EntityOperationPermissionDO::getModelId, modelId)
                .eq(EntityOperationPermissionDO::getDeleted, false));
    }

    /**
     * 根据角色ID列表和实体ID查询权限列表
     */
    default List<EntityOperationPermissionDO> selectByRoleIdsAndEntityId(Collection<Long> roleIds, Long entityId) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return selectList(new LambdaQueryWrapperX<EntityOperationPermissionDO>()
                .in(EntityOperationPermissionDO::getRoleId, roleIds)
                .eq(EntityOperationPermissionDO::getEntityId, entityId)
                .eq(EntityOperationPermissionDO::getDeleted, false));
    }

    /**
     * 根据角色ID列表和模型ID查询权限列表（模型级别权限）
     */
    default List<EntityOperationPermissionDO> selectByRoleIdsAndModelId(Collection<Long> roleIds, Long modelId) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return selectList(new LambdaQueryWrapperX<EntityOperationPermissionDO>()
                .in(EntityOperationPermissionDO::getRoleId, roleIds)
                .eq(EntityOperationPermissionDO::getModelId, modelId)
                .isNull(EntityOperationPermissionDO::getEntityId)
                .eq(EntityOperationPermissionDO::getDeleted, false));
    }

    /**
     * 删除角色的所有操作权限
     */
    default void deleteByRoleId(Long roleId) {
        delete(new LambdaQueryWrapperX<EntityOperationPermissionDO>()
                .eq(EntityOperationPermissionDO::getRoleId, roleId));
    }

    /**
     * 删除实体的所有操作权限
     */
    default void deleteByEntityId(Long entityId) {
        delete(new LambdaQueryWrapperX<EntityOperationPermissionDO>()
                .eq(EntityOperationPermissionDO::getEntityId, entityId));
    }

    /**
     * 删除模型的所有操作权限
     */
    default void deleteByModelId(Long modelId) {
        delete(new LambdaQueryWrapperX<EntityOperationPermissionDO>()
                .eq(EntityOperationPermissionDO::getModelId, modelId));
    }

    /**
     * 根据角色ID和模型ID查询权限（模型级别，entityId为空）
     */
    default EntityOperationPermissionDO selectByRoleIdAndModelId(Long roleId, Long modelId) {
        return selectOne(new LambdaQueryWrapperX<EntityOperationPermissionDO>()
                .eq(EntityOperationPermissionDO::getRoleId, roleId)
                .eq(EntityOperationPermissionDO::getModelId, modelId)
                .isNull(EntityOperationPermissionDO::getEntityId)
                .eq(EntityOperationPermissionDO::getDeleted, false));
    }

    /**
     * 根据角色ID和实体ID查询权限（实体级别）
     */
    default EntityOperationPermissionDO selectByRoleIdAndEntityId(Long roleId, Long entityId) {
        return selectOne(new LambdaQueryWrapperX<EntityOperationPermissionDO>()
                .eq(EntityOperationPermissionDO::getRoleId, roleId)
                .eq(EntityOperationPermissionDO::getEntityId, entityId)
                .eq(EntityOperationPermissionDO::getDeleted, false));
    }
}
