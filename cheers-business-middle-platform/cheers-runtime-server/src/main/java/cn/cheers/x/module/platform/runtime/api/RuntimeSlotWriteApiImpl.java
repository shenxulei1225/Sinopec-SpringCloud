package cn.cheers.x.module.platform.runtime.api;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimeSlotStatusUpdateReqDTO;
import cn.cheers.x.module.platform.runtime.service.RuntimeSlotWriteService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class RuntimeSlotWriteApiImpl implements RuntimeSlotWriteApi {

    @Resource
    private RuntimeSlotWriteService runtimeSlotWriteService;

    @Override
    public CommonResult<Boolean> updateSlotStatus(RuntimeSlotStatusUpdateReqDTO request) {
        runtimeSlotWriteService.updateSlotStatus(request);
        return success(true);
    }
}
