package cn.cheers.x.module.dynamicbusiness.controller.admin.tenantmenu.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单管理页和顶栏读到的同一份结果。
 * saved 为 false 时，nodes 只是按已开通业务排出来的，还没有写入。
 */
@Data
public class TenantPortalMenuRespVO {

    private boolean businessTenant;
    private boolean saved;
    private List<TenantPortalMenuNodeVO> nodes = new ArrayList<>();
    private List<TenantPortalMenuPageVO> unplaced = new ArrayList<>();
    /** 左边按业务列出的已开通页面。 */
    private List<TenantPortalMenuPageGroupVO> pageGroups = new ArrayList<>();
}
