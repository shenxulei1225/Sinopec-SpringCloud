package cn.cheers.x.module.dynamicbusiness.controller.admin.tenantcapability;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.tenantcapability.vo.ManagementUnitBusinessCatalogRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.tenantcapability.vo.ManagementUnitBusinessCatalogSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.tenantcapability.vo.ManagementUnitCapabilityPackRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.tenantcapability.vo.ManagementUnitCapabilityPackSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.tenantcapability.vo.UnitEnablementPackOptionRespVO;
import cn.cheers.x.module.dynamicbusiness.service.tenantcapability.ManagementUnitBusinessEnablementService;
import cn.cheers.x.module.dynamicbusiness.service.tenantcapability.ManagementUnitCapabilityPackService;
import cn.cheers.x.module.dynamicbusiness.service.tenantcapability.UnitEnablementPackOption;
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
 * 管理点业务启用：界面勾选已开通的业务。业务自带数据目录，这里不另存一份目录勾选。
 * 租户管理员在本租户内维护；超级管理员开通租户包在 {@link TenantCapabilityPackController}。
 */
@Tag(name = "管理后台 - 管理单元业务启用")
@RestController
@RequestMapping("/dynamicbusiness/management-unit-capability-pack")
@Validated
public class ManagementUnitCapabilityPackController {

    @Resource
    private ManagementUnitCapabilityPackService managementUnitCapabilityPackService;
    @Resource
    private ManagementUnitBusinessEnablementService managementUnitBusinessEnablementService;

    @GetMapping("/options")
    @Operation(summary = "某锚点目录下、本租户已开通且可按单元启用的业务能力包")
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<List<UnitEnablementPackOptionRespVO>> options(
            @RequestParam("registryCode") String registryCode) {
        List<UnitEnablementPackOptionRespVO> rows = new ArrayList<>();
        for (UnitEnablementPackOption option : managementUnitCapabilityPackService.listEnablementOptionsForRegistry(
                registryCode)) {
            UnitEnablementPackOptionRespVO row = new UnitEnablementPackOptionRespVO();
            row.setCode(option.code());
            row.setName(option.name());
            row.setDescription(option.description());
            rows.add(row);
        }
        return success(rows);
    }

    @GetMapping("/enabled")
    @Operation(summary = "读取某锚点上已启用的业务能力包编码")
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<ManagementUnitCapabilityPackRespVO> enabled(
            @RequestParam("registryCode") String registryCode,
            @RequestParam("managementUnitId") Long managementUnitId) {
        ManagementUnitCapabilityPackRespVO resp = new ManagementUnitCapabilityPackRespVO();
        resp.setRegistryCode(registryCode);
        resp.setManagementUnitId(managementUnitId);
        resp.setPackCodes(managementUnitCapabilityPackService.listEnabledPackCodes(registryCode, managementUnitId));
        return success(resp);
    }

    @PutMapping("/save")
    @Operation(summary = "整份保存某锚点上启用的业务能力包")
    @PreAuthorize("@ss.hasPermission('system:entity-type:update')")
    public CommonResult<Boolean> save(@Valid @RequestBody ManagementUnitCapabilityPackSaveReqVO reqVO) {
        managementUnitCapabilityPackService.replaceEnabledPacks(
                reqVO.getRegistryCode(), reqVO.getManagementUnitId(), reqVO.getPackCodes());
        return success(true);
    }

    @GetMapping("/business-catalogs/selectable")
    @Operation(summary = "本租户可在管理点上勾选的业务目录编码")
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<List<String>> selectableBusinessCatalogs() {
        return success(managementUnitBusinessEnablementService.listSelectableCatalogCodes());
    }

    @GetMapping("/business-catalogs/enabled")
    @Operation(summary = "某管理点上已勾选的业务目录")
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<ManagementUnitBusinessCatalogRespVO> enabledBusinessCatalogs(
            @RequestParam("registryCode") String registryCode,
            @RequestParam("managementUnitId") Long managementUnitId) {
        ManagementUnitBusinessCatalogRespVO resp = new ManagementUnitBusinessCatalogRespVO();
        resp.setRegistryCode(registryCode);
        resp.setManagementUnitId(managementUnitId);
        resp.setCatalogCodes(
                managementUnitBusinessEnablementService.listEnabledCatalogCodes(registryCode, managementUnitId));
        return success(resp);
    }

    @PutMapping("/business-catalogs/save")
    @Operation(summary = "整份保存管理点上勾选的业务目录")
    @PreAuthorize("@ss.hasPermission('system:entity-type:update')")
    public CommonResult<Boolean> saveBusinessCatalogs(@Valid @RequestBody ManagementUnitBusinessCatalogSaveReqVO reqVO) {
        managementUnitBusinessEnablementService.replaceEnabledCatalogs(
                reqVO.getRegistryCode(), reqVO.getManagementUnitId(), reqVO.getCatalogCodes());
        return success(true);
    }
}