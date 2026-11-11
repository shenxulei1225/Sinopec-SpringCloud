package cn.cheers.x.module.dynamicbusiness.service.tenantmenu;

import java.util.List;

/**
 * 菜单管理页和顶栏共用这一份读结果。
 * businessTenant 为 false：当前不是业务租户，nodes 为空。
 * saved 为 false：这个租户还没保存过菜单，nodes 是按已开通业务现生成的，没有写入。
 * unplaced：已开通但没出现在 nodes 里的页面。顶栏不显示这些页。
 * pageGroups：左边按业务列出的已开通页面。和顶栏菜单不是同一份结构。
 */
public record TenantPortalMenuView(
        boolean businessTenant,
        boolean saved,
        List<TenantPortalMenuNode> nodes,
        List<TenantPortalMenuPage> unplaced,
        List<TenantPortalMenuPageGroup> pageGroups
) {
}
