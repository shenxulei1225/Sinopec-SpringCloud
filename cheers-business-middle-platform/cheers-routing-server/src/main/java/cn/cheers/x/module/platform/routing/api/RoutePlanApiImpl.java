package cn.cheers.x.module.platform.routing.api;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewDTO;
import cn.cheers.x.module.platform.contract.dto.route.RouteRequestDTO;
import cn.cheers.x.module.platform.routing.service.RoutePlanService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class RoutePlanApiImpl implements RoutePlanApi {

    @Resource
    private RoutePlanService routePlanService;

    @Override
    public CommonResult<RoutePreviewDTO> plan(RouteRequestDTO request) {
        return success(routePlanService.plan(request));
    }
}
