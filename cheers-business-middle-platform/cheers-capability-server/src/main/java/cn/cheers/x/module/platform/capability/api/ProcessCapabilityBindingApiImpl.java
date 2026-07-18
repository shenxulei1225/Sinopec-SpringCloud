package cn.cheers.x.module.platform.capability.api;

import cn.cheers.x.module.platform.capability.api.dto.ProcessCapabilityBindingRespDTO;
import cn.cheers.x.module.platform.capability.service.ProcessCapabilityBindingService;
import cn.cheers.x.framework.common.pojo.CommonResult;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class ProcessCapabilityBindingApiImpl implements ProcessCapabilityBindingApi {

    @Resource
    private ProcessCapabilityBindingService processCapabilityBindingService;

    @Override
    public CommonResult<ProcessCapabilityBindingRespDTO> getPublishedBinding(String entityTypeCode) {
        return success(processCapabilityBindingService.getPublishedBinding(entityTypeCode));
    }
}
