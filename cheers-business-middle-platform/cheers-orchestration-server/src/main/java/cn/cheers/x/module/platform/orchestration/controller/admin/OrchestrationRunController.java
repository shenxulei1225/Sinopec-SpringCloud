package cn.cheers.x.module.platform.orchestration.controller.admin;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platform.orchestration.api.dto.OrchestrationRunRequest;
import cn.cheers.x.module.platform.orchestration.api.dto.OrchestrationRunResponse;
import cn.cheers.x.module.platform.orchestration.service.OrchestrationRunner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 业务编排运行")
@RestController
@RequestMapping("/platform/orchestration")
@Validated
public class OrchestrationRunController {

    @Resource
    private OrchestrationRunner orchestrationRunner;

    @PostMapping("/run")
    @Operation(summary = "按编排模板引用运行（validate→expand→…）")
    public CommonResult<OrchestrationRunResponse> run(@Valid @RequestBody OrchestrationRunRequest request) {
        return success(orchestrationRunner.run(request));
    }
}
