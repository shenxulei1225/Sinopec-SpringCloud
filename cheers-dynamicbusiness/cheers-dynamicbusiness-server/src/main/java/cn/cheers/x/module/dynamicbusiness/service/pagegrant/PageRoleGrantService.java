package cn.cheers.x.module.dynamicbusiness.service.pagegrant;

import java.util.List;

/**
 * 数据目录以外的页面授权。
 * 负责：按角色整份替换这些页面上的勾选。
 * 不负责：放行各业务页面的接口。不读写数据目录授权。
 */
public interface PageRoleGrantService {

    /** 角色页可勾分组：与 {@link PagePermissionRegistry} 同一份登记。 */
    List<PagePermissionRegistry.GroupDef> listGroups();

    List<PageRoleGrantItem> listByRoleId(Long roleId);

    void replaceRoleGrants(Long roleId, List<PageRoleGrantItem> items);

    /**
     * 给角色补上这些页面的查看类动作，保留原有其它勾选。
     * 开通业务能力包时写给租户管理员用。不删已有动作。
     */
    void mergeViewActions(Long roleId, List<String> pageCodes);
}
