package cn.cheers.x.module.dynamicbusiness.api.directory;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.directory.dto.DepartmentDTO;
import cn.cheers.x.module.dynamicbusiness.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 部门目录跨模块只读接口。
 *
 * <p>权威来源是动态业务 {@code department} 实体及其 {@code leader_user_id} 固定列。
 * 本接口不访问 {@code system_dept}/{@code system_users}，也不返回通用字段袋。</p>
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 部门目录")
public interface DepartmentDirectoryApi {

    String PREFIX = ApiConstants.DYNAMICBUSINESS_PREFIX + "/department-directory";

    /** 按 ID 读取部门。部门不存在时显式报错。 */
    @GetMapping(PREFIX + "/get")
    @Operation(summary = "按 ID 读取部门")
    CommonResult<DepartmentDTO> getDepartment(@RequestParam("id") Long id);

    /** 一次读取全部指定部门。任一 ID 不存在时显式报错。 */
    @GetMapping(PREFIX + "/list-by-ids")
    @Operation(summary = "按 ID 批量读取部门")
    CommonResult<List<DepartmentDTO>> listDepartmentsByIds(@RequestParam("ids") List<Long> ids);

    /** 校验部门存在且启用。校验失败时显式报错。 */
    @GetMapping(PREFIX + "/validate-enabled")
    @Operation(summary = "校验部门存在且启用")
    CommonResult<Boolean> validateDepartmentEnabled(@RequestParam("id") Long id);

    /** 返回指定部门及其全部子孙部门。 */
    @GetMapping(PREFIX + "/subtree")
    @Operation(summary = "读取含自身的部门子树闭包")
    CommonResult<List<DepartmentDTO>> listDepartmentSubtree(@RequestParam("id") Long id);

    /** 读取部门负责人对应的用户目录 ID。未配置时返回 null。 */
    @GetMapping(PREFIX + "/leader-user-id")
    @Operation(summary = "读取部门负责人用户目录 ID")
    CommonResult<Long> getLeaderUserAccountId(@RequestParam("departmentId") Long departmentId);

    /** 按部门分组批量返回用户目录 ID。 */
    @GetMapping(PREFIX + "/user-account-ids")
    @Operation(summary = "按部门 ID 批量读取用户目录 ID")
    CommonResult<Map<Long, List<Long>>> listUserAccountIdsByDepartmentIds(
            @RequestParam("departmentIds") List<Long> departmentIds);
}
