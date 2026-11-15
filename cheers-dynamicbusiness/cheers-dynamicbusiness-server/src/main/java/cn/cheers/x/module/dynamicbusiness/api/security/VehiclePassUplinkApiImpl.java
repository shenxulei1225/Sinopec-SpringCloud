package cn.cheers.x.module.dynamicbusiness.api.security;

import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.security.dto.VehicleLiftResultReq;
import cn.cheers.x.module.dynamicbusiness.api.security.dto.VehiclePassNotifyResp;
import cn.cheers.x.module.dynamicbusiness.api.security.dto.VehicleRestrictionDTO;
import cn.cheers.x.module.dynamicbusiness.service.security.VehiclePassSample;
import cn.cheers.x.module.dynamicbusiness.service.security.VehicleRecognitionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 车辆进出接收网关样本。
 * <p>负责：协议合格的识别到车、已经进出交给进出账。抬杆结果回写时间。限制只读车辆登记和外来车。
 * <p>不负责：核对说明书、自己拼厂商地址。
 * <p>禁止：不合格样本仍写入。把限制读数写成进出。抬杆没被接受仍写进入时间。
 */
@RestController
@Validated
public class VehiclePassUplinkApiImpl implements VehiclePassUplinkApi {

    private final VehicleRecognitionService vehicleRecognitionService;

    public VehiclePassUplinkApiImpl(VehicleRecognitionService vehicleRecognitionService) {
        this.vehicleRecognitionService = vehicleRecognitionService;
    }

    @Override
    public CommonResult<VehiclePassNotifyResp> notify(CollectionSample sample) {
        if (sample == null || !sample.isProtocolQualified()) {
            throw new ServiceException(400, "这一条车辆进出协议不合格，没有写入");
        }
        if (!VehiclePassEventCodes.isVehiclePass(sample.messageKind())) {
            throw new ServiceException(400, "这不是车辆进出事件");
        }
        VehiclePassSample.Parsed parsed = VehiclePassSample.read(sample);
        if (parsed.seen()) {
            VehicleRecognitionService.SeenPass seen = vehicleRecognitionService.acceptSeen(
                    parsed.laneId(), parsed.plate(), parsed.plateColor(),
                    parsed.scenePhoto(), parsed.platePhoto(), parsed.uncertain());
            return success(new VehiclePassNotifyResp(
                    seen.id(), seen.lift(), seen.deviceCode(), seen.plate(),
                    seen.laneId(), seen.direction(), seen.enter()));
        }
        long id = vehicleRecognitionService.acceptRecorded(
                parsed.laneId(), parsed.plate(), parsed.direction(), parsed.when(),
                parsed.scenePhoto(), parsed.platePhoto());
        return success(new VehiclePassNotifyResp(id, false, "", parsed.plate(), parsed.laneId(), parsed.direction(), false));
    }

    @Override
    public CommonResult<Boolean> finishLift(VehicleLiftResultReq request) {
        if (request == null) {
            throw new ServiceException(400, "没有这一条过车");
        }
        vehicleRecognitionService.finishLift(request.passId(), request.sent(), request.message(), request.enter());
        return success(true);
    }

    @Override
    public CommonResult<List<VehicleRestrictionDTO>> restrictions(long facilityId) {
        return success(vehicleRecognitionService.restrictions(facilityId));
    }
}
