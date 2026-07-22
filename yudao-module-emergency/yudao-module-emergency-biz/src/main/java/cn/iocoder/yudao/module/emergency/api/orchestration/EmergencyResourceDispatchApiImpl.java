package cn.iocoder.yudao.module.emergency.api.orchestration;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.emergency.api.orchestration.dto.EmergencyResourceDispatchExpandRespDTO;
import cn.iocoder.yudao.module.emergency.api.orchestration.dto.EmergencyResourceDispatchReqDTO;
import cn.iocoder.yudao.module.emergency.service.dispatch.ResourceDispatchService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class EmergencyResourceDispatchApiImpl implements EmergencyResourceDispatchApi {

    @Resource
    private ResourceDispatchService resourceDispatchService;

    @Override
    public CommonResult<Boolean> validate(EmergencyResourceDispatchReqDTO req) {
        resourceDispatchService.validateDispatchForOrchestration(req);
        return success(true);
    }

    @Override
    public CommonResult<EmergencyResourceDispatchExpandRespDTO> expand(EmergencyResourceDispatchReqDTO req) {
        return success(resourceDispatchService.expandDispatchForOrchestration(req));
    }

    @Override
    public CommonResult<EmergencyResourceDispatchExpandRespDTO> solve(EmergencyResourceDispatchExpandRespDTO expand) {
        return success(resourceDispatchService.solveDispatchForOrchestration(expand));
    }

    @Override
    public CommonResult<EmergencyResourceDispatchExpandRespDTO> persist(EmergencyResourceDispatchExpandRespDTO expandResult) {
        return success(resourceDispatchService.persistDispatchForOrchestration(expandResult));
    }
}
