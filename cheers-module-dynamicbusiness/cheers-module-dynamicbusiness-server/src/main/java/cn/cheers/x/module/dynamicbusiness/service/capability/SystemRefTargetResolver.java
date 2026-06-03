package cn.cheers.x.module.dynamicbusiness.service.capability;

import cn.hutool.core.util.StrUtil;

/**
 * 从 VO 字段名 + 资源上下文推导 ref 能力键（system:dept 等）。
 */
final class SystemRefTargetResolver {

    private SystemRefTargetResolver() {
    }

    static String resolve(String resourceCode, String fieldKey) {
        if (StrUtil.isBlank(fieldKey)) {
            return null;
        }
        return switch (fieldKey) {
            case "deptId" -> "system:dept";
            case "roleId" -> "system:role";
            case "leaderUserId", "userId" -> "system:user";
            case "postIds", "postId" -> "system:post";
            case "parentId" -> SystemRefTargetResolver.resolveParentRef(resourceCode);
            case "tenantId" -> "system:tenant";
            case "menuId" -> "system:menu";
            default -> fieldKey.endsWith("Id") || fieldKey.endsWith("Ids") ? null : null;
        };
    }

    static String resolveParentRef(String resourceCode) {
        return switch (resourceCode) {
            case "dept" -> "system:dept";
            case "menu" -> "system:menu";
            default -> null;
        };
    }
}
