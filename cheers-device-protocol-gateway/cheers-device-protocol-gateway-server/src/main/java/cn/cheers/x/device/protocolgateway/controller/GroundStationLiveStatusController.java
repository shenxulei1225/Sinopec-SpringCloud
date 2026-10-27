package cn.cheers.x.device.protocolgateway.controller;

import cn.cheers.x.device.protocolgateway.api.GroundStationLiveStatusApi;
import cn.cheers.x.device.protocolgateway.api.dto.GroundStationLiveStatusDTO;
import cn.cheers.x.device.protocolgateway.protocol.uplink.GroundStationLiveElectricityQuery;
import cn.cheers.x.framework.common.pojo.CommonResult;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 把东古地面站当前电量交给巡检预计线和开跑前的电量检查。
 * <p>内存里已经有最新一帧就用这一帧。还没有时向设备要一次「获取设备当前实时状态」。
 * <p>不负责比较秒数，不写执行记录。
 */
@RestController
@Validated
@RequiredArgsConstructor
public class GroundStationLiveStatusController implements GroundStationLiveStatusApi {

    private final GroundStationLiveElectricityQuery liveElectricityQuery;

    @Override
    public CommonResult<GroundStationLiveStatusDTO> latest(String deviceId) {
        return success(liveElectricityQuery.read(deviceId));
    }
}
