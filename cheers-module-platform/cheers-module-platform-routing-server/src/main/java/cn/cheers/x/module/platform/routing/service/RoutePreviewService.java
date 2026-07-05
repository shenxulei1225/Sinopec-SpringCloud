package cn.cheers.x.module.platform.routing.service;

import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewDTO;
import cn.cheers.x.module.platform.contract.dto.route.RouteRequestDTO;

public interface RoutePreviewService {

    RoutePreviewDTO preview(RouteRequestDTO request);
}
