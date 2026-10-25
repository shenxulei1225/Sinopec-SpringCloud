package cn.cheers.x.module.dynamicbusiness.api.directory;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.directory.dto.DepartmentDTO;
import cn.cheers.x.module.dynamicbusiness.service.directory.DepartmentDirectoryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 部门目录 RPC 入口。
 *
 * <p>只负责参数接收和响应包装。查询、启用校验和批量分组统一由部门目录查询服务完成。</p>
 */
@RestController
@Validated
@RequiredArgsConstructor
public class DepartmentDirectoryApiImpl implements DepartmentDirectoryApi {

    private final DepartmentDirectoryQueryService queryService;

    @Override
    public CommonResult<DepartmentDTO> getDepartment(Long id) {
        return success(queryService.getDepartment(id));
    }

    @Override
    public CommonResult<List<DepartmentDTO>> listDepartmentsByIds(List<Long> ids) {
        return success(queryService.listDepartmentsByIds(ids));
    }

    @Override
    public CommonResult<Boolean> validateDepartmentEnabled(Long id) {
        queryService.validateEnabled(id);
        return success(true);
    }

    @Override
    public CommonResult<List<DepartmentDTO>> listDepartmentSubtree(Long id) {
        return success(queryService.listSubtree(id));
    }

    @Override
    public CommonResult<Long> getLeaderUserAccountId(Long departmentId) {
        return success(queryService.getLeaderUserAccountId(departmentId));
    }

    @Override
    public CommonResult<Map<Long, List<Long>>> listUserAccountIdsByDepartmentIds(List<Long> departmentIds) {
        return success(queryService.listUserAccountIdsByDepartmentIds(departmentIds));
    }
}
