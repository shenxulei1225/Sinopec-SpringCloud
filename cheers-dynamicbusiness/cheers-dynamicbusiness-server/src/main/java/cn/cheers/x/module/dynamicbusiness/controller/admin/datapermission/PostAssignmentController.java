package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo.DutyPostRoleBindingRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo.DutyPostRoleSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo.PostAssignmentCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo.PostAssignmentRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo.PostAssignmentUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo.PostAssignmentUserCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo.PostScopeRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.PostAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.PostAssignmentQueryService;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.PostAssignmentService;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.PostScopeQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
 * 任职和角色页的「绑定职务」。
 * 数据资源管理按管理单元加人、改起止日期、撤销；人员详情只读列出这个人的任职。
 * 数据资源管理「人员的数据范围」给一个人加几个组织的任职，「按范围看」列出能看到某个组织的任职。
 * 组织架构页职务分组和人员任职展开时，读「能看的数据」。三处写的都是同一张任职表。
 * 只做入参、权限注解和转换。核对在 {@link PostAssignmentService}。
 */
@Tag(name = "管理后台 - 任职与职务绑定角色")
@RestController
@RequestMapping("/dynamicbusiness")
@Validated
public class PostAssignmentController {

    @Resource
    private PostAssignmentService postAssignmentService;
    @Resource
    private PostAssignmentQueryService postAssignmentQueryService;
    @Resource
    private PostScopeQueryService postScopeQueryService;

    @GetMapping("/post-assignment/list")
    @Operation(summary = "人员详情：列出某人全部任职，以及每条任职能看的目录")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public CommonResult<List<PostAssignmentRespVO>> list(@RequestParam("userId") Long userId) {
        return success(postAssignmentQueryService.listViewsByUserId(userId).stream()
                .map(PostAssignmentController::toResp).toList());
    }

    @GetMapping("/post-assignment/list-by-unit")
    @Operation(summary = "组织架构页：列出一个管理单元里的全部任职")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public CommonResult<List<PostAssignmentRespVO>> listByUnit(@RequestParam("managementUnitId") Long managementUnitId) {
        return success(postAssignmentQueryService.listViewsByManagementUnitId(managementUnitId).stream()
                .map(PostAssignmentController::toResp).toList());
    }

    @GetMapping("/post-assignment/list-covering-unit")
    @Operation(summary = "按范围看：能看到这个组织的任职，含任职在它上级的")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public CommonResult<List<PostAssignmentRespVO>> listCoveringUnit(@RequestParam("managementUnitId") Long managementUnitId) {
        return success(postAssignmentQueryService.listViewsCoveringUnit(managementUnitId).stream()
                .map(PostAssignmentController::toResp).toList());
    }

    @GetMapping("/post-assignment/scope")
    @Operation(summary = "能看的数据：某个组织里任某个职务的人能看到的范围、目录和操作")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public CommonResult<PostScopeRespVO> scope(@RequestParam("managementUnitId") Long managementUnitId,
                                               @RequestParam("dutyPostId") Long dutyPostId) {
        return success(PostScopeRespVO.from(postScopeQueryService.describe(managementUnitId, dutyPostId)));
    }

    @PostMapping("/post-assignment/create-for-user")
    @Operation(summary = "给一个人在一个组织上任几个职务")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-user-role')")
    public CommonResult<List<Long>> createForUser(@Valid @RequestBody PostAssignmentUserCreateReqVO reqVO) {
        return success(postAssignmentService.createAtUnit(new PostAssignmentService.UnitPostsBatch(reqVO.getUserId(),
                reqVO.getManagementUnitId(), reqVO.getDutyPostIds(), reqVO.getValidFrom(), reqVO.getValidTo())));
    }

    @PostMapping("/post-assignment/create")
    @Operation(summary = "往一个管理单元按职务加一批人")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-user-role')")
    public CommonResult<List<Long>> create(@Valid @RequestBody PostAssignmentCreateReqVO reqVO) {
        return success(postAssignmentService.createBatch(new PostAssignmentService.Batch(reqVO.getManagementUnitId(),
                reqVO.getDutyPostId(), reqVO.getUserIds(), reqVO.getValidFrom(), reqVO.getValidTo())));
    }

    @PutMapping("/post-assignment/update")
    @Operation(summary = "改一条任职的起止日期")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-user-role')")
    public CommonResult<Boolean> update(@Valid @RequestBody PostAssignmentUpdateReqVO reqVO) {
        postAssignmentService.updateValidity(reqVO.getId(), reqVO.getValidFrom(), reqVO.getValidTo());
        return success(true);
    }

    @DeleteMapping("/post-assignment/delete")
    @Operation(summary = "撤销一条任职")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-user-role')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        postAssignmentService.delete(id);
        return success(true);
    }

    @GetMapping("/duty-post-role/list")
    @Operation(summary = "角色页回显这个角色绑定的职务")
    @PreAuthorize("@ss.hasPermission('system:role:query')")
    public CommonResult<List<Long>> dutyPosts(@RequestParam("roleId") Long roleId) {
        return success(postAssignmentQueryService.listDutyPostIdsByRoleId(roleId));
    }

    @GetMapping("/duty-post-role/list-by-duty-post")
    @Operation(summary = "改权限弹窗：这个职务绑定的角色，每个角色带上绑定它的全部职务")
    @PreAuthorize("@ss.hasPermission('system:role:query')")
    public CommonResult<List<DutyPostRoleBindingRespVO>> rolesOfDutyPost(@RequestParam("dutyPostId") Long dutyPostId) {
        return success(postAssignmentQueryService.listRoleBindingsOfDutyPost(dutyPostId).stream()
                .map(DutyPostRoleBindingRespVO::from).toList());
    }

    @PutMapping("/duty-post-role/save")
    @Operation(summary = "角色页整份保存这个角色绑定的职务")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-role-menu')")
    public CommonResult<Boolean> saveDutyPosts(@Valid @RequestBody DutyPostRoleSaveReqVO reqVO) {
        postAssignmentService.replaceRoleDutyPosts(reqVO.getRoleId(), reqVO.getDutyPostIds());
        return success(true);
    }

    private static PostAssignmentRespVO toResp(PostAssignmentQueryService.PostAssignmentView view) {
        PostAssignmentDO row = view.row();
        PostAssignmentRespVO resp = new PostAssignmentRespVO();
        resp.setId(row.getId());
        resp.setUserId(row.getUserId());
        resp.setUserName(view.userName());
        resp.setDutyPostId(row.getDutyPostId());
        resp.setDutyPostName(view.dutyPostName());
        resp.setManagementUnitId(row.getManagementUnitId());
        resp.setManagementUnitName(view.managementUnitName());
        resp.setValidFrom(row.getValidFrom());
        resp.setValidTo(row.getValidTo());
        resp.setVisibleCatalogs(view.visibleCatalogs().stream().map(catalog -> {
            PostAssignmentRespVO.CatalogName item = new PostAssignmentRespVO.CatalogName();
            item.setRegistryCode(catalog.registryCode());
            item.setName(catalog.name());
            return item;
        }).toList());
        return resp;
    }
}
