package cn.cheers.x.system.api.permission;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.system.api.permission.dto.RoleRespDTO;
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

    @Override
    public CommonResult<Boolean> validRoleList(Collection<Long> ids) {
        roleService.validateRoleList(ids);
        return success(true);
    }

    @Override
    public CommonResult<RoleRespDTO> getRole(Long id) {
        return success(null);
    }

    @Override
    public CommonResult<List<RoleRespDTO>> getRoleList(Collection<Long> ids) {
        return success(List.of());
    }

    @Override
    public CommonResult<Collection<Long>> getUserRoleIdListByUserId(Long userId) {
        return success(roleService.getRoleIdListByUserId(userId));
    }
}
