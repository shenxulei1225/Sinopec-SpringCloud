package cn.cheers.x.system.controller.admin.view;

import cn.cheers.x.framework.apilog.core.annotation.ApiAccessLog;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.system.controller.admin.view.vo.ViewCreateReqVO;
import cn.cheers.x.system.controller.admin.view.vo.ViewRespVO;
import cn.cheers.x.system.controller.admin.view.vo.ViewUpdateReqVO;
import cn.cheers.x.system.service.view.ViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.*;
import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 视图管理
 */
@Tag(name = "管理后台 - 视图")
@RestController
@RequestMapping("/system/views")
public class ViewController {

    @Resource
    private ViewService viewService;

    // ==================== 客户端接口 ====================

    @GetMapping
    @Operation(summary = "获取所有启用的视图")
    public CommonResult<Map<String, ViewRespVO>> getEnabledViews() {
        return success(viewService.getEnabledViews());
    }

    @GetMapping("/{key}")
    @Operation(summary = "获取视图详情")
    public CommonResult<ViewRespVO> getView(@PathVariable("key") String key) {
        return success(viewService.getView(key));
    }

    // ==================== 管理后台接口 ====================

    @GetMapping("/list")
    @Operation(summary = "获取视图列表")
    @PreAuthorize("@ss.hasPermission('system:view:query')")
    public CommonResult<List<ViewRespVO>> getViewList() {
        return success(viewService.getViewList());
    }

    @GetMapping("/list/{key}")
    @Operation(summary = "获取视图详情")
    @PreAuthorize("@ss.hasPermission('system:view:query')")
    public CommonResult<ViewRespVO> getViewDetail(@PathVariable("key") String key) {
        return success(viewService.getView(key));
    }

    @PostMapping("/create")
    @Operation(summary = "创建视图")
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@ss.hasPermission('system:view:create')")
    public CommonResult<Long> createView(@Valid @RequestBody ViewCreateReqVO reqVO) {
        return success(viewService.createView(reqVO));
    }

    @PutMapping("/update/{key}")
    @Operation(summary = "更新视图")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:view:update')")
    public CommonResult<Boolean> updateView(
            @PathVariable("key") String key,
            @Valid @RequestBody ViewUpdateReqVO reqVO) {
        viewService.updateView(key, reqVO);
        return success(true);
    }

    @DeleteMapping("/delete/{key}")
    @Operation(summary = "删除视图")
    @ApiAccessLog(operateType = DELETE)
    @PreAuthorize("@ss.hasPermission('system:view:delete')")
    public CommonResult<Boolean> deleteView(@PathVariable("key") String key) {
        viewService.deleteView(key);
        return success(true);
    }
}
