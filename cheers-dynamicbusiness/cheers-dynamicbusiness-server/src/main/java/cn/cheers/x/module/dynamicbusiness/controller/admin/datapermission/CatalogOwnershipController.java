package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo.CatalogManagementOptionVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo.CatalogManagementPreviewRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo.CatalogOwnershipOverviewRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo.CatalogOwnershipSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.CatalogManagementQueryService;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.CatalogManagementQueryService.ManagementChoice;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.CatalogManagementTarget;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.CatalogOwnershipQueryService;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.CatalogOwnershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 「数据资源管理」页「数据管理方式」标签：所有目录按什么管理在这一处看、这一处改。
 * 用户只选按哪本目录管理，所需字段由 {@link CatalogOwnershipService#applyManagement} 挂上。
 * 只做入参、权限注解和转换。规划在 {@link CatalogManagementQueryService}，链路计算在 {@link CatalogOwnershipQueryService}。
 * 平台配置接口，按数据目录的菜单权限核对。
 */
@Tag(name = "管理后台 - 数据管理方式")
@RestController
@RequestMapping("/dynamicbusiness/catalog-ownership")
@Validated
public class CatalogOwnershipController {

    @Resource
    private CatalogOwnershipService catalogOwnershipService;
    @Resource
    private CatalogOwnershipQueryService catalogOwnershipQueryService;
    @Resource
    private CatalogManagementQueryService catalogManagementQueryService;

    @GetMapping("/overview")
    @Operation(summary = "全部数据目录的归属声明，以及每条声明到管理单元目录的整条链")
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<CatalogOwnershipOverviewRespVO> overview() {
        return success(CatalogOwnershipOverviewRespVO.from(catalogOwnershipQueryService.listOverview()));
    }

    @GetMapping("/targets")
    @Operation(summary = "这本目录在数据管理方式里勾了按哪些目录管理")
    @PreAuthorize("@catalogAccess.allow(#registryCode, 'query')")
    public CommonResult<List<CatalogManagementTarget>> managementTargets(@RequestParam("registryCode") String registryCode) {
        return success(catalogOwnershipQueryService.listManagementTargets(registryCode));
    }

    @GetMapping("/management-options")
    @Operation(summary = "一本目录能选哪些管理方式（按哪本目录管理）；新建目录时不传 registryCode")
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<List<CatalogManagementOptionVO>> managementOptions(
            @RequestParam(value = "registryCode", required = false) String registryCode) {
        return success(catalogManagementQueryService.listOptions(registryCode).stream()
                .map(CatalogManagementOptionVO::from).toList());
    }

    @PostMapping("/management-preview")
    @Operation(summary = "保存管理方式前的提示：会用哪个字段、要不要新加、有多少条记录还没填")
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<List<CatalogManagementPreviewRespVO>> managementPreview(@Valid @RequestBody CatalogOwnershipSaveReqVO reqVO) {
        return success(catalogManagementQueryService.preview(reqVO.getRegistryCode(), toChoices(reqVO)).stream()
                .map(CatalogManagementPreviewRespVO::from).toList());
    }

    @PutMapping("/save")
    @Operation(summary = "保存一本数据目录的管理方式：缺字段就挂上或新建，再整份替换声明")
    @PreAuthorize("@ss.hasPermission('system:entity-type:update')")
    public CommonResult<Boolean> save(@Valid @RequestBody CatalogOwnershipSaveReqVO reqVO) {
        catalogOwnershipService.applyManagement(reqVO.getRegistryCode(), toChoices(reqVO));
        return success(true);
    }

    private static List<ManagementChoice> toChoices(CatalogOwnershipSaveReqVO reqVO) {
        return reqVO.getTargets().stream()
                .map(item -> new ManagementChoice(item.getTargetRegistryCode(), item.getFieldCode())).toList();
    }
}
