package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo.ManagementUnitCatalogVO;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.ManagementUnitCatalogQueryService;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.ManagementUnitCatalogService;
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
 * 平台配置页「数据资源管理」：登记管理单元类型和各自能管的数据目录。
 * 平台配置接口，按数据目录的菜单权限核对。只做入参、权限注解和转换。核对在 {@link ManagementUnitCatalogService}。
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

    @GetMapping("/get")
    @Operation(summary = "读取管理单元目录和各管理单元类型能管的目录")
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
            item.setRegistryCodes(type.registryCodes());
            types.add(item);
        }
        resp.setTypes(types);
        return success(resp);
    }

    @PutMapping("/save")
    @Operation(summary = "整份保存可管目录")
    @PreAuthorize("@ss.hasPermission('system:entity-type:update')")
    public CommonResult<Boolean> save(@Valid @RequestBody ManagementUnitCatalogVO.SaveReq reqVO) {
        managementUnitCatalogService.replaceAll(reqVO.getTypes().stream()
                .map(item -> new ManagementUnitCatalogService.TypeRegistration(item.getModelId(), item.getRegistryCodes()))
                .toList());
        return success(true);
    }
}
