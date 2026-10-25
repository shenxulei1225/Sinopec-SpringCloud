package cn.cheers.x.module.dynamicbusiness.controller.admin.organization;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo.OrganizationDepartmentRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo.OrganizationTreeNodeRespVO;
import cn.cheers.x.module.dynamicbusiness.service.organization.OrganizationTreeQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 组织架构页左侧的组织树。只读，不新建部门，不改场站分类。
 */
@Tag(name = "管理后台 - 组织树")
@RestController
@RequestMapping("/dynamicbusiness/organization-tree")
@Validated
public class OrganizationTreeController {

    @Resource
    private OrganizationTreeQueryService organizationTreeQueryService;

    @GetMapping
    @Operation(summary = "组织架构页的组织树", description = "运营区域分类下挂已关联的场站。展开收起由页面处理。")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<List<OrganizationTreeNodeRespVO>> getTree() {
        return success(organizationTreeQueryService.getTree());
    }

    @GetMapping("/company-departments")
    @Operation(summary = "挂在这家公司或分公司上的部门")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<List<OrganizationDepartmentRespVO>> listCompanyDepartments(
            @RequestParam("companyEntityId") Long companyEntityId) {
        return success(organizationTreeQueryService.listCompanyDepartments(companyEntityId));
    }
}
