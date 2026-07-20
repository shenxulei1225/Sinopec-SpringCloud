package cn.iocoder.yudao.module.emergency.service.permission;

import cn.iocoder.yudao.module.emergency.enums.EmergencyRoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 应急管理角色权限服务实现
 * 
 * 注意：这里简化实现，实际应该与系统权限模块集成
 * 当前实现基于内存配置，实际应该从数据库或配置中心读取
 */
@Slf4j
@Service
public class EmergencyRolePermissionServiceImpl implements EmergencyRolePermissionService {

    /**
     * 角色权限映射（简化实现，实际应该从数据库读取）
     */
    private static final Map<EmergencyRoleEnum, Set<String>> ROLE_PERMISSIONS = new HashMap<>();

    static {
        // 报告人权限：可以创建事件、上报信息
        ROLE_PERMISSIONS.put(EmergencyRoleEnum.REPORTER, Set.of(
                "emergency:event:create",
                "emergency:event:report",
                "emergency:event:view"
        ));

        // 执行人权限：可以查看任务、执行任务
        ROLE_PERMISSIONS.put(EmergencyRoleEnum.EXECUTOR, Set.of(
                "emergency:task:view",
                "emergency:task:execute",
                "emergency:task:complete",
                "emergency:event:view"
        ));

        // 指挥人员权限：可以管理响应、分配任务、分配资源
        ROLE_PERMISSIONS.put(EmergencyRoleEnum.COMMANDER, Set.of(
                "emergency:response:manage",
                "emergency:response:start",
                "emergency:response:upgrade",
                "emergency:response:cancel",
                "emergency:task:assign",
                "emergency:resource:dispatch",
                "emergency:event:view",
                "emergency:event:confirm",
                "emergency:event:assess"
        ));

        // 评估人员权限：可以评估事件和响应效果
        ROLE_PERMISSIONS.put(EmergencyRoleEnum.ASSESSOR, Set.of(
                "emergency:event:assess",
                "emergency:response:assess",
                "emergency:event:view",
                "emergency:response:view"
        ));

        // 资源管理员权限：可以管理资源池
        ROLE_PERMISSIONS.put(EmergencyRoleEnum.RESOURCE_MANAGER, Set.of(
                "emergency:resource:manage",
                "emergency:resource:create",
                "emergency:resource:update",
                "emergency:resource:delete",
                "emergency:resource:view"
        ));

        // 预案管理员权限：可以管理应急预案
        ROLE_PERMISSIONS.put(EmergencyRoleEnum.PLAN_MANAGER, Set.of(
                "emergency:plan:manage",
                "emergency:plan:create",
                "emergency:plan:update",
                "emergency:plan:delete",
                "emergency:plan:view"
        ));

        // 系统管理员权限：所有权限
        ROLE_PERMISSIONS.put(EmergencyRoleEnum.SYSTEM_ADMIN, Set.of("*"));
    }

    /**
     * 用户角色映射（简化实现，实际应该从数据库读取）
     * TODO: 应该与系统权限模块集成，从用户角色关联表读取
     */
    private final Map<Long, List<String>> userRoles = new HashMap<>();

    @Override
    public List<String> getUserRoles(Long userId) {
        // TODO: 实际应该从数据库或系统权限模块读取用户角色
        return userRoles.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public boolean hasRole(Long userId, EmergencyRoleEnum role) {
        return hasRoleCode(userId, role.getCode());
    }

    @Override
    public boolean hasRoleCode(Long userId, String roleCode) {
        List<String> roles = getUserRoles(userId);
        return roles.contains(roleCode);
    }

    @Override
    public Set<String> getRolePermissions(EmergencyRoleEnum role) {
        return ROLE_PERMISSIONS.getOrDefault(role, Collections.emptySet());
    }

    @Override
    public boolean hasPermission(Long userId, String permission) {
        List<String> roleCodes = getUserRoles(userId);
        
        // 检查每个角色的权限
        for (String roleCode : roleCodes) {
            EmergencyRoleEnum role = EmergencyRoleEnum.getByCode(roleCode);
            if (role != null) {
                Set<String> permissions = getRolePermissions(role);
                // 系统管理员拥有所有权限
                if (permissions.contains("*")) {
                    return true;
                }
                if (permissions.contains(permission)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    @Override
    public void assignRoles(Long userId, List<String> roleCodes) {
        // TODO: 实际应该保存到数据库或系统权限模块
        userRoles.put(userId, new ArrayList<>(roleCodes));
        log.info("为用户 {} 分配角色：{}", userId, roleCodes);
    }

    @Override
    public void removeRoles(Long userId, List<String> roleCodes) {
        // TODO: 实际应该从数据库或系统权限模块删除
        List<String> currentRoles = userRoles.getOrDefault(userId, new ArrayList<>());
        currentRoles.removeAll(roleCodes);
        userRoles.put(userId, currentRoles);
        log.info("移除用户 {} 的角色：{}", userId, roleCodes);
    }
}



