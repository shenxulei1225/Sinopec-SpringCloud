package cn.iocoder.yudao.module.emergency.controller.admin.organization;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.organization.vo.*;
import cn.iocoder.yudao.module.emergency.service.organization.EmergencyOrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/emergency/organizations")
@Tag(name = "管理后台 - 应急组织")
public class EmergencyOrganizationController {

    @Resource
    private EmergencyOrganizationService organizationService;

    @PostMapping
    @Operation(summary = "创建应急组织")
    public CommonResult<Long> createOrganization(@Valid @RequestBody OrganizationCreateReqVO createReqVO) {
        return success(organizationService.createOrganization(createReqVO));
    }

    @PutMapping
    @Operation(summary = "更新应急组织")
    public CommonResult<Boolean> updateOrganization(@Valid @RequestBody OrganizationUpdateReqVO updateReqVO) {
        organizationService.updateOrganization(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除应急组织")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteOrganization(@PathVariable("id") Long id) {
        organizationService.deleteOrganization(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得应急组织")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<OrganizationRespVO> getOrganization(@RequestParam("id") Long id) {
        OrganizationRespVO organization = organizationService.getOrganization(id);
        return success(organization);
    }

    @GetMapping("/page")
    @Operation(summary = "获得应急组织分页")
    public CommonResult<PageResult<OrganizationRespVO>> getOrganizationPage(@Valid OrganizationPageReqVO pageReqVO) {
        PageResult<OrganizationRespVO> pageResult = organizationService.getOrganizationPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/list")
    @Operation(summary = "获得应急组织列表")
    @Parameter(name = "orgType", description = "组织类型", example = "leadership_group")
    public CommonResult<List<OrganizationRespVO>> getOrganizationList(@RequestParam(value = "orgType", required = false) String orgType) {
        List<OrganizationRespVO> list = organizationService.getOrganizationList(orgType);
        return success(list);
    }

    @PostMapping("/members")
    @Operation(summary = "添加组织成员")
    public CommonResult<Long> addOrganizationMember(@Valid @RequestBody OrganizationMemberCreateReqVO createReqVO) {
        return success(organizationService.addOrganizationMember(createReqVO));
    }

    @DeleteMapping("/members/{id}")
    @Operation(summary = "删除组织成员")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> removeOrganizationMember(@PathVariable("id") Long id) {
        organizationService.removeOrganizationMember(id);
        return success(true);
    }

    @GetMapping("/members")
    @Operation(summary = "获得组织成员列表")
    @Parameter(name = "orgId", description = "组织ID", required = true, example = "1")
    public CommonResult<List<OrganizationMemberRespVO>> getOrganizationMemberList(@RequestParam("orgId") Long orgId) {
        List<OrganizationMemberRespVO> list = organizationService.getOrganizationMemberList(orgId);
        return success(list);
    }
}
