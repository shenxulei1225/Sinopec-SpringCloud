package cn.cheers.x.module.dynamicbusiness.controller.admin.entity;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryPermissionCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryPermissionRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.*;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 业务实体权限管理 Controller
 */
@Tag(name = "管理后台 - 业务实体权限管理")
@RestController
@RequestMapping("/dynamicbusiness/entity-permission")
@Validated
public class EntityPermissionController {

    @Resource
    private EntityPermissionService entityPermissionService;

    // ========== 实体访问权限 ==========

    @PostMapping("/access/create")
    @Operation(summary = "创建实体访问权限")
    @PreAuthorize("@ss.hasPermission('system:entity-permission:create')")
    public CommonResult<Long> createAccessPermission(@Valid @RequestBody EntityAccessPermissionCreateReqVO reqVO) {
        return success(entityPermissionService.createAccessPermission(reqVO));
    }

    @PutMapping("/access/update")
    @Operation(summary = "更新实体访问权限")
    @PreAuthorize("@ss.hasPermission('system:entity-permission:update')")
    public CommonResult<Boolean> updateAccessPermission(@RequestParam("id") Long id,
                                                        @RequestParam("canView") Boolean canView) {
        entityPermissionService.updateAccessPermission(id, canView);
        return success(true);
    }

    @DeleteMapping("/access/delete")
    @Operation(summary = "删除实体访问权限")
    @PreAuthorize("@ss.hasPermission('system:entity-permission:delete')")
    public CommonResult<Boolean> deleteAccessPermission(@RequestParam("id") Long id) {
        entityPermissionService.deleteAccessPermission(id);
        return success(true);
    }

    @GetMapping("/access/get")
    @Operation(summary = "获取实体访问权限")
    @Parameter(name = "id", description = "权限ID", required = true)
    @Parameter(name = "entityTypeCode", description = "业务类型编码", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity-permission:query')")
    public CommonResult<EntityAccessPermissionRespVO> getAccessPermission(
            @RequestParam("id") Long id,
            @RequestParam("entityTypeCode") String entityTypeCode) {
        return success(entityPermissionService.getAccessPermission(id, entityTypeCode));
    }

    @GetMapping("/access/list-by-role")
    @Operation(summary = "获取角色的实体访问权限列表")
    @Parameter(name = "roleId", description = "角色ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity-permission:query')")
    public CommonResult<List<EntityAccessPermissionRespVO>> getAccessPermissionsByRoleId(@RequestParam("roleId") Long roleId) {
        return success(entityPermissionService.getAccessPermissionsByRoleId(roleId));
    }

    @GetMapping("/access/list-by-entity")
    @Operation(summary = "获取实体的访问权限列表")
    @Parameter(name = "entityId", description = "实体ID", required = true)
    @Parameter(name = "entityTypeCode", description = "业务类型编码（动态表实体必须提供）", required = false)
    @PreAuthorize("@ss.hasPermission('system:entity-permission:query')")
    public CommonResult<List<EntityAccessPermissionRespVO>> getAccessPermissionsByEntityId(
            @RequestParam("entityId") Long entityId,
            @RequestParam(value = "entityTypeCode") String entityTypeCode) {
        return success(entityPermissionService.getAccessPermissionsByEntityId(entityId, entityTypeCode));
    }

    // ========== 实体操作权限 ==========

    @PostMapping("/operation/create")
    @Operation(summary = "创建实体操作权限")
    @PreAuthorize("@ss.hasPermission('system:entity-permission:create')")
    public CommonResult<Long> createOperationPermission(@Valid @RequestBody EntityOperationPermissionCreateReqVO reqVO) {
        return success(entityPermissionService.createOperationPermission(reqVO));
    }

    @PutMapping("/operation/update")
    @Operation(summary = "更新实体操作权限")
    @PreAuthorize("@ss.hasPermission('system:entity-permission:update')")
    public CommonResult<Boolean> updateOperationPermission(@RequestParam("id") Long id,
                                                           @RequestParam(value = "canCreate", required = false) Boolean canCreate,
                                                           @RequestParam(value = "canUpdate", required = false) Boolean canUpdate,
                                                           @RequestParam(value = "canDelete", required = false) Boolean canDelete) {
        entityPermissionService.updateOperationPermission(id, canCreate, canUpdate, canDelete);
        return success(true);
    }

    @DeleteMapping("/operation/delete")
    @Operation(summary = "删除实体操作权限")
    @PreAuthorize("@ss.hasPermission('system:entity-permission:delete')")
    public CommonResult<Boolean> deleteOperationPermission(@RequestParam("id") Long id) {
        entityPermissionService.deleteOperationPermission(id);
        return success(true);
    }

    @GetMapping("/operation/get")
    @Operation(summary = "获取实体操作权限")
    @Parameter(name = "id", description = "权限ID", required = true)
    @Parameter(name = "entityTypeCode", description = "业务类型编码", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity-permission:query')")
    public CommonResult<EntityOperationPermissionRespVO> getOperationPermission(
            @RequestParam("id") Long id,
            @RequestParam("entityTypeCode") String entityTypeCode) {
        return success(entityPermissionService.getOperationPermission(id, entityTypeCode));
    }

    @GetMapping("/operation/list-by-role")
    @Operation(summary = "获取角色的实体操作权限列表")
    @Parameter(name = "roleId", description = "角色ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity-permission:query')")
    public CommonResult<List<EntityOperationPermissionRespVO>> getOperationPermissionsByRoleId(@RequestParam("roleId") Long roleId) {
        return success(entityPermissionService.getOperationPermissionsByRoleId(roleId));
    }


    // ========== 字段级权限 ==========

    @PostMapping("/field/create")
    @Operation(summary = "创建字段级权限")
    @PreAuthorize("@ss.hasPermission('system:entity-permission:create')")
    public CommonResult<Long> createFieldPermission(@Valid @RequestBody EntityFieldPermissionCreateReqVO reqVO) {
        return success(entityPermissionService.createFieldPermission(reqVO));
    }

    @PutMapping("/field/update")
    @Operation(summary = "更新字段级权限")
    @PreAuthorize("@ss.hasPermission('system:entity-permission:update')")
    public CommonResult<Boolean> updateFieldPermission(@RequestParam("id") Long id,
                                                       @RequestParam(value = "canView", required = false) Boolean canView,
                                                       @RequestParam(value = "canEdit", required = false) Boolean canEdit) {
        entityPermissionService.updateFieldPermission(id, canView, canEdit);
        return success(true);
    }

    @DeleteMapping("/field/delete")
    @Operation(summary = "删除字段级权限")
    @PreAuthorize("@ss.hasPermission('system:entity-permission:delete')")
    public CommonResult<Boolean> deleteFieldPermission(@RequestParam("id") Long id) {
        entityPermissionService.deleteFieldPermission(id);
        return success(true);
    }

    @GetMapping("/field/get")
    @Operation(summary = "获取字段级权限")
    @Parameter(name = "id", description = "权限ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity-permission:query')")
    public CommonResult<EntityFieldPermissionRespVO> getFieldPermission(@RequestParam("id") Long id) {
        return success(entityPermissionService.getFieldPermission(id));
    }

    @GetMapping("/field/list-by-role")
    @Operation(summary = "获取角色的字段级权限列表")
    @Parameter(name = "roleId", description = "角色ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity-permission:query')")
    public CommonResult<List<EntityFieldPermissionRespVO>> getFieldPermissionsByRoleId(@RequestParam("roleId") Long roleId) {
        return success(entityPermissionService.getFieldPermissionsByRoleId(roleId));
    }

    @GetMapping("/field/list-by-model")
    @Operation(summary = "获取模型的字段级权限列表")
    @Parameter(name = "modelId", description = "模型ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity-permission:query')")
    public CommonResult<List<EntityFieldPermissionRespVO>> getFieldPermissionsByModelId(@RequestParam("modelId") Long modelId) {
        return success(entityPermissionService.getFieldPermissionsByModelId(modelId));
    }

    // ========== 分类级权限 ==========

    @PostMapping("/category/create")
    @Operation(summary = "创建分类级权限")
    @PreAuthorize("@ss.hasPermission('system:entity-permission:create')")
    public CommonResult<Long> createCategoryPermission(@Valid @RequestBody CategoryPermissionCreateReqVO reqVO) {
        return success(entityPermissionService.createCategoryPermission(reqVO));
    }

    @PutMapping("/category/update")
    @Operation(summary = "更新分类级权限")
    @PreAuthorize("@ss.hasPermission('system:entity-permission:update')")
    public CommonResult<Boolean> updateCategoryPermission(@RequestParam("id") Long id,
                                                          @RequestParam(value = "canView", required = false) Boolean canView,
                                                          @RequestParam(value = "canManage", required = false) Boolean canManage) {
        entityPermissionService.updateCategoryPermission(id, canView, canManage);
        return success(true);
    }

    @DeleteMapping("/category/delete")
    @Operation(summary = "删除分类级权限")
    @PreAuthorize("@ss.hasPermission('system:entity-permission:delete')")
    public CommonResult<Boolean> deleteCategoryPermission(@RequestParam("id") Long id) {
        entityPermissionService.deleteCategoryPermission(id);
        return success(true);
    }

    @GetMapping("/category/get")
    @Operation(summary = "获取分类级权限")
    @Parameter(name = "id", description = "权限ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity-permission:query')")
    public CommonResult<CategoryPermissionRespVO> getCategoryPermission(@RequestParam("id") Long id) {
        return success(entityPermissionService.getCategoryPermission(id));
    }

    @GetMapping("/category/list-by-role")
    @Operation(summary = "获取角色的分类级权限列表")
    @Parameter(name = "roleId", description = "角色ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity-permission:query')")
    public CommonResult<List<CategoryPermissionRespVO>> getCategoryPermissionsByRoleId(@RequestParam("roleId") Long roleId) {
        return success(entityPermissionService.getCategoryPermissionsByRoleId(roleId));
    }

    @GetMapping("/category/list-by-category")
    @Operation(summary = "获取分类的权限列表")
    @Parameter(name = "categoryId", description = "分类ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity-permission:query')")
    public CommonResult<List<CategoryPermissionRespVO>> getCategoryPermissionsByCategoryId(@RequestParam("categoryId") Long categoryId) {
        return success(entityPermissionService.getCategoryPermissionsByCategoryId(categoryId));
    }

    // ========== 权限检查 ==========

    @GetMapping("/check/entity")
    @Operation(summary = "检查当前用户对实体的权限")
    @Parameter(name = "entityId", description = "实体ID", required = true)
    @Parameter(name = "entityTypeCode", description = "业务类型编码", required = true)
    public CommonResult<EntityPermissionCheckRespVO> checkEntityPermission(
            @RequestParam("entityId") Long entityId,
            @RequestParam("entityTypeCode") String entityTypeCode) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(entityPermissionService.checkEntityPermission(userId, entityId, entityTypeCode));
    }

    @GetMapping("/check/model")
    @Operation(summary = "检查当前用户对模型的权限")
    @Parameter(name = "modelId", description = "模型ID", required = true)
    public CommonResult<EntityPermissionCheckRespVO> checkModelPermission(@RequestParam("modelId") Long modelId) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(entityPermissionService.checkModelPermission(userId, modelId));
    }

    // ========== 批量操作 ==========

    @PostMapping("/batch/access")
    @Operation(summary = "批量创建实体访问权限")
    @PreAuthorize("@ss.hasPermission('system:entity-permission:create')")
    public CommonResult<Boolean> batchCreateAccessPermissions(@Valid @RequestBody BatchPermissionCreateReqVO.Access reqVO) {
        entityPermissionService.batchCreateAccessPermissions(reqVO.getRoleIds(), reqVO.getEntityIds(), reqVO.getCanView());
        return success(true);
    }

    @PostMapping("/batch/operation")
    @Operation(summary = "批量创建实体操作权限")
    @PreAuthorize("@ss.hasPermission('system:entity-permission:create')")
    public CommonResult<Boolean> batchCreateOperationPermissions(@Valid @RequestBody BatchPermissionCreateReqVO.Operation reqVO) {
        entityPermissionService.batchCreateOperationPermissions(reqVO.getRoleIds(), reqVO.getModelId(), 
                reqVO.getEntityIds(), reqVO.getCanCreate(), reqVO.getCanUpdate(), reqVO.getCanDelete());
        return success(true);
    }

    @PostMapping("/batch/field")
    @Operation(summary = "批量创建字段级权限")
    @PreAuthorize("@ss.hasPermission('system:entity-permission:create')")
    public CommonResult<Boolean> batchCreateFieldPermissions(@Valid @RequestBody BatchPermissionCreateReqVO.Field reqVO) {
        entityPermissionService.batchCreateFieldPermissions(reqVO.getRoleIds(), reqVO.getModelId(), 
                reqVO.getFieldIds(), reqVO.getCanView(), reqVO.getCanEdit());
        return success(true);
    }

    @PostMapping("/batch/category")
    @Operation(summary = "批量创建分类级权限")
    @PreAuthorize("@ss.hasPermission('system:entity-permission:create')")
    public CommonResult<Boolean> batchCreateCategoryPermissions(@Valid @RequestBody BatchPermissionCreateReqVO.Category reqVO) {
        entityPermissionService.batchCreateCategoryPermissions(reqVO.getRoleIds(), reqVO.getCategoryIds(), 
                reqVO.getCanView(), reqVO.getCanManage());
        return success(true);
    }

    @DeleteMapping("/batch/role")
    @Operation(summary = "删除角色的所有权限")
    @PreAuthorize("@ss.hasPermission('system:entity-permission:delete')")
    public CommonResult<Boolean> deleteAllPermissionsByRoleId(@RequestParam("roleId") Long roleId) {
        entityPermissionService.deleteAllPermissionsByRoleId(roleId);
        return success(true);
    }
}
