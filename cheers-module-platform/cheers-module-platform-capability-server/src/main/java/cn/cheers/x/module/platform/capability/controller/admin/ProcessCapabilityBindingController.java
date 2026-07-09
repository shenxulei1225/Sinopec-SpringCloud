package cn.cheers.x.module.platform.capability.controller.admin;

import cn.cheers.x.module.platform.capability.api.dto.ProcessCapabilityBindingRespDTO;
import cn.cheers.x.module.platform.capability.api.dto.ProcessCapabilityBindingSaveReqDTO;
import cn.cheers.x.module.platform.capability.service.ProcessCapabilityBindingService;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 过程能力绑定")
@RestController
@RequestMapping("/platform/registry")
public class ProcessCapabilityBindingController {

    @Resource
    private ProcessCapabilityBindingService processCapabilityBindingService;

    @PutMapping("/{entityTypeCode}/process-capability-binding")
    @Operation(summary = "保存过程能力绑定")
    public CommonResult<ProcessCapabilityBindingRespDTO> saveBinding(
            @PathVariable("entityTypeCode") String entityTypeCode,
            @Valid @RequestBody ProcessCapabilityBindingSaveReqDTO request) {
        return success(processCapabilityBindingService.saveBinding(entityTypeCode, request));
    }

    @GetMapping("/{entityTypeCode}/process-capability-binding")
    @Operation(summary = "读取过程能力绑定")
    public CommonResult<ProcessCapabilityBindingRespDTO> getBinding(
            @PathVariable("entityTypeCode") String entityTypeCode) {
        return success(processCapabilityBindingService.getBinding(entityTypeCode));
    }

    @PostMapping("/{entityTypeCode}/process-capability-binding/publish")
    @Operation(summary = "发布过程能力绑定")
    public CommonResult<ProcessCapabilityBindingRespDTO> publishBinding(
            @PathVariable("entityTypeCode") String entityTypeCode) {
        return success(processCapabilityBindingService.publishBinding(entityTypeCode));
    }
}
