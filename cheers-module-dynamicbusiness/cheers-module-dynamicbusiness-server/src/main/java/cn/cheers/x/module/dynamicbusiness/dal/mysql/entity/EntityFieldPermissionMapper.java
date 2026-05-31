package cn.cheers.x.module.dynamicbusiness.dal.mysql.entity;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityFieldPermissionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 字段级权限 Mapper
 */
@Mapper
public interface EntityFieldPermissionMapper extends BaseMapperX<EntityFieldPermissionDO> {

        /**
         * 根据角色ID查询权限列表
         */
        default List<EntityFieldPermissionDO> selectByRoleId(Long roleId) {
                return selectList(new LambdaQueryWrapperX<EntityFieldPermissionDO>()
                        .eq(EntityFieldPermissionDO::getRoleId, roleId)
                        .eq(EntityFieldPermissionDO::getDeleted, false));
        }

        /**
         * 根据模型ID查询权限列表
         */
        default List<EntityFieldPermissionDO> selectByModelId(Long modelId) {
                return selectList(new LambdaQueryWrapperX<EntityFieldPermissionDO>()
                        .eq(EntityFieldPermissionDO::getModelId, modelId)
                        .eq(EntityFieldPermissionDO::getDeleted, false));
        }

        /**
         * 根据字段ID查询权限列表
         */
        default List<EntityFieldPermissionDO> selectByFieldId(Long fieldId) {
                return selectList(new LambdaQueryWrapperX<EntityFieldPermissionDO>()
                        .eq(EntityFieldPermissionDO::getFieldId, fieldId)
                        .eq(EntityFieldPermissionDO::getDeleted, false));
        }

        /**
         * 根据角色ID、模型ID和字段ID查询权限
         */
        default EntityFieldPermissionDO selectByRoleIdAndModelIdAndFieldId(Long roleId, Long modelId, Long fieldId) {
                return selectOne(new LambdaQueryWrapperX<EntityFieldPermissionDO>()
                        .eq(EntityFieldPermissionDO::getRoleId, roleId)
                        .eq(EntityFieldPermissionDO::getModelId, modelId)
                        .eq(EntityFieldPermissionDO::getFieldId, fieldId)
                        .eq(EntityFieldPermissionDO::getDeleted, false));
        }

        /**
         * 根据角色ID列表和模型ID查询权限列表
         */
        default List<EntityFieldPermissionDO> selectByRoleIdsAndModelId(Collection<Long> roleIds, Long modelId) {
                if (roleIds == null || roleIds.isEmpty()) {
                        return Collections.emptyList();
                }
                return selectList(new LambdaQueryWrapperX<EntityFieldPermissionDO>()
                        .in(EntityFieldPermissionDO::getRoleId, roleIds)
                        .eq(EntityFieldPermissionDO::getModelId, modelId)
                        .eq(EntityFieldPermissionDO::getDeleted, false));
        }

        /**
         * 根据角色ID列表和模型ID查询可查看的字段ID列表
         */
        default List<Long> selectViewableFieldIds(Collection<Long> roleIds, Long modelId) {
                if (roleIds == null || roleIds.isEmpty()) {
                        return Collections.emptyList();
                }
                return selectList(new LambdaQueryWrapperX<EntityFieldPermissionDO>()
                                .select(EntityFieldPermissionDO::getFieldId)
                                .in(EntityFieldPermissionDO::getRoleId, roleIds)
                                .eq(EntityFieldPermissionDO::getModelId, modelId)
                                .eq(EntityFieldPermissionDO::getCanView, true)
                                .eq(EntityFieldPermissionDO::getDeleted, false))
                        .stream()
                        .map(EntityFieldPermissionDO::getFieldId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList();
        }

        /**
         * 安全版：角色集合为空时直接返回空，避免 IN () 问题
         */
        default List<Long> selectViewableFieldIdsSafe(Collection<Long> roleIds, Long modelId) {
                if (roleIds == null || roleIds.isEmpty()) {
                        return Collections.emptyList();
                }
                return selectViewableFieldIds(roleIds, modelId);
        }

        /**
         * 根据角色ID列表和模型ID查询可编辑的字段ID列表
         */
        default List<Long> selectEditableFieldIds(Collection<Long> roleIds, Long modelId) {
                if (roleIds == null || roleIds.isEmpty()) {
                        return Collections.emptyList();
                }
                return selectList(new LambdaQueryWrapperX<EntityFieldPermissionDO>()
                                .select(EntityFieldPermissionDO::getFieldId)
                                .in(EntityFieldPermissionDO::getRoleId, roleIds)
                                .eq(EntityFieldPermissionDO::getModelId, modelId)
                                .eq(EntityFieldPermissionDO::getCanEdit, true)
                                .eq(EntityFieldPermissionDO::getDeleted, false))
                        .stream()
                        .map(EntityFieldPermissionDO::getFieldId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList();
        }

        /**
         * 安全版：角色集合为空时直接返回空，避免 IN () 问题
         */
        default List<Long> selectEditableFieldIdsSafe(Collection<Long> roleIds, Long modelId) {
                if (roleIds == null || roleIds.isEmpty()) {
                        return Collections.emptyList();
                }
                return selectEditableFieldIds(roleIds, modelId);
        }

        /**
         * 删除角色的所有字段权限
         */
        default void deleteByRoleId(Long roleId) {
                delete(new LambdaQueryWrapperX<EntityFieldPermissionDO>()
                        .eq(EntityFieldPermissionDO::getRoleId, roleId));
        }

        /**
         * 删除模型的所有字段权限
         */
        default void deleteByModelId(Long modelId) {
                delete(new LambdaQueryWrapperX<EntityFieldPermissionDO>()
                        .eq(EntityFieldPermissionDO::getModelId, modelId));
        }

        /**
         * 删除字段的所有权限
         */
        default void deleteByFieldId(Long fieldId) {
                delete(new LambdaQueryWrapperX<EntityFieldPermissionDO>()
                        .eq(EntityFieldPermissionDO::getFieldId, fieldId));
        }
}
