package cn.cheers.x.system.controller.admin.permission;

import cn.hutool.core.collection.CollUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.system.controller.admin.permission.vo.permission.PermissionAssignRoleDataScopeReqVO;
import cn.cheers.x.system.controller.admin.permission.vo.permission.PermissionAssignRoleMenuReqVO;
import cn.cheers.x.system.controller.admin.permission.vo.permission.PermissionAssignUserRoleReqVO;
import cn.cheers.x.system.service.permission.PermissionService;
import cn.cheers.x.system.service.tenant.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.Set;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 权限 Controller，提供赋予用户、角色的权限的 API 接口
 *
 * 
 */
@Tag(name = "管理后台 - 权限")
@RestController
@RequestMapping("/system/permission")
public class PermissionController {

    @Resource
    private PermissionService permissionService;
    @Resource
    private TenantService tenantService;

    @Operation(summary = "获得角色拥有的菜单编号")
    @Parameter(name = "roleId", description = "角色编号", required = true)
    @GetMapping("/list-role-menus")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-role-menu')")
    public CommonResult<Set<Long>> getRoleMenuList(Long roleId) {
        return success(permissionService.getRoleMenuListByRoleId(roleId));
    }

    @PostMapping("/assign-role-menu")
    @Operation(summary = "赋予角色菜单")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-role-menu')")
    public CommonResult<Boolean> assignRoleMenu(@Validated @RequestBody PermissionAssignRoleMenuReqVO reqVO) {
        // 开启多租户的情况下，需要过滤掉未开通的菜单
        tenantService.handleTenantMenu(menuIds -> reqVO.getMenuIds().removeIf(menuId -> !CollUtil.contains(menuIds, menuId)));

        // 执行菜单的分配
        permissionService.assignRoleMenu(reqVO.getRoleId(), reqVO.getMenuIds());
        return success(true);
    }

    @PostMapping("/assign-role-data-scope")
    @Operation(summary = "赋予角色数据权限")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-role-data-scope')")
    public CommonResult<Boolean> assignRoleDataScope(@Valid @RequestBody PermissionAssignRoleDataScopeReqVO reqVO) {
        permissionService.assignRoleDataScope(reqVO.getRoleId(), reqVO.getDataScope(), reqVO.getDataScopeDeptIds());
        return success(true);
    }

    @Operation(summary = "获得管理员拥有的角色编号列表")
    @Parameter(name = "userId", description = "用户编号", required = true)
    @GetMapping("/list-user-roles")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-user-role')")
    public CommonResult<Set<Long>> listAdminRoles(@RequestParam("userId") Long userId) {
        return success(permissionService.getUserRoleIdListByUserId(userId));
    }

    /**
     * 租户管理列「租户管理员」：按角色反查用户，避免扫全租户用户目录。
     */
    @Operation(summary = "获得拥有该角色的用户编号列表")
    @Parameter(name = "roleId", description = "角色编号", required = true)
    @GetMapping("/list-role-users")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-user-role')")
    public CommonResult<Set<Long>> listRoleUsers(@RequestParam("roleId") Long roleId) {
        return success(permissionService.getUserRoleIdListByRoleId(java.util.Collections.singleton(roleId)));
    }

    @Operation(summary = "赋予用户角色")
    @PostMapping("/assign-user-role")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-user-role')")
    public CommonResult<Boolean> assignUserRole(@Validated @RequestBody PermissionAssignUserRoleReqVO reqVO) {
        permissionService.assignUserRole(reqVO.getUserId(), reqVO.getRoleIds());
        return success(true);
    }

}
