package cn.cheers.x.module.platform.runtime.api;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimePersistReqDTO;
import cn.cheers.x.module.platform.runtime.service.RuntimePersistService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class RuntimePersistApiImpl implements RuntimePersistApi {

    @Resource
    private RuntimePersistService runtimePersistService;

    @Override
    public CommonResult<Boolean> persist(RuntimePersistReqDTO request) {
        runtimePersistService.saveJobWithSlots(request.getJob(), request.getSlots(), request.getSiteId());
        return success(true);
    }
}
