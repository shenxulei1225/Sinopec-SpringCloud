package cn.cheers.x.device.protocolgateway.protocol.processor;

import cn.cheers.x.device.protocolgateway.api.dto.DeviceUplinkEventDTO;
import cn.cheers.x.device.protocolgateway.api.identity.UplinkIdentity;
import cn.cheers.x.device.protocolgateway.api.vehicle.VehicleGateActions;
import cn.cheers.x.device.protocolgateway.api.vehicle.VehicleGateCommandResp;
import cn.cheers.x.device.protocolgateway.vehicle.VehicleHttpsCommandService;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.security.VehiclePassEventCodes;
import cn.cheers.x.module.dynamicbusiness.api.security.VehiclePassUplinkApi;
import cn.cheers.x.module.dynamicbusiness.api.security.dto.VehicleLiftResultReq;
import cn.cheers.x.module.dynamicbusiness.api.security.dto.VehiclePassNotifyResp;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 车辆进出的上报入口。
 * <p>负责：报文种类已经是识别到车或已经进出时，把样本交给车辆进出。允许放行时再按对照发抬杆。
 * <p>不负责：按厂商名拆包、下发限制。
 * <p>禁止：巡检或其他报文种类也送进车辆进出。协议不合格仍交给进出账。抬杆没发出仍写成已经进入。
 */
@Slf4j
public final class VehiclePassUplinkProcessor implements UplinkProcessor {

    private final VehiclePassUplinkApi vehiclePassUplinkApi;
    private final VehicleHttpsCommandService commands;

    public VehiclePassUplinkProcessor(
            VehiclePassUplinkApi vehiclePassUplinkApi,
            VehicleHttpsCommandService commands) {
        this.vehiclePassUplinkApi = vehiclePassUplinkApi;
        this.commands = commands;
    }

    @Override
    public boolean supports(UplinkIdentity identity) {
        return VehiclePassEventCodes.isVehiclePass(identity.messageKind());
    }

    @Override
    public void process(UplinkIdentity identity, DeviceUplinkEventDTO event) {
        if (!event.sample().isProtocolQualified()) {
            throw new IllegalStateException("车辆进出上报协议不合格，不写入进出账");
        }
        CommonResult<VehiclePassNotifyResp> result = vehiclePassUplinkApi.notify(event.sample());
        if (result == null || !result.isSuccess() || result.getData() == null) {
            throw new IllegalStateException("车辆进出没有写入 deviceId="
                    + identity.deviceId()
                    + " messageKind=" + identity.messageKind()
                    + " msg=" + (result == null ? "无响应" : result.getMsg()));
        }
        VehiclePassNotifyResp written = result.getData();
        if (written.lift()) {
            lift(written);
        }
        log.info("[device-protocol] 车辆进出已交给进出账 deviceId={} messageKind={}",
                identity.deviceId(), identity.messageKind());
    }

    private void lift(VehiclePassNotifyResp written) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("车牌", written.plate());
        params.put("车道", written.laneId());
        params.put("方向", written.direction());
        VehicleGateCommandResp sent = commands.send(written.deviceCode(), VehicleGateActions.LIFT, params);
        boolean ok = sent != null && sent.sent();
        String message = sent == null ? "指令没有发出" : sent.message();
        CommonResult<Boolean> finished = vehiclePassUplinkApi.finishLift(
                new VehicleLiftResultReq(written.passId(), ok, message, written.enter()));
        if (finished == null || !finished.isSuccess()) {
            throw new IllegalStateException("抬杆结果没有写回进出账 passId=" + written.passId());
        }
    }
}
