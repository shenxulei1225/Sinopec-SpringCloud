package cn.cheers.x.system.controller.admin.user;

import cn.hutool.core.collection.CollUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.datapermission.core.annotation.DataPermission;
import cn.cheers.x.module.dynamicbusiness.api.directory.DepartmentDirectoryApi;
import cn.cheers.x.module.dynamicbusiness.api.directory.UserAccountApi;
import cn.cheers.x.module.dynamicbusiness.api.directory.dto.DepartmentDTO;
import cn.cheers.x.module.dynamicbusiness.api.directory.dto.UserAccountDTO;
import cn.cheers.x.system.controller.admin.user.vo.profile.UserProfileRespVO;
import cn.cheers.x.system.controller.admin.user.vo.profile.UserProfileUpdatePasswordReqVO;
import cn.cheers.x.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import cn.cheers.x.system.convert.user.UserConvert;
import cn.cheers.x.system.dal.dataobject.dept.PostDO;
import cn.cheers.x.system.dal.dataobject.dept.UserPostDO;
import cn.cheers.x.system.dal.dataobject.permission.RoleDO;
import cn.cheers.x.system.dal.mysql.dept.UserPostMapper;
import cn.cheers.x.system.service.dept.PostService;
import cn.cheers.x.system.service.permission.PermissionService;
import cn.cheers.x.system.service.permission.RoleService;
import cn.cheers.x.system.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;
import static cn.cheers.x.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 登录用户个人中心。
 * 管什么：读当前登录用户目录资料、改自己的登录口令。
 * 不管什么：管理员重置别人口令；组织里改姓名手机。
 * 禁止：用 system_users 查登录用户编号。
 */
@Tag(name = "管理后台 - 用户个人中心")
@RestController
@RequestMapping("/system/user/profile")
@Validated
@Slf4j
public class UserProfileController {

    @Resource
    private AdminUserService userService;
    @Resource
    private UserAccountApi userAccountApi;
    @Resource
    private DepartmentDirectoryApi departmentDirectoryApi;
    @Resource
    private UserPostMapper userPostMapper;
    @Resource
    private PostService postService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private RoleService roleService;

    @GetMapping("/get")
    @Operation(summary = "获得登录用户信息")
    @DataPermission(enable = false) // 关闭数据权限，避免只查看自己时，查询不到部门。
    public CommonResult<UserProfileRespVO> getUserProfile() {
        Long loginUserId = getLoginUserId();
        // 登录编号是用户目录 id。资料只读用户目录。
        UserAccountDTO user = userAccountApi.getUserAccount(loginUserId).getCheckedData();
        List<RoleDO> userRoles = roleService.getRoleListFromCache(
                permissionService.getUserRoleIdListByUserId(loginUserId));
        DepartmentDTO dept = user.getDepartmentId() != null
                ? departmentDirectoryApi.listDepartmentsByIds(List.of(user.getDepartmentId()))
                        .getCheckedData().get(0) : null;
        Set<Long> postIds = convertSet(userPostMapper.selectListByUserId(loginUserId), UserPostDO::getPostId);
        List<PostDO> posts = CollUtil.isNotEmpty(postIds) ? postService.getPostList(postIds) : null;
        return success(UserConvert.INSTANCE.convert(user, userRoles, dept, posts));
    }

    @PutMapping("/update")
    @Operation(summary = "修改用户个人信息",
            description = "登录用户编号已是用户目录 id。资料请在组织用户目录改，本接口拒绝写系统用户表。")
    public CommonResult<Boolean> updateUserProfile(@Valid @RequestBody UserProfileUpdateReqVO reqVO) {
        userService.updateUserProfile(getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/update-password")
    @Operation(summary = "修改用户个人密码",
            description = "按当前登录的用户目录编号，核对旧口令后写入 system_login_credential。不查 system_users。")
    @DataPermission(enable = false) // 改自己的密码只按登录用户查自己，不按部门可见范围过滤
    public CommonResult<Boolean> updateUserProfilePassword(@Valid @RequestBody UserProfileUpdatePasswordReqVO reqVO) {
        userService.updateUserPassword(getLoginUserId(), reqVO);
        return success(true);
    }

}
