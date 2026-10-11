package cn.cheers.x.device.protocolgateway.controller;

import cn.cheers.x.device.protocolgateway.api.DjiCloudAircraftStatusApi;
import cn.cheers.x.device.protocolgateway.api.dto.AircraftFlightStatusDTO;
import cn.cheers.x.device.protocolgateway.djicloud.DjiCloudAircraftStatusService;
import cn.cheers.x.framework.common.pojo.CommonResult;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 把网关内存里的飞机状态交给巡检画面。
 * <p>不读巡检账，不在这里编序列号。
 */
@RestController
@Validated
@RequiredArgsConstructor
public class DjiCloudAircraftStatusController implements DjiCloudAircraftStatusApi {

    private final DjiCloudAircraftStatusService statusService;

    @Override
    public CommonResult<AircraftFlightStatusDTO> status(String gatewaySn) {
        return success(statusService.forGateway(gatewaySn));
    }
}
