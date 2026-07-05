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

    @PutMapping("/{businessTypeCode}/process-capability-binding")
    @Operation(summary = "保存过程能力绑定")
    public CommonResult<ProcessCapabilityBindingRespDTO> saveBinding(
            @PathVariable("businessTypeCode") String businessTypeCode,
            @Valid @RequestBody ProcessCapabilityBindingSaveReqDTO request) {
        return success(processCapabilityBindingService.saveBinding(businessTypeCode, request));
    }

    @GetMapping("/{businessTypeCode}/process-capability-binding")
    @Operation(summary = "读取过程能力绑定")
    public CommonResult<ProcessCapabilityBindingRespDTO> getBinding(
            @PathVariable("businessTypeCode") String businessTypeCode) {
        return success(processCapabilityBindingService.getBinding(businessTypeCode));
    }

    @PostMapping("/{businessTypeCode}/process-capability-binding/publish")
    @Operation(summary = "发布过程能力绑定")
    public CommonResult<ProcessCapabilityBindingRespDTO> publishBinding(
            @PathVariable("businessTypeCode") String businessTypeCode) {
        return success(processCapabilityBindingService.publishBinding(businessTypeCode));
    }
}
