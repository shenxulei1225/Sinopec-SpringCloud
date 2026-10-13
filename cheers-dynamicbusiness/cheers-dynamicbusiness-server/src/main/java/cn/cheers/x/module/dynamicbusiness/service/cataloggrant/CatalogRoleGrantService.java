package cn.cheers.x.module.dynamicbusiness.service.cataloggrant;

import java.util.List;

/**
 * 数据目录上的角色授权。
 * 负责：按角色整份替换四项操作；判断当前用户能否对某个目录做某一项。
 * 不负责：选实体表；不把存数编码当成目录。
 */
public interface CatalogRoleGrantService {

    List<CatalogRoleGrantItem> listByRoleId(Long roleId);

    void replaceRoleGrants(Long roleId, List<CatalogRoleGrantItem> items);

    /**
     * 当前登录用户能否对这个数据目录做这一项。
     * 目录注册编码为空、目录不在本租户、操作不在四项内，都返回 false。
     */
    boolean allow(Long userId, String registryCode, String operation);
}
