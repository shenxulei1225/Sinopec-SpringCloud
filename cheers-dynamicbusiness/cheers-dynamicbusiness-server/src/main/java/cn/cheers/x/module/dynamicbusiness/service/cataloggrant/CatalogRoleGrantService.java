package cn.cheers.x.module.dynamicbusiness.service.cataloggrant;

import java.util.List;

/**
 * 数据目录上的角色授权。
 * 负责：按角色整份替换分类、模型、实体、详情上的操作；判断当前用户能否对某个目录里的实体做某一项。
 * 不负责：选实体表；不把存数编码当成目录；不判断分类、模型和详情接口。
 */
public interface CatalogRoleGrantService {

    List<CatalogRoleGrantItem> listByRoleId(Long roleId);

    void replaceRoleGrants(Long roleId, List<CatalogRoleGrantItem> items);

    /**
     * 同一次保存目录操作和场站范围。
     * stationScopes 为 null 时不改范围。
     */
    void saveRoleCatalogPermission(Long roleId, List<CatalogRoleGrantItem> items, List<CatalogStationScope> stationScopes);

    /**
     * 当前登录用户能否对这个数据目录里的实体做这一项。
     * 只认实体上的授权行。分类、模型和详情的勾选不放行实体。
     * 目录注册编码为空、目录不在本租户、操作不在四项内，都返回 false。
     */
    boolean allow(Long userId, String registryCode, String operation);
}
