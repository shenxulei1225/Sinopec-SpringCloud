package cn.cheers.x.system.api.permission;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.system.api.permission.dto.RoleRespDTO;
import cn.cheers.x.system.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 角色")
public interface RoleApi {

    String PREFIX = ApiConstants.PREFIX + "/role";

    @GetMapping(PREFIX + "/valid")
    @Operation(summary = "校验角色是否合法")
    @Parameter(name = "ids", description = "角色编号数组", example = "1,2", required = true)
    CommonResult<Boolean> validRoleList(@RequestParam("ids") Collection<Long> ids);

    @GetMapping(PREFIX + "/get")
    @Operation(summary = "获取角色")
    CommonResult<RoleRespDTO> getRole(@RequestParam("id") Long id);

    @GetMapping(PREFIX + "/list")
    @Operation(summary = "获取角色列表")
    CommonResult<List<RoleRespDTO>> getRoleList(@RequestParam("ids") Collection<Long> ids);

    @GetMapping(PREFIX + "/list-by-user")
    @Operation(summary = "获取用户拥有的角色编号")
    CommonResult<Collection<Long>> getUserRoleIdListByUserId(@RequestParam("userId") Long userId);

}