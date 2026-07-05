package cn.cheers.x.module.platform.capability.api;

import cn.cheers.x.module.platform.capability.api.dto.ProcessCapabilityBindingRespDTO;
import cn.cheers.x.module.platform.capability.enums.ApiConstants;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 平台能力包")
public interface ProcessCapabilityBindingApi {

    String PREFIX = ApiConstants.PREFIX;

    @GetMapping(PREFIX + "/bindings/{businessTypeCode}/published")
    @Operation(summary = "读取已发布的过程能力绑定")
    CommonResult<ProcessCapabilityBindingRespDTO> getPublishedBinding(
            @PathVariable("businessTypeCode") String businessTypeCode);
}
