package cn.cheers.x.module.platform.runtime.api;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimePersistReqDTO;
import cn.cheers.x.module.platform.runtime.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 平台 L4 运行时")
public interface RuntimePersistApi {

    String PREFIX = ApiConstants.PREFIX + "/persist";

    @PostMapping(PREFIX)
    @Operation(summary = "保存运行作业与计划点")
    CommonResult<Boolean> persist(@Valid @RequestBody RuntimePersistReqDTO request);
}
