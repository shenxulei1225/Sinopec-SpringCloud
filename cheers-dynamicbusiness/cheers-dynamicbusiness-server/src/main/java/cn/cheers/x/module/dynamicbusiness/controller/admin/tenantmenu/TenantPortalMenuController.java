package cn.cheers.x.module.dynamicbusiness.controller.admin.tenantmenu;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.tenantmenu.vo.TenantPortalMenuNodeVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.tenantmenu.vo.TenantPortalMenuPageGroupVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.tenantmenu.vo.TenantPortalMenuPageVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.tenantmenu.vo.TenantPortalMenuRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.tenantmenu.vo.TenantPortalMenuSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.service.pagegrant.PagePermissionRegistry;
import cn.cheers.x.module.dynamicbusiness.service.tenantmenu.TenantPortalMenuNode;
import cn.cheers.x.module.dynamicbusiness.service.tenantmenu.TenantPortalMenuPage;
import cn.cheers.x.module.dynamicbusiness.service.tenantmenu.TenantPortalMenuPageGroup;
import cn.cheers.x.module.dynamicbusiness.service.tenantmenu.TenantPortalMenuService;
import cn.cheers.x.module.dynamicbusiness.service.tenantmenu.TenantPortalMenuView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 平台配置「菜单管理」。
 * 读给本租户登录用户看顶栏。写只给租户管理员和超级管理员。
 * 不读取角色动作，不改开通结果。
 */
@Tag(name = "管理后台 - 租户菜单")
@RestController
@RequestMapping("/dynamicbusiness/tenant-portal-menu")
@Validated
public class TenantPortalMenuController {

    @Resource
    private TenantPortalMenuService tenantPortalMenuService;

    @GetMapping
    @Operation(summary = "读取当前租户的顶栏菜单")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<TenantPortalMenuRespVO> load() {
        return success(toResp(tenantPortalMenuService.loadCurrent()));
    }

    @PutMapping
    @Operation(summary = "保存当前租户的顶栏菜单")
    @PreAuthorize("@ss.hasAnyRoles('super_admin', 'tenant_admin')")
    public CommonResult<Boolean> save(@Valid @RequestBody TenantPortalMenuSaveReqVO reqVO) {
        tenantPortalMenuService.saveCurrent(toNodes(reqVO.getNodes()));
        return success(true);
    }

    private static List<TenantPortalMenuNode> toNodes(List<TenantPortalMenuNodeVO> nodes) {
        List<TenantPortalMenuNode> result = new ArrayList<>();
        if (nodes == null) {
            return result;
        }
        for (TenantPortalMenuNodeVO node : nodes) {
            result.add(new TenantPortalMenuNode(node.getTitle(), node.getPageCode(), toNodes(node.getChildren())));
        }
        return result;
    }

    private static TenantPortalMenuRespVO toResp(TenantPortalMenuView view) {
        TenantPortalMenuRespVO resp = new TenantPortalMenuRespVO();
        resp.setBusinessTenant(view.businessTenant());
        resp.setSaved(view.saved());
        resp.setNodes(toNodeResp(view.nodes()));
        List<TenantPortalMenuPageVO> unplaced = new ArrayList<>();
        for (TenantPortalMenuPage page : view.unplaced()) {
            TenantPortalMenuPageVO row = new TenantPortalMenuPageVO();
            row.setCode(page.code());
            row.setName(page.name());
            unplaced.add(row);
        }
        resp.setUnplaced(unplaced);
        resp.setPageGroups(toGroupResp(view.pageGroups()));
        return resp;
    }

    private static List<TenantPortalMenuPageGroupVO> toGroupResp(List<TenantPortalMenuPageGroup> groups) {
        List<TenantPortalMenuPageGroupVO> result = new ArrayList<>();
        if (groups == null) {
            return result;
        }
        for (TenantPortalMenuPageGroup group : groups) {
            TenantPortalMenuPageGroupVO row = new TenantPortalMenuPageGroupVO();
            row.setCode(group.code());
            row.setName(group.name());
            List<TenantPortalMenuPageVO> pages = new ArrayList<>();
            for (TenantPortalMenuPage page : group.pages()) {
                TenantPortalMenuPageVO item = new TenantPortalMenuPageVO();
                item.setCode(page.code());
                item.setName(page.name());
                pages.add(item);
            }
            row.setPages(pages);
            result.add(row);
        }
        return result;
    }

    private static List<TenantPortalMenuNodeVO> toNodeResp(List<TenantPortalMenuNode> nodes) {
        List<TenantPortalMenuNodeVO> result = new ArrayList<>();
        if (nodes == null) {
            return result;
        }
        for (TenantPortalMenuNode node : nodes) {
            TenantPortalMenuNodeVO row = new TenantPortalMenuNodeVO();
            row.setTitle(node.title());
            row.setPageCode(node.pageCode());
            if (node.pageCode() != null && !node.pageCode().isBlank()) {
                row.setPageName(PagePermissionRegistry.pageName(node.pageCode()));
            }
            row.setChildren(toNodeResp(node.children()));
            result.add(row);
        }
        return result;
    }
}
