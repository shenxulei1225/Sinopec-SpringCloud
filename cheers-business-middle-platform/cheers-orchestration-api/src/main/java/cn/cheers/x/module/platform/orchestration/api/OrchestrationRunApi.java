package cn.cheers.x.module.platform.orchestration.api;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platform.orchestration.api.dto.OrchestrationRunRequest;
import cn.cheers.x.module.platform.orchestration.api.dto.OrchestrationRunResponse;
import cn.cheers.x.module.platform.orchestration.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 业务编排运行")
public interface OrchestrationRunApi {

    String PREFIX = ApiConstants.PREFIX + "/run";

    @PostMapping(PREFIX)
    @Operation(summary = "按编排模板引用运行阶段机")
    CommonResult<OrchestrationRunResponse> run(@Valid @RequestBody OrchestrationRunRequest request);
}
