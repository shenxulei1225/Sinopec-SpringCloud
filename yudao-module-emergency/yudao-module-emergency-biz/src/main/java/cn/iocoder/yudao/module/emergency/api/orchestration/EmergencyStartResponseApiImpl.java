package cn.iocoder.yudao.module.emergency.api.orchestration;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.emergency.api.orchestration.dto.EmergencyStartResponseExpandRespDTO;
import cn.iocoder.yudao.module.emergency.api.orchestration.dto.EmergencyStartResponseReqDTO;
import cn.iocoder.yudao.module.emergency.service.response.EmergencyResponseService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class EmergencyStartResponseApiImpl implements EmergencyStartResponseApi {

    @Resource
    private EmergencyResponseService emergencyResponseService;

    @Override
    public CommonResult<Boolean> validate(EmergencyStartResponseReqDTO req) {
        emergencyResponseService.validateStartForOrchestration(req);
        return success(true);
    }

    @Override
    public CommonResult<EmergencyStartResponseExpandRespDTO> expand(EmergencyStartResponseReqDTO req) {
        return success(emergencyResponseService.expandStartForOrchestration(req));
    }

    @Override
    public CommonResult<Boolean> persist(EmergencyStartResponseExpandRespDTO expandResult) {
        emergencyResponseService.persistStartForOrchestration(expandResult);
        return success(true);
    }
}
