package cn.cheers.x.device.protocolgateway.api;

import cn.cheers.x.device.protocolgateway.api.dto.GroundStationLiveStatusDTO;
import cn.cheers.x.device.protocolgateway.enums.ApiConstants;
import cn.cheers.x.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 读取东古地面站刚报上的电量。
 * <p>不读巡检账，不把电量换算成剩余可飞秒数。
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 地面站实时电量")
public interface GroundStationLiveStatusApi {

    String PREFIX = ApiConstants.PREFIX + "/ground-station-live-status";

    @GetMapping(PREFIX)
    @Operation(summary = "读取这台地面站设备最近一次状态里的电量")
    CommonResult<GroundStationLiveStatusDTO> latest(@RequestParam("deviceId") String deviceId);
}
