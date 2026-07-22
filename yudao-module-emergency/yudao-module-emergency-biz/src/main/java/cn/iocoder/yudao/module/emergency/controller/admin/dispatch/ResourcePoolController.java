package cn.iocoder.yudao.module.emergency.controller.admin.dispatch;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.dispatch.vo.*;
import cn.iocoder.yudao.module.emergency.service.dispatch.ResourcePoolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/admin-api/emergency/resources")
@Tag(name = "管理后台 - 资源池管理")
public class ResourcePoolController {

    @Resource
    private ResourcePoolService resourcePoolService;

    @PostMapping("/create")
    @Operation(summary = "创建资源")
    public CommonResult<Long> createResource(@Valid @RequestBody ResourcePoolCreateReqVO createReqVO) {
        Long resourceId = resourcePoolService.createResource(createReqVO);
        return success(resourceId);
    }

    @PutMapping("/update")
    @Operation(summary = "更新资源")
    public CommonResult<Boolean> updateResource(@Valid @RequestBody ResourcePoolUpdateReqVO updateReqVO) {
        resourcePoolService.updateResource(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除资源")
    @Parameter(name = "id", description = "资源编号", required = true)
    public CommonResult<Boolean> deleteResource(@RequestParam("id") Long id) {
        resourcePoolService.deleteResource(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得资源详情")
    @Parameter(name = "id", description = "资源编号", required = true)
    public CommonResult<ResourcePoolRespVO> getResource(@RequestParam("id") Long id) {
        ResourcePoolRespVO resource = resourcePoolService.getResourceDetail(id);
        return success(resource);
    }

    @GetMapping("/page")
    @Operation(summary = "获得资源分页")
    public CommonResult<PageResult<ResourcePoolRespVO>> getResourcePage(@Valid ResourcePoolPageReqVO pageReqVO) {
        PageResult<ResourcePoolRespVO> pageResult = resourcePoolService.getResourcePage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/list")
    @Operation(summary = "获得资源列表")
    @Parameter(name = "ids", description = "资源编号数组", required = true)
    public CommonResult<List<ResourcePoolRespVO>> getResourceList(@RequestParam("ids") Collection<Long> ids) {
        List<ResourcePoolRespVO> list = resourcePoolService.getResourceList(ids);
        return success(list);
    }

    @GetMapping("/available")
    @Operation(summary = "获得指定类型的可用资源")
    @Parameter(name = "type", description = "资源类型", required = true)
    public CommonResult<List<cn.iocoder.yudao.module.emergency.dal.dataobject.dispatch.ResourcePoolDO>> getAvailableResources(
            @RequestParam("type") String type) {
        List<cn.iocoder.yudao.module.emergency.dal.dataobject.dispatch.ResourcePoolDO> resources =
            resourcePoolService.getAvailableResourcesByType(type);
        return success(resources);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新资源状态")
    @Parameter(name = "id", description = "资源编号", required = true)
    public CommonResult<Boolean> updateResourceStatus(@PathVariable("id") Long id,
                                                      @RequestParam("status") String status) {
        resourcePoolService.updateResourceStatus(id, status);
        return success(true);
    }

    @GetMapping("/count")
    @Operation(summary = "统计资源数量")
    @Parameter(name = "type", description = "资源类型")
    @Parameter(name = "status", description = "资源状态")
    public CommonResult<Integer> countResources(@RequestParam(value = "type", required = false) String type,
                                                @RequestParam(value = "status", required = false) String status) {
        Integer count = resourcePoolService.countResourcesByTypeAndStatus(type, status);
        return success(count);
    }
}



