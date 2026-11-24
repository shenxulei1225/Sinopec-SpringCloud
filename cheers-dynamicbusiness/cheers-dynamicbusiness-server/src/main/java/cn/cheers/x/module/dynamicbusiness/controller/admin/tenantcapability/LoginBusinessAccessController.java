package cn.cheers.x.module.dynamicbusiness.controller.admin.tenantcapability;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.service.tenantcapability.LoginBusinessAccessQueryService;
import cn.cheers.x.module.dynamicbusiness.service.tenantcapability.LoginBusinessAccessQueryService.CatalogFacilityEnablement;
import cn.cheers.x.module.dynamicbusiness.service.tenantcapability.LoginBusinessAccessQueryService.WorkUnit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 登录人进入某个业务页面后，右上角可选的管理单元。
 * 不负责：裁顶栏（见租户菜单的 forNavigation）；保存任职或业务开通。
 */
@Tag(name = "管理后台 - 登录业务范围")
@RestController
@RequestMapping("/dynamicbusiness/login-business-access")
@Validated
public class LoginBusinessAccessController {

    @Resource
    private LoginBusinessAccessQueryService loginBusinessAccessQueryService;

    @GetMapping("/units")
    @Operation(summary = "当前页面可选的管理单元")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<List<WorkUnitResp>> units(@RequestParam("pageCode") String pageCode) {
        List<WorkUnitResp> rows = new ArrayList<>();
        for (WorkUnit unit : loginBusinessAccessQueryService.unitsForPage(pageCode)) {
            WorkUnitResp row = new WorkUnitResp();
            row.setRegistryCode(unit.registryCode());
            row.setId(unit.id());
            row.setName(unit.name());
            rows.add(row);
        }
        return success(rows);
    }

    @GetMapping("/catalog-facilities")
    @Operation(summary = "数据目录场站列：没开通这项业务的站不返回")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<CatalogFacilityResp> catalogFacilities(@RequestParam("registryCode") String registryCode) {
        CatalogFacilityEnablement enablement = loginBusinessAccessQueryService.facilitiesEnabledForCatalog(registryCode);
        CatalogFacilityResp resp = new CatalogFacilityResp();
        resp.setRestrict(enablement.restrict());
        resp.setFacilityIds(enablement.facilityIds());
        return success(resp);
    }

    /** 右上角一条管理单元。 */
    public static class WorkUnitResp {
        private String registryCode;
        private Long id;
        private String name;

        public String getRegistryCode() {
            return registryCode;
        }

        public void setRegistryCode(String registryCode) {
            this.registryCode = registryCode;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /** 数据目录场站列的开通收窄。restrict 为 false 时不使用 facilityIds。 */
    public static class CatalogFacilityResp {
        private boolean restrict;
        private List<Long> facilityIds;

        public boolean isRestrict() {
            return restrict;
        }

        public void setRestrict(boolean restrict) {
            this.restrict = restrict;
        }

        public List<Long> getFacilityIds() {
            return facilityIds;
        }

        public void setFacilityIds(List<Long> facilityIds) {
            this.facilityIds = facilityIds;
        }
    }
}
