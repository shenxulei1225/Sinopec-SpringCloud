package cn.cheers.x.device.protocolgateway.api.vehicle;

import cn.cheers.x.device.protocolgateway.enums.ApiConstants;
import cn.cheers.x.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 车辆进出的下发。抬杆和下发限制都走这里。
 * <p>负责：按设备编号找到协议版本，按动作编码找到已经配好的对照，HTTPS 指令按说明书上的地址发出。
 * <p>不负责：写进出账、比对名单。
 * <p>禁止：没有对照时编一条厂商地址。巡检通道改拿来发车辆指令。
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 车辆指令下发")
public interface VehicleGateCommandApi {

    String PREFIX = ApiConstants.PREFIX + "/vehicle-command";

    @PostMapping(PREFIX + "/send")
    @Operation(summary = "按指令对照发送一条车辆 HTTPS 指令")
    CommonResult<VehicleGateCommandResp> send(@RequestBody VehicleGateCommandReq request);
}
