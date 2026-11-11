package cn.cheers.x.system.controller.admin.tenant;

import cn.cheers.x.framework.apilog.core.annotation.ApiAccessLog;
import cn.cheers.x.framework.common.enums.CommonStatusEnum;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageParam;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.framework.excel.core.util.ExcelUtils;
import cn.cheers.x.framework.tenant.core.aop.TenantIgnore;
import cn.cheers.x.system.controller.admin.tenant.vo.tenant.TenantPageReqVO;
import cn.cheers.x.system.controller.admin.tenant.vo.tenant.TenantPortalSaveReqVO;
import cn.cheers.x.system.controller.admin.tenant.vo.tenant.TenantRespVO;
import cn.cheers.x.system.controller.admin.tenant.vo.tenant.TenantSaveReqVO;
import cn.cheers.x.system.dal.dataobject.tenant.TenantDO;
import cn.cheers.x.system.service.tenant.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.cheers.x.framework.common.pojo.CommonResult.success;
import static cn.cheers.x.framework.common.util.collection.CollectionUtils.convertList;

@Tag(name = "管理后台 - 租户")
@RestController
@RequestMapping("/system/tenant")
@Validated
public class TenantController {

    @Resource
    private TenantService tenantService;

    @GetMapping("/get-id-by-name")
    @PermitAll
    @TenantIgnore
    @Operation(summary = "使用租户名，获得租户编号", description = "登录界面，根据用户的租户名，获得租户编号")
    @Parameter(name = "name", description = "租户名", required = true, example = "1024")
    public CommonResult<Long> getTenantIdByName(@RequestParam("name") String name) {
        TenantDO tenant = tenantService.getTenantByName(name);
        return success(tenant != null ? tenant.getId() : null);
    }

    @GetMapping({ "simple-list" })
    @PermitAll
    @TenantIgnore
    @Operation(summary = "获取租户精简信息列表", description = "只包含被开启的业务租户，用于登录页选择；不含平台租户")
    public CommonResult<List<TenantRespVO>> getTenantSimpleList() {
        List<TenantDO> list = tenantService.getTenantListByStatus(CommonStatusEnum.ENABLE.getStatus());
        list.removeIf(tenant -> TenantDO.SCOPE_PLATFORM.equals(tenant.getScope()));
        return success(convertList(list, tenantDO ->
                new TenantRespVO().setId(tenantDO.getId()).setName(tenantDO.getName())
                        .setPortalName(tenantDO.getPortalName()).setPortalNameEn(tenantDO.getPortalNameEn())
                        .setLogoUrl(tenantDO.getLogoUrl())));
    }

    @GetMapping("/platform-login-entry")
    @PermitAll
    @TenantIgnore
    @Operation(summary = "登录页「租户管理」入口", description = "返回平台租户编号；前端显示名为租户管理，不展示平台二字")
    public CommonResult<TenantRespVO> getPlatformLoginEntry() {
        Long platformId = tenantService.getPlatformTenantId();
        return success(new TenantRespVO().setId(platformId).setName("租户管理"));
    }

    @GetMapping("/business-list")
    @Operation(summary = "业务租户列表（租户管理页）")
    @PreAuthorize("@ss.hasAnyRoles('super_admin')")
    public CommonResult<List<TenantRespVO>> getBusinessTenantList() {
        List<TenantDO> list = tenantService.getTenantListByStatus(CommonStatusEnum.ENABLE.getStatus());
        // 只列业务租户；平台租户走「租户管理」入口，不出现在本列表。
        list.removeIf(tenant -> TenantDO.SCOPE_PLATFORM.equals(tenant.getScope()));
        return success(BeanUtils.toBean(list, TenantRespVO.class));
    }

    @GetMapping("/get-by-website")
    @PermitAll
    @TenantIgnore
    @Operation(summary = "使用域名，获得租户信息", description = "登录界面，根据用户的域名，获得租户信息")
    @Parameter(name = "website", description = "域名", required = true, example = "www.iocoder.cn")
    public CommonResult<TenantRespVO> getTenantByWebsite(
            @RequestParam("website") @Pattern(regexp = "^[a-zA-Z0-9.-]+$", message = "网站域名格式不正确") String website) {
        TenantDO tenant = tenantService.getTenantByWebsite(website);
        if (tenant == null || CommonStatusEnum.isDisable(tenant.getStatus())) {
            return success(null);
        }
        return success(new TenantRespVO().setId(tenant.getId()).setName(tenant.getName()));
    }

    @PostMapping("/create")
    @Operation(summary = "创建租户")
    @PreAuthorize("@ss.hasPermission('system:tenant:create')")
    public CommonResult<Long> createTenant(@Valid @RequestBody TenantSaveReqVO createReqVO) {
        return success(tenantService.createTenant(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新租户")
    @PreAuthorize("@ss.hasPermission('system:tenant:update')")
    public CommonResult<Boolean> updateTenant(@Valid @RequestBody TenantSaveReqVO updateReqVO) {
        tenantService.updateTenant(updateReqVO);
        return success(true);
    }

    @PutMapping("/portal")
    @Operation(summary = "更新当前租户的门户名称和 Logo")
    @PreAuthorize("@ss.hasAnyRoles('super_admin', 'tenant_admin')")
    public CommonResult<Boolean> updateTenantPortal(@Valid @RequestBody TenantPortalSaveReqVO reqVO) {
        tenantService.updateTenantPortal(reqVO.getPortalName(), reqVO.getPortalNameEn(), reqVO.getLogoUrl());
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除租户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:tenant:delete')")
    public CommonResult<Boolean> deleteTenant(@RequestParam("id") Long id) {
        tenantService.deleteTenant(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @Operation(summary = "批量删除租户")
    @PreAuthorize("@ss.hasPermission('system:tenant:delete')")
    public CommonResult<Boolean> deleteTenantList(@RequestParam("ids") List<Long> ids) {
        tenantService.deleteTenantList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得租户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:tenant:query')")
    public CommonResult<TenantRespVO> getTenant(@RequestParam("id") Long id) {
        TenantDO tenant = tenantService.getTenant(id);
        return success(BeanUtils.toBean(tenant, TenantRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得租户分页")
    @PreAuthorize("@ss.hasPermission('system:tenant:query')")
    public CommonResult<PageResult<TenantRespVO>> getTenantPage(@Valid TenantPageReqVO pageVO) {
        PageResult<TenantDO> pageResult = tenantService.getTenantPage(pageVO);
        return success(BeanUtils.toBean(pageResult, TenantRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出租户 Excel")
    @PreAuthorize("@ss.hasPermission('system:tenant:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportTenantExcel(@Valid TenantPageReqVO exportReqVO, HttpServletResponse response) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<TenantDO> list = tenantService.getTenantPage(exportReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "租户.xls", "数据", TenantRespVO.class,
                BeanUtils.toBean(list, TenantRespVO.class));
    }

}
