package cn.iocoder.yudao.module.emergency.api.process;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.emergency.service.process.EmergencyProcessRuntimeService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class EmergencyProcessTaskApiImpl implements EmergencyProcessTaskApi {

    @Resource
    private EmergencyProcessRuntimeService processRuntimeService;

    @Override
    public CommonResult<Boolean> startResponse(StartResponseServiceTaskReq req) {
        processRuntimeService.onServiceTaskStartResponse(req.getEventId());
        return success(Boolean.TRUE);
    }
}
