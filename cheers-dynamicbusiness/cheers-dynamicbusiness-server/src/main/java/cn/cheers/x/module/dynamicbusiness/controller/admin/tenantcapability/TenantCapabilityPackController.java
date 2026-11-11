package cn.cheers.x.module.dynamicbusiness.controller.admin.tenantcapability;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.controller.admin.tenantcapability.vo.TenantCapabilityOpenedPackRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.tenantcapability.vo.TenantCapabilityPackDefRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.tenantcapability.vo.TenantCapabilityPackOpenReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.tenantcapability.vo.TenantCapabilityScopeRespVO;
import cn.cheers.x.module.dynamicbusiness.service.pagegrant.PagePermissionRegistry;
import cn.cheers.x.module.dynamicbusiness.service.tenantcapability.BusinessCapabilityPackDefinition;
import cn.cheers.x.module.dynamicbusiness.service.tenantcapability.TenantCapabilityOpenedScope;
import cn.cheers.x.module.dynamicbusiness.service.tenantcapability.TenantCapabilityPackService;
import cn.cheers.x.module.dynamicbusiness.service.tenantcapability.TenantPackPageSelection;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 租户管理：业务能力包定义与开通；业务门户读当前租户开通范围。
 * 开通写入仅超级管理员。current-scope 给本租户登录用户裁顶栏与角色可勾范围。
 */
@Tag(name = "管理后台 - 租户业务能力包")
@RestController
@RequestMapping("/dynamicbusiness/tenant-capability-pack")
@Validated
public class TenantCapabilityPackController {

    @Resource
    private TenantCapabilityPackService tenantCapabilityPackService;

    @GetMapping("/definitions")
    @Operation(summary = "列出平台预置的业务能力包")
    @PreAuthorize("@ss.hasAnyRoles('super_admin')")
    public CommonResult<List<TenantCapabilityPackDefRespVO>> definitions() {
        List<TenantCapabilityPackDefRespVO> rows = new ArrayList<>();
        for (BusinessCapabilityPackDefinition pack : tenantCapabilityPackService.listDefinitions()) {
            TenantCapabilityPackDefRespVO row = new TenantCapabilityPackDefRespVO();
            row.setCode(pack.code());
            row.setName(pack.name());
            row.setDescription(pack.description());
            row.setPageCodes(pack.pageCodes());
            List<TenantCapabilityPackDefRespVO.PageItem> pages = new ArrayList<>();
            for (String pageCode : pack.pageCodes()) {
                TenantCapabilityPackDefRespVO.PageItem page = new TenantCapabilityPackDefRespVO.PageItem();
                page.setCode(pageCode);
                page.setName(PagePermissionRegistry.pageName(pageCode));
                pages.add(page);
            }
            row.setPages(pages);
            row.setCatalogCodes(pack.catalogCodes());
            rows.add(row);
        }
        return success(rows);
    }

    @GetMapping("/opened")
    @Operation(summary = "读取业务租户已开通的业务，以及每个业务勾上的页面")
    @PreAuthorize("@ss.hasAnyRoles('super_admin')")
    public CommonResult<List<TenantCapabilityOpenedPackRespVO>> opened(@RequestParam("tenantId") Long tenantId) {
        List<TenantCapabilityOpenedPackRespVO> rows = new ArrayList<>();
        for (TenantPackPageSelection selection : tenantCapabilityPackService.listOpenedSelections(tenantId)) {
            TenantCapabilityOpenedPackRespVO row = new TenantCapabilityOpenedPackRespVO();
            row.setPackCode(selection.packCode());
            row.setPageCodes(selection.pageCodes());
            rows.add(row);
        }
        return success(rows);
    }

    @GetMapping("/current-scope")
    @Operation(summary = "读取当前登录租户已开通能力包推导的业务页与目录范围")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<TenantCapabilityScopeRespVO> currentScope() {
        Long tenantId = TenantContextHolder.getTenantId();
        TenantCapabilityOpenedScope scope = tenantCapabilityPackService.resolveOpenedScope(tenantId);
        TenantCapabilityScopeRespVO resp = new TenantCapabilityScopeRespVO();
        resp.setPackCodes(scope.packCodes());
        resp.setPageCodes(scope.pageCodes());
        resp.setCatalogCodes(scope.catalogCodes());
        return success(resp);
    }

    @PutMapping("/open")
    @Operation(summary = "整份保存业务租户开通的业务与页面，并编排目录与租户管理员查看动作")
    @PreAuthorize("@ss.hasAnyRoles('super_admin')")
    public CommonResult<Boolean> open(@Valid @RequestBody TenantCapabilityPackOpenReqVO reqVO) {
        if (reqVO.getSelections() != null) {
            List<TenantPackPageSelection> selections = new ArrayList<>();
            for (TenantCapabilityPackOpenReqVO.Selection item : reqVO.getSelections()) {
                if (item == null) {
                    continue;
                }
                selections.add(new TenantPackPageSelection(item.getPackCode(), item.getPageCodes()));
            }
            tenantCapabilityPackService.replaceOpenedSelections(reqVO.getTenantId(), selections);
        } else {
            tenantCapabilityPackService.replaceOpenedPacks(reqVO.getTenantId(), reqVO.getPackCodes());
        }
        return success(true);
    }
}
