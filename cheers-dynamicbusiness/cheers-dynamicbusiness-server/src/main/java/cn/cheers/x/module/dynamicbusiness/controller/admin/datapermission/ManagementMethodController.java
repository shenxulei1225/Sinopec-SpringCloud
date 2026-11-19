package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo.CatalogManagementOptionVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo.ManagementMethodSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.CatalogManagementQueryService;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.ManagementMethodService;
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

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 「数据资源管理」第一个标签：登记可选的数据管理方式（按场站、按管线、按组织机构……）。
 * 已登记的方式和各自用量由 {@code /catalog-ownership/overview} 一起给出，这里不另出列表接口。
 * 只做入参、权限注解和转换。核对在 {@link ManagementMethodService}。平台配置接口，按数据目录的菜单权限核对。
 */
@Tag(name = "管理后台 - 可选的数据管理方式")
@RestController
@RequestMapping("/dynamicbusiness/management-method")
@Validated
public class ManagementMethodController {

    @Resource
    private ManagementMethodService managementMethodService;
    @Resource
    private CatalogManagementQueryService catalogManagementQueryService;

    @GetMapping("/candidates")
    @Operation(summary = "还能登记成管理方式的目录：能对应到单位、还没登记的")
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<List<CatalogManagementOptionVO>> candidates() {
        return success(catalogManagementQueryService.listMethodCandidates().stream()
                .map(CatalogManagementOptionVO::from).toList());
    }

    @PutMapping("/save")
    @Operation(summary = "整份保存可选的管理方式，顺序就是表格列顺序")
    @PreAuthorize("@ss.hasPermission('system:entity-type:update')")
    public CommonResult<Boolean> save(@Valid @RequestBody ManagementMethodSaveReqVO reqVO) {
        managementMethodService.replaceAll(reqVO.getRegistryCodes());
        return success(true);
    }
}
