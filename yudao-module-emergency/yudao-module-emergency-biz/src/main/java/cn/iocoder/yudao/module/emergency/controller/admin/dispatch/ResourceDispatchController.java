package cn.iocoder.yudao.module.emergency.controller.admin.dispatch;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.emergency.controller.admin.dispatch.vo.ResourceDispatchCreateReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.dispatch.vo.ResourceDispatchRecoverReqVO;
import cn.iocoder.yudao.module.emergency.service.dispatch.ResourceDispatchService;
import cn.iocoder.yudao.module.emergency.dal.mysql.dispatch.ResourceDispatchMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/emergency/resources")
@Tag(name = "管理后台 - 资源调度")
public class ResourceDispatchController {

    @Resource
    private ResourceDispatchService dispatchService;
    @Resource
    private ResourceDispatchMapper dispatchMapper;

    @GetMapping("/dispatch/{id}")
    @Operation(summary = "获取调度详情", description = "根据ID获取资源调度记录详情")
    @Parameter(name = "id", description = "调度ID", required = true)
    public CommonResult<cn.iocoder.yudao.module.emergency.dal.dataobject.dispatch.ResourceDispatchDO> get(@PathVariable("id") Long id) {
        return success(dispatchMapper.selectById(id));
    }

    @PostMapping("/dispatch")
    @Operation(summary = "创建资源调度", description = "创建新的资源调度记录")
    public CommonResult<Long> create(@Valid @RequestBody ResourceDispatchCreateReqVO reqVO) {
        var entity = dispatchService.createDispatch(reqVO.toDO());
        return success(entity.getId());
    }

    @PostMapping("/dispatch/{id}/dispatch")
    @Operation(summary = "派发资源", description = "将待派发状态的资源标记为已派发")
    @Parameter(name = "id", description = "调度ID", required = true)
    public CommonResult<Boolean> dispatch(@PathVariable("id") Long id) {
        dispatchService.dispatch(id);
        return success(true);
    }

    @PostMapping("/dispatch/{id}/in-use")
    @Operation(summary = "标记资源使用中", description = "将已派发的资源标记为使用中")
    @Parameter(name = "id", description = "调度ID", required = true)
    public CommonResult<Boolean> markInUse(@PathVariable("id") Long id) {
        dispatchService.markInUse(id);
        return success(true);
    }

    @PostMapping("/dispatch/{id}/recover")
    @Operation(summary = "回收资源", description = "回收资源，标记为已回收状态")
    @Parameter(name = "id", description = "调度ID", required = true)
    public CommonResult<Boolean> recover(@PathVariable("id") Long id,
                                         @Valid @RequestBody ResourceDispatchRecoverReqVO reqVO) {
        dispatchService.recover(id, reqVO.getReason());
        return success(true);
    }
}

