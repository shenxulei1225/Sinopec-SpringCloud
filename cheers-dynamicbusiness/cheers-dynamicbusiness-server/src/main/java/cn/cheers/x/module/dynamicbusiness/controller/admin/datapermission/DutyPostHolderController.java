package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo.DutyPostRolesSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo.DutyPostUserCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo.DutyPostUserRespVO;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.DutyPostUserQueryService;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.DutyPostUserService;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.PostAssignmentService;
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
 * 组织架构「职务管理」：给职务分配人员、给职务分配角色。
 * 人员不写任职，不带管理单元。角色写的是职务绑定角色，和角色页是同一张表。
 * 不负责：谁在哪个管理单元能看数据。
 */
@Tag(name = "管理后台 - 职务上的人员和角色")
@RestController
@RequestMapping("/dynamicbusiness")
@Validated
public class DutyPostHolderController {

    @Resource
    private DutyPostUserService dutyPostUserService;
    @Resource
    private DutyPostUserQueryService dutyPostUserQueryService;
    @Resource
    private PostAssignmentService postAssignmentService;

    @GetMapping("/duty-post-user/list")
    @Operation(summary = "列出这个职务上的人员")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public CommonResult<List<DutyPostUserRespVO>> listUsers(@RequestParam("dutyPostId") Long dutyPostId) {
        return success(dutyPostUserQueryService.listByDutyPostId(dutyPostId).stream()
                .map(DutyPostUserRespVO::from).toList());
    }

    @PostMapping("/duty-post-user/create")
    @Operation(summary = "给这个职务分配一批人")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-user-role')")
    public CommonResult<List<Long>> createUsers(@Valid @RequestBody DutyPostUserCreateReqVO reqVO) {
        return success(dutyPostUserService.createBatch(reqVO.getDutyPostId(), reqVO.getUserIds()));
    }

    @DeleteMapping("/duty-post-user/delete")
    @Operation(summary = "从这个职务去掉一个人")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-user-role')")
    public CommonResult<Boolean> deleteUser(@RequestParam("id") Long id) {
        dutyPostUserService.delete(id);
        return success(true);
    }

    @PutMapping("/duty-post-role/save-by-duty-post")
    @Operation(summary = "职务管理整份保存这个职务绑定的角色")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-role-menu')")
    public CommonResult<Boolean> saveRoles(@Valid @RequestBody DutyPostRolesSaveReqVO reqVO) {
        postAssignmentService.replaceRolesOfDutyPost(reqVO.getDutyPostId(), reqVO.getRoleIds());
        return success(true);
    }
}
