package cn.cheers.x.module.platform.policy.controller.admin;

import cn.cheers.x.module.platform.policy.api.dto.InstantiatePolicySetReqDTO;
import cn.cheers.x.module.platform.policy.api.dto.PolicySetRespDTO;
import cn.cheers.x.module.platform.policy.service.PolicySetService;
import cn.cheers.x.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 平台策略集")
@RestController
@RequestMapping("/platform/policy")
public class PolicySetController {

    @Resource
    private PolicySetService policySetService;

    @PostMapping("/policy-sets/instantiate-from-template")
    @Operation(summary = "从模板实例化策略集")
    public CommonResult<PolicySetRespDTO> instantiateFromTemplate(
            @Valid @RequestBody InstantiatePolicySetReqDTO request) {
        return success(policySetService.instantiateFromTemplate(request));
    }

    @PostMapping("/policy-sets/{id}/publish")
    @Operation(summary = "发布策略集")
    public CommonResult<PolicySetRespDTO> publish(@PathVariable("id") String id) {
        return success(policySetService.publish(id));
    }

    @GetMapping("/policy-sets/{id}")
    @Operation(summary = "读取策略集")
    public CommonResult<PolicySetRespDTO> get(@PathVariable("id") String id) {
        return success(policySetService.get(id));
    }
}
