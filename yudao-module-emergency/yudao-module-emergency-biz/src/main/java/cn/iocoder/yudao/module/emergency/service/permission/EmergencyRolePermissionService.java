package cn.iocoder.yudao.module.emergency.service.permission;

import cn.iocoder.yudao.module.emergency.enums.EmergencyRoleEnum;

import java.util.List;
import java.util.Set;

/**
 * 应急管理角色权限服务接口
 * 
 * 负责管理应急管理系统的角色权限配置，包括：
 * - 角色定义（报告人、执行人、指挥人员等）
 * - 角色权限配置
 * - 权限校验逻辑
 */
public interface EmergencyRolePermissionService {

    /**
     * 获取用户的所有应急管理角色
     * 
     * @param userId 用户ID
     * @return 角色代码列表
     */
    List<String> getUserRoles(Long userId);

    /**
     * 检查用户是否具有指定角色
     * 
     * @param userId 用户ID
     * @param role 角色枚举
     * @return 是否具有该角色
     */
    boolean hasRole(Long userId, EmergencyRoleEnum role);

    /**
     * 检查用户是否具有指定角色代码
     * 
     * @param userId 用户ID
     * @param roleCode 角色代码
     * @return 是否具有该角色
     */
    boolean hasRoleCode(Long userId, String roleCode);

    /**
     * 获取角色的权限列表
     * 
     * @param role 角色枚举
     * @return 权限代码集合
     */
    Set<String> getRolePermissions(EmergencyRoleEnum role);

    /**
     * 检查用户是否具有指定权限
     * 
     * @param userId 用户ID
     * @param permission 权限代码
     * @return 是否具有该权限
     */
    boolean hasPermission(Long userId, String permission);

    /**
     * 为用户分配角色
     * 
     * @param userId 用户ID
     * @param roleCodes 角色代码列表
     */
    void assignRoles(Long userId, List<String> roleCodes);

    /**
     * 移除用户的角色
     * 
     * @param userId 用户ID
     * @param roleCodes 角色代码列表
     */
    void removeRoles(Long userId, List<String> roleCodes);
}



