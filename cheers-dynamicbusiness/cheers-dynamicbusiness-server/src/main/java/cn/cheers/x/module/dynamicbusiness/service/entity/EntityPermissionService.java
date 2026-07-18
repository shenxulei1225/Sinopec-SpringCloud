package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryPermissionCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryPermissionRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.*;

import java.util.List;
import java.util.Set;

/**
 * 业务实体权限管理 Service 接口
 * 
 * 提供访问权限、操作权限、字段级权限、分类级权限的管理功能
 * 当用户拥有多个角色时，所有权限类型都取并集（最宽松）
 */
public interface EntityPermissionService {

    // ========== 实体访问权限 ==========

    /**
     * 创建实体访问权限
     */
    Long createAccessPermission(EntityAccessPermissionCreateReqVO reqVO);

    /**
     * 更新实体访问权限
     */
    void updateAccessPermission(Long id, Boolean canView);

    /**
     * 删除实体访问权限
     */
    void deleteAccessPermission(Long id);

    /**
     * 获取实体访问权限
     * 
     * @deprecated 使用 {@link #getAccessPermission(Long, String)} 代替，需要传递 entityTypeCode 以支持动态表路由
     */
    @Deprecated
    EntityAccessPermissionRespVO getAccessPermission(Long id);

    /**
     * 获取实体访问权限（指定业务类型）
     * 
     * <p>根据业务类型路由到对应的存储策略查询实体信息。
     * 推荐使用此方法，避免先查 dynamic_entity 表。</p>
     * 
     * @param id 权限ID
     * @param entityTypeCode 业务类型编码
     * @return 访问权限详情
     */
    EntityAccessPermissionRespVO getAccessPermission(Long id, String entityTypeCode);

    /**
     * 获取角色的实体访问权限列表
     */
    List<EntityAccessPermissionRespVO> getAccessPermissionsByRoleId(Long roleId);

    /**
     * 获取实体的访问权限列表
     * 
     * @deprecated 使用 {@link #getAccessPermissionsByEntityId(Long, String)} 代替，需要传递 entityTypeCode 以支持动态表路由
     */
    @Deprecated
    List<EntityAccessPermissionRespVO> getAccessPermissionsByEntityId(Long entityId);

    /**
     * 获取实体的访问权限列表（指定业务类型）
     * 
     * <p>根据业务类型路由到对应的存储策略查询实体信息。
     * 推荐使用此方法，避免先查 dynamic_entity 表。</p>
     * 
     * @param entityId 实体ID
     * @param entityTypeCode 业务类型编码
     * @return 访问权限列表
     */
    List<EntityAccessPermissionRespVO> getAccessPermissionsByEntityId(Long entityId, String entityTypeCode);

    /**
     * 检查用户是否可以访问实体
     * 
     * @param userId 用户ID
     * @param entityId 实体ID
     * @return 是否可访问
     */
    boolean canAccessEntity(Long userId, Long entityId);

    /**
     * 获取用户可访问的实体ID列表
     * 
     * @param userId 用户ID
     * @return 可访问的实体ID列表
     */
    Set<Long> getAccessibleEntityIds(Long userId);

    // ========== 实体操作权限 ==========

    /**
     * 创建实体操作权限
     */
    Long createOperationPermission(EntityOperationPermissionCreateReqVO reqVO);

    /**
     * 更新实体操作权限
     */
    void updateOperationPermission(Long id, Boolean canCreate, Boolean canUpdate, Boolean canDelete);

    /**
     * 删除实体操作权限
     */
    void deleteOperationPermission(Long id);

    /**
     * 获取实体操作权限
     *
     * @deprecated 使用 {@link #getOperationPermission(Long, String)} 代替，需要传递 entityTypeCode 以支持动态表路由
     */
    @Deprecated
    EntityOperationPermissionRespVO getOperationPermission(Long id);

    /**
     * 获取实体操作权限（指定业务类型）
     *
     * <p>根据业务类型路由到对应的存储策略查询实体信息。
     * 推荐使用此方法，避免先查 dynamic_entity 表。</p>
     *
     * @param id 权限ID
     * @param entityTypeCode 业务类型编码
     * @return 操作权限详情
     */
    EntityOperationPermissionRespVO getOperationPermission(Long id, String entityTypeCode);

    /**
     * 获取角色的实体操作权限列表
     */
    List<EntityOperationPermissionRespVO> getOperationPermissionsByRoleId(Long roleId);

    /**
     * 检查用户是否可以创建实体
     *
     * @param userId 用户ID
     * @param modelId 模型ID
     * @return 是否可创建
     */
    boolean canCreateEntity(Long userId, Long modelId);

    /**
     * 检查用户是否可以更新实体
     *
     * @deprecated 使用 {@link #canUpdateEntity(Long, Long, String)} 代替，需要传递 entityTypeCode 以支持动态表路由
     * @param userId 用户ID
     * @param entityId 实体ID
     * @return 是否可更新
     */
    @Deprecated
    boolean canUpdateEntity(Long userId, Long entityId);

    /**
     * 检查用户是否可以更新实体（指定业务类型）
     *
     * <p>根据业务类型路由到对应的存储策略查询实体信息。
     * 推荐使用此方法，避免先查 dynamic_entity 表。</p>
     *
     * @param userId 用户ID
     * @param entityId 实体ID
     * @param entityTypeCode 业务类型编码
     * @return 是否可更新
     */
    boolean canUpdateEntity(Long userId, Long entityId, String entityTypeCode);

    /**
     * 检查用户是否可以删除实体
     *
     * @deprecated 使用 {@link #canDeleteEntity(Long, Long, String)} 代替，需要传递 entityTypeCode 以支持动态表路由
     * @param userId 用户ID
     * @param entityId 实体ID
     * @return 是否可删除
     */
    @Deprecated
    boolean canDeleteEntity(Long userId, Long entityId);

    /**
     * 检查用户是否可以删除实体（指定业务类型）
     *
     * <p>根据业务类型路由到对应的存储策略查询实体信息。
     * 推荐使用此方法，避免先查 dynamic_entity 表。</p>
     *
     * @param userId 用户ID
     * @param entityId 实体ID
     * @param entityTypeCode 业务类型编码
     * @return 是否可删除
     */
    boolean canDeleteEntity(Long userId, Long entityId, String entityTypeCode);

    // ========== 字段级权限 ==========

    /**
     * 创建字段级权限
     */
    Long createFieldPermission(EntityFieldPermissionCreateReqVO reqVO);

    /**
     * 更新字段级权限
     */
    void updateFieldPermission(Long id, Boolean canView, Boolean canEdit);

    /**
     * 删除字段级权限
     */
    void deleteFieldPermission(Long id);

    /**
     * 获取字段级权限
     */
    EntityFieldPermissionRespVO getFieldPermission(Long id);

    /**
     * 获取角色的字段级权限列表
     */
    List<EntityFieldPermissionRespVO> getFieldPermissionsByRoleId(Long roleId);

    /**
     * 获取模型的字段级权限列表
     */
    List<EntityFieldPermissionRespVO> getFieldPermissionsByModelId(Long modelId);

    /**
     * 获取用户可查看的字段ID列表
     *
     * @param userId 用户ID
     * @param modelId 模型ID
     * @return 可查看的字段ID列表
     */
    Set<Long> getViewableFieldIds(Long userId, Long modelId);

    /**
     * 获取用户可编辑的字段ID列表
     *
     * @param userId 用户ID
     * @param modelId 模型ID
     * @return 可编辑的字段ID列表
     */
    Set<Long> getEditableFieldIds(Long userId, Long modelId);

    // ========== 分类级权限 ==========

    /**
     * 创建分类级权限
     */
    Long createCategoryPermission(CategoryPermissionCreateReqVO reqVO);

    /**
     * 更新分类级权限
     */
    void updateCategoryPermission(Long id, Boolean canView, Boolean canManage);

    /**
     * 删除分类级权限
     */
    void deleteCategoryPermission(Long id);

    /**
     * 获取分类级权限
     */
    CategoryPermissionRespVO getCategoryPermission(Long id);

    /**
     * 获取角色的分类级权限列表
     */
    List<CategoryPermissionRespVO> getCategoryPermissionsByRoleId(Long roleId);

    /**
     * 获取分类的权限列表
     */
    List<CategoryPermissionRespVO> getCategoryPermissionsByCategoryId(Long categoryId);

    /**
     * 检查用户是否可以访问分类
     *
     * @param userId 用户ID
     * @param categoryId 分类ID
     * @return 是否可访问
     */
    boolean canAccessCategory(Long userId, Long categoryId);

    /**
     * 检查用户是否可以管理分类
     *
     * @param userId 用户ID
     * @param categoryId 分类ID
     * @return 是否可管理
     */
    boolean canManageCategory(Long userId, Long categoryId);

    /**
     * 获取用户可访问的分类ID列表
     *
     * @param userId 用户ID
     * @return 可访问的分类ID列表
     */
    Set<Long> getAccessibleCategoryIds(Long userId);

    /**
     * 获取用户可管理的分类ID列表
     *
     * @param userId 用户ID
     * @return 可管理的分类ID列表
     */
    Set<Long> getManageableCategoryIds(Long userId);

    // ========== 综合权限检查 ==========

    /**
     * 获取用户对实体的完整权限信息
     *
     * @deprecated 使用 {@link #checkEntityPermission(Long, Long, String)} 代替，需要传递 entityTypeCode 以支持动态表路由
     * @param userId 用户ID
     * @param entityId 实体ID
     * @return 权限检查结果
     */
    @Deprecated
    EntityPermissionCheckRespVO checkEntityPermission(Long userId, Long entityId);

    /**
     * 获取用户对实体的完整权限信息（指定业务类型）
     *
     * <p>根据业务类型路由到对应的存储策略查询实体信息。
     * 推荐使用此方法，避免先查 dynamic_entity 表。</p>
     *
     * @param userId 用户ID
     * @param entityId 实体ID
     * @param entityTypeCode 业务类型编码
     * @return 权限检查结果
     */
    EntityPermissionCheckRespVO checkEntityPermission(Long userId, Long entityId, String entityTypeCode);

    /**
     * 获取用户对模型的完整权限信息
     *
     * @param userId 用户ID
     * @param modelId 模型ID
     * @return 权限检查结果
     */
    EntityPermissionCheckRespVO checkModelPermission(Long userId, Long modelId);

    // ========== 批量操作 ==========

    /**
     * 批量创建实体访问权限
     *
     * @param roleIds 角色ID列表
     * @param entityIds 实体ID列表
     * @param canView 是否可查看
     */
    void batchCreateAccessPermissions(List<Long> roleIds, List<Long> entityIds, Boolean canView);

    /**
     * 批量创建实体操作权限
     *
     * @param roleIds 角色ID列表
     * @param modelId 模型ID（用于模型级别权限，与entityIds二选一）
     * @param entityIds 实体ID列表（用于实体级别权限，与modelId二选一）
     * @param canCreate 是否可创建
     * @param canUpdate 是否可更新
     * @param canDelete 是否可删除
     */
    void batchCreateOperationPermissions(List<Long> roleIds, Long modelId, List<Long> entityIds, 
                                         Boolean canCreate, Boolean canUpdate, Boolean canDelete);

    /**
     * 批量创建字段级权限
     *
     * @param roleIds 角色ID列表
     * @param modelId 模型ID
     * @param fieldIds 字段ID列表
     * @param canView 是否可查看
     * @param canEdit 是否可编辑
     */
    void batchCreateFieldPermissions(List<Long> roleIds, Long modelId, List<Long> fieldIds, Boolean canView, Boolean canEdit);

    /**
     * 批量创建分类级权限
     *
     * @param roleIds 角色ID列表
     * @param categoryIds 分类ID列表
     * @param canView 是否可查看
     * @param canManage 是否可管理
     */
    void batchCreateCategoryPermissions(List<Long> roleIds, List<Long> categoryIds, Boolean canView, Boolean canManage);

    /**
     * 删除角色的所有权限
     */
    void deleteAllPermissionsByRoleId(Long roleId);

    /**
     * 删除实体的所有权限
     */
    void deleteAllPermissionsByEntityId(Long entityId);

    /**
     * 删除模型的所有权限
     */
    void deleteAllPermissionsByModelId(Long modelId);

    /**
     * 删除分类的所有权限
     */
    void deleteAllPermissionsByCategoryId(Long categoryId);
}
