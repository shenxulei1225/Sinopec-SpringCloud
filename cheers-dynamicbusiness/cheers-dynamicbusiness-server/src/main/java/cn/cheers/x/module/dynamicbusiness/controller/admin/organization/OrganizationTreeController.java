package cn.cheers.x.module.dynamicbusiness.controller.admin.organization;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo.OrganizationDepartmentRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo.OrganizationTreeNodeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo.SolePlaceDepartmentsRespVO;
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
 * 组织架构页左侧的组织树和中间一列的部门。只读，不新建部门，不改场站或部门的归属。
 */
@Tag(name = "管理后台 - 组织树")
@RestController
@RequestMapping("/dynamicbusiness/organization-tree")
@Validated
public class OrganizationTreeController {

    @Resource
    private OrganizationTreeQueryService organizationTreeQueryService;

    @GetMapping
    @Operation(summary = "组织架构页的组织树", description = "组织机构按上下级展开（集团、省公司、分公司、作业区）。场站不在树上。展开收起由页面处理。")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<List<OrganizationTreeNodeRespVO>> getTree() {
        return success(organizationTreeQueryService.getTree());
    }

    @GetMapping("/sole-place-departments")
    @Operation(summary = "只有一条场站记录时的部门",
            description = "场站目录恰好一条启用记录时，返回这一条和挂在它上面的部门。零条或多条时 data 为空，页面仍显示组织列。")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<SolePlaceDepartmentsRespVO> listSolePlaceDepartments() {
        return success(organizationTreeQueryService.listSolePlaceDepartments());
    }

    @GetMapping("/departments")
    @Operation(summary = "这个组织节点的部门",
            description = "直接挂在这个节点上的部门，以及所属组织是这个节点的场站里的部门（带站名）。")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<List<OrganizationDepartmentRespVO>> listDepartments(
            @RequestParam("organizationEntityId") Long organizationEntityId) {
        return success(organizationTreeQueryService.listDepartments(organizationEntityId));
    }
}
