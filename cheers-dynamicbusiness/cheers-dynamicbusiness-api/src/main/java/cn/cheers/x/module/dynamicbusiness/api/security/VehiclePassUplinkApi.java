package cn.cheers.x.module.dynamicbusiness.api.security;

import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.security.dto.VehicleLiftResultReq;
import cn.cheers.x.module.dynamicbusiness.api.security.dto.VehiclePassNotifyResp;
import cn.cheers.x.module.dynamicbusiness.api.security.dto.VehicleRestrictionDTO;
import cn.cheers.x.module.dynamicbusiness.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 车辆进出接收协议网关已经核对过的样本。
 * <p>负责：识别到车写入进出账并按车辆登记比对。已经进出按样本里的时间写入进出账。下发限制只读车辆登记上已经写下的栏。
 * <p>不负责：拆原始报文、按厂商名填包、抬杆。
 * <p>禁止：协议不合格仍写进出。把限制读数写成一条进出。把车辆进出写进人员门禁。
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 车辆进出上报")
public interface VehiclePassUplinkApi {

    String PREFIX = ApiConstants.DYNAMICBUSINESS_PREFIX + "/vehicle-pass-uplink";

    @PostMapping(PREFIX + "/notify")
    @Operation(summary = "接收车辆进出标准事件，写入进出账")
    CommonResult<VehiclePassNotifyResp> notify(@RequestBody CollectionSample sample);

    @PostMapping(PREFIX + "/lift-result")
    @Operation(summary = "抬杆发出之后，回写进入或离开时间")
    CommonResult<Boolean> finishLift(@RequestBody VehicleLiftResultReq request);

    @GetMapping(PREFIX + "/restrictions")
    @Operation(summary = "读出这一座站车辆登记上要下发的进出限制")
    CommonResult<List<VehicleRestrictionDTO>> restrictions(@RequestParam("facilityId") long facilityId);
}
