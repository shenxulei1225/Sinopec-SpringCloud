package cn.cheers.x.module.dynamicbusiness.service.tenantmenu;

import java.util.List;

/**
 * 本租户顶栏菜单。
 * 负责：读当前租户已保存的菜单；还没保存时按已开通业务现生成一份给页面看，不写入。
 * 不负责：开通哪些页面；角色能不能进页面。读的时候不补写菜单行。
 */
public interface TenantPortalMenuService {

    TenantPortalMenuView loadCurrent();

    void saveCurrent(List<TenantPortalMenuNode> nodes);
}
