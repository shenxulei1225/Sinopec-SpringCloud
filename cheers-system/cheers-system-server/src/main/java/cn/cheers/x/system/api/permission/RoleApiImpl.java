package cn.cheers.x.system.api.permission;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.system.api.permission.dto.RoleRespDTO;
import cn.cheers.x.system.dal.dataobject.permission.RoleDO;
import cn.cheers.x.system.service.permission.PermissionService;
import cn.cheers.x.system.service.permission.RoleService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class RoleApiImpl implements RoleApi {

    @Resource
    private RoleService roleService;
    @Resource
    private PermissionService permissionService;

    @Override
    public CommonResult<Boolean> validRoleList(Collection<Long> ids) {
        roleService.validateRoleList(ids);
        return success(true);
    }

    /**
     * 按编号读取角色记录，名称在 {@link RoleRespDTO#getName()}。
     * 编号没有对应角色时返回空。不负责这个人直接分配了哪些角色。
     */
    @Override
    public CommonResult<RoleRespDTO> getRole(Long id) {
        RoleDO role = roleService.getRole(id);
        return success(role == null ? null : BeanUtils.toBean(role, RoleRespDTO.class));
    }

    /** 按编号批量读取角色记录，供其它服务在自己的接口里带上角色名称。 */
    @Override
    public CommonResult<List<RoleRespDTO>> getRoleList(Collection<Long> ids) {
        return success(BeanUtils.toBean(roleService.getRoleList(ids), RoleRespDTO.class));
    }

    /**
     * 目录授权要按这个用户实际挂的角色判断。
     * 角色服务上的同名方法固定返回空列表，这里不能再走它。
     */
    @Override
    public CommonResult<Collection<Long>> getUserRoleIdListByUserId(Long userId) {
        return success(permissionService.getUserRoleIdListByUserId(userId));
    }
}
