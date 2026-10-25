package cn.cheers.x.system.api.user;

import cn.hutool.core.util.ObjUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.datapermission.core.annotation.DataPermission;
import cn.cheers.x.module.dynamicbusiness.api.directory.DepartmentDirectoryApi;
import cn.cheers.x.module.dynamicbusiness.api.directory.UserAccountApi;
import cn.cheers.x.module.dynamicbusiness.api.directory.dto.DepartmentDTO;
import cn.cheers.x.module.dynamicbusiness.api.directory.dto.UserAccountDTO;
import cn.cheers.x.system.api.user.dto.AdminUserRespDTO;
import cn.cheers.x.system.dal.dataobject.dept.UserPostDO;
import cn.cheers.x.system.dal.mysql.dept.UserPostMapper;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.framework.common.pojo.CommonResult.success;
import static cn.cheers.x.framework.common.util.collection.CollectionUtils.convertList;
import static cn.cheers.x.system.enums.ErrorCodeConstants.USER_IS_DISABLE;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class AdminUserApiImpl implements AdminUserApi {

    /** 用户目录状态：1 表示启用。禁止套用系统用户 0=启用。 */
    private static final Integer USER_ACCOUNT_ENABLED = 1;

    @Resource
    private DepartmentDirectoryApi departmentDirectoryApi;
    @Resource
    private UserAccountApi userAccountApi;
    @Resource
    private UserPostMapper userPostMapper;

    @Override
    @DataPermission(enable = false) // 忽略数据权限，避免因为过滤，导致无法查询用户。类似：https://github.com/YunaiV/ruoyi-vue-pro/issues/1051
    public CommonResult<AdminUserRespDTO> getUser(Long id) {
        return success(toResponse(userAccountApi.getUserAccount(id).getCheckedData()));
    }

    @Override
    public CommonResult<List<AdminUserRespDTO>> getUserListBySubordinate(Long id) {
        UserAccountDTO user = userAccountApi.getUserAccount(id).getCheckedData();
        if (user.getDepartmentId() == null) {
            return success(Collections.emptyList());
        }
        DepartmentDTO dept = departmentDirectoryApi.getDepartment(user.getDepartmentId()).getCheckedData();
        if (ObjUtil.notEqual(dept.getLeaderUserId(), id)) { // 校验为负责人
            return success(Collections.emptyList());
        }
        List<Long> departmentIds = convertList(
                departmentDirectoryApi.listDepartmentSubtree(dept.getId()).getCheckedData(),
                DepartmentDTO::getId);
        return success(loadUsersByDepartmentIds(departmentIds).stream()
                .filter(item -> ObjUtil.notEqual(item.getId(), id))
                .toList());
    }

    @Override
    public CommonResult<List<AdminUserRespDTO>> getUserList(Collection<Long> ids) {
        return success(loadUserAccounts(ids));
    }

    @Override
    public CommonResult<List<AdminUserRespDTO>> getUserListByDeptIds(Collection<Long> deptIds) {
        return success(loadUsersByDepartmentIds(List.copyOf(deptIds)));
    }

    @Override
    public CommonResult<List<AdminUserRespDTO>> getUserListByPostIds(Collection<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return success(Collections.emptyList());
        }
        // 岗位关系上的用户编号已经迁成用户目录编号。禁止再用它查询 system_users。
        List<Long> userIds = userPostMapper.selectListByPostIds(postIds).stream()
                .map(UserPostDO::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        return success(loadUserAccounts(userIds));
    }

    @Override
    public CommonResult<List<AdminUserRespDTO>> getUserListByNickname(String nickname) {
        return success(convertList(
                userAccountApi.listUserAccountsByNickname(nickname).getCheckedData(),
                this::toResponse));
    }

    @Override
    public CommonResult<Boolean> validateUserList(Collection<Long> ids) {
        List<UserAccountDTO> users = loadUserAccountDtos(ids);
        users.forEach(user -> {
            if (!USER_ACCOUNT_ENABLED.equals(user.getStatus())) {
                throw exception(USER_IS_DISABLE, user.getNickname());
            }
        });
        return success(true);
    }

    /**
     * 按动态部门目录中的人员挂接读取用户名册。
     * 这里不查询 system_users，也不在缺少目录记录时返回旧用户资料。
     */
    private List<AdminUserRespDTO> loadUsersByDepartmentIds(List<Long> departmentIds) {
        if (departmentIds.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, List<Long>> userIdsByDepartment = departmentDirectoryApi
                .listUserAccountIdsByDepartmentIds(departmentIds).getCheckedData();
        List<Long> userIds = departmentIds.stream()
                .flatMap(departmentId -> userIdsByDepartment
                        .getOrDefault(departmentId, Collections.emptyList()).stream())
                .distinct()
                .toList();
        return loadUserAccounts(userIds);
    }

    private List<AdminUserRespDTO> loadUserAccounts(Collection<Long> ids) {
        return convertList(loadUserAccountDtos(ids), this::toResponse);
    }

    private List<UserAccountDTO> loadUserAccountDtos(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return userAccountApi.listUserAccountsByIds(List.copyOf(ids)).getCheckedData();
    }

    private AdminUserRespDTO toResponse(UserAccountDTO user) {
        AdminUserRespDTO response = new AdminUserRespDTO();
        response.setId(user.getId());
        response.setUsername(user.getAccount());
        response.setNickname(user.getNickname());
        response.setStatus(user.getStatus());
        response.setDeptId(user.getDepartmentId());
        response.setMobile(user.getMobile());
        return response;
    }

}
