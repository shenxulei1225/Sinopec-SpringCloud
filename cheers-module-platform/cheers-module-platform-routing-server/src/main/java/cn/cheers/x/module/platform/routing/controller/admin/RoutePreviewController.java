package cn.cheers.x.module.platform.routing.controller.admin;

import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewDTO;
import cn.cheers.x.module.platform.contract.dto.route.RouteRequestDTO;
import cn.cheers.x.module.platform.routing.service.RoutePreviewService;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 路径规划")
@RestController
@RequestMapping("/platform/routing")
public class RoutePreviewController {

    @Resource
    private RoutePreviewService routePreviewService;

    @PostMapping("/preview")
    @Operation(summary = "路径规划预览")
    public CommonResult<RoutePreviewDTO> preview(@Valid @RequestBody RouteRequestDTO request) {
        return success(routePreviewService.preview(request));
    }
}
