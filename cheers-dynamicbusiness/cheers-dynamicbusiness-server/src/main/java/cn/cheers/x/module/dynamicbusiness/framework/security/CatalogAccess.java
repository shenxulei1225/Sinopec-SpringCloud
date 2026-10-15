package cn.cheers.x.module.dynamicbusiness.framework.security;

import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.module.dynamicbusiness.service.cataloggrant.CatalogRoleGrantService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 实体接口上的目录权限。
 * 注解传入这次打开的数据目录注册编码和操作。只认实体上的授权。
 * 不负责选表。不判断分类、模型和详情。请求里没有目录注册编码时返回 false，接口 403。
 */
@Component("catalogAccess")
public class CatalogAccess {

    @Resource
    private CatalogRoleGrantService catalogRoleGrantService;

    public boolean allow(String registryCode, String operation) {
        return catalogRoleGrantService.allow(SecurityFrameworkUtils.getLoginUserId(), registryCode, operation);
    }
}
