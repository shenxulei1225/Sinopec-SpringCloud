package cn.cheers.x.module.dynamicbusiness.controller.admin.tenantmenu.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/** 菜单管理左边的一个业务，以及它下面已开通的页面。 */
@Data
public class TenantPortalMenuPageGroupVO {

    private String code;
    private String name;
    private List<TenantPortalMenuPageVO> pages = new ArrayList<>();
}
