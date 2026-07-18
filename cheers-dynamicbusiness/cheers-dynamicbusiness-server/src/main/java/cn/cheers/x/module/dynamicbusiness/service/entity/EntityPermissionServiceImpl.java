package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryPermissionCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryPermissionRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.*;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryPermissionDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityAccessPermissionDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityFieldPermissionDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityOperationPermissionDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryPermissionMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityAccessPermissionMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityFieldPermissionMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityOperationPermissionMapper;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.system.api.permission.RoleApi;
import cn.cheers.x.system.api.permission.dto.RoleRespDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 业务实体权限管理 Service 实现类
 *
 * <p>通过 EntityService 进行实体查询，支持多存储策略（通用表和动态表）。</p>
 *
 * @author 系统模块
 */
@Service
@Validated
@Slf4j
public class EntityPermissionServiceImpl implements EntityPermissionService {

    @Resource
    private EntityAccessPermissionMapper accessPermissionMapper;

    @Resource
    private EntityOperationPermissionMapper operationPermissionMapper;

    @Resource
    private EntityFieldPermissionMapper fieldPermissionMapper;

    @Resource
    private CategoryPermissionMapper categoryPermissionMapper;

    @Resource
    @Lazy // 避免循环依赖
    private EntityService entityService;

    @Resource
    private RoleApi roleApi;

    // ========== 实体访问权限 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAccessPermission(EntityAccessPermissionCreateReqVO reqVO) {
        // 检查是否已存在
        EntityAccessPermissionDO existing = accessPermissionMapper.selectByRoleIdAndEntityId(
                reqVO.getRoleId(), reqVO.getEntityId());
        if (existing != null) {
            throw new ServiceException(400, "该角色对该实体的访问权限已存在");
        }

        EntityAccessPermissionDO permission = BeanUtils.toBean(reqVO, EntityAccessPermissionDO.class);
        accessPermissionMapper.insert(permission);
        return permission.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAccessPermission(Long id, Boolean canView) {
        EntityAccessPermissionDO permission = accessPermissionMapper.selectById(id);
        if (permission == null) {
            throw new ServiceException(404, "访问权限不存在");
        }
        permission.setCanView(canView);
        accessPermissionMapper.updateById(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAccessPermission(Long id) {
        accessPermissionMapper.deleteById(id);
    }

    @Override
    @Deprecated
    public EntityAccessPermissionRespVO getAccessPermission(Long id) {
        EntityAccessPermissionDO permission = accessPermissionMapper.selectById(id);
        if (permission == null) {
            throw new ServiceException(404, "访问权限不存在");
        }
        EntityAccessPermissionRespVO respVO = BeanUtils.toBean(permission, EntityAccessPermissionRespVO.class);
        // 填充角色名称
        RoleRespDTO role = getRole(permission.getRoleId());
        if (role != null) {
            respVO.setRoleName(role.getName());
        }
        // 填充实体名称（通过 EntityService 支持多存储策略）
        // 注意：此方法已废弃，无法正确处理动态表存储的实体
        log.warn("[getAccessPermission][使用了已废弃的方法，permissionId={}，建议使用 getAccessPermission(id, entityTypeCode)]", id);
        try {
            EntityRespVO entity = entityService.get(permission.getEntityId(), null);
            if (entity != null) {
                respVO.setEntityName(entity.getName());
            }
        } catch (Exception e) {
            log.debug("获取实体名称失败: entityId={}", permission.getEntityId());
        }
        return respVO;
    }

    @Override
    public EntityAccessPermissionRespVO getAccessPermission(Long id, String entityTypeCode) {
        EntityAccessPermissionDO permission = accessPermissionMapper.selectById(id);
        if (permission == null) {
            throw new ServiceException(404, "访问权限不存在");
        }
        EntityAccessPermissionRespVO respVO = BeanUtils.toBean(permission, EntityAccessPermissionRespVO.class);
        // 填充 entityTypeCode
        respVO.setEntityTypeCode(entityTypeCode);
        // 填充角色名称
        RoleRespDTO role = getRole(permission.getRoleId());
        if (role != null) {
            respVO.setRoleName(role.getName());
        }
        // 填充实体名称（通过 EntityService 支持多存储策略，使用 entityTypeCode 路由）
        try {
            EntityRespVO entity = entityService.get(permission.getEntityId(), entityTypeCode);
            if (entity != null) {
                respVO.setEntityName(entity.getName());
            }
        } catch (Exception e) {
            log.debug("获取实体名称失败: entityId={}, entityTypeCode={}", permission.getEntityId(), entityTypeCode);
        }
        return respVO;
    }

    @Override
    public List<EntityAccessPermissionRespVO> getAccessPermissionsByRoleId(Long roleId) {
        List<EntityAccessPermissionDO> permissions = accessPermissionMapper.selectByRoleId(roleId);
        return convertAccessPermissionList(permissions);
    }

    @Override
    @Deprecated
    public List<EntityAccessPermissionRespVO> getAccessPermissionsByEntityId(Long entityId) {
        log.warn("[getAccessPermissionsByEntityId][使用了已废弃的方法，entityId={}，建议使用 getAccessPermissionsByEntityId(entityId, entityTypeCode)]", entityId);
        List<EntityAccessPermissionDO> permissions = accessPermissionMapper.selectByEntityId(entityId);
        return convertAccessPermissionList(permissions);
    }

    @Override
    public List<EntityAccessPermissionRespVO> getAccessPermissionsByEntityId(Long entityId, String entityTypeCode) {
        List<EntityAccessPermissionDO> permissions = accessPermissionMapper.selectByEntityId(entityId);
        return convertAccessPermissionList(permissions, entityTypeCode);
    }

    @Override
    public boolean canAccessEntity(Long userId, Long entityId) {
        Set<Long> roleIds = getUserRoleIds(userId);
        if (roleIds.isEmpty()) {
            return false;
        }
        List<Long> accessibleEntityIds = accessPermissionMapper.selectAccessibleEntityIds(roleIds);
        return accessibleEntityIds.contains(entityId);
    }

    @Override
    public Set<Long> getAccessibleEntityIds(Long userId) {
        Set<Long> roleIds = getUserRoleIds(userId);
        if (roleIds.isEmpty()) {
            return Collections.emptySet();
        }
        List<Long> accessibleEntityIds = accessPermissionMapper.selectAccessibleEntityIds(roleIds);
        return new HashSet<>(accessibleEntityIds);
    }

    // ========== 实体操作权限 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOperationPermission(EntityOperationPermissionCreateReqVO reqVO) {
        EntityOperationPermissionDO permission = BeanUtils.toBean(reqVO, EntityOperationPermissionDO.class);
        operationPermissionMapper.insert(permission);
        return permission.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOperationPermission(Long id, Boolean canCreate, Boolean canUpdate, Boolean canDelete) {
        EntityOperationPermissionDO permission = operationPermissionMapper.selectById(id);
        if (permission == null) {
            throw new ServiceException(404, "操作权限不存在");
        }
        if (canCreate != null) {
            permission.setCanCreate(canCreate);
        }
        if (canUpdate != null) {
            permission.setCanUpdate(canUpdate);
        }
        if (canDelete != null) {
            permission.setCanDelete(canDelete);
        }
        operationPermissionMapper.updateById(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOperationPermission(Long id) {
        operationPermissionMapper.deleteById(id);
    }

    @Override
    @Deprecated
    public EntityOperationPermissionRespVO getOperationPermission(Long id) {
        EntityOperationPermissionDO permission = operationPermissionMapper.selectById(id);
        if (permission == null) {
            throw new ServiceException(404, "操作权限不存在");
        }
        return BeanUtils.toBean(permission, EntityOperationPermissionRespVO.class);
    }

    @Override
    public EntityOperationPermissionRespVO getOperationPermission(Long id, String entityTypeCode) {
        EntityOperationPermissionDO permission = operationPermissionMapper.selectById(id);
        if (permission == null) {
            throw new ServiceException(404, "操作权限不存在");
        }
        EntityOperationPermissionRespVO respVO = BeanUtils.toBean(permission, EntityOperationPermissionRespVO.class);
        // 填充 entityTypeCode
        respVO.setEntityTypeCode(entityTypeCode);
        return respVO;
    }

    @Override
    public List<EntityOperationPermissionRespVO> getOperationPermissionsByRoleId(Long roleId) {
        List<EntityOperationPermissionDO> permissions = operationPermissionMapper.selectByRoleId(roleId);
        return BeanUtils.toBean(permissions, EntityOperationPermissionRespVO.class);
    }

    @Override
    public boolean canCreateEntity(Long userId, Long modelId) {
        Set<Long> roleIds = getUserRoleIds(userId);
        if (roleIds.isEmpty()) {
            return false;
        }
        List<EntityOperationPermissionDO> permissions = operationPermissionMapper.selectByRoleIdsAndModelId(roleIds, modelId);
        return permissions.stream().anyMatch(p -> Boolean.TRUE.equals(p.getCanCreate()));
    }

    @Override
    @Deprecated
    public boolean canUpdateEntity(Long userId, Long entityId) {
        // 此方法已废弃，建议使用 canUpdateEntity(Long userId, Long entityId, String entityTypeCode)
        log.warn("使用了已废弃的 canUpdateEntity(userId, entityId) 方法，建议使用带 entityTypeCode 的版本");
        return false;
    }

    @Override
    public boolean canUpdateEntity(Long userId, Long entityId, String entityTypeCode) {
        Set<Long> roleIds = getUserRoleIds(userId);
        if (roleIds.isEmpty()) {
            return false;
        }
        // 通过 EntityService 获取实体（支持多存储策略，使用 entityTypeCode 路由）
        EntityRespVO entity;
        try {
            entity = entityService.get(entityId, entityTypeCode);
        } catch (Exception e) {
            log.debug("获取实体失败: entityId={}, entityTypeCode={}", entityId, entityTypeCode);
            return false;
        }
        if (entity == null) {
            return false;
        }
        // 检查实体级别的权限
        List<EntityOperationPermissionDO> entityPermissions = operationPermissionMapper.selectByRoleIdsAndEntityId(roleIds, entityId);
        if (!entityPermissions.isEmpty()) {
            return entityPermissions.stream().anyMatch(p -> Boolean.TRUE.equals(p.getCanUpdate()));
        }
        // 检查模型级别的权限
        List<EntityOperationPermissionDO> modelPermissions = operationPermissionMapper.selectByRoleIdsAndModelId(roleIds, entity.getModelId());
        return modelPermissions.stream().anyMatch(p -> Boolean.TRUE.equals(p.getCanUpdate()));
    }

    @Override
    @Deprecated
    public boolean canDeleteEntity(Long userId, Long entityId) {
        // 此方法已废弃，建议使用 canDeleteEntity(Long userId, Long entityId, String entityTypeCode)
        log.warn("使用了已废弃的 canDeleteEntity(userId, entityId) 方法，建议使用带 entityTypeCode 的版本");
        return false;
    }

    @Override
    public boolean canDeleteEntity(Long userId, Long entityId, String entityTypeCode) {
        Set<Long> roleIds = getUserRoleIds(userId);
        if (roleIds.isEmpty()) {
            return false;
        }
        // 通过 EntityService 获取实体（支持多存储策略，使用 entityTypeCode 路由）
        EntityRespVO entity;
        try {
            entity = entityService.get(entityId, entityTypeCode);
        } catch (Exception e) {
            log.debug("获取实体失败: entityId={}, entityTypeCode={}", entityId, entityTypeCode);
            return false;
        }
        if (entity == null) {
            return false;
        }
        // 检查实体级别的权限
        List<EntityOperationPermissionDO> entityPermissions = operationPermissionMapper.selectByRoleIdsAndEntityId(roleIds, entityId);
        if (!entityPermissions.isEmpty()) {
            return entityPermissions.stream().anyMatch(p -> Boolean.TRUE.equals(p.getCanDelete()));
        }
        // 检查模型级别的权限
        List<EntityOperationPermissionDO> modelPermissions = operationPermissionMapper.selectByRoleIdsAndModelId(roleIds, entity.getModelId());
        return modelPermissions.stream().anyMatch(p -> Boolean.TRUE.equals(p.getCanDelete()));
    }

    // ========== 字段级权限 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createFieldPermission(EntityFieldPermissionCreateReqVO reqVO) {
        EntityFieldPermissionDO permission = BeanUtils.toBean(reqVO, EntityFieldPermissionDO.class);
        fieldPermissionMapper.insert(permission);
        return permission.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFieldPermission(Long id, Boolean canView, Boolean canEdit) {
        EntityFieldPermissionDO permission = fieldPermissionMapper.selectById(id);
        if (permission == null) {
            throw new ServiceException(404, "字段权限不存在");
        }
        if (canView != null) {
            permission.setCanView(canView);
        }
        if (canEdit != null) {
            permission.setCanEdit(canEdit);
        }
        fieldPermissionMapper.updateById(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFieldPermission(Long id) {
        fieldPermissionMapper.deleteById(id);
    }

    @Override
    public EntityFieldPermissionRespVO getFieldPermission(Long id) {
        EntityFieldPermissionDO permission = fieldPermissionMapper.selectById(id);
        if (permission == null) {
            throw new ServiceException(404, "字段权限不存在");
        }
        return BeanUtils.toBean(permission, EntityFieldPermissionRespVO.class);
    }

    @Override
    public List<EntityFieldPermissionRespVO> getFieldPermissionsByRoleId(Long roleId) {
        List<EntityFieldPermissionDO> permissions = fieldPermissionMapper.selectByRoleId(roleId);
        return BeanUtils.toBean(permissions, EntityFieldPermissionRespVO.class);
    }

    @Override
    public List<EntityFieldPermissionRespVO> getFieldPermissionsByModelId(Long modelId) {
        List<EntityFieldPermissionDO> permissions = fieldPermissionMapper.selectByModelId(modelId);
        return BeanUtils.toBean(permissions, EntityFieldPermissionRespVO.class);
    }

    @Override
    public Set<Long> getViewableFieldIds(Long userId, Long modelId) {
        Set<Long> roleIds = getUserRoleIds(userId);
        if (roleIds.isEmpty()) {
            return Collections.emptySet();
        }
        List<Long> viewableFieldIds = fieldPermissionMapper.selectViewableFieldIds(roleIds, modelId);
        return new HashSet<>(viewableFieldIds);
    }

    @Override
    public Set<Long> getEditableFieldIds(Long userId, Long modelId) {
        Set<Long> roleIds = getUserRoleIds(userId);
        if (roleIds.isEmpty()) {
            return Collections.emptySet();
        }
        List<Long> editableFieldIds = fieldPermissionMapper.selectEditableFieldIds(roleIds, modelId);
        return new HashSet<>(editableFieldIds);
    }

    // ========== 分类级权限 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCategoryPermission(CategoryPermissionCreateReqVO reqVO) {
        CategoryPermissionDO permission = BeanUtils.toBean(reqVO, CategoryPermissionDO.class);
        categoryPermissionMapper.insert(permission);
        return permission.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategoryPermission(Long id, Boolean canView, Boolean canManage) {
        CategoryPermissionDO permission = categoryPermissionMapper.selectById(id);
        if (permission == null) {
            throw new ServiceException(404, "分类权限不存在");
        }
        if (canView != null) {
            permission.setCanView(canView);
        }
        if (canManage != null) {
            permission.setCanManage(canManage);
        }
        categoryPermissionMapper.updateById(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategoryPermission(Long id) {
        categoryPermissionMapper.deleteById(id);
    }

    @Override
    public CategoryPermissionRespVO getCategoryPermission(Long id) {
        CategoryPermissionDO permission = categoryPermissionMapper.selectById(id);
        if (permission == null) {
            throw new ServiceException(404, "分类权限不存在");
        }
        return BeanUtils.toBean(permission, CategoryPermissionRespVO.class);
    }

    @Override
    public List<CategoryPermissionRespVO> getCategoryPermissionsByRoleId(Long roleId) {
        List<CategoryPermissionDO> permissions = categoryPermissionMapper.selectByRoleId(roleId);
        return BeanUtils.toBean(permissions, CategoryPermissionRespVO.class);
    }

    @Override
    public List<CategoryPermissionRespVO> getCategoryPermissionsByCategoryId(Long categoryId) {
        List<CategoryPermissionDO> permissions = categoryPermissionMapper.selectByCategoryId(categoryId);
        return BeanUtils.toBean(permissions, CategoryPermissionRespVO.class);
    }

    @Override
    public boolean canAccessCategory(Long userId, Long categoryId) {
        Set<Long> roleIds = getUserRoleIds(userId);
        if (roleIds.isEmpty()) {
            return false;
        }
        List<Long> viewableCategoryIds = categoryPermissionMapper.selectViewableCategoryIdsByRoleIds(roleIds);
        return viewableCategoryIds.contains(categoryId);
    }

    @Override
    public boolean canManageCategory(Long userId, Long categoryId) {
        Set<Long> roleIds = getUserRoleIds(userId);
        if (roleIds.isEmpty()) {
            return false;
        }
        List<Long> manageableCategoryIds = categoryPermissionMapper.selectManageableCategoryIdsByRoleIds(roleIds);
        return manageableCategoryIds.contains(categoryId);
    }

    @Override
    public Set<Long> getAccessibleCategoryIds(Long userId) {
        Set<Long> roleIds = getUserRoleIds(userId);
        if (roleIds.isEmpty()) {
            return Collections.emptySet();
        }
        List<Long> viewableCategoryIds = categoryPermissionMapper.selectViewableCategoryIdsByRoleIds(roleIds);
        return new HashSet<>(viewableCategoryIds);
    }
    
    @Override
    public Set<Long> getManageableCategoryIds(Long userId) {
        Set<Long> roleIds = getUserRoleIds(userId);
        if (roleIds.isEmpty()) {
            return Collections.emptySet();
        }
        List<Long> manageableCategoryIds = categoryPermissionMapper.selectManageableCategoryIdsByRoleIds(roleIds);
        return new HashSet<>(manageableCategoryIds);
    }

    // ========== 综合权限检查 ==========

    @Override
    @Deprecated
    public EntityPermissionCheckRespVO checkEntityPermission(Long userId, Long entityId) {
        // 此方法已废弃，建议使用 checkEntityPermission(Long userId, Long entityId, String entityTypeCode)
        log.warn("使用了已废弃的 checkEntityPermission(userId, entityId) 方法，建议使用带 entityTypeCode 的版本");
        throw new ServiceException(400, "请使用带 entityTypeCode 的方法");
    }

    @Override
    public EntityPermissionCheckRespVO checkEntityPermission(Long userId, Long entityId, String entityTypeCode) {
        // 通过 EntityService 获取实体（支持多存储策略，使用 entityTypeCode 路由）
        EntityRespVO entity;
        try {
            entity = entityService.get(entityId, entityTypeCode);
        } catch (Exception e) {
            log.warn("获取实体失败: entityId={}, entityTypeCode={}, error={}", entityId, entityTypeCode, e.getMessage());
            throw new ServiceException(404, "实体不存在");
        }
        if (entity == null) {
            throw new ServiceException(404, "实体不存在");
        }

        EntityPermissionCheckRespVO respVO = EntityPermissionCheckRespVO.builder()
                .canAccess(canAccessEntity(userId, entityId))
                .canCreate(canCreateEntity(userId, entity.getModelId()))
                .canUpdate(canUpdateEntity(userId, entityId, entityTypeCode))
                .canDelete(canDeleteEntity(userId, entityId, entityTypeCode))
                .viewableFieldIds(getViewableFieldIds(userId, entity.getModelId()))
                .editableFieldIds(getEditableFieldIds(userId, entity.getModelId()))
                .build();

        return respVO;
    }

    @Override
    public EntityPermissionCheckRespVO checkModelPermission(Long userId, Long modelId) {
        EntityPermissionCheckRespVO respVO = EntityPermissionCheckRespVO.builder()
                .canCreate(canCreateEntity(userId, modelId))
                .viewableFieldIds(getViewableFieldIds(userId, modelId))
                .editableFieldIds(getEditableFieldIds(userId, modelId))
                .build();

        return respVO;
    }

    // ========== 批量操作 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCreateAccessPermissions(List<Long> roleIds, List<Long> entityIds, Boolean canView) {
        for (Long roleId : roleIds) {
            for (Long entityId : entityIds) {
                EntityAccessPermissionDO existing = accessPermissionMapper.selectByRoleIdAndEntityId(roleId, entityId);
                if (existing == null) {
                    EntityAccessPermissionDO permission = EntityAccessPermissionDO.builder()
                            .roleId(roleId)
                            .entityId(entityId)
                            .canView(canView)
                            .build();
                    accessPermissionMapper.insert(permission);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCreateOperationPermissions(List<Long> roleIds, Long modelId, List<Long> entityIds,
                                                 Boolean canCreate, Boolean canUpdate, Boolean canDelete) {
        for (Long roleId : roleIds) {
            // 模型级别权限：entityIds 为空或 null 时，创建模型级别的操作权限
            if (entityIds == null || entityIds.isEmpty()) {
                if (modelId == null) {
                    throw new ServiceException(400, "模型ID和实体ID列表不能同时为空");
                }
                EntityOperationPermissionDO existing = operationPermissionMapper.selectByRoleIdAndModelId(roleId, modelId);
                if (existing == null) {
                    EntityOperationPermissionDO permission = EntityOperationPermissionDO.builder()
                            .roleId(roleId)
                            .modelId(modelId)
                            .entityId(null)
                            .canCreate(canCreate != null ? canCreate : false)
                            .canUpdate(canUpdate != null ? canUpdate : false)
                            .canDelete(canDelete != null ? canDelete : false)
                            .build();
                    operationPermissionMapper.insert(permission);
                }
            } else {
                // 实体级别权限：为每个实体创建操作权限
                for (Long entityId : entityIds) {
                    EntityOperationPermissionDO existing = operationPermissionMapper.selectByRoleIdAndEntityId(roleId, entityId);
                    if (existing == null) {
                        EntityOperationPermissionDO permission = EntityOperationPermissionDO.builder()
                                .roleId(roleId)
                                .modelId(modelId)
                                .entityId(entityId)
                                .canCreate(canCreate != null ? canCreate : false)
                                .canUpdate(canUpdate != null ? canUpdate : false)
                                .canDelete(canDelete != null ? canDelete : false)
                                .build();
                        operationPermissionMapper.insert(permission);
                    }
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCreateFieldPermissions(List<Long> roleIds, Long modelId, List<Long> fieldIds, Boolean canView, Boolean canEdit) {
        for (Long roleId : roleIds) {
            for (Long fieldId : fieldIds) {
                EntityFieldPermissionDO existing = fieldPermissionMapper.selectByRoleIdAndModelIdAndFieldId(roleId, modelId, fieldId);
                if (existing == null) {
                    EntityFieldPermissionDO permission = EntityFieldPermissionDO.builder()
                            .roleId(roleId)
                            .modelId(modelId)
                            .fieldId(fieldId)
                            .canView(canView)
                            .canEdit(canEdit)
                            .build();
                    fieldPermissionMapper.insert(permission);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCreateCategoryPermissions(List<Long> roleIds, List<Long> categoryIds, Boolean canView, Boolean canManage) {
        for (Long roleId : roleIds) {
            for (Long categoryId : categoryIds) {
                CategoryPermissionDO existing = categoryPermissionMapper.selectByRoleIdAndCategoryId(roleId, categoryId);
                if (existing == null) {
                    CategoryPermissionDO permission = CategoryPermissionDO.builder()
                            .roleId(roleId)
                            .categoryId(categoryId)
                            .canView(canView)
                            .canManage(canManage)
                            .build();
                    categoryPermissionMapper.insert(permission);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllPermissionsByRoleId(Long roleId) {
        accessPermissionMapper.deleteByRoleId(roleId);
        operationPermissionMapper.deleteByRoleId(roleId);
        fieldPermissionMapper.deleteByRoleId(roleId);
        categoryPermissionMapper.deleteByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllPermissionsByEntityId(Long entityId) {
        accessPermissionMapper.deleteByEntityId(entityId);
        operationPermissionMapper.deleteByEntityId(entityId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllPermissionsByModelId(Long modelId) {
        operationPermissionMapper.deleteByModelId(modelId);
        fieldPermissionMapper.deleteByModelId(modelId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllPermissionsByCategoryId(Long categoryId) {
        categoryPermissionMapper.deleteByCategoryId(categoryId);
    }

    // ========== 私有辅助方法 ==========

    private RoleRespDTO getRole(Long roleId) {
        if (roleApi == null) {
            log.debug("获取角色失败: RoleApi bean 不可用，roleId={}", roleId);
            return null;
        }
        try {
            CommonResult<RoleRespDTO> result = roleApi.getRole(roleId);
            return result != null ? result.getData() : null;
        } catch (Exception e) {
            log.debug("获取角色失败: roleId={}", roleId);
            return null;
        }
    }

    private List<RoleRespDTO> getRoleList(Set<Long> roleIds) {
        if (roleApi == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            CommonResult<List<RoleRespDTO>> result = roleApi.getRoleList(roleIds);
            return result != null && result.getData() != null ? result.getData() : Collections.emptyList();
        } catch (Exception e) {
            log.debug("批量获取角色失败: roleIds={}", roleIds);
            return Collections.emptyList();
        }
    }

    private Set<Long> getUserRoleIds(Long userId) {
        try {
            CommonResult<Collection<Long>> result = roleApi.getUserRoleIdListByUserId(userId);
            if (result == null || result.getData() == null) {
                return Collections.emptySet();
            }
            return new HashSet<>(result.getData());
        } catch (Exception e) {
            log.debug("获取用户角色失败: userId={}", userId);
            return Collections.emptySet();
        }
    }

    /**
     * 转换访问权限列表
     * 
     * @deprecated 此方法无法正确处理动态表存储的实体，建议使用 convertAccessPermissionList(permissions, entityTypeCode)
     */
    @Deprecated
    private List<EntityAccessPermissionRespVO> convertAccessPermissionList(List<EntityAccessPermissionDO> permissions) {
        if (permissions.isEmpty()) {
            return Collections.emptyList();
        }

        // 批量查询角色信息
        Set<Long> roleIds = permissions.stream().map(EntityAccessPermissionDO::getRoleId).collect(Collectors.toSet());
        Map<Long, RoleRespDTO> roleMap = getRoleList(roleIds).stream()
                .collect(Collectors.toMap(RoleRespDTO::getId, r -> r));

        // 注意：此方法无法正确处理动态表存储的实体
        // 建议在调用方传入 entityTypeCode 或从权限记录中获取
        return permissions.stream().map(permission -> {
            EntityAccessPermissionRespVO respVO = BeanUtils.toBean(permission, EntityAccessPermissionRespVO.class);
            RoleRespDTO role = roleMap.get(permission.getRoleId());
            if (role != null) {
                respVO.setRoleName(role.getName());
            }
            // 实体名称无法获取，因为没有 entityTypeCode
            return respVO;
        }).collect(Collectors.toList());
    }

    /**
     * 转换访问权限列表（带 entityTypeCode）
     */
    private List<EntityAccessPermissionRespVO> convertAccessPermissionList(List<EntityAccessPermissionDO> permissions, String entityTypeCode) {
        if (permissions.isEmpty()) {
            return Collections.emptyList();
        }

        // 批量查询角色信息
        Set<Long> roleIds = permissions.stream().map(EntityAccessPermissionDO::getRoleId).collect(Collectors.toSet());
        Map<Long, RoleRespDTO> roleMap = getRoleList(roleIds).stream()
                .collect(Collectors.toMap(RoleRespDTO::getId, r -> r));

        // 批量查询实体信息（通过 EntityService 支持多存储策略，使用 entityTypeCode 路由）
        Set<Long> entityIds = permissions.stream().map(EntityAccessPermissionDO::getEntityId).collect(Collectors.toSet());
        Map<Long, EntityRespVO> entityMap = new HashMap<>();
        for (Long entityId : entityIds) {
            try {
                EntityRespVO entity = entityService.get(entityId, entityTypeCode);
                if (entity != null) {
                    entityMap.put(entityId, entity);
                }
            } catch (Exception e) {
                log.debug("获取实体失败: entityId={}, entityTypeCode={}", entityId, entityTypeCode);
            }
        }

        return permissions.stream().map(permission -> {
            EntityAccessPermissionRespVO respVO = BeanUtils.toBean(permission, EntityAccessPermissionRespVO.class);
            respVO.setEntityTypeCode(entityTypeCode);
            RoleRespDTO role = roleMap.get(permission.getRoleId());
            if (role != null) {
                respVO.setRoleName(role.getName());
            }
            EntityRespVO entity = entityMap.get(permission.getEntityId());
            if (entity != null) {
                respVO.setEntityName(entity.getName());
            }
            return respVO;
        }).collect(Collectors.toList());
    }
}

