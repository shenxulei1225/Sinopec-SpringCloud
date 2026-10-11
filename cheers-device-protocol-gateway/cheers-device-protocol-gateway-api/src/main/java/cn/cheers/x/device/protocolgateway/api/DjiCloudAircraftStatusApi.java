package cn.cheers.x.device.protocolgateway.api;

import cn.cheers.x.device.protocolgateway.api.dto.AircraftFlightStatusDTO;
import cn.cheers.x.device.protocolgateway.enums.ApiConstants;
import cn.cheers.x.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 按网关取它下面那一架飞机最近一帧飞行状态。
 * <p>不读巡检账，不编序列号。多于一架或还没报到时，结果里写明缺口。
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 大疆飞机状态")
public interface DjiCloudAircraftStatusApi {

    String PREFIX = ApiConstants.PREFIX + "/aircraft-status";

    @GetMapping(PREFIX)
    @Operation(summary = "按网关读取其下唯一飞机的最新飞行状态")
    CommonResult<AircraftFlightStatusDTO> status(@RequestParam("gatewaySn") String gatewaySn);
}
