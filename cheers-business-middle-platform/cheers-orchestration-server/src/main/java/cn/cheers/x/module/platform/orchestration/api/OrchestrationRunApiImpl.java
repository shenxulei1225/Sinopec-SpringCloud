package cn.cheers.x.module.platform.orchestration.api;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platform.orchestration.api.dto.OrchestrationRunRequest;
import cn.cheers.x.module.platform.orchestration.api.dto.OrchestrationRunResponse;
import cn.cheers.x.module.platform.orchestration.service.OrchestrationRunner;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class OrchestrationRunApiImpl implements OrchestrationRunApi {

    @Resource
    private OrchestrationRunner orchestrationRunner;

    @Override
    public CommonResult<OrchestrationRunResponse> run(OrchestrationRunRequest request) {
        return success(orchestrationRunner.run(request));
    }
}
