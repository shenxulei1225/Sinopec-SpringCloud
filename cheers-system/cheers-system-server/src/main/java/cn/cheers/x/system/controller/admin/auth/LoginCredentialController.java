package cn.cheers.x.system.controller.admin.auth;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.system.controller.admin.auth.vo.LoginCredentialChangeReqVO;
import cn.cheers.x.system.controller.admin.auth.vo.LoginCredentialSaveReqVO;
import cn.cheers.x.system.service.auth.LoginCredentialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 认证的口令接口。
 *
 * 管什么：按记录编号保存新口令，或核对旧密码后更换。
 * 不管什么：用户目录上的昵称、手机、部门；不在这里新建人员。
 * 禁止：把口令写进日志。
 */
@Tag(name = "管理后台 - 登录凭证")
@RestController
@RequestMapping("/system/auth/credential")
@Validated
public class LoginCredentialController {

    @Resource
    private LoginCredentialService loginCredentialService;

    @PostMapping("/save")
    @Operation(summary = "保存新口令", description = "这个编号还没有口令时写入。已有口令则拒绝。")
    @PreAuthorize("@ss.hasPermission('system:user:create')")
    public CommonResult<Boolean> save(@Valid @RequestBody LoginCredentialSaveReqVO reqVO) {
        loginCredentialService.saveNew(reqVO.getUserId(), reqVO.getPassword());
        return success(true);
    }

    @PostMapping("/change")
    @Operation(summary = "修改密码", description = "核对旧密码后更换。没有口令时拒绝，不新建一份。")
    @PreAuthorize("@ss.hasPermission('system:user:update-password')")
    public CommonResult<Boolean> change(@Valid @RequestBody LoginCredentialChangeReqVO reqVO) {
        loginCredentialService.change(reqVO.getUserId(), reqVO.getOldPassword(), reqVO.getNewPassword());
        return success(true);
    }
}
