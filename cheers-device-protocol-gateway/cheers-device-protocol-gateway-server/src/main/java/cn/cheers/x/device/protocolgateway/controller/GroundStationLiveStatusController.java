package cn.cheers.x.device.protocolgateway.controller;

import cn.cheers.x.device.protocolgateway.api.GroundStationLiveStatusApi;
import cn.cheers.x.device.protocolgateway.api.dto.GroundStationLiveStatusDTO;
import cn.cheers.x.device.protocolgateway.protocol.uplink.GroundStationLiveStatusStore;
import cn.cheers.x.framework.common.pojo.CommonResult;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 把东古地面站最近一次电量交给巡检开跑前的电量检查。
 * <p>不负责比较秒数，不写执行记录。
 */
@RestController
@Validated
@RequiredArgsConstructor
public class GroundStationLiveStatusController implements GroundStationLiveStatusApi {

    private final GroundStationLiveStatusStore store;

    @Override
    public CommonResult<GroundStationLiveStatusDTO> latest(String deviceId) {
        GroundStationLiveStatusStore.Frame frame = store.latest(deviceId);
        if (frame == null) {
            return success(new GroundStationLiveStatusDTO(null, null));
        }
        return success(new GroundStationLiveStatusDTO(frame.electricity(), frame.receivedAtEpochMs()));
    }
}
