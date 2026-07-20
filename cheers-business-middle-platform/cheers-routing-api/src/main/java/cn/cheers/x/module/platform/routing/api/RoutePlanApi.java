package cn.cheers.x.module.platform.routing.api;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewDTO;
import cn.cheers.x.module.platform.contract.dto.route.RouteRequestDTO;
import cn.cheers.x.module.platform.routing.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 路径规划")
public interface RoutePlanApi {

    String PREFIX = ApiConstants.PREFIX + "/plan";

    @PostMapping(PREFIX)
    @Operation(summary = "路径规划")
    CommonResult<RoutePreviewDTO> plan(@Valid @RequestBody RouteRequestDTO request);
}
