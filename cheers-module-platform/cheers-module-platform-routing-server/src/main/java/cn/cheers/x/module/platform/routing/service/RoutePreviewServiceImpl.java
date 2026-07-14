package cn.cheers.x.module.platform.routing.service;

import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewDTO;
import cn.cheers.x.module.platform.contract.dto.route.RouteRequestDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class RoutePreviewServiceImpl implements RoutePreviewService {

    @Resource
    private RoutePlanService routePlanService;

    @Override
    public RoutePreviewDTO preview(RouteRequestDTO request) {
        return routePlanService.plan(request);
    }
}
