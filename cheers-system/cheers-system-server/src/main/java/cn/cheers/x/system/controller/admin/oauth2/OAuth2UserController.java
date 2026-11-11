package cn.cheers.x.system.controller.admin.oauth2;

import cn.hutool.core.collection.CollUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.module.dynamicbusiness.api.directory.DepartmentDirectoryApi;
import cn.cheers.x.module.dynamicbusiness.api.directory.UserAccountApi;
import cn.cheers.x.module.dynamicbusiness.api.directory.dto.DepartmentDTO;
import cn.cheers.x.module.dynamicbusiness.api.directory.dto.UserAccountDTO;
import cn.cheers.x.system.controller.admin.oauth2.vo.user.OAuth2UserInfoRespVO;
import cn.cheers.x.system.controller.admin.oauth2.vo.user.OAuth2UserUpdateReqVO;
import cn.cheers.x.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import cn.cheers.x.system.dal.dataobject.dept.PostDO;
import cn.cheers.x.system.dal.dataobject.dept.UserPostDO;
import cn.cheers.x.system.dal.mysql.dept.UserPostMapper;
import cn.cheers.x.system.service.dept.PostService;
import cn.cheers.x.system.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;
import static cn.cheers.x.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 提供给外部应用调用为主。
 *
 * 管什么：按登录用户目录编号读基本资料。
 * 禁止：用 system_users 查登录用户编号。
 */
@Tag(name = "管理后台 - OAuth2.0 用户")
@RestController
@RequestMapping("/system/oauth2/user")
@Validated
@Slf4j
public class OAuth2UserController {

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

    @GetMapping("/get")
    @Operation(summary = "获得用户基本信息")
    @PreAuthorize("@ss.hasScope('user.read')")
    public CommonResult<OAuth2UserInfoRespVO> getUserInfo() {
        Long loginUserId = getLoginUserId();
        UserAccountDTO user = userAccountApi.getUserAccount(loginUserId).getCheckedData();
        OAuth2UserInfoRespVO resp = new OAuth2UserInfoRespVO();
        resp.setId(user.getId());
        resp.setUsername(user.getAccount());
        resp.setNickname(user.getNickname());
        resp.setMobile(user.getMobile());
        if (user.getDepartmentId() != null) {
            DepartmentDTO dept = departmentDirectoryApi.listDepartmentsByIds(List.of(user.getDepartmentId()))
                    .getCheckedData().get(0);
            resp.setDept(BeanUtils.toBean(dept, OAuth2UserInfoRespVO.Dept.class));
        }
        Set<Long> postIds = convertSet(userPostMapper.selectListByUserId(loginUserId), UserPostDO::getPostId);
        if (CollUtil.isNotEmpty(postIds)) {
            List<PostDO> posts = postService.getPostList(postIds);
            resp.setPosts(BeanUtils.toBean(posts, OAuth2UserInfoRespVO.Post.class));
        }
        return success(resp);
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户基本信息")
    @PreAuthorize("@ss.hasScope('user.write')")
    public CommonResult<Boolean> updateUserInfo(@Valid @RequestBody OAuth2UserUpdateReqVO reqVO) {
        // 复用个人中心更新：目录用户会明确拒绝写系统用户表。
        userService.updateUserProfile(getLoginUserId(), BeanUtils.toBean(reqVO, UserProfileUpdateReqVO.class));
        return success(true);
    }

}
