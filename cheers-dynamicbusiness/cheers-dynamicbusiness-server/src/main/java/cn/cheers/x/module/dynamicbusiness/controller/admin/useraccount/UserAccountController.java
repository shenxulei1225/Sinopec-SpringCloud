package cn.cheers.x.module.dynamicbusiness.controller.admin.useraccount;

import cn.cheers.x.framework.apilog.core.annotation.ApiAccessLog;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.useraccount.vo.UserAccountCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.useraccount.vo.UserAccountDepartmentListReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.useraccount.vo.UserAccountDepartmentListRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.useraccount.vo.UserAccountProfileUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.useraccount.vo.UserAccountResetPasswordReqVO;
import cn.cheers.x.module.dynamicbusiness.service.useraccount.UserAccountCatalogEnsure;
import cn.cheers.x.module.dynamicbusiness.service.useraccount.UserAccountCommandService;
import cn.cheers.x.module.dynamicbusiness.service.useraccount.UserAccountQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.CREATE;
import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.UPDATE;
import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 准备用户目录。人员列表和新建走数据管理的实体接口，不走这里。
 * 不负责角色，不负责改部门树。
 */
@Tag(name = "管理后台 - 用户目录")
@RestController
@RequestMapping("/dynamicbusiness/user-account")
@Validated
public class UserAccountController {

    @Resource
    private UserAccountCommandService userAccountCommandService;
    @Resource
    private UserAccountQueryService userAccountQueryService;
    @Resource
    private UserAccountCatalogEnsure userAccountCatalogEnsure;

    @PostMapping("/prepare-catalog")
    @Operation(summary = "准备用户目录和用户型号")
    @PreAuthorize("@ss.hasPermission('system:user:create')")
    public CommonResult<Long> prepareCatalog() {
        return success(userAccountCatalogEnsure.ensureModelId());
    }

    @PostMapping("/create")
    @Operation(summary = "新建用户")
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@ss.hasPermission('system:user:create')")
    public CommonResult<Long> create(@Valid @RequestBody UserAccountCreateReqVO request) {
        return success(userAccountCommandService.create(request));
    }

    @PostMapping("/list-by-departments")
    @Operation(summary = "按部门列出用户目录里的人")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public CommonResult<UserAccountDepartmentListRespVO> listByDepartments(
            @Valid @RequestBody UserAccountDepartmentListReqVO request) {
        List<Long> categoryIds = request == null ? null : request.getCategoryIds();
        return success(userAccountQueryService.listByDepartments(categoryIds));
    }

    @PutMapping("/update-profile")
    @Operation(summary = "修改用户的昵称、手机和状态")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:user:update')")
    public CommonResult<Boolean> updateProfile(@Valid @RequestBody UserAccountProfileUpdateReqVO request) {
        userAccountCommandService.updateProfile(request);
        return success(true);
    }

    @PutMapping("/reset-password")
    @Operation(summary = "重置用户口令")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:user:update-password')")
    public CommonResult<Boolean> resetPassword(@Valid @RequestBody UserAccountResetPasswordReqVO request) {
        Long id = request == null ? null : request.getId();
        String password = request == null ? null : request.getPassword();
        userAccountCommandService.resetPassword(id, password);
        return success(true);
    }
}
