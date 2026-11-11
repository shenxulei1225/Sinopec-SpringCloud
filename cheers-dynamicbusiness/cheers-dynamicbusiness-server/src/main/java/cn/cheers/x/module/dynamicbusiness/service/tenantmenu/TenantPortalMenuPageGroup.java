package cn.cheers.x.module.dynamicbusiness.service.tenantmenu;

import java.util.List;

/**
 * 菜单管理左边的一组页面。
 * name 是已开通业务的名字。pages 是这一业务下已开通的页面。
 * 不表示顶栏上的一级菜单。
 */
public record TenantPortalMenuPageGroup(String code, String name, List<TenantPortalMenuPage> pages) {
}
