package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo.ManagementUnitCatalogVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo.OrganizationTreeNodeRespVO;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.ManagementUnitCatalogQueryService;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.ManagementUnitCatalogService;
import cn.cheers.x.module.dynamicbusiness.service.organization.OrganizationTreeQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理单元类型登记；按管理点读写可管目录；已登记目录的上下级树。
 * 不负责：组织架构页。组织架构页读组织架构目录，不读这里。
 */
@Tag(name = "管理后台 - 可管目录")
@RestController
@RequestMapping("/dynamicbusiness/management-unit-catalog")
@Validated
public class ManagementUnitCatalogController {

    @Resource
    private ManagementUnitCatalogService managementUnitCatalogService;
    @Resource
    private ManagementUnitCatalogQueryService managementUnitCatalogQueryService;
    @Resource
    private OrganizationTreeQueryService organizationTreeQueryService;

    @GetMapping("/get")
    @Operation(summary = "读取管理单元目录与已登记的管理单元类型")
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<ManagementUnitCatalogVO.Resp> get() {
        ManagementUnitCatalogQueryService.Registration registration = managementUnitCatalogQueryService.getRegistration();
        ManagementUnitCatalogVO.Resp resp = new ManagementUnitCatalogVO.Resp();
        resp.setManagementUnitRegistryCode(registration.managementUnitRegistryCode());
        List<ManagementUnitCatalogVO.TypeItem> types = new ArrayList<>();
        for (ManagementUnitCatalogQueryService.ManagementUnitType type : registration.types()) {
            ManagementUnitCatalogVO.TypeItem item = new ManagementUnitCatalogVO.TypeItem();
            item.setModelId(type.modelId());
            item.setModelName(type.modelName());
            item.setRegistryCodes(List.of());
            types.add(item);
        }
        resp.setTypes(types);
        return success(resp);
    }

    @GetMapping("/registries")
    @Operation(summary = "已添加为管理单元的数据目录",
            description = "顺序就是第二步业务配置标签从左到右的顺序。")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<ManagementUnitCatalogVO.RegistryResp> registries() {
        ManagementUnitCatalogVO.RegistryResp resp = new ManagementUnitCatalogVO.RegistryResp();
        resp.setRegistryCodes(managementUnitCatalogQueryService.listRegistryCodes());
        return success(resp);
    }

    @PutMapping("/registries")
    @Operation(summary = "整份保存已添加的管理单元目录")
    @PreAuthorize("@ss.hasPermission('system:entity-type:update')")
    public CommonResult<Boolean> saveRegistries(@Valid @RequestBody ManagementUnitCatalogVO.RegistrySaveReq reqVO) {
        managementUnitCatalogService.replaceRegistries(reqVO.getRegistryCodes());
        return success(true);
    }

    @GetMapping("/tree")
    @Operation(summary = "某一本管理单元目录按自己的上下级展开",
            description = "带 registryCode 时只展开已添加的那一本。不带时仍按数据范围用的那一本目录展开，给数据资源管理用。组织架构页不调用这个接口。")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<List<OrganizationTreeNodeRespVO>> tree(
            @RequestParam(value = "registryCode", required = false) String registryCode) {
        if (registryCode != null && !registryCode.isBlank()) {
            String code = registryCode.trim();
            if (!managementUnitCatalogQueryService.isRegistryAdded(code)) {
                throw new ServiceException(400, "这本目录还没添加为管理单元");
            }
            return success(organizationTreeQueryService.listParentChild(code));
        }
        String registered = managementUnitCatalogQueryService.findManagementUnitRegistryCode();
        if (registered == null || registered.isBlank()) {
            return success(List.of());
        }
        return success(organizationTreeQueryService.listParentChild(registered));
    }

    @PutMapping("/save")
    @Operation(summary = "整份保存已登记的管理单元类型（不含可管目录）")
    @PreAuthorize("@ss.hasPermission('system:entity-type:update')")
    public CommonResult<Boolean> save(@Valid @RequestBody ManagementUnitCatalogVO.SaveReq reqVO) {
        managementUnitCatalogService.replaceAll(reqVO.getTypes().stream()
                .map(item -> new ManagementUnitCatalogService.TypeRegistration(item.getModelId(), List.of()))
                .toList());
        return success(true);
    }

    @GetMapping("/point/enabled")
    @Operation(summary = "读取某管理点已登记的可管数据目录")
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<Map<String, List<String>>> enabledAtPoint(
            @RequestParam("managementUnitRegistryCode") String managementUnitRegistryCode,
            @Parameter(description = "管理点记录编号") @RequestParam("managementUnitId") Long managementUnitId) {
        List<String> codes = managementUnitCatalogQueryService.listCatalogCodesForUnit(
                managementUnitRegistryCode, managementUnitId);
        Map<String, List<String>> body = new HashMap<>();
        body.put("registryCodes", codes);
        return success(body);
    }

    @PutMapping("/point/save")
    @Operation(summary = "保存某管理点的可管数据目录")
    @PreAuthorize("@ss.hasPermission('system:entity-type:update')")
    public CommonResult<Boolean> saveAtPoint(@Valid @RequestBody ManagementUnitCatalogVO.PointSaveReq reqVO) {
        managementUnitCatalogService.replaceCatalogsForUnit(
                reqVO.getManagementUnitRegistryCode(),
                reqVO.getManagementUnitId(),
                reqVO.getRegistryCodes());
        return success(true);
    }
}