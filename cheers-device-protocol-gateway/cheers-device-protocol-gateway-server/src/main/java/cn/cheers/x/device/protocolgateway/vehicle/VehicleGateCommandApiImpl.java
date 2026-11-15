package cn.cheers.x.device.protocolgateway.vehicle;

import cn.cheers.x.device.protocolgateway.api.vehicle.VehicleGateCommandApi;
import cn.cheers.x.device.protocolgateway.api.vehicle.VehicleGateCommandReq;
import cn.cheers.x.device.protocolgateway.api.vehicle.VehicleGateCommandResp;
import cn.cheers.x.framework.common.pojo.CommonResult;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 车辆 HTTPS 指令的入口。
 * <p>负责：把请求交给对照发送。
 * <p>不负责：写进出账。
 */
@RestController
public class VehicleGateCommandApiImpl implements VehicleGateCommandApi {

    private final VehicleHttpsCommandService commands;

    public VehicleGateCommandApiImpl(VehicleHttpsCommandService commands) {
        this.commands = commands;
    }

    @Override
    public CommonResult<VehicleGateCommandResp> send(VehicleGateCommandReq request) {
        if (request == null) {
            return success(new VehicleGateCommandResp(false, "没有要下发的动作"));
        }
        return success(commands.send(request.deviceCode(), request.actionCode(), request.params()));
    }
}
