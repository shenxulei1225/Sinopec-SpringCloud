package cn.cheers.x.module.dynamicbusiness.service.tenantmenu;

import java.util.List;

/**
 * 顶栏上的一项。pageCode 为空表示这一项只是分组，自己不打开页面。
 * 分组下面可以是页面，也可以再是分组。安防下面是人防、通信、技防。
 */
public record TenantPortalMenuNode(
        String title,
        String pageCode,
        List<TenantPortalMenuNode> children
) {
}
